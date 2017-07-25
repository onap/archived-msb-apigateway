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
	_VERSION = '1.0.0'
}
local policymodule = require "loadbalance.policy.roundrobin"
local tbl_util  = require('lib.utils.table_util')
local peerwatcher = require "loadbalance.peerwatcher"
local tbl_isempty = tbl_util.isempty

function _M.get_backserver(svc_key,servers)
	if tbl_isempty(servers) then return nil,"server list is empty" end

	local servers_num = #servers
	if not ngx.ctx.tried_num then
		ngx.ctx.tried_num = 0
	end
	local server
	if servers_num==1 then
		ngx.ctx.tried_num = ngx.ctx.tried_num+1
		-- return it directly if there is only one server
		server = servers[1]
		if peerwatcher.is_server_ok(svc_key,server) then
			return server,"" 
		else 
			return nil,"only one server but is not available"
		end
	end
	for i=ngx.ctx.tried_num+1,servers_num do
		ngx.ctx.tried_num = ngx.ctx.tried_num+1
		server = policymodule.select_backserver(servers,svc_key)
		if peerwatcher.is_server_ok(svc_key,server) then
			return server,""
		end
	end
	return nil,"serveral server but no one is available"
end

function _M.can_retry(svc_key,servers)
	return ngx.ctx.tried_num < #servers
end

function _M.mark_srv_failed(svc_key, srv)
	peerwatcher.set_srv_status(svc_key, srv, true)
end

function _M.check_and_reset_srv_status_ifneed(svc_key, servers)
	peerwatcher.check_and_reset_srv_status_ifneed(svc_key,servers)
end
return _M