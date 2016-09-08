====
    Copyright 2016 2015-2016 ZTE, Inc. and others. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

        Author: Zhaoxing Meng
        email: meng.zhaoxing1@zte.com.cn
====

########################## initialize default routeInfo to redis ##########################

#when msb is starting, it will automatically read all json files under this folder, and initializes to redis. 
#If the routeInfo is exist, it will be ignored, otherwise it will be saved.


# JSON File content must be routeInfo format array like below examples:

#  optional:
#  apiJsonType: 1:user-defined json type    0:pre-defined json type
#  control:   0:default   1:readonly  2:hidden
#  status:    0:disabled   1:enabled  
#  Tip��control��status��weight are  non-mandatory 

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