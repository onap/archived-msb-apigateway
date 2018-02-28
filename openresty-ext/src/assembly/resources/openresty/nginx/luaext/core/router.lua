--[[

    Copyright (C) 2017 ZTE, Inc. and others. All rights reserved. (ZTE)

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

-- unified layer to access back DB, using two levels of cache mechanism(LRUCache and shcache)
local _M = {}
_M._VERSION = '1.0.0'
local msbConf   =  require('conf.msbinit')
local dbclient  =  require('dao.db_access')
local tbl_util  =  require('lib.utils.table_util')
local svc_util  =  require('lib.utils.svc_util')
local log_util  =  require('lib.utils.log_util')
local stats     =  require ('monitor.stats')
local error_handler  =  require('core.error_handler')
local dns_util  =  require('lib.utils.dns_util')

local defaultport = msbConf.systemConf.defaultport
local defaulthttpsport = msbConf.systemConf.defaulthttpsport
local defaultprefix = msbConf.systemConf.defaultprefix
local router_subdomain = msbConf.routerConf.subdomain
local router_defaultprefix = msbConf.routerConf.defaultprefix
local str_sub = string.sub
local str_len = string.len
local str_low = string.lower
local tbl_concat = table.concat
local tbl_isempty = tbl_util.isempty
local svc_is_api_related_types = svc_util.is_api_related_types
local svc_isactive = svc_util.isactive
local svc_get_url = svc_util.get_url
local svc_get_backend_protocol = svc_util.get_backend_protocol
local svc_use_own_upstream = svc_util.use_own_upstream
local svc_enable_refer_match = svc_util.enable_refer_match
local svc_is_allow_access = svc_util.is_allow_access
local ngx_var = ngx.var
local log = log_util.log
local error_svc_not_found = error_handler.svc_not_found
--local error_upstream_not_found = error_handler.upstream_not_found
local error_no_server_available = error_handler.no_server_available
local error_svc_not_allow_access = error_handler.svc_not_allow_access
local dns_query = dns_util.query
local tbl_insert = table.insert

local enablerefercheck = msbConf.systemConf.enablerefercheck
local useconsultemplate = msbConf.systemConf.useconsultemplate

local function _get_key_prefix(server_port)
	if(not server_port) then
		server_port = ngx_var.server_port
	end
	local svc_name = ngx_var.svc_name
	if ("microservices" == svc_name or "msdiscover" == svc_name) then
		return defaultprefix
	elseif (server_port == defaultport or server_port == defaulthttpsport) then
		local m, err = ngx.re.match(ngx_var.host, "(?<hostname>.+)\\."..router_subdomain,"o")
		if m then
			return router_defaultprefix..":"..m["hostname"]
		else
			return defaultprefix
		end
	else
		return "msb:"..server_port
	end
end

local function _load_common_svc_info(svc_type,server_port)
	local key_prefix = _get_key_prefix(server_port)
	local req_res  = ngx_var.req_res
	local svc_name = ngx_var.svc_name
	local svc_key = ""
	local svc_pub_url = ""
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
			ngx_var.req_res = version2..req_res
		end
		-- remove the slash in front of the version (e.g. /V1.0)
		local svc_version=str_sub(version,2,str_len(version))
		svc_key = tbl_concat({key_prefix,"api",svc_name,svc_version},":")
		svc_pub_url = "/"..svc_type.."/"..svc_name
		if(svc_version ~= "") then svc_pub_url = svc_pub_url.."/"..svc_version end
	else
		svc_key = tbl_concat({key_prefix,"iui",svc_name},":")
		svc_pub_url = "/iui/"..svc_name
	end

	local svcinfo = dbclient.load_serviceinfo(svc_key)
	if tbl_isempty(svcinfo) then
		return nil,"","","Common not match. key--"..svc_key
	end

	if not svc_isactive(svcinfo) then
		return nil,"","","Common matched but service is disabled! key--"..svc_key
	end

	ngx.ctx.svc_pub_url = svc_pub_url
	return svcinfo,svc_key,svc_name,""
end

local function _load_custom_svc_info(svc_type,server_port)
	local key_prefix = _get_key_prefix(server_port)
	local get_svckey_custom = function(svcname)
		return tbl_concat({key_prefix,"custom",svcname},":")
	end
	local custom_svc_keypattern = tbl_concat({key_prefix,"custom","*"},":")
	local svcnames,err = dbclient.load_customsvcnames(custom_svc_keypattern)
	if not svcnames then
		error_svc_not_found("Failed to load the route table!","keypattern--"..custom_svc_keypattern)
	end
	ngx.ctx.svcnames = svcnames
	local matchedsvcname
	local svcinfo
	local svc_key = ""
	for _, svcname in ipairs(svcnames) do
		if (svcname == "/") then
			svc_key = get_svckey_custom(svcname)
			local svc_info,err = dbclient.load_serviceinfo(svc_key)
			if svc_info and svc_isactive(svc_info)then
				matchedsvcname = svcname
				svcinfo = svc_info
				break
			end
		end
		local from, to, err = ngx.re.find(ngx_var.uri, "^"..svcname.."(/(.*))?$", "jo")
		--check whether svcname is the prefix of the req uri
		if from then
			svc_key = get_svckey_custom(svcname)
			local svc_info,err = dbclient.load_serviceinfo(svc_key)
			if svc_info and svc_isactive(svc_info) then 
				matchedsvcname = svcname
				svcinfo = svc_info
				break
			end
		else
			--do nothing
		end
	end
	--add by wangyg:20160418 special handler for refer
	if not matchedsvcname and enablerefercheck then 
		local refer =  ngx_var.http_referer
		if(refer and refer~="") then
			for _, svcname in ipairs(svcnames) do
				local urlreg ="^(https://|http://|)(([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))\\.)(([0-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))\\.){2}([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))(:\\d{1,5})?"..svcname.."(/(.*))?$";
				local from, to, err = ngx.re.find(refer, urlreg, "jo")
				----check whether svcname is the prefix of the req refer
				if from then
					svc_key = get_svckey_custom(svcname)
					local svc_info,err = dbclient.load_serviceinfo(svc_key)
					if svc_info and svc_isactive(svc_info) and svc_enable_refer_match(svc_info) then 
						matchedsvcname = svcname
						svcinfo = svc_info
						ngx.ctx.matched_usingrefer = true
						log("matched_usingrefer",true)
						break
					end
				end
			end
		end
	end
	--end of special handler for refer
	if not matchedsvcname or tbl_isempty(svcinfo) then
		return nil,"","","Custom not match"
	end
	ngx.ctx.svc_pub_url = matchedsvcname
	return svcinfo,svc_key,matchedsvcname,""
end

-- syntax: svc_info,svc_key,matched_svcname,err = _load_service_info(svc_type,server_port)
local function _load_service_info(svc_type,server_port)
	if(svc_type ~= "custom") then
		return _load_common_svc_info(svc_type,server_port)
	else
		return _load_custom_svc_info(svc_type,server_port)
	end
end

---------------------------------------------------------------
--Main Entry of the rewrite module
---------------------------------------------------------------

function _M.execute(server_port,system_tag)
	---------------------------------------------------------------
	--step1:query the service info from share memory or backend db
	--      svc_info: the requested service information
	--      svc_key: the redis key
	--      matched_svcname: the matched service name in the route table
	--      err: the detail error info while load failed
	---------------------------------------------------------------
	local svc_type = ngx_var.svc_type
	local svc_info,svc_key,matched_svcname,err = _load_service_info(svc_type,server_port)
	if(not svc_info) then
		error_svc_not_found("No route found for this request!",err)
	end
	ngx.ctx.svc_key = svc_key
	ngx.ctx.svc_info = svc_info
	--log the route info
	log("matched",matched_svcname)

	if not svc_is_allow_access(system_tag,svc_info) then 
		error_svc_not_allow_access("Route is not allowed to access!","system_tag:"..system_tag.." svc_key:"..svc_key)
	end	
	---------------------------------------------------------------
	--step2:rewrite the request uri using the svc_info
	---------------------------------------------------------------
	local svc_url = svc_get_url(svc_info,svc_type)
	local rewrited_uri =""
	if(svc_type ~= "custom") then
		rewrited_uri = svc_url..ngx_var.req_res
	elseif (matched_svcname == "/") then
		--special handling: if "/" matched, contact directly
		rewrited_uri = svc_url..ngx_var.uri
	else
		local newuri,n,err = ngx.re.sub(ngx_var.uri, "^"..matched_svcname.."(/.*)?", svc_url.."$1", "o")
		--add by wangyg:20160418 special handler for refer
		if(n==0 and ngx.ctx.matched_usingrefer) then newuri = svc_url..ngx_var.uri end --special handling if matched using refer
		--end of add by wangyg:20160418 special handler for refer
		rewrited_uri = newuri
	end
	if (rewrited_uri == "") then return ngx.redirect(ngx.var.uri.."/") end 
	ngx.req.set_uri(rewrited_uri)
	ngx.ctx.svc_url = svc_url

	---------------------------------------------------------------
	--step3:process the proxy upstream part
	-- con1-using consul template:set the upstream name
	-- con2-using msb balancer:query the server list and store in the ctx
	---------------------------------------------------------------
	--set the http_protocol used by proxy_pass directive
	ngx_var.http_protocol = svc_get_backend_protocol(svc_info)

	--[[
	if svc_use_own_upstream(svc_info) then
		ngx.ctx.use_ownupstream = true
	end
		local consul_servicename = svc_info.spec["consulServiceName"]
		if not consul_servicename or consul_servicename == "" then
			error_upstream_not_found()
		end
		ngx_var.backend = consul_servicename
		ngx.ctx.use_ownupstream = true
	else
	]]--
	local backservers = svc_info.spec.nodes
	if tbl_isempty(backservers) then
		error_no_server_available("No active backend server found!"," key--"..svc_key)
	end
	local new_backservers = {}
	for i, server in ipairs(backservers) do
		local m, err = ngx.re.match(server["ip"], "^([0-9a-zA-Z]([0-9a-zA-Z-]+[\\.]{1})+[a-zA-Z-]+)$","o")
		if m then
			local ipaddr = dns_query(server["ip"])
			if ipaddr then
				local new_server = {}
				new_server["ip"] = ipaddr
				new_server["port"] = server["port"]
				tbl_insert(new_backservers,new_server)
			end
		else
			local new_server = {}
			new_server["ip"] = server["ip"]
			new_server["port"] = server["port"]
			tbl_insert(new_backservers,new_server)
		end
	end
	if tbl_isempty(new_backservers) then
		error_no_server_available("The domain name of backendserver was not resolved!"," key--"..svc_key)
	end
	ngx.ctx.backservers = new_backservers
	--end
end

return _M