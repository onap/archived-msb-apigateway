--[[

    Copyright (C) 201-2018 ZTE, Inc. and others. All rights reserved. (ZTE)

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
local url_matcher = require "plugins.redirect-transformer.url_matcher"
local log = log_util.log
local url_match_msb_route = url_matcher.is_match_msb_route
local RedirectTransformerPluginHandler = BasePlugin:extend()

function RedirectTransformerPluginHandler:new()
	RedirectTransformerPluginHandler.super.new(self, "redirect-transformer-plugin")
end

function RedirectTransformerPluginHandler:header_filter()
	RedirectTransformerPluginHandler.super.header_filter(self)
	local originloc = ngx.header.Location
	local newloc
	if(originloc) then
		log("origin location:",originloc)
		local patten_conform,route_match = url_match_msb_route(originloc)
		if not patten_conform then
			log("redirect-transformer output:","The redirect address may be outside msb, do nothing temporarily.")
			return
		end

		if route_match then
			--if the redirect address can be forwarded by msb,then donot modify it's url
			newloc = ngx.re.sub(originloc, "^(https|http)(.*)", ngx.var.scheme.."$2", "oi")
		else
			--if the redirect address can not be forwarded by msb,then try to modify it's url
			local svc_pub_url = ngx.ctx.svc_pub_url
			local svc_url = ngx.ctx.svc_url
			if(svc_pub_url and svc_pub_url == "/") then
				--replace $svc_url with ""
				newloc = ngx.re.sub(originloc, "^(https|http)://([^/]+)"..svc_url, ngx.var.scheme.."://".."$2", "oi")
			else
				--replace $svc_url with $svc_pub_url
				newloc = ngx.re.sub(originloc, "^(https|http)://([^/]+)"..svc_url, ngx.var.scheme.."://".."$2"..svc_pub_url, "oi")
			end
		end
		-- replace the backend server with the host of msb
		local last_peer = ngx.ctx.last_peer
		if last_peer then
			local backend_ip = ngx.re.gsub(last_peer.ip, "\\.", "\\.", "o")
			newloc = ngx.re.sub(newloc, "^(https://|http://)"..backend_ip..":"..last_peer.port, "$1"..ngx.var.host..":"..ngx.var.server_port, "o")
		end	
		ngx.header["Location"] = newloc
		log("redirect-transformer output:","replace the redirect address to :"..newloc)
		ngx.log(ngx.WARN, "redirect-transformer replace the redirect address to:"..newloc, " origin location:",originloc)
	end
end

return RedirectTransformerPluginHandler
