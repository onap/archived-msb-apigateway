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
local msbConf   =  require('conf.msbinit')
local enablefullsearch = msbConf.systemConf.enablefullsearch
local ngx_var = ngx.var
local error_page_head = '<html><head><title>502 Bad Gateway</title></head><body bgcolor="white"><center><h1>502 Bad Gateway</h1></center><center>error message:'
local error_page_foot = '</center><hr><center>nginx</center></body></html>'
local upstream_not_found_err = "service info is incorrect:using own upstream flag is on but upstream name is empty"

function _M.svc_not_found(err_info,detail_info)
	ngx.log(ngx.WARN, ngx.var.request_id.." "..(err_info or "").." detail_info:"..(detail_info or ""))
	if enablefullsearch and ngx_var.svc_type ~= "custom" then
		-- test against the custom services after the commonrewrite phase
		--ngx.status = ngx.HTTP_GONE
		return ngx.exec("@commonnotfound");
	else
		ngx.status = ngx.HTTP_BAD_GATEWAY
		ngx.print(error_page_head..err_info..error_page_foot)
	end
	return ngx.exit(ngx.status)
end

function _M.svc_not_allow_access(err_info,detail_info)
	ngx.log(ngx.WARN, ngx.var.request_id.." "..(err_info or "").." detail_info:"..(detail_info or ""))
	ngx.status = ngx.HTTP_FORBIDDEN
	return ngx.exit(ngx.status)
end

function _M.upstream_not_found()
	ngx.log(ngx.WARN, ngx.var.request_id.." "..upstream_not_found_err)
	ngx.status = ngx.HTTP_BAD_GATEWAY
	ngx.print(error_page_head..upstream_not_found_err..error_page_foot)
	return ngx.exit(ngx.status)
end

function _M.no_server_available(err_info,detail_info)
	ngx.log(ngx.WARN, ngx.var.request_id.." "..(err_info or "").." detail_info:"..(detail_info or ""))
	ngx.status = ngx.HTTP_BAD_GATEWAY
	ngx.print(error_page_head..err_info..error_page_foot)
	return ngx.exit(ngx.status)
end

return _M