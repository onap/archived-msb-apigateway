Apigateway can synchronized service informations from the service discovery  by configuring filter tag�� 
Synchronization filter configuration file path��apiroute\ext\initRouteLabels\initVisualRangeMatches.json,
{
    "namespace":"",
	"routeLabels":
	{
		"visualRange" : "0",
		"network_plane_type":""	
	}
}
namespaceΪ����ռ����������ֵΪ������Դ�����ˣ�����ֻ�з���namespace����������һ�µķ���Ž�����һ�����Զ����ǩƥ�䡣
routeLabelsΪ�Զ����ǩ���û����Զ����ֵ��ƥ��������֧�ֶ���ֵ����|�ָ�����һ����ǩ��ֵ���㼴ͬ��
��ͬ��������Ϣʱʹ��visualRange�����ǩɸѡ��ȡֵ��Χ  ϵͳ��:0��Ĭ�ϣ�  ϵͳ��:1������ϵͳ�佫�Է���·�ɷ�������Ȩ����

�����docker������ѡ��apigateway������������env����������ȡ��
��������NAMESPACE
��������ROUTE_LABELS   ����ֵ��ʽ��visualRange��0; network_plane_type:xx|yy
