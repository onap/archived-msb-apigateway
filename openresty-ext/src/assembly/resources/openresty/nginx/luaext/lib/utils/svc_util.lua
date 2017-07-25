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

local _M = {}
_M._VERSION = '1.0.0'

local msbConf= require('conf.msbinit')
local svcConf   =  require('conf.svcconf')
local log_util  =  require('lib.utils.log_util')

local log = log_util.log
local ngx_var = ngx.var

local defaultport = msbConf.systemConf.defaultport
local defaulthttpsport = msbConf.systemConf.defaulthttpsport
local defaultprefix = msbConf.systemConf.defaultprefix
local router_subdomain = msbConf.routerConf.subdomain
local router_defaultprefix = msbConf.routerConf.defaultprefix
local useconsultemplate = msbConf.systemConf.useconsultemplate
local urlfieldMap = svcConf.urlfieldMap
local apiRelatedTypes = svcConf.apiRelatedTypes

function _M.isactive(svcinfo)
	if svcinfo["status"] == "1" then
		return true
	else
		return false
	end
end

function _M.use_own_upstream(svcinfo)
	if useconsultemplate and svcinfo.spec.useOwnUpstream == "1" then
		log("useOwnUpstream",true)
		return true
	else
		return false
	end
end

function _M.get_url(svcinfo,svc_type)
	return svcinfo.spec[urlfieldMap[svc_type]]
end

function _M.get_backend_protocol(svcinfo)
	local svc_enable_ssl = svcinfo.spec["enable_ssl"]
	if svc_enable_ssl then
		return "https"
	else
		return "http"
	end
end

function _M.get_key_prefix()
	--now assemble the key prefix according the svc_name and server_port
	local key_prefix = ""
	local server_port = ngx_var.server_port
	local svc_name = ngx_var.svc_name
	if (svc_name == "microservices" or svc_name == "msdiscover") then
		key_prefix = defaultprefix
	elseif (server_port == defaultport or server_port == defaulthttpsport) then
		local m, err = ngx.re.match(ngx_var.host, "(?<hostname>.+)\\."..router_subdomain,"o")
		if m then
			key_prefix = router_defaultprefix..":"..m["hostname"]
		else
			key_prefix = defaultprefix
		end
	else
		key_prefix = "msb:"..server_port
	end
	return key_prefix
end

function _M.is_api_related_types(svc_type)
	if(apiRelatedTypes[svc_type]) then
		return true
	else
		return false
	end
end

return _M