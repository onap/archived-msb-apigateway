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

-- unified layer to access back DB, using two levels of cache mechanism(LRUCache and shcache)
local _M = {}
_M._VERSION = '1.0.0'

local cache = require('lib.tools.db_cache')
local dao = require('dao.dao')
local msbConf = require('conf.msbinit')
local cjson_safe = require "cjson.safe"
local cacheConf = msbConf.cacheConf
local lru_ttl = cacheConf.lru_ttl or 10

function _M.load_serviceinfo(key)
	-- Try to get from cache
	local cached_value = cache.get(key)
	if cached_value then
		if cache.is_empty(cached_value) then
			return nil
		end
		return cached_value
	end
	-- Get from shcache or backend redis
	local svcinfo_str = dao.load_serviceinfo(key)
	local value,err
	if svcinfo_str then
		value, err = cjson_safe.decode(svcinfo_str)
		if err then
			value = nil
			ngx.log(ngx.WARN, "decode service info error: ", err, " service info: ", svcinfo_str)
		end
	else
		value = nil
	end
	cache.set(key, value, lru_ttl)
	return value
end

function _M.load_backservers(keypattern)
	local value, err
	-- Try to get from cache
	value = cache.get(keypattern)
	if not value then
		-- Get from shcache or backend redis
		value = dao.load_backservers(keypattern)
		cache.set(keypattern, value, lru_ttl)
	end
	if cache.is_empty(value) then
		return nil
	end
	return value
end

function _M.load_customsvcnames(keypattern)
	local value, err
	-- Try to get from cache
	value = cache.get(keypattern)
	if not value then
		-- Get from shcache or backend redis
		value = dao.load_customsvcnames(keypattern)
		cache.set(keypattern, value, lru_ttl)
	end
	if cache.is_empty(value) then
		return nil
	end
	return value
end

return _M