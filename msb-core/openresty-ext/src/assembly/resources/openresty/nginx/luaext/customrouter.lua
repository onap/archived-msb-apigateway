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

local function query_ipurl_updatecache(red,key)
	local keyprefix = "msb:routing:custom:"..key


	local infokey=keyprefix..":info"
	-- first of all check whether the status is 1(enabled)
	local status = red:hget(infokey,"status")
	if not (status=="1")  then
		ngx.log(ngx.WARN, key.."is disabled.status=", status)
		return nil
	end

	-- Try to get url for key
	local url, err = red:hget(infokey,"url")
	ngx.log(ngx.WARN, "==url:", url)
	if not url or url == ngx.null then
		return nil
	end

	-- Try to get ip:port for key
	local serverkey=keyprefix..":lb:server1"
	local server, err = red:hget(serverkey,"ip")..":"..red:hget(serverkey,"port")
	ngx.log(ngx.WARN, "==server:", server)
	if not server or server == ngx.null then
		return nil
	end

	-- get the local cache
	local cache = ngx.shared.ceryx
	local uri = ngx.var.uri
	-- Save found key to local cache for 5 seconds
	cache:set("custom:key:"..uri,key,5)   
	cache:set("custom:server:"..uri,server,5)
	cache:set("custom:url:"..uri,url,5)

	ngx.var.key = key
	ngx.var.server = server
	ngx.var.url = url
	return true
end

local function query_allkeys_updatecache(red)
	-- Try to get all keys start with "msb:routing:custom:"
	local allkeys, err = red:keys("msb:routing:custom:*")
	if not allkeys or allkeys == ngx.null then
		ngx.log(ngx.ERR,err)
		return ""
	end
	--把所有键值处理后放到集合中，去除重复
	local key_set={}
	for key, value in ipairs(allkeys) do
		name = string.gsub(string.gsub(string.gsub(value,"msb:routing:custom:",""),":info",""),":lb:server1","")
		key_set[name]=true						   
	end
	--取出所有的�?放到table中准备排�?
	local key_table = {}
	local index = 1
	for key,_ in pairs(key_set) do
		--为了避免效率问题，暂时不用table.insert()
		--table.insert(key_table,key)
		key_table[index] = key
		index = index + 1
	end
	--对所有键进行倒序排序，用于实现最长前缀匹配
	table.sort(key_table, function (a, b)
			return a > b
		end)

	local servicenames = ""
	local delimiter = "<>"
	for i=1,#key_table do
		servicenames=servicenames..key_table[i]..delimiter
	end

	-- get the local cache
	local cache = ngx.shared.ceryx
	-- Save all keys to local cache for 30 seconds(0.5 minutes)
	cache:set("customrouter:allkeys", servicenames, 30)
	return servicenames;
end

local function query_router_info()
	local uri = ngx.var.uri
	ngx.log(ngx.WARN, "==uri:", uri)

	-- Check if key exists in local cache
	local cache = ngx.shared.ceryx
	local key, flags = cache:get("custom:key:"..uri)   
	local server, flags = cache:get("custom:server:"..uri)
	local url, flags = cache:get("custom:url:"..uri)   
	if key and server and url then
		ngx.var.key = key
		ngx.var.server = server
		ngx.var.url = url
		ngx.log(ngx.WARN, "==using custom cache:", key.."&&"..server.."&&"..url)
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

	-- Check if all servicenames exists in local cache
	local servicenames, flags = cache:get("customrouter:allkeys")
	if servicenames then 
		ngx.log(ngx.WARN,"==get all keys from cache:",servicenames)
	else
		servicenames = query_allkeys_updatecache(red)
	end

	local delimiter = "<>"
	-- '.-' 表示最短匹�?
	for key in string.gmatch(servicenames,"(.-)"..delimiter) do
		ngx.log(ngx.WARN, "==key_table key:", key)
		local from, to, err = ngx.re.find(uri, "^"..key.."(/(.*))?$", "jo")
		--判断key是否为输入uri�?前缀"
		if from then
			ngx.log(ngx.WARN,"Matched! start-end:",from,"-",to)
			local result = query_ipurl_updatecache(red,key)
			if result then
				break
			end
		else
			ngx.log(ngx.WARN,"not Matched")
			if err then
				ngx.log(ngx.WARN,"ngx.re.find throw error: ",err)
				return
			end
		end
	end

	return close_redis(red)
end

local function rewrite_router_url()
	local server=ngx.var.server
	if server=="fallback" then
		ngx.status = ngx.HTTP_NOT_FOUND
		ngx.exit(ngx.status)
	end
	local url=ngx.var.url
	local key=ngx.var.key
	local rewriteduri = ngx.re.sub(ngx.var.uri, "^"..key.."(.*)", url.."$1", "o")
	ngx.log(ngx.WARN, "==rewrited uri:", rewriteduri)
	ngx.req.set_uri(rewriteduri)
end

query_router_info()
rewrite_router_url()