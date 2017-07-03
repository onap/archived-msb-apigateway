########################## initialize default routeInfo to redis ##########################

#when msb is starting, it will automatically read all json files under this folder, and initializes to redis. 
#If the routeInfo is exist, it will be ignored, otherwise it will be saved.


# JSON File content must be routeInfo format array like below examples:

#  optional:
#  apiJsonType: 1:user-defined json type    0:pre-defined json type
#  control:   0:default   1:readonly  2:hidden
#  status:    0:disabled   1:enabled  
#  Tip£ºcontrol¡¢status¡¢weight are  non-mandatory 

[
##########################apiRoute example##########################

{
		"serviceName" : "microservices",
		"version" : "v1",
		"url" : "/api/microservices/v1",
		"apiJson" : "/api/microservices/v1/swagger.json",
		"apiJsonType" : "1",
		"metricsUrl" : "/admin/metrics",
		"control" : "1",
		"status" : "1",
		"servers" : [{
				"ip" : "127.0.0.1",
				"port" : "8086",
				"weight" : 0
			}
		]
	},
	
##########################iuiRoute example##########################
	
{
		"serviceName" : "microservices",
		"url" : "/iui/microservices",
		"control" : "1",
		"status" : "1",
		"servers" : [{
				"ip" : "127.0.0.1",
				"port" : "8086",
				"weight" : 0
			}
		]
	},
	
##########################customRoute example##########################	
	{
		"serviceName" : "/test",
		"url" : "/test",
		"control" : "0",
		"status" : "1",
		"servers" : [{
				"ip" : "10.74.56.36",
				"port" : "8989",
				"weight" : 0
			}
		]
	}
]