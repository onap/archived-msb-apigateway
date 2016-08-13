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

local msbConf   =  require('conf.msbinit')
local svcConf   =  require('conf.svcconf')
local dbclient  =  require('dao.db_access')
local tbl_util  =  require('lib.utils.table_util')
local svc_util  =  require('lib.utils.svc_util')
local log_util  =  require('lib.utils.log_util')

local tbl_concat = table.concat
local tbl_isempty = tbl_util.isempty
local svc_isactive = svc_util.isactive
local svc_setauthheader = svc_util.setauthheader
local svc_isautodiscover = svc_util.isautodiscover

local str_sub = string.sub
local str_len = string.len
local str_low = string.lower
local ngx_var = ngx.var
local ngx_ctx = ngx.ctx
local log = log_util.log

local defaultport = msbConf.systemConf.defaultport
local defaultprefix = msbConf.systemConf.defaultprefix
local enablefullsearch = msbConf.systemConf.enablefullsearch
local useconsultemplate = msbConf.systemConf.useconsultemplate
local apiRelatedTypes = svcConf.apiRelatedTypes
local urlfieldMap = svcConf.urlfieldMap

local donotfound = function()
	if enablefullsearch then
		-- test against the custom services after the commonrewrite phase
		ngx.status = ngx.HTTP_GONE
	else
		ngx.status = ngx.HTTP_NOT_FOUND
		ngx.say("service info not found!")
	end
	return ngx.exit(ngx.status)
end

---------------------------------------------------------------
--preCheck:
--     determine whether it is websocket request 
--     and do internal redirect
---------------------------------------------------------------
local http_upgrade = ngx_var.http_upgrade
if(ngx_var.websocket_internal_redirect == "on") and http_upgrade and str_low(http_upgrade) == "websocket" then
	ngx.log(ngx.ERR, "Websocket request and redirect to @commonwebsocket")
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

local sys_prefix = ""
local server_port = ngx_var.server_port
if(server_port == defaultport) then
	sys_prefix = defaultprefix
else
	sys_prefix = server_port
end

local svc_info_key = ""
local svc_server_keypattern = ""
local upstream_name_consultemplate = ""
if(apiRelatedTypes[svc_type]) then
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
	svc_info_key = tbl_concat({sys_prefix,"api",svc_name,svc_version,"info"},":")
	svc_server_keypattern = tbl_concat({sys_prefix,"api",svc_name,svc_version,"lb:server*"},":")
	upstream_name_consultemplate = svc_name
else
	svc_info_key = tbl_concat({sys_prefix,"iui",svc_name,"info"},":")
	svc_server_keypattern = tbl_concat({sys_prefix,"iui",svc_name,"lb:server*"},":")
	upstream_name_consultemplate = "IUI_"..svc_name
end

---------------------------------------------------------------
--step1:query the service info from share memory or back db
--      svcinfo: the requested service information
---------------------------------------------------------------
local svcinfo = dbclient.load_serviceinfo(svc_info_key)
if not svc_isactive(svcinfo) then 
	donotfound()
end

local svc_url = svcinfo[urlfieldMap[svc_type]]
if not svc_url then 
	donotfound()
end

---------------------------------------------------------------
--step2:rewrite the request uri using the svcinfo
---------------------------------------------------------------
local rewrited_uri = svc_url..req_res
--special handling: avoid throws internal error when it is empty
if (rewrited_uri == "") then rewrited_uri = "/" end 
ngx.req.set_uri(rewrited_uri)

log("matchedservice",svc_name)
log("rewrited_uri",rewrited_uri)
---------------------------------------------------------------
--step2.1:if this service is inter-system,add custom http header
---------------------------------------------------------------
svc_setauthheader(svcinfo)

---------------------------------------------------------------
--step3:process the proxy upstream part
-- con1-using consul template:set the upstream name
-- con2-using msb balancer:query the server list and store in the ctx
---------------------------------------------------------------
if useconsultemplate and svc_isautodiscover(svcinfo) then 
	ngx_var.backend = upstream_name_consultemplate
else
	local backservers = dbclient.load_backservers(svc_server_keypattern) 
	if tbl_isempty(backservers) then
		donotfound()
	end
	ngx_ctx.backservers = backservers
	ngx_ctx.svcserverpattern = svc_server_keypattern
end