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

local _M = {
	_VERSION = '1.0.0',
	STATUS_OK = 0, STATUS_UNSTABLE = 1, STATUS_ERR = 2
}
local msbConf   =  require('conf.msbinit')
local str_format    = string.format
local now           = ngx.now
local fail_timeout = msbConf.server.fail_timeout or 10
local max_fails = msbConf.server.max_fails  or 1

local cluster_status = {}
_M.cluster_status = cluster_status

function _M.is_server_ok(skey, srv)
	return _M.get_srv_status(skey, srv)==_M.STATUS_OK
end

function _M.get_srv_status(skey, srv)
	local server_status = cluster_status[skey]
	if not server_status then
		return _M.STATUS_OK
	end

	local srv_key = str_format("%s:%d", srv.ip, srv.port)
	local srv_status = server_status[srv_key]

	if srv_status and srv_status.lastmodify + fail_timeout > now() then
		return srv_status.status
	end

	return _M.STATUS_OK
end

function _M.set_srv_status(skey, srv, failed)
	local server_status = cluster_status[skey]
	if not server_status then
		server_status = {}
		cluster_status[skey] = server_status
	end

	local time_now = now()
	local srv_key = str_format("%s:%d", srv.ip, srv.port)
	local srv_status = server_status[srv_key]
	if not srv_status then  -- first set
		srv_status = {
			status = _M.STATUS_OK,
			failed_count = 0,
			lastmodify = time_now
		}
		server_status[srv_key] = srv_status
	elseif srv_status.lastmodify + fail_timeout < time_now then -- srv_status expired
		srv_status.status = _M.STATUS_OK
		srv_status.failed_count = 0
		srv_status.lastmodify = time_now
	end

	if failed then
		srv_status.failed_count = srv_status.failed_count + 1
		if srv_status.failed_count >= max_fails then
			srv_status.status = _M.STATUS_ERR
		end
	end
end

function _M.check_and_reset_srv_status_ifneed(skey,servers)
	local server_status = cluster_status[skey]
	--if disabled servers of the service is empty,do nothing
	if not server_status then 
		ngx.log(ngx.DEBUG, "service:",skey,"  server_status is nil")
		return 
	end
	local need_reset = true
	for _, srv in ipairs(servers) do
		local srv_key = str_format("%s:%d", srv.ip, srv.port)
	    local srv_status = server_status[srv_key]
		if not (srv_status and srv_status.status == _M.STATUS_ERR and srv_status.lastmodify + fail_timeout > now()) then
			--once find the server is not disabled now, no need to reset the status table. break the loop
			ngx.log(ngx.DEBUG, "service:",skey," donot need reset,break the loop")
		    need_reset = false
			break
	    end
    end
	if need_reset then
		ngx.log(ngx.DEBUG, "service:",skey," need reset")
		cluster_status[skey] = {}
	end
end

return _M