--[[

    Copyright 2016 Huawei Technologies Co., Ltd.

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
local _HEADER = "X-Driver-Parameter"

--extract driver header if present in request
function get_driver_header()
  local header = ""
  local driver_header = ngx.req.get_headers()[_HEADER]  
  if (driver_header ~= nil)
  then
    header = driver_header
  end
  return header
end

-- generate query url
function get_query_url(x_driver_header)
  local drivermgr_uri = '/openoapi/drivermgr/v1/drivers'
  local url = drivermgr_uri.."?".._HEADER.."="..tostring(ngx.escape_uri(x_driver_header)).."&service_url="..ngx.var.uri
  return url
end

-- generate driver url
function get_driver_url(driver_header)
  local cjson = require "cjson"
  local query_url = get_query_url(driver_header)
  local res = ngx.location.capture(query_url, { method = ngx.HTTP_GET})
  ngx.log (ngx.ERR, "Driver manager resp url : ", tostring(res.body))
  if (res.status == 200 and res.body ~= nil and res.body ~= '')
  then
    return tostring(cjson.new().decode(res.body).url)
  else
    return ''
  end
end

-- get headers
function get_headers()
  local headers = {}
  local h = ngx.req.get_headers()
  for k, value in pairs(h)
  do
     headers[k] = value
  end
  return headers
end

function get_body_params()
  ngx.req.read_body()
  local actual_body = ""  
  local body_param = ngx.req.get_body_data()
  if(body_param ~= nil)
  then
    actual_body = tostring(body_param)
  end
  return actual_body
end



ngx.log(ngx.INFO, "DRIVER MANAGER LUA", "***********************")

-- extract X-Driver-Parameter header param
local driver_header = get_driver_header()
ngx.log(ngx.ERR, "X-Driver-Parameter: ", driver_header)


-- ignore driver redirection if not driver manager request.
if (driver_header ~= "")
then 
  
  local driver_url = get_driver_url(driver_header)
  ngx.log (ngx.ERR, "Driver manager URl:: ", driver_url)
 
  local http = require "resty.http"
  local actual_headers = get_headers()
  local actual_body = get_body_params()

  ngx.log(ngx.ERR, "HTTP request to driver... ", " Request to driver manager")
  local res, err = http.new():request_uri(driver_url, {
        method = ngx.req.get_method(),
        body = actual_body,
        headers = actual_headers
      })

      if not res then
        ngx.say("Request to driver failed : ", err)
        return
      end
 ngx.log(ngx.ERR, "Response from driver : ", tostring(res.body))
 ngx.say(res.body)

else
  ngx.log(ngx.ERR, "X-Driver-Parameter not present", " Redirect to same url")
end