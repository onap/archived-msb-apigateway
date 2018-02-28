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

local _M = {}
_M._VERSION = '1.0.0'

local pl_stringx = require "pl.stringx"
local split      = pl_stringx.split
local strip      = pl_stringx.strip

function _M.mark_empty_as_nil(t)
	if t == "" then
		return nil
	else
		return t
	end
end

--- splits a string.
-- just a placeholder to the penlight `pl.stringx.split` function
-- @function split
_M.split = split

--- strips whitespace from a string.
-- just a placeholder to the penlight `pl.stringx.strip` function
-- @function strip
_M.strip = strip

return _M
