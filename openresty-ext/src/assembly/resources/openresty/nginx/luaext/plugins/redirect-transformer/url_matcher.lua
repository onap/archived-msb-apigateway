--[[

    Copyright (C) 2018 ZTE, Inc. and others. All rights reserved. (ZTE)

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

local _M = {}
_M._VERSION = '1.0.0'

local tbl_util  =  require('lib.utils.table_util')
local dbclient  =  require('dao.db_access')
local msbConf   =  require('conf.msbinit')

local tbl_concat = table.concat
local defaultport = msbConf.systemConf.defaultport
local defaulthttpsport = msbConf.systemConf.defaulthttpsport
local defaultprefix = msbConf.systemConf.defaultprefix
local router_subdomain = msbConf.routerConf.subdomain
local router_defaultprefix = msbConf.routerConf.defaultprefix
local tbl_isempty = tbl_util.isempty

local function _get_key_prefix(scheme,host,server_port)
	if(not server_port) then
		if(scheme == "https") then
			server_port = 443
		else 
			server_port = 80
		end
	end
	if (server_port == defaultport or server_port == defaulthttpsport) then
		local m, err = ngx.re.match(host, "(?<hostname>.+)\\."..router_subdomain,"o")
		if m then
			return router_defaultprefix..":"..m["hostname"]
		else
			return defaultprefix
		end
	else
		return "msb:"..server_port
	end
end

local function _is_match_route_api(uri,key_prefix)
	local svc_name,svc_version
	local m, err = ngx.re.match(uri, "^/(api|admin|apijson)(/[Vv]\\d+(?:\\.\\d+)*)?/([^/]+)(/[Vv]\\d+(?:\\.\\d+)*)?(.*)", "o")
	if m then
		svc_name = m[3]
		local svc_version1 = m[2] or ""
		local svc_version2 = m[4] or ""
		if(not svc_version1 or svc_version1 == "") then
			svc_version  = svc_version2
		else
			svc_version = svc_version1
		end	
		local svc_key = tbl_concat({key_prefix,"api",svc_name,svc_version},":")
		local svcinfo = dbclient.load_serviceinfo(svc_key)
		if tbl_isempty(svcinfo) then
			return false
		else
			return true
		end
	end
	return false
end

local function _is_match_route_iui(uri,key_prefix)
	local m, err = ngx.re.match(uri, "^/iui/([^/]+)(.*)", "o")
	if m then
		local svc_name = m[1]
		local svc_key = tbl_concat({key_prefix,"iui",svc_name},":")
		local svcinfo = dbclient.load_serviceinfo(svc_key)
		if tbl_isempty(svcinfo) then
			return false
		else
			return true
		end
	end
	return false
end

local function _is_match_route_custom(uri,key_prefix)
	--[[
	local custom_svc_keypattern = tbl_concat({key_prefix,"custom","*"},":")
	local svcnames,err = dbclient.load_customsvcnames(custom_svc_keypattern)
	]]
	local svcnames = ngx.ctx.svcnames
	if not svcnames then
		return false
	end
	for _, svcname in ipairs(svcnames) do
		if (svcname == "/") then
			return true
		end
		local from, to, err = ngx.re.find(uri, "^"..svcname.."(/(.*))?$", "jo")
		--check whether svcname is the prefix of the req uri
		if from then
			return true
		else
			--do nothing
		end
	end
	return false
end

local function _is_patten_conform(host)
	local m1, err = ngx.re.match(host, "^([0-9a-zA-Z]([0-9a-zA-Z-]+[\\.]{1})+[a-zA-Z-]+)$","o")
	if m1 then
		-- domain
		local m2, err = ngx.re.match(host, "(?<hostname>.+)\\."..router_subdomain,"o")
		if m2 then 
			return true
		else
			return false
		end
	else
		--ip
		if host == ngx.var.host then
			return true
		else
			local last_peer = ngx.ctx.last_peer
			if(last_peer and host == last_peer.ip) then
				return true
			else
				return false
			end
		end
	end	
end

-- syntax: patten_conform,route_match = is_match_msb_route(location)
function _M.is_match_msb_route(location)
	local m, err = ngx.re.match(location, "^(\\w+)://([^/:]*)(?::(\\d+))?([^/?]*).*", "o")
	local scheme,host,port,uri
	if m then
		scheme = m[1]
		host = m[2]
		port = m[3]
		uri = m[4]
	else
		return false,false --It is not normal to enter this branch. This match result just let redirect transformer ignore this request(do nothing)
	end

	-- check the host whether conform to msb rules 
	if not _is_patten_conform(host) then
		return false,false
	end

	local key_prefix = _get_key_prefix(scheme,host,port)
	--return true,_is_match_route_api(uri,key_prefix)  or _is_match_route_iui(uri,key_prefix) or _is_match_route_custom(uri,key_prefix)
	return true,_is_match_route_custom(uri,key_prefix)
end

return _M
