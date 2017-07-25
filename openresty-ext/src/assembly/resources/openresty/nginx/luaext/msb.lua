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
}
_M._VERSION = '1.0.0'
_M._DESCRIPTION = 'msb plugins controller'

local default_conf   =  require('plugins.config_default')
local custom_conf   =  require('plugins.config_custom')
local table_insert = table.insert
local string_find = string.find
local str_low = string.lower

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
		ngx.log(ngx.DEBUG, "Loading plugin: "..v)
		table_insert(plugins, {
				name = v,
				handler = plugin_handler_mod()
			})
	end
	package.loaded.plugins = plugins
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