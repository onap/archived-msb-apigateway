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

local floor      = math.floor
local str_byte   = string.byte
local tab_sort   = table.sort
local tab_insert = table.insert

local MOD       = 2 ^ 32
local REPLICAS  = 20
local LUCKY_NUM = 13


local tbl_util  = require('lib.utils.table_util')
local tbl_isempty = tbl_util.isempty
local tablex = require('pl.tablex')
local peerwatcher = require "core.peerwatcher"
local ngx_var = ngx.var
local hash_data = {}

local function hash_string(str)
    local key = 0
    for i = 1, #str do
        key = (key * 31 + str_byte(str, i)) % MOD
    end
    return key
end


local function init_consistent_hash_state(servers)
    local weight_sum = 0
    local weight = 1
    for _, srv in ipairs(servers) do
        if srv.weight  and srv.weight ~= 0 then
            weight = srv.weight
        end
        weight_sum = weight_sum + weight
    end

    local circle, members = {}, 0
    for index, srv in ipairs(servers) do
        local key = ("%s:%s"):format(srv.ip, srv.port)
        local base_hash = hash_string(key)
        for c = 1, REPLICAS * weight_sum do
            local hash = (base_hash * c * LUCKY_NUM) % MOD
            tab_insert(circle, { hash, index })
        end
        
        members = members + 1
    end
    tab_sort(circle, function(a, b) return a[1] < b[1] end)
    return { circle = circle, members = members }
end

local function update_consistent_hash_state(hash_data,servers,svckey)
    -- compare servers in ctx with servers in cache
    -- update the hash data if changes occur
    local serverscache = tablex.deepcopy(hash_data[svckey].servers)
    tab_sort(serverscache, function(a, b) return a.ip < b.ip end)
    tab_sort(servers, function(a, b) return a.ip < b.ip end)
    if  not tablex.deepcompare(serverscache, servers, false) then
        local tmp_chash = init_consistent_hash_state(servers)
        hash_data[svckey].servers =servers
        hash_data[svckey].chash = tmp_chash
    end
end

local function binary_search(circle, key)
    local size = #circle
    local st, ed, mid = 1, size

    while st <= ed do
        mid = floor((st + ed) / 2)
        if circle[mid][1] < key then
            st = mid + 1
        else
            ed = mid - 1
        end
    end

    return st == size + 1 and 1 or st
end


function _M.select_backserver(servers,svckey)

    if hash_data[svckey] == nil then
        local tbl = {}
        tbl['servers'] = {}
        tbl['chash'] = {}
        hash_data[svckey] = tbl
    end

    if tbl_isempty(hash_data[svckey].servers) then
        local tmp_chash = init_consistent_hash_state(servers)
        hash_data[svckey].servers = servers
        hash_data[svckey].chash = tmp_chash
    else
        update_consistent_hash_state(hash_data,servers,svckey)
    end

    local chash = hash_data[svckey].chash
    local circle = chash.circle
    local hash_key = ngx_var.remote_addr
    local st = binary_search(circle, hash_string(hash_key))
    local size = #circle
    local ed = st + size - 1
    for i = st, ed do
        local idx = circle[(i - 1) % size + 1][2]
        if peerwatcher.is_server_ok(svckey,hash_data[svckey].servers[idx]) then
            return hash_data[svckey].servers[idx]
        end
    end
    return nil, "consistent hash: no servers available"
end

return _M
