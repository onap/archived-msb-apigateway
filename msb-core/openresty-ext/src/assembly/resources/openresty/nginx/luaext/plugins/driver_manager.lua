--[[

    Copyright 2016 2015-2016 OPEN-O. and others. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

]]
local _M = {}
_M._VERSION = '1.0.0'

function _M.access()
	ngx.log(ngx.INFO, "running driver_manager plugin")
	--add your own code here
	--choose the right backend server,and then tell nginx, e.g. ngx.var.backend = XX.XX.XX.XX:8888
end

return _M