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

-- the client for redis, include the connection pool management and api implements
local redis = require('resty.redis')
local tbl_util  = require('lib.utils.table_util')

local _M = {
}
_M._VERSION = '0.0.1'
_M._DESCRIPTION = 'msb_redis_module'

local mt = { __index = _M }

local tbl_insert = table.insert
local tbl_sort = table.sort
local tbl_isempty = tbl_util.isempty

function _M.new(self, conf)
	self.host        = conf.host
	self.port        = conf.port
	self.timeout     = conf.timeout
	self.dbid        = conf.dbid
	self.poolsize    = conf.poolsize
	self.idletimeout = conf.idletimeout

	local red = redis:new()
	return setmetatable({redis = red}, mt)
end

function _M.connectdb(self)
	local host  = self.host
	local port  = self.port
	local dbid  = self.dbid
	local red   = self.redis

	if not (host and port) then
		return nil, 'no host:port avaliable provided'
	end

	--set default value
	if not dbid then dbid = 0 end
	local timeout   = self.timeout 
	if not timeout then 
		timeout = 1000   -- 1s
	end

	red:set_timeout(timeout)

	local ok, err 
	if host and port then
		ok, err = red:connect(host, port)
		if ok then return red:select(dbid) end
	end

	return ok, err
end

function _M.keepalivedb(self)
	local   max_idle_timeout  = self.idletimeout --ms
	local   pool_size         = self.poolsize

	if not pool_size then pool_size = 100 end
	if not max_idle_timeout then max_idle_timeout = 90000 end --90s

	local ok, err = self.redis:set_keepalive(max_idle_timeout, pool_size)
	if not ok then
		ngx.log(ngx.ERR, "redis pool keepalive error",err)
		return
	end
	return
end

--inner function,only used in this module
local function _hgetall(red,key)
	local resp,err = red:hgetall(key)
	--if not resp or next(resp) == nil then
	if tbl_isempty(resp) then
		return nil, "key "..key.." not found"
	end
	local hashinfo = red:array_to_hash(resp)
	return hashinfo,nil
end

function _M.getserviceinfo(self,key)
	if not key then
		return nil,'no key is provided'
	end
	local c, err = self:connectdb()
	if not c then
		return nil, err
	end

	local red   = self.redis

	local resp,err = red:hgetall(key) --the key will create dynamically
	--if not resp or next(resp) == nil then
	if tbl_isempty(resp) then
		self:keepalivedb()
		return nil, "key "..key.." not found"
	end

	local serviceinfo = red:array_to_hash(resp)

	self:keepalivedb()

	return serviceinfo,nil
end

function _M.getbackservers(self,keypattern)
	if not keypattern then
		return nil,'no keypattern is provided'
	end
	local c, err = self:connectdb()
	if not c then
		return nil, err
	end

	local red = self.redis

	local resp, err = red:keys(keypattern)
	if tbl_isempty(resp) then
		self:keepalivedb()
		return nil, "no server matched"
	end

	local servers = {}
	for i, v in ipairs(resp) do
		local serverinfo,err = _hgetall(red,v)
		if serverinfo then
			tbl_insert(servers,serverinfo)
		end
	end
	self:keepalivedb()
	return servers,nil
end

function _M.getcustomsvcnames(self,keypattern)
	if not keypattern then
		return nil,'no keypattern is provided'
	end
	local c, err = self:connectdb()
	if not c then
		return nil, err
	end

	local red = self.redis

	local resp, err = red:keys(keypattern)
	if tbl_isempty(resp) then
		self:keepalivedb()
		return {}, "no custome service name found"
	end

	local svcnames = {}
	--store svc names into the Set
	local key_set={}
	local name
	for key, value in ipairs(resp) do
		local m, err = ngx.re.match(value, "^.+:custom:([^:]+):.*", "o")
		if m then
			name = m[1]
			key_set[name]=true		
		end
	end

	for key,_ in pairs(key_set) do
		tbl_insert(svcnames,key)
	end
	--sort the key_table in reverse order
	tbl_sort(svcnames, function (a, b)
			return a > b
		end)

	self:keepalivedb()
	return svcnames,nil
end
return _M