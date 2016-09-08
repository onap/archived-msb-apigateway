--[[

    Copyright 2016 2015-2016 ZTE, Inc. and others. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

        Author: Zhaoxing Meng
        email: meng.zhaoxing1@zte.com.cn

]]
-- put red into the connection pool of size 100,
-- with 10 seconds max idle time
local function close_redis(red)
	if not red then
		return
	end
	--release connection to the pool
	local pool_max_idle_time = 10000
	local pool_size = 100
	local ok, err = red:set_keepalive(pool_max_idle_time, pool_size)
	if not ok then
		ngx.log(ngx.ERR, "set keepalive error:", err)
	end
	return
end

local function rewrite_apijson_url()
	local apiserver=ngx.var.apiserver
	if apiserver=="fallback" then
		ngx.status = ngx.HTTP_NOT_FOUND
		ngx.exit(ngx.status)
	end
	local apijsonurl=ngx.var.apijsonurl
	local uri = ngx.re.sub(ngx.var.uri, "^/apijson/([^/]+)(/[Vv][^/]*)?(.*)", apijsonurl.."$3", "o")
	ngx.req.set_uri(uri)
end

local function query_apijson_info()
	local apiserver = ngx.var.apiserver
	local apiname = ngx.var.apiname
	local apiversion = ngx.var.apiversion
	apiversion=string.sub(apiversion,2,string.len(apiversion))

	-- Check if key exists in local cache
	local cache = ngx.shared.ceryx
	local server, flags = cache:get("server:apijson:"..apiname..":"..apiversion)
	local url, flags = cache:get("url:apijson:"..apiname..":"..apiversion)
	if server and url then
		ngx.var.apiserver = server
		ngx.var.apijsonurl = url
		ngx.log(ngx.WARN, "==using apijson cache:", server.."&&"..url)
		return
	end

	local redis = require "resty.redis"
	local red = redis:new()
	red:set_timeout(1000) -- 1000 ms
	local redis_host = "127.0.0.1" 
	local redis_port = 6379 
	local res, err = red:connect(redis_host, redis_port)

	-- Return if could not connect to Redis
	if not res then
		ngx.log(ngx.ERR, "connect to redis error:", err)
		return
	end

	-- Construct Redis key
	local prefix = "msb"
	local keyprefix = prefix..":routing:api:"..apiname..":"..apiversion


	-- Try to get ip:port for apiname
	local serverkey=keyprefix..":lb:server1"
	local server, err = red:hget(serverkey,"ip")..":"..red:hget(serverkey,"port")
	ngx.log(ngx.WARN, "==apijsonserver:", server)
	if not server or server == ngx.null then
		return close_redis(red) 
	end

	-- Try to get apijson url for apiname
	local infokey=keyprefix..":info"
	local url, err = red:hget(infokey,"apijson")
	ngx.log(ngx.WARN, "==apijsonurl:", url)
	if not url or url == ngx.null then
		return close_redis(red) 
	end

	-- Save found key to local cache for 5 seconds
	cache:set("server:apijson:"..apiname..":"..apiversion, server, 5)
	cache:set("url:apijson:"..apiname..":"..apiversion, url, 5)

	ngx.log(ngx.WARN, "==apijson result:", server.."&&"..url)
	ngx.var.apiserver = server
	ngx.var.apijsonurl = url

	return close_redis(red)
end
query_apijson_info()
rewrite_apijson_url()