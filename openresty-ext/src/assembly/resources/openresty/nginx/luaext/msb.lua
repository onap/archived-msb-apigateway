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
local _M = {
}
_M._VERSION = '1.0.0'
_M._DESCRIPTION = 'msb plugins controller'

local default_conf   =  require('plugins.config_default')
local custom_conf   =  require('plugins.config_custom')
local msb_router=  require('core.router')
local table_insert = table.insert
local string_find = string.find
local str_low = string.lower
local ngx_var = ngx.var

--- Borrowed from Kong
--- Try to load a module.
-- Will not throw an error if the module was not found, but will throw an error if the
-- loading failed for another reason (eg: syntax error).
-- @param module_name Path of the module to load (ex: kong.plugins.keyauth.api).
-- @return success A boolean indicating wether the module was found.
-- @return module The retrieved module.
local load_module_if_exists = function(module_name)
	local status, res = pcall(require, module_name)
	if status then
		return true, res
		-- Here we match any character because if a module has a dash '-' in its name, we would need to escape it.
	elseif type(res) == "string" and string_find(res, "module '"..module_name.."' not found", nil, true) then
		return false
	else
		error(res)
	end
end


function _M.load_plugins()
	local pluginnames = {}
	for _, plugin in ipairs(default_conf.plugins_default) do
		if(str_low(plugin.status) =="on") then
			table_insert(pluginnames,plugin.name)
		end
	end
	for _, plugin in ipairs(custom_conf.plugins_custom) do
		if(str_low(plugin.status) =="on") then
			table_insert(pluginnames,plugin.name)
		end
	end
	local plugins = {}
	for _, v in ipairs(pluginnames) do
		local loaded, plugin_handler_mod = load_module_if_exists("plugins."..v..".handler")
		if not loaded then
			error("The following plugin has been enabled in the configuration but it is not installed on the system: "..v)
		end
		ngx.log(ngx.WARN, "Loading plugin: "..v)
		table_insert(plugins, {
				name = v,
				handler = plugin_handler_mod()
			})
	end
	package.loaded.plugins = plugins
end

function _M.filter_websocket_req()
	local http_upgrade = ngx_var.http_upgrade
	if http_upgrade and str_low(http_upgrade) == "websocket" then
		--ngx.log(ngx.ERR, "Websocket request and redirect to @commonwebsocket")
		return ngx.exec("@websocket");
	end
end

function _M.route()
	msb_router.execute(ngx_var.server_port,"ROUTER")
end

local function prepare_route()
	local uri = ngx_var.uri
	local m, err = ngx.re.match(uri, "^/(api|admin|apijson)(/[Vv]\\d+(?:\\.\\d+)*)?/([^/]+)(/[Vv]\\d+(?:\\.\\d+)*)?(.*)", "o")
	if m then
		ngx_var.svc_type = m[1]
		ngx_var.svc_name = m[3]
		ngx_var.svc_version1 = m[2] or ""
		ngx_var.svc_version2 = m[4] or ""
		ngx_var.req_res = m[5]
		return
	end
	local m, err = ngx.re.match(uri, "^/iui/([^/]+)(.*)", "o")
	if m then
		ngx_var.svc_type = "iui"
		ngx_var.svc_name = m[1]
		ngx_var.req_res = m[2]
		return
	end
	ngx_var.svc_type = "custom"
	return
end

function _M.external_route(server_port,system_tag)
	if not server_port or not system_tag then
		local err = "server_port and system_tag are required while routing!"
		ngx.log(ngx.WARN, ngx.var.request_id.." "..err)
		ngx.status = ngx.HTTP_BAD_GATEWAY
		ngx.print(err)
		return ngx.exit(ngx.status)
	end
	prepare_route()
	msb_router.execute(server_port,system_tag)
end

function _M.access()
	local plugins = package.loaded.plugins
	for _, plugin in ipairs(plugins) do
		plugin.handler:access()
	end
end

function _M.header_filter()
	local plugins = package.loaded.plugins
	for _, plugin in ipairs(plugins) do
		plugin.handler:header_filter()
	end
end

return _M
