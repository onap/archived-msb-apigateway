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

local BasePlugin = require "plugins.base_plugin"

local SamplePluginHandler = BasePlugin:extend()

function SamplePluginHandler:new()
	SamplePluginHandler.super.new(self, "sampleplugin")
end

function SamplePluginHandler:access()
	SamplePluginHandler.super.access(self)
	--[[more about the use of APIs please refer to github doc 
	    https://github.com/openresty/lua-nginx-module
	]]
	--validate and rewrite
	if(ngx.req.get_method() == "GET") then
		ngx.req.set_uri("/sayhello")
		ngx.var.backend = "127.0.0.1:10089"
	else
		ngx.status = ngx.HTTP_NOT_ALLOWED
		ngx.exit(ngx.status)
	end
	--access
	--[[
	local client_ip = ngx.var.remote_addr
	if(client_ip ~= "127.0.0.1") then
		ngx.status = ngx.HTTP_FORBIDDEN
		ngx.exit(ngx.status)
	end
	]]
end

return SamplePluginHandler