--[[

    Copyright (C) 2017-2018 ZTE, Inc. and others. All rights reserved. (ZTE)

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
local bit = require("bit")

local log = log_util.log
local useconsultemplate = msbConf.systemConf.useconsultemplate
local urlfieldMap = svcConf.urlfieldMap
local apiRelatedTypes = svcConf.apiRelatedTypes

local SYS_SCENARIO_FLAG = {  -- cos    router
	["ROUTER"]  = 1,         -- 0      1
	["COS"]     = 2          -- 1      0
}

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

function _M.is_api_related_types(svc_type)
	if(apiRelatedTypes[svc_type]) then
		return true
	else
		return false
	end
end

function _M.get_connect_timeout(svcinfo)
	local connect_timeout = svcinfo.spec["connect_timeout"]
	if connect_timeout then
		connect_timeout = tonumber(connect_timeout)
		if connect_timeout and connect_timeout<=0 then 
			ngx.log(ngx.WARN, ngx.var.request_id.." ".."bad connect timeout!Zero and negative timeout values are not allowed.Input value:"..connect_timeout)
			return nil
		else
			return connect_timeout
		end
	else
		return nil
	end
end

function _M.get_send_timeout(svcinfo)
	local send_timeout = svcinfo.spec["send_timeout"]
	if send_timeout then
		send_timeout = tonumber(send_timeout)
		if send_timeout and send_timeout<=0 then 
			ngx.log(ngx.WARN, ngx.var.request_id.." ".."bad send timeout!Zero and negative timeout values are not allowed.Input value:"..send_timeout)
			return nil
		else
			return send_timeout
		end
	else
		return nil
	end
end

function _M.get_read_timeout(svcinfo)
	local read_timeout = svcinfo.spec["read_timeout"]
	if read_timeout then
		read_timeout = tonumber(read_timeout)
		if read_timeout and read_timeout <= 0 then 
			ngx.log(ngx.WARN, ngx.var.request_id.." ".."bad send timeout!Zero and negative timeout values are not allowed.Input value:"..read_timeout)
			return nil
		else
			return read_timeout
		end
	else
		return nil
	end
end

function _M.enable_refer_match(svcinfo)
	local enable_refer_match = svcinfo.spec["enable_refer_match"]
	--Be compatible with the old service info. If the field is not filled, the refer match is enabled by default.
	if enable_refer_match == nil or enable_refer_match then
		return true
	else
		return false
	end
end

function _M.is_allow_access(system_tag,svcinfo)
	local scenario = svcinfo.spec["scenario"] or 1
	local ok,res = pcall(function() return bit.band(SYS_SCENARIO_FLAG[system_tag], scenario) end)
	if ok and res==SYS_SCENARIO_FLAG[system_tag] then 
		return true
	else
		return false
	end
end
return _M
