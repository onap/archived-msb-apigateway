--[[

    Copyright 2016 2015-2016 OEPN-O. and others. All rights reserved.

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
local auth_url = '/openoapi/auth/v1';
local auth_token_url = auth_url..'/tokens';
local auth_token_key = "X-Auth-Token";
local redirect_url = "/openoui/auth/v1/login/html/login.html"

local white_list= {
  auth_token_url,
  redirect_url,
  '/openoui/auth/v1/login/'
};

local function verify_value(value)
  if (nil == value or 0 == #value)
  then
    return false;
  else
    return true;
  end
end

--[[checks str2 starts with str1]]--
local function starts_with(str1, str2)
  return string.sub(str2, 1, string.len(str1)) == str1;
end

--  Check and ignore the request if it is from auth module.--
local function is_white_list(url)
  for i, value in ipairs(white_list)
  do
    if (starts_with(value, url))
      then
        return true;
      end
  end
  return false;
end

local function set_header(tokens)
  for key,value in pairs(tokens)
  do
    ngx.log (ngx.ERR, "Headers: ", key, value);
    ngx.req.set_header(key, value);
  end

end
--[[ validates the token with auth ]]--
local function validate_token(tokens)
  -- auth expects the token in header.
  set_header(tokens);
  -- call auth token check url to validate.
  local res = ngx.location.capture(auth_token_url, { method = ngx.HTTP_HEAD});
  ngx.log (ngx.ERR, "Auth Result:", res.status);
  if (nil == res)
  then
    return false;
  end
  return (ngx.HTTP_OK == res.status);
end

--[[ get auth token from cookies ]]--
local function get_cookies()
  local cookie_name = "cookie_"..auth_token_key;
  local auth_token = ngx.var[cookie_name];
  local tokens = {};
  -- verify whether its empty or null.
  if (verify_value(auth_token))
  then
  ngx.log(ngx.ERR, "token : ", auth_token );
    tokens[auth_token_key] = auth_token;
  end
  return tokens;
end

local function get_service_url()
  -- get host.
  local host = ngx.var.host;
  --get port
  local port = ":"..ngx.var.server_port;
  local proto = "";
  --get protocol
  if (ngx.var.https == "on")
  then
    proto = "https://";
  else
    proto = "http://";
  end
  --get url
  local uri = ngx.var.rui;
  --form complete service url.
  --local complete_url = proto..host..port..url
  local complete_url = uri;
  local service = "?service="
  --add arguments if any.
  if ngx.var.args ~= nil
  then
    complete_url = complete_url.."?"..ngx.var.args;
  end
  ngx.log(ngx.ERR, "service url : ", complete_url);
  return service..ngx.escape_uri(complete_url);
end

local function redirect(url)
  local service = get_service_url();
  ngx.log(ngx.ERR, "redirect: ", url..service);
  ngx.redirect(url..service);
end

function _M.access()

    ngx.log(ngx.ERR, "==============start check token===============: ");
    local url = ngx.var.uri;
    ngx.log(ngx.ERR, "Url : ", url);

    -- ignore token validation if auth request.
    if (is_white_list(url))
    then
      return;
    end



    -- get auth token from cookies.
    local auth_tokens = get_cookies();

    -- check if auth token is empty,
    -- redirect it to login page in that case.
    if (nil == next(auth_tokens))
    then
      ngx.log(ngx.ERR, "Token Invalidate, redirect to ", redirect_url);
      redirect(redirect_url);
      return;
    end

    -- validate the token with auth module.
    -- continue if success, else redirect to login page.
    if(validate_token(auth_tokens))
    then
      ngx.log(ngx.ERR, "Token Validate.");
      return;
    else
      redirect(redirect_url);
    end
    	ngx.log(ngx.INFO, "running auth plugin")
    end

return _M