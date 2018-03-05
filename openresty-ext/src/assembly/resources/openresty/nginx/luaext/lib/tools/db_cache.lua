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

-- db cache over LRUCache， used within one worker processes
local _M = {}
_M._VERSION = '1.0.0'

local lrucache = require "resty.lrucache"

local EMPTY_DATA = '_EMPTY_'

-- we need to initialize the cache on the lua module level so that
-- it can be shared by all the requests served by each nginx worker process:
local cache,err = lrucache.new(200)  -- allow up to 200 items in the cache
if not cache then
	return ngx.log(ngx.ERR,"failed to create the cache: " .. (err or "unknown"))
end

function _M.get(key)
	return cache:get(key)
end

function _M.set(key,value)
	return _M:set(key, value, nil)
end

function _M.set(key,value,ttl)
	if not value then 
		value = EMPTY_DATA
	end
	return cache:set(key, value, ttl)
end

-- check if the data returned by get() is considered empty
function _M.is_empty(data)
	return data == EMPTY_DATA
end

return _M