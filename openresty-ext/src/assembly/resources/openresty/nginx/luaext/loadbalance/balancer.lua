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

local b = require "ngx.balancer"
local baseupstream = require "loadbalance.baseupstream"
local stats = require "monitor.stats"
local svc_util  =  require 'lib.utils.svc_util'

local servers = ngx.ctx.backservers
local svc_key = ngx.ctx.svc_key
local svc_info = ngx.ctx.svc_info
local svc_get_connect_timeout = svc_util.get_connect_timeout
local svc_get_send_timeout = svc_util.get_send_timeout
local svc_get_read_timeout = svc_util.get_read_timeout


local status = b.get_last_failure()
if status == nil then
	--only reset the server status table of the service in the first attempt
	baseupstream.check_and_reset_srv_status_ifneed(svc_key,servers)
elseif status == "failed" then
	local last_peer = ngx.ctx.last_peer
	--mark the srv failed one time
	baseupstream.mark_srv_failed(svc_key,last_peer)
	stats.backend_failed()
end

local server,err = baseupstream.get_backserver(svc_key,servers)
if server == nil then 
	ngx.log(ngx.WARN, ngx.var.request_id.." ".."No active backend server found! detail_info: key--"..svc_key.." "..(err or ""))
	return
end
if baseupstream.can_retry(svc_key,servers) then
	b.set_more_tries(1)
end
b.set_current_peer(server["ip"],server["port"])
b.set_timeouts(svc_get_connect_timeout(svc_info), svc_get_send_timeout(svc_info), svc_get_read_timeout(svc_info))
ngx.ctx.last_peer = { ip=server["ip"], port=server["port"] }
stats.forward_backend()
