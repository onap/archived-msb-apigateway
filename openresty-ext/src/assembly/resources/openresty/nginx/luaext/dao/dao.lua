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

--This module integrates the shcache with an abstraction for various databases(redis and so on)
local _M = {}
_M._VERSION = '1.0.0'

local shcache = require("vendor.shcache")
local cjson   = require "cjson"
local msbConf = require('conf.msbinit')

local DB      = require('dao.redis_db')
local options = msbConf.redisConf

local cacheConf = msbConf.cacheConf
local positive_ttl = cacheConf.positive_ttl or 60
local negative_ttl = cacheConf.negative_ttl or 2

local svc_shcache = ngx.shared.svc_cache

local function load_serviceinfo(key)

	-- closure to perform external lookup to redis
	local fetch_svcinfo_from_db = function ()
		local _db = DB:new(options)
		return _db:getserviceinfo(key)
	end

	local svcinfo_cache_table = shcache:new(
		svc_shcache,
		{ external_lookup = fetch_svcinfo_from_db,
			--encode = cmsgpack.pack,
			encode = cjson.encode,
			--decode = cmsgpack.unpack
			decode = cjson.decode
		},
		{   positive_ttl = positive_ttl,           -- default cache good data for 60s 
			negative_ttl = negative_ttl,           -- default cache failed lookup for 5s
			name = 'svcinfo_cache'       -- "named" cache, useful for debug / report
		}
	)

	local serviceinfo, from_cache = svcinfo_cache_table:load(key)

	return serviceinfo
end
_M.load_serviceinfo = load_serviceinfo

local function load_backservers(keypattern)

	-- closure to perform external lookup to redis
	local fetch_servers_from_db = function ()
		local _db = DB:new(options)
		return _db:getbackservers(keypattern)
	end

	local servers_cache_table = shcache:new(
		svc_shcache,
		{ external_lookup = fetch_servers_from_db,
			--encode = cmsgpack.pack,
			encode = cjson.encode,
			--decode = cmsgpack.unpack
			decode = cjson.decode
		},
		{   positive_ttl = positive_ttl,           -- default cache good data for 60s 
			negative_ttl = negative_ttl,           -- default cache failed lookup for 5s
			name = 'servers_cache'           -- "named" cache, useful for debug / report
		}
	)

	local servers_table, from_cache = servers_cache_table:load(keypattern)

	return servers_table
end
_M.load_backservers = load_backservers


local function load_customsvcnames(keypattern)
	-- closure to perform external lookup to redis
	local fetch_svcnames_from_db = function ()
		local _db = DB:new(options)
		return _db:getcustomsvcnames(keypattern)
	end

	local svcnames_cache_table = shcache:new(
		svc_shcache,
		{ external_lookup = fetch_svcnames_from_db,
			--encode = cmsgpack.pack,
			encode = cjson.encode,
			--decode = cmsgpack.unpack
			decode = cjson.decode
		},
		{   positive_ttl = positive_ttl,           -- default cache good data for 60s 
			negative_ttl = negative_ttl,           -- default cache failed lookup for 5s
			name = 'svcnames_cache'      -- "named" cache, useful for debug / report
		}
	)

	local service_names, from_cache = svcnames_cache_table:load(keypattern)

	return service_names
end
_M.load_customsvcnames = load_customsvcnames

return _M