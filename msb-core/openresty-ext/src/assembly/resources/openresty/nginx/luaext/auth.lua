local cache = ngx.shared.ceryx
local client_ip = ngx.var.remote_addr
local host = ngx.var.host
if client_ip == '127.0.0.1' or client_ip == host or client_ip == ngx.var.server_addr then
	return
end	
local succ, err, forcible = cache:replace(client_ip, "place_holder", 3600)
if not succ then
	if err == 'not found' then
		ngx.log(ngx.WARN, "access record not found for "..client_ip)
		ngx.exit(401)
	else
		ngx.log(ngx.WARN, err)
	end	
end