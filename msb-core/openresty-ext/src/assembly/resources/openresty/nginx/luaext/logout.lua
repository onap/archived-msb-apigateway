local cache = ngx.shared.ceryx
local client_ip = ngx.var.remote_addr
local succ, err, forcible = cache:delete(client_ip)
if not succ then
	ngx.log(ngx.WARN, err)
end