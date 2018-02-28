--[[

    Copyright (C) 2017 ZTE, Inc. and others. All rights reserved. (ZTE)

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

local cjson_safe = require "cjson.safe"
local dao = require('dao.dao')
local tbl_util  = require('lib.utils.table_util')

local stats_cache = ngx.shared.stats
local new_timer = ngx.timer.at
local tbl_isempty = tbl_util.isempty
local accept_preparing_forward_key = "accept_preparing_forward"
local forward_waiting_response_key = "forward_waiting_response"
local receive_resp_not_return_key = "receive_resp_not_return"
local req_num_stats_key = "req_num_stats"
local reset_delay = 60 -- in seconds
local remove_delay = 24*60*60 -- in seconds,24 hours
local lastday_stats = ""


function _M.accept_new_request()
    local newval, err = stats_cache:incr(accept_preparing_forward_key, 1, 0)
    if (not newval) then
        ngx.log(ngx.ERR, "increase number of accept_not_forward_key error:", err)
    end
end

function _M.forward_backend()
    if not ngx.ctx.not_first_forward then
        local newval, err = stats_cache:incr(accept_preparing_forward_key, -1, 1)
        if (not newval) then
            ngx.log(ngx.ERR, "decrease number of accept_preparing_forward error:", err)
        end
    end
    local newval, err = stats_cache:incr(forward_waiting_response_key, 1, 0)
    if (not newval) then
        ngx.log(ngx.ERR, "increase number of forward_waiting_response error:", err)
    end
    ngx.ctx.waiting_backend_resp = true
    ngx.ctx.not_first_forward = true
end

function _M.backend_failed()
    ngx.ctx.waiting_backend_resp = false
    local newval, err = stats_cache:incr(forward_waiting_response_key, -1, 1)
    if (not newval) then
        ngx.log(ngx.ERR, "decrease number of forward_waiting_response error:", err)
    end
end

function _M.receive_response()
    if ngx.ctx.waiting_backend_resp then
        ngx.ctx.waiting_backend_resp = false
        local newval, err = stats_cache:incr(forward_waiting_response_key, -1, 1)
        if (not newval) then
            ngx.log(ngx.ERR, "decrease number of forward_waiting_response error:", err)
        end
    else
        local newval, err = stats_cache:incr(accept_preparing_forward_key, -1, 1)
        if (not newval) then
            ngx.log(ngx.ERR, "decrease number of accept_preparing_forward error:", err)
        end
    end
    local newval, err = stats_cache:incr(receive_resp_not_return_key, 1, 0)
    if (not newval) then
        ngx.log(ngx.ERR, "increase number of receive_resp_not_return error:", err)
    end
end

function _M.return_response()
    local newval, err = stats_cache:incr(receive_resp_not_return_key, -1, 1)
    if (not newval) then
        ngx.log(ngx.ERR, "decrease number of receive_resp_not_return error:", err)
    end

    local newval, err = stats_cache:incr(req_num_stats_key, 1, 0)
    if (not newval) then
        ngx.log(ngx.ERR, "increase number of total_req_num error:", err)
    end
end

function _M.format_req_status()
    local req_status = {}
    req_status[accept_preparing_forward_key] = stats_cache:get(accept_preparing_forward_key) or 0
    req_status[forward_waiting_response_key] = stats_cache:get(forward_waiting_response_key) or 0
    req_status[receive_resp_not_return_key] = stats_cache:get(receive_resp_not_return_key) or 0
    local value, err = cjson_safe.encode(req_status)
    if err then
        return "Collect real-time request status failed! Error:" .. err
    end
    return value
end

function _M.format_conn_status()
    local conn_status = {}
    conn_status["connections_active"] = ngx.var.connections_active
    conn_status["connections_reading"] = ngx.var.connections_reading
    conn_status["connections_writing"] = ngx.var.connections_writing
    conn_status["connections_waiting"] = ngx.var.connections_waiting

    local value, err = cjson_safe.encode(conn_status)
    if err then
        return "Collect real-time connection status failed! Error:" .. err
    end
    return value
end

function _M.get_reqnum_stats(latest_num)
    local resp = dao.get_reqnum_stats(ngx.today(), latest_num)
    if tbl_isempty(resp) then
        return "[]"
    end
	return cjson_safe.encode(resp)
end

_reset_reqnum_stats = function(premature)
	if premature then return end
	local count = stats_cache:get(req_num_stats_key) or 0
    dao.save_reqnum_stats(ngx.today(), ngx.time().."|"..count)
    stats_cache:set(req_num_stats_key, 0)

    local ok, err = new_timer(reset_delay, _reset_reqnum_stats)
    if not ok then
        ngx.log(ngx.ERR, "failed to create _reset_reqnum_stats timer: ", err)
        return
    end
end

_remove_old_stats = function(premature)
	if premature then return end
	ngx.update_time()
	if lastday_stats ~= "" and lastday_stats ~= ngx.today() then
		ngx.log(ngx.ERR, "delete old data ")
		dao.delete_reqnum_stats(lastday_stats)
	end
    local ok, err = new_timer(remove_delay, _remove_old_stats)
    if not ok then
        ngx.log(ngx.ERR, "failed to create _remove_old_stats timer: ", err)
        return
    end
	lastday_stats = ngx.today()
end

function _M.init_timer()
    if 0 == ngx.worker.id() then
        local ok, err = new_timer(reset_delay, _reset_reqnum_stats)
        if not ok then
            ngx.log(ngx.ERR, "failed to create _reset_reqnum_stats timer: ", err)
            return
        end
		
		ok, err = new_timer(10, _remove_old_stats)
        if not ok then
            ngx.log(ngx.ERR, "failed to create _remove_old_stats timer: ", err)
            return
        end
    end
end

return _M