if ngx.req.get_method() == "GET" then
	local services = {
		{serviceName="odlsdnia",apiJson="/api/odlsdnia/v1/swagger.json"},
		{serviceName="roc",apiJson="/api/roc/v1/swagger.json"},
		{serviceName="tackeria",apiJson="/api/tackeria/v1/swagger.json"},
		{serviceName="etsi",apiJson="/api/etsiia/v1/swagger.json"}
	}
	local cjson = require "cjson"
	local jsonData = cjson.encode(services)
	jsonData = string.gsub(jsonData,"\\/","/")
	ngx.print(jsonData)
else
	ngx.log(ngx.WARN, "not a GET request.")
	ngx.exit(500)
end