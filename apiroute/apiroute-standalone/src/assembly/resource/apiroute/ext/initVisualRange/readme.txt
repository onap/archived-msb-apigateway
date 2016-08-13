########################## initialize default visualRange Matches from msdiscover to redis ##########################


visualRange:服务的可见范围   系统间:0   系统内:1 

#when msb is starting, it will automatically read this json file named "initVisualRangeMatches.json"

#when  msdiscover will sysn datas  to apiGateway,only visualRange Matches will be save to redis. 



# JSON File content must be  format array like below examples:

0 or 1 or 0,1