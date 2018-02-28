--[[

    Copyright (C) 2018 ZTE, Inc. and others. All rights reserved. (ZTE)

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

local _M = {}
_M._VERSION = '1.0.0'

local shcache = require("vendor.shcache")
local msbConf   =  require('conf.msbinit')
local resolver = require "resty.dns.resolver"
local str_util  =  require('lib.utils.str_util')

local dns_cache = ngx.shared.dns_cache
local dns_servers = msbConf.dns.servers
local dns_cache_positive_ttl = msbConf.dns.cache_positive_ttl or 60
local dns_cache_negative_ttl = msbConf.dns.cache_negative_ttl or 2
local dns_cache_actualize_ttl = msbConf.dns.cache_actualize_ttl or 120
local str_split = str_util.split

local nameservers = nil
ngx.log(ngx.WARN, "environment variable UPSTREAM_DNS_SERVERS:",dns_servers)
local ok,res = pcall(function() return str_split(dns_servers,",") end)
if not ok then
	ngx.log(ngx.WARN, "failed to parse the DNS Servers from the environment variable UPSTREAM_DNS_SERVERS"," Error:"..res)
else
	nameservers = res
end

local function query(domain)
	-- closure to perform external lookup to redis
	local dns_query_from_server = function ()
		local r, err = resolver:new{
			nameservers = nameservers,
			retrans = 5,  -- 5 retransmissions on receive timeout
			timeout = 2000,  -- 2 sec
		}

		if not r then
			ngx.log(ngx.ERR, "failed to instantiate the resolver:",err)
			return nil,"failed to instantiate the resolver:"..err
		end

		--local answers, err = r:query("wygtest.service.openpalette")
		local answers, err = r:query(domain)
		if not answers then
			ngx.log(ngx.ERR, "failed to query the DNS server:",err)
			return nil,"failed to query the DNS server:"..err
		end

		if answers.errcode then
			ngx.log(ngx.ERR, "server returned error code: ", answers.errcode,
				": ", answers.errstr)
			return nil,"server returned error code: "..answers.errcode..
			": ".. answers.errstr
		end

		for i, ans in ipairs(answers) do
			if r.TYPE_A==ans.type and r.CLASS_IN==ans.class then
				return ans.address
			end
		end
		return nil,"dns servers return answers,but no server is TYPE_A and CLASS_IN"
	end

	local dns_cache_table = shcache:new(
		dns_cache,
		{ external_lookup = dns_query_from_server,
			--encode = cmsgpack.pack,
			--encode = cjson_safe.encode,
			--decode = cmsgpack.unpack
			--decode = cjson_safe.decode
		},
		{   positive_ttl = dns_cache_positive_ttl,       -- default cache good data for 60s 
			negative_ttl = dns_cache_negative_ttl,           -- default cache failed lookup for 1s
			actualize_ttl = dns_cache_actualize_ttl,
			name = 'dns_cache'                  -- "named" cache, useful for debug / report
		}
	)
	local server, from_cache = dns_cache_table:load(domain)

	return server
end
_M.query = query

return _M
