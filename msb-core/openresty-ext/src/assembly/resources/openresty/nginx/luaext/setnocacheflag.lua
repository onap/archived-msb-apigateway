--[[

    Copyright 2016 ZTE Corporation.

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
local from, to, err = ngx.re.find(ngx.var.uri, "\\.(gif|jpg|jpeg|png|bmp|ico)$", "jo")
--Based on the request file type to determine whether to cache
if from then
    --use cache
	return 0
else
	--do not use cache
	return 1
end