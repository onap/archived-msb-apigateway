--[[

    Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--]]

local dbclient  =  require('dao.db_access')
local tbl_util  =  require('lib.utils.table_util')
local svc_util  =  require('lib.utils.svc_util')
local log_util  =  require('lib.utils.log_util')
local error_handler  =  require('lib.utils.error_handler')

local tbl_concat = table.concat
local tbl_isempty = tbl_util.isempty
local svc_isactive = svc_util.isactive
local svc_get_url = svc_util.get_url
local svc_get_backend_protocol = svc_util.get_backend_protocol
local svc_use_own_upstream = svc_util.use_own_upstream
local svc_get_key_prefix = svc_util.get_key_prefix
local svc_is_api_related_types = svc_util.is_api_related_types
local error_svc_not_found = error_handler.svc_not_found
local error_upstream_not_found = error_handler.upstream_not_found

local str_sub = string.sub
local str_len = string.len
local str_low = string.lower
local ngx_var = ngx.var
local ngx_ctx = ngx.ctx
local log = log_util.log

---------------------------------------------------------------
--preCheck:
--     determine whether it is websocket request 
--     and do internal redirect
---------------------------------------------------------------
local http_upgrade = ngx_var.http_upgrade
if(ngx_var.websocket_internal_redirect == "on") and http_upgrade and str_low(http_upgrade) == "websocket" then
	--ngx.log(ngx.ERR, "Websocket request and redirect to @commonwebsocket")
	return ngx.exec("@commonwebsocket");
end

---------------------------------------------------------------
--step0:Preparation
--      svc_info_key
--      svc_server_keypattern
---------------------------------------------------------------
local svc_name = ngx_var.svc_name
local req_res  = ngx_var.req_res
local svc_type = ngx_var.svc_type

local key_prefix = svc_get_key_prefix()

local svc_key = ""
if(svc_is_api_related_types(svc_type)) then
	-- process version info first
	local version1 = ngx_var.svc_version1
	local version2 = ngx_var.svc_version2
	local version = ""
	-- check version info appearing befor or after
	if(not version2) then version2 = "" end	--convert nil to empty sting avoiding throw error
	if(not version1 or version1 == "") then
		version = version2
	else
		version = version1
		req_res = version2..req_res
	end
	-- remove the slash in front of the version (e.g. /V1.0)
	local svc_version=str_sub(version,2,str_len(version))
	svc_key = tbl_concat({key_prefix,"api",svc_name,svc_version},":")
else
	svc_key = tbl_concat({key_prefix,"iui",svc_name},":")
end

---------------------------------------------------------------
--step1:query the service info from share memory or back db
--      svcinfo: the requested service information
---------------------------------------------------------------
local svcinfo = dbclient.load_serviceinfo(svc_key)
if tbl_isempty(svcinfo) then
	error_svc_not_found("No route found for this request!","common not match. key--"..svc_key)
end
if not svc_isactive(svcinfo) then
	error_svc_not_found("Service is disabled!","common not match. key--"..svc_key)
end

local svc_url = svc_get_url(svcinfo,svc_type)

---------------------------------------------------------------
--step2:rewrite the request uri using the svcinfo
---------------------------------------------------------------
local rewrited_uri = svc_url..req_res
--special handling: avoid throws internal error when it is empty
--if (rewrited_uri == "") then rewrited_uri = "/" end
if (rewrited_uri == "") then return ngx.redirect(ngx.var.uri.."/") end 
ngx.req.set_uri(rewrited_uri)

log("matched",svc_name)
--log("rewrited_uri",rewrited_uri)
---------------------------------------------------------------
--step2.1:store the svcinfo in the context, plugins may use it
---------------------------------------------------------------
ngx_ctx.svcinfo = svcinfo

---------------------------------------------------------------
--step3:process the proxy upstream part
-- con1-using consul template:set the upstream name
-- con2-using msb balancer:query the server list and store in the ctx
---------------------------------------------------------------
--set the http_protocol used by proxy_pass directive
ngx_var.http_protocol = svc_get_backend_protocol(svcinfo)

if svc_use_own_upstream(svcinfo) then
	local consul_servicename = svcinfo.spec["consulServiceName"]
	if not consul_servicename or consul_servicename == "" then
		error_upstream_not_found()
	end
	ngx_var.backend = consul_servicename
	ngx.ctx.use_ownupstream = true
else
	local backservers = svcinfo.spec.nodes
	if tbl_isempty(backservers) then
		error_svc_not_found("No active backend server found!"," key--"..svc_key)
	end
	ngx_ctx.backservers = backservers
	ngx_ctx.svc_key = svc_key
end