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

local msbConf   =  require('conf.msbinit')
local svcConf   =  require('conf.svcconf')
local dbclient  =  require('dao.db_access')
local tbl_util  =  require('lib.utils.table_util')
local svc_util  =  require('lib.utils.svc_util')
local log_util  =  require('lib.utils.log_util')

local tbl_concat = table.concat
local tbl_isempty = tbl_util.isempty
local svc_isactive = svc_util.isactive
local svc_setauthheader = svc_util.setauthheader
local svc_isautodiscover = svc_util.isautodiscover
local str_sub = string.sub
local str_len = string.len
local str_low = string.lower
local ngx_var = ngx.var
local ngx_ctx = ngx.ctx
local log = log_util.log

local defaultport = msbConf.systemConf.defaultport
local defaultprefix = msbConf.systemConf.defaultprefix
local enablerefercheck = msbConf.systemConf.enablerefercheck
local useconsultemplate = msbConf.systemConf.useconsultemplate
local urlfieldMap = svcConf.urlfieldMap

local donotfound = function() 
	ngx.status = ngx.HTTP_NOT_FOUND
	ngx.say("service info not found!")
	return ngx.exit(ngx.status)
end

---------------------------------------------------------------
--preCheck:
--     determine whether it is websocket request 
--     and do internal redirect
---------------------------------------------------------------
local http_upgrade = ngx_var.http_upgrade
if(ngx_var.websocket_internal_redirect == "on") and http_upgrade and str_low(http_upgrade)== "websocket" then
	ngx.log(ngx.ERR, "Websocket request and redirect to @customwebsocket")
	return ngx.exec("@customwebsocket");
end

---------------------------------------------------------------
--step0:Preparation
--      svcnames:service names registered under this port
---------------------------------------------------------------
local sys_prefix = ""
local server_port = ngx_var.server_port
if(server_port == defaultport) then
	sys_prefix = defaultprefix
else
	sys_prefix = server_port
end

local custom_svc_keypattern = tbl_concat({sys_prefix,"custom","*"},":")

local get_svcinfokey_custom = function(svcname) 
	return tbl_concat({sys_prefix,"custom",svcname,"info"},":")
end

local get_svcserverpattern_custom = function(svcname) 
	return tbl_concat({sys_prefix,"custom",svcname,"lb:server*"},":")
end

local svcnames,err = dbclient.load_customsvcnames(custom_svc_keypattern)
if not svcnames then 
	donotfound()
end

---------------------------------------------------------------
--step1:run the match process(check whether the request 
--      match the name in the svcnames one by one) 
--      and return the matched serice info
---------------------------------------------------------------
local req_res  = ngx_var.uri
local svc_type = ngx_var.svc_type
local matchedsvcname
local svcinfo
--add by wangyg:20160418 special handler for refer
local matched_usingrefer = false
--end of add by wangyg:20160418 special handler for refer
for _, svcname in ipairs(svcnames) do
	if (svcname == "/") then
		local svc_info_key = get_svcinfokey_custom(svcname)
		local svc_info,err = dbclient.load_serviceinfo(svc_info_key)
		if svc_info and svc_isactive(svc_info)then
			matchedsvcname = svcname
			svcinfo = svc_info
			break
		end
	end
	local from, to, err = ngx.re.find(req_res, "^"..svcname.."(/(.*))?$", "jo")
	--check whether svcname is the prefix of the req uri
	if from then
		local svc_info_key = get_svcinfokey_custom(svcname)
		local svc_info,err = dbclient.load_serviceinfo(svc_info_key)
		if svc_info and svc_isactive(svc_info) then 
			matchedsvcname = svcname
			svcinfo = svc_info
			break
		end
	else
		--do nothing
	end
end
---------------------------------------------------------------
--step1.1:additional process,test against the refer 
--        similar to step1
---------------------------------------------------------------
--add by wangyg:20160418 special handler for refer
if not matchedsvcname and enablerefercheck then 
	local refer =  ngx_var.http_referer
	if(refer and refer~="") then
		for _, svcname in ipairs(svcnames) do
			local urlreg ="^(https://|http://|)(([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))\\.)(([0-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))\\.){2}([1-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))(:\\d{1,5})?"..svcname.."(/(.*))?$";
			local from, to, err = ngx.re.find(refer, urlreg, "jo")
			----check whether svcname is the prefix of the req refer
			if from then
				local svc_info_key = get_svcinfokey_custom(svcname)
				local svc_info,err = dbclient.load_serviceinfo(svc_info_key)
				if svc_info and svc_isactive(svc_info) then 
					matchedsvcname = svcname
					svcinfo = svc_info
					matched_usingrefer = true
					break
				end
			end
		end
	end
end
--end of add by wangyg:20160418 special handler for refer

if not matchedsvcname or tbl_isempty(svcinfo) then 
	donotfound()
end

local svc_url = svcinfo[urlfieldMap[svc_type]]
if not svc_url then 
	donotfound()
end

---------------------------------------------------------------
--step2:rewrite the request uri using the svcinfo
---------------------------------------------------------------
local rewrited_uri =""
if (matchedsvcname == "/") then
	--special handling: if "/" matched, contact directly
	rewrited_uri = svc_url..req_res
else
	--rewrited_uri = ngx.re.sub(req_res, "^"..matchedsvcname.."(.*)", svc_url.."$1", "o")
	local newuri,n,err = ngx.re.sub(req_res, "^"..matchedsvcname.."(/.*)?", svc_url.."$1", "o")
	--add by wangyg:20160418 special handler for refer
	if(n==0 and matched_usingrefer) then newuri = svc_url..req_res end --special handling if matched using refer
	--end of add by wangyg:20160418 special handler for refer
	rewrited_uri = newuri
end
if (rewrited_uri == "") then rewrited_uri = "/" end --avoid throws internal error when it is empty
ngx.req.set_uri(rewrited_uri)

--set the matched service info,used in the proxy_redirect directive 
ngx_var.svc_name = matchedsvcname
ngx_var.svc_url =  svc_url

--log the route info
log("matchedservice",matchedsvcname)
if(matched_usingrefer) then log("matched_usingrefer",true) end 
log("rewrited_uri",rewrited_uri)

---------------------------------------------------------------
--step2.1:if this service is inter-system,add custom http header
---------------------------------------------------------------
svc_setauthheader(svcinfo)

---------------------------------------------------------------
--step3:process the proxy upstream part
-- con1-using consul template:set the upstream name
-- con2-using msb balancer:query the backserver list and store in the ctx
---------------------------------------------------------------
if useconsultemplate and svc_isautodiscover(svcinfo) then
	--FIX ME:if svcname contains "/", the upstream name may be illegal
	ngx_var.backend = ngx.re.sub(matchedsvcname, "^/(.*)", "$1", "o")
else
	local svc_server_keypattern = get_svcserverpattern_custom(matchedsvcname)
	local backservers,err = dbclient.load_backservers(svc_server_keypattern)
	if tbl_isempty(backservers) then
		donotfound()
	end
	ngx_ctx.backservers = backservers
	ngx_ctx.svcserverpattern = svc_server_keypattern
end