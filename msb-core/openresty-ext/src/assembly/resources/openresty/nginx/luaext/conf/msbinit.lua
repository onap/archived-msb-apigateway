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

local modulename = "msbinit"
local _M = {}
_M._VERSION = '1.0.0'

_M.systemConf = {
	["defaultport"]      = "10080",
	["defaultprefix"]    = "msb:routing",
	["enablefullsearch"] = true, --whether test against the custom services after common match processing
	["enablerefercheck"] = true, --whether use refer to test against the service names as the last solution
	["enableauthcheck"] = true, --whether add custom auth header or not
	["useconsultemplate"] = false --whether using consul template or not
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
	["positive_ttl"]  = 60, --shcache use,in seconds
	["negative_ttl"]  = 2,  --shcache use,in seconds
	["lru_ttl"]    = 10 --in seconds
}
return _M