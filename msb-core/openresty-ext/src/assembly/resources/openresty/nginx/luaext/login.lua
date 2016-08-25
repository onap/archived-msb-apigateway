local h = ngx.resp.get_headers()
if h["openoauth"] and h["openoauth"] == "true" then
	local cache = ngx.shared.ceryx
	local client_ip = ngx.var.remote_addr
	local succ, err, forcible = cache:set(client_ip, "place_holder", 3600)
	if not succ then
		ngx.log(ngx.WARN, err)
	end
end	