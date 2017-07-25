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

local cjson = require "cjson"

if ngx.ctx.logtbl then
	local jsonData = cjson.encode(ngx.ctx.logtbl)
	ngx.log(ngx.INFO, ngx.var.request_id.." "..string.gsub(jsonData,"\\/","/"))
end