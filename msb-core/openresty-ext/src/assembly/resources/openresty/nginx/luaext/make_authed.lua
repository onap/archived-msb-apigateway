function ipValidator(ip)
	local chunks = {ip:match("(%d+)%.(%d+)%.(%d+)%.(%d+)")}
	if #chunks == 4 then
		for _,v in pairs(chunks) do
			if tonumber(v) > 255 then return R.STRING end
		end
		return true
	end
	local chunks = {ip:match(("([a-fA-F0-9]*):"):rep(8):gsub(":$","$"))}
	if #chunks == 8 then
		for _,v in pairs(chunks) do
			if #v > 0 and tonumber(v, 16) > 65535 then return R.STRING end
		end
		return true
	end
	return false
end	

if ngx.req.get_method() == "POST" then
	ngx.req.read_body()
	local body = ngx.req.get_body_data()	
	local json = require('cjson')
	local tab = json.decode(body)
	local ip = tab["passIp"]
	if not ip then
		ngx.log(ngx.WARN, "ip is nil.")
		ngx.exit(500)
	end
	if ipValidator(ip) then
		local cache = ngx.shared.ceryx
		local succ, err, forcible = cache:set(ip, "place_holder", 3600)
		if not succ then
			ngx.log(ngx.WARN, err)
			ngx.exit(500)
		end
	else
		ngx.log(ngx.WARN, "not a valid ip.")
		ngx.exit(500)
	end		
	ngx.exit(201)
else
	ngx.log(ngx.WARN, "not a POST request.")
	ngx.exit(500)
end