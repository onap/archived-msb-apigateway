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

local tbl_util  = require('lib.utils.table_util')
local msbConf= require('conf.msbinit')
local log_util  =  require('lib.utils.log_util')
local tbl_isempty = tbl_util.isempty
local enableauthcheck = msbConf.systemConf.enableauthcheck
local log = log_util.log

function _M.isactive(svcinfo)
	if tbl_isempty(svcinfo) then
		return false
	end
	if svcinfo["status"] == "1" then
		return true
	else
		return false
	end
end

function _M.isautodiscover(svcinfo)
	if tbl_isempty(svcinfo) then
		return false
	end
	if svcinfo["autoDiscover"] == "1" then
		return true
	else
		return false
	end
end

function _M.setauthheader(svcinfo)
	--if auth check enabled and this service is inter-system then add sth
	if enableauthcheck and svcinfo["visualRange"] == "0" then
		ngx.req.set_header("Z-EXTENT", "C012089CF43DE687B23B2C0176B344EE")
		log("add Z-EXTENT",true)
	end
end

return _M