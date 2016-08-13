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

local _M = {}
_M._VERSION = '1.0.0'

local tbl_util  = require('lib.utils.table_util')
local tbl_isempty = tbl_util.isempty

function _M.get_backserver(servers,svckey)
	if tbl_isempty(servers) then return nil,"input server list is empty" end

	local length = #servers
	if length==1 then
		-- return it directly if there is only one server
		return servers[1],nil
	end

	local resty_lock = require "resty.lock"
	local roundrobin_cache = ngx.shared.rr_cache

	--step1:acquire lock
	local opts = {["timeout"] = 0.002,["exptime"] = 0.05}--this can be set using the conf file
	local rrlock = resty_lock:new("rr_locks",opts)
	local elapsed, err = rrlock:lock(svckey)
	if not elapsed then
		--return fail("failed to acquire the lock: ", err)
	end
	--step2:lock successfully acquired!incr the index
	local index, err = roundrobin_cache:get(svckey)
	if not index then
		index = 0
	end
	index = index%length+1

	--step3:update the shm cache with the new index
	roundrobin_cache:set(svckey,index)

	--step4:release the lock
	local ok, err = rrlock:unlock()

	return servers[index],nil
end

return _M