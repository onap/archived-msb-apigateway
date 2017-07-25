Apigateway can synchronized service informations from the service discovery  by configuring filter tag。 
Synchronization filter configuration file path：apiroute\ext\initRouteLabels\initVisualRangeMatches.json,
{
    "namespace":"",
	"routeLabels":
	{
		"visualRange" : "0",
		"network_plane_type":""	
	}
}
namespace为命令空间过滤条件，值为空则忽略此项过滤，否则只有服务namespace属性与条件一致的服务才进行下一步的自定义标签匹配。
routeLabels为自定义标签，用户可自定义键值对匹配条件，支持多项值，以|分隔。任一个标签的值满足即同步
当同步服务信息时使用visualRange这个标签筛选，取值范围  系统间:0（默认）  系统内:1，其中系统间将对服务路由访问做鉴权处理。

如果是docker部署首选在apigateway所在容器配置env环境变量获取：
变量名：NAMESPACE
变量名：ROUTE_LABELS   变量值格式：visualRange：0; network_plane_type:xx|yy
