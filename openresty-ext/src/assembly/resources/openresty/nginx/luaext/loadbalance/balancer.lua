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

local b = require "ngx.balancer"
local policymodule = require "loadbalance.policy.roundrobin"
local log_util  =  require('lib.utils.log_util')

local ngx_ctx = ngx.ctx
local log = log_util.log

local doservernil = function() 
	ngx.status = ngx.HTTP_NOT_FOUND
	ngx.say("no on-line server found!")
	return ngx.exit(ngx.status)
end

local servers = ngx_ctx.backservers
local svckeypattern = ngx_ctx.svcserverpattern
local server,err = policymodule.get_backserver(servers,svckeypattern)
if not server then 
	doservernil()
end
--b.set_current_peer(server["ip"]..":"..server["port"])
b.set_current_peer(server["ip"],server["port"])
log("upstreamserver",server["ip"]..":"..server["port"])