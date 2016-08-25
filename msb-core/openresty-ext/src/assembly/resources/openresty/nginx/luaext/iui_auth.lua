local cache = ngx.shared.ceryx
local client_ip = ngx.var.remote_addr
local uri = ngx.var.uri
if uri == "/iui/framework/login.html" then
	local value, flags = cache:get(client_ip)
	if not value then
		return
	else
		ngx.redirect("/iui/framework/main-page.html")
	end
end

local referer =  ngx.var.http_referer
local refererList = {
	"/iui/framework/login.html",
	"/iui/framework/css/login.css",
	"/iui/component/thirdparty/font%-awesome/css/font%-awesome.min.css",
	"/iui/framework/css/style%-custom.css"
}	
local function referer_matches(t, r)
	for k,_ in pairs(t) do
		if string.match(r, t[k]) then
			return true
		end
	end
	return false
end	

if referer and referer_matches(refererList, referer) then
	return
end	

local succ, err, forcible = cache:replace(client_ip, "place_holder", 3600)
if not succ then
	if err == 'not found' then
		ngx.log(ngx.WARN, "access record not found for "..client_ip..",redirect to login page")
		ngx.redirect("/iui/framework/login.html")
	else
		ngx.log(ngx.WARN, err)
	end
end