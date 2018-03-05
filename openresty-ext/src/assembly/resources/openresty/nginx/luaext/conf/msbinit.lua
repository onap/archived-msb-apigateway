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

local modulename = "msbinit"
local _M = {}
_M._VERSION = '1.0.0'

local str_util  =  require('lib.utils.str_util')
local mark_empty_as_nil = str_util.mark_empty_as_nil

_M.systemConf = {
	["defaultport"]       = mark_empty_as_nil(os.getenv("HTTP_OVERWRITE_PORT")) or "80",
	["defaulthttpsport"]  = mark_empty_as_nil(os.getenv("HTTPS_OVERWRITE_PORT")) or "443",
	["defaultprefix"]     = "msb:routing",
	["enablefullsearch"]  = true, --whether test against the custom services after common match processing
	["enablerefercheck"]  = true, --whether use refer to test against the service names as the last solution
	["useconsultemplate"] = true --whether using consul template or not
}
_M.redisConf = {
	["host"]        = "127.0.0.1",
	["port"]        = 6379,
	["poolsize"]    = 100,
	["idletimeout"] = 90000, 
	["timeout"]     = 1000,
	["dbid"]        = 0
}
_M.cacheConf = {
	["positive_ttl"] = 5, --shcache use,in seconds
	["negative_ttl"] = 2,  --shcache use,in seconds
	["actualize_ttl"] = 120, --shcache use,in seconds
	["lru_ttl"]      = 2  --in seconds
}
_M.routerConf = {
	-- cross-domain plugin uses this field in init_by_lua context, ngx.re.gsub is not allowed in this context, using string.gsub instead
	--["subdomain"]    = ngx.re.gsub(os.getenv("ROUTER_SBUDOMAIN") or "openpalette.zte.com.cn", "\\.", "\\.", "o"),
	["subdomain"]    = string.gsub(mark_empty_as_nil(os.getenv("ROUTER_SUBDOMAIN")) or "openpalette.zte.com.cn", "%.", "\\."),
	["defaultprefix"]= "msb:host"
}
_M.server = {
	["fail_timeout"]  = 10,
	["max_fails"]     = 1
}
_M.dns = {
	["servers"]  = mark_empty_as_nil(os.getenv("UPSTREAM_DNS_SERVERS")),
	["cache_positive_ttl"] = 180, --shcache use,in seconds
	["cache_negative_ttl"] = 2,  --shcache use,in seconds
	["cache_actualize_ttl"] = 120, --shcache use,in seconds
}
return _M
