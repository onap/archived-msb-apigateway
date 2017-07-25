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
local msbConf   =  require('conf.msbinit')
local log_util  =  require('lib.utils.log_util')
local log = log_util.log

local RedirectTransformerPluginHandler = BasePlugin:extend()

function RedirectTransformerPluginHandler:new()
	RedirectTransformerPluginHandler.super.new(self, "redirect-transformer-plugin")
end

function RedirectTransformerPluginHandler:header_filter()
	RedirectTransformerPluginHandler.super.header_filter(self)
	local originloc = ngx.header.Location
	if(originloc) then
		local newloc = ngx.re.sub(originloc, "^(https|http)(.*)", ngx.var.scheme.."$2", "oi")
		ngx.header["Location"] = newloc
		log("origin Location:",originloc)
		log("req scheme:",ngx.var.scheme)
		log("new Location:",newloc)
	end
end

return RedirectTransformerPluginHandler