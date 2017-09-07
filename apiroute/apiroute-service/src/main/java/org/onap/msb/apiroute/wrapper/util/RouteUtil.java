/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.onap.msb.apiroute.wrapper.util;

import org.apache.commons.lang3.StringUtils;
import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.api.Node;
import org.onap.msb.apiroute.api.RouteInfo;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.api.exception.UnprocessableEntityException;


public class RouteUtil {

    
    public static final int consulDeafultPort=8500;
  
	public static final String ROUTE_PATH="msb:routing";   
	
	public static final String ROUTE_PORT_PATH="msb:";  
	
	public static final String ROUTE_PATH_HOST="msb:host";   
	
	public static final String APIROUTE="api";  
	
	public static final String IUIROUTE="iui";  
	
	public static final String CUSTOMROUTE="custom";  
	
	public static final String HTTPS_PROTOCOL="https"; 
	
	public static final String CUSTOM_PORTAL="portal"; 
	
	
    public static final String PROTOCOL_LIST="REST,HTTP,UI,MQ,FTP,SNMP,TCP,UDP"; 
    
    public static final String MSB_ROUTE_URL = "/api/microservices/v1/services";
    
    public static final String MSB_CHECK_URL = "/api/catalog/v1/service/router-all";
    
    public static final String visualRangeRange="0,1";
    
    public static final String controlRangeMatches="0,1,2";
    
    public static final String statusRangeMatches="0,1";
    
    public static final String useOwnUpstreamRangeMatches="0,1";
    
    public static final String ROUTEWAY_IP="ip";
    
    public static final String ROUTEWAY_DOMAIN="domain";
    
    public static final String SPLIT_LINE="|";
    
    public static final String PROTOCOL_REST="REST";
    
    public static final String PROTOCOL_UI="UI";
    
    public static final String PROTOCOL_HTTP="HTTP";
    
    public static final String FILTER_PROTOCOLS="REST,UI,HTTP";
    
    public static final int SERVICE_DATA_QUEUE_NUM=5;
    
    public static final int SERVICE_QUEUE_CAPACITY=100;
    
    public static final int SERVICE_LIST_QUEUE_CAPACITY=5;
    
    public static final int WATCH_SECOND=120;
    
    public static final String HEALTH_CHECK_PASSING="passing";
    

    
	
	/** 
	* @Title: getPrefixedKey  
	* @Description: TODO(Add base path prefix radis assembly path) 
	* @param: @param serviceName
	* @param: @param version
	* @param: @param type
	* @param: @return      
	* @return: String    
	*/
	
	public static String getPrefixedKey(String...paths){
		StringBuffer sb= new StringBuffer();
		
		    if(paths[0].trim().equals("") || paths[0].equals(ConfigUtil.getInstance().getServerPort())){
	            sb.append(ROUTE_PATH);
	        }
	        else{
	           sb.append(ROUTE_PORT_PATH).append(paths[0]); 
	        }
	
		for (int i = 1; i < paths.length; i++) {
			sb.append(":");
			sb.append(paths[i]);
		}
		return sb.toString();
	}
	
	public static String getPrefixedKey4Host(String...paths){
      StringBuffer sb= new StringBuffer();
      
      sb.append(ROUTE_PATH_HOST);
         
  
      for (int i = 0; i < paths.length; i++) {
          sb.append(":");
          sb.append(paths[i]);
      }
      return sb.toString();
  }
	
	
	

	public static void checkRouteWay(String routeWay){
	    if(!CommonUtil.contain(ConfigUtil.getInstance().getRouteWay(),routeWay)){
	      String errInfo = "routeWay does not support,must be ip or domain";
	      throw new UnprocessableEntityException(errInfo);
	    }
	  }
	
	public static void checkServiceNameAndVersion(String serviceName,String version){
	  if (StringUtils.isBlank(serviceName)) {
	      throw new UnprocessableEntityException("serviceName  can't be empty");
	    }

	    if (StringUtils.isNotBlank(version)) {
	      if (!RegExpTestUtil.versionRegExpTest(version)) {
	        throw new UnprocessableEntityException("version  is not a valid  format");
	      }
	    }
    }

	public static void checkServiceStatus(String status){
	  if (!CommonUtil.contain(statusRangeMatches, status)) {
	      throw new UnprocessableEntityException(
	          "save RouteInfo Status FAIL:status is wrong,value range:("
	              + RouteUtil.statusRangeMatches + ")");
	    }     
    }
	
	  
	  
	  public static void checkRouterInfoFormat(RouteInfo routeInfo) {
	    
	    if (StringUtils.isBlank(routeInfo.getServiceName()) || routeInfo.getServers().length == 0) {
	      throw new UnprocessableEntityException(
	          "save RouteInfo FAIL: Some required fields are empty");
	    }

	    if (StringUtils.isNotBlank(routeInfo.getUrl())) {
	      if (!RegExpTestUtil.urlRegExpTest(routeInfo.getUrl())) {
	        throw new UnprocessableEntityException(
	            "save RouteInfo FAIL:url is not a valid format(url must be begin with /)");

	      }
	    }

	    if (!CommonUtil.contain(RouteUtil.visualRangeRange, routeInfo.getVisualRange())) {
	      throw new UnprocessableEntityException(
	          "save RouteInfo FAIL:VisualRange is wrong,value range:("
	              + RouteUtil.visualRangeRange + ")");
	    }

	    if (!CommonUtil.contain(RouteUtil.controlRangeMatches, routeInfo.getControl())) {
	      throw new UnprocessableEntityException(
	          "save RouteInfo FAIL:control is wrong,value range:("
	              + RouteUtil.controlRangeMatches + ")");
	    }

	    if (!CommonUtil.contain(RouteUtil.statusRangeMatches, routeInfo.getStatus())) {
	      throw new UnprocessableEntityException(
	          "save RouteInfo FAIL:status is wrong,value range:("
	              + RouteUtil.statusRangeMatches + ")");
	    }

	    if (!CommonUtil.contain(RouteUtil.useOwnUpstreamRangeMatches, routeInfo.getUseOwnUpstream())) {
	      throw new UnprocessableEntityException(
	          "save RouteInfo FAIL:useOwnUpstream is wrong,value range:("
	              + RouteUtil.useOwnUpstreamRangeMatches + ")");
	    }

	    // Check the service instance format
	    RouteServer[] serverList = routeInfo.getServers();
	    for (int i = 0; i < serverList.length; i++) {
	      RouteServer server = serverList[i];
	      if (!RegExpTestUtil.ipRegExpTest(server.getIp())) {
	        throw new UnprocessableEntityException("save RouteInfo FAIL:IP(" + server.getIp()
	            + ")is not a valid ip address");
	      }

	      if (!RegExpTestUtil.portRegExpTest(server.getPort())) {
	        throw new UnprocessableEntityException("save RouteInfo FAIL:Port(" + server.getPort()
	            + ")is not a valid Port address");
	      }
	    }
	  }
	  
	  public static void checkMicroServiceInfoFormat(MicroServiceFullInfo microServiceInfo,String requestIP){
	    // Check the service instance format
	    if (StringUtils.isBlank(microServiceInfo.getServiceName())
	        || StringUtils.isBlank(microServiceInfo.getProtocol())
	        || microServiceInfo.getNodes().size() == 0) {
	      throw new UnprocessableEntityException(
	          "register MicroServiceInfo FAIL: Some required fields are empty");
	    }

	    for (Node node : microServiceInfo.getNodes()) {

	      if (node.getIp() == null || node.getIp().isEmpty()) {
	        node.setIp(requestIP);
	      } else if (!RegExpTestUtil.ipRegExpTest(node.getIp())) {
	        throw new UnprocessableEntityException("register MicroServiceInfo FAIL:IP(" + node.getIp()
	            + ")is not a valid ip address");
	      }

	      if (!RegExpTestUtil.portRegExpTest(node.getPort())) {
	        throw new UnprocessableEntityException("register MicroServiceInfo FAIL:Port("
	            + node.getPort() + ")is not a valid Port address");
	      }
	    }

	    if (StringUtils.isNotBlank(microServiceInfo.getVersion())) {
	      if (!RegExpTestUtil.versionRegExpTest(microServiceInfo.getVersion())) {
	        throw new UnprocessableEntityException(
	            "register MicroServiceInfo FAIL:version is not a valid  format");

	      }
	    }

	    if (StringUtils.isNotBlank(microServiceInfo.getUrl().trim())) {
	      if (!RegExpTestUtil.urlRegExpTest(microServiceInfo.getUrl())) {
	        throw new UnprocessableEntityException(
	            "register MicroServiceInfo FAIL:url is not a valid format(url must be begin with /)");

	      }
	    }


	    if (RouteUtil.PROTOCOL_LIST.indexOf(microServiceInfo.getProtocol().trim()) == -1) {
	      throw new UnprocessableEntityException(
	          "register MicroServiceInfo FAIL:Protocol is wrong,value range:("
	              + RouteUtil.PROTOCOL_LIST + ")");
	    }

	  }
	  

	  public static String getAPIRedisPrefixedKey(String routeName, String version, String host,String publish_port,String routeWay){
	    String redisPrefixedKey;
	    if(ROUTEWAY_DOMAIN.equals(routeWay)){
	      redisPrefixedKey= RouteUtil.getPrefixedKey4Host(host, APIROUTE, routeName, version);
	    }
	    else{
	      redisPrefixedKey=RouteUtil.getPrefixedKey(publish_port, APIROUTE, routeName, version);
	    }
	    
	    return redisPrefixedKey;
	  }
	  
	  public static String getRedisPrefixedKey(String routeType,String routeName, String host,String publish_port,String routeWay){
        String redisPrefixedKey;
        if(ROUTEWAY_DOMAIN.equals(routeWay)){
          redisPrefixedKey= RouteUtil.getPrefixedKey4Host(host, routeType, routeName);
        }
        else{
          redisPrefixedKey=RouteUtil.getPrefixedKey(publish_port, routeType, routeName);
        }
        
        return redisPrefixedKey;
      }
	  
	  public static String getMutiRedisKey(String routeType,String routeWay){
	    String redisKey;
	    if(RouteUtil.ROUTEWAY_DOMAIN.equals(routeWay)){
	         redisKey =
	            RouteUtil.getPrefixedKey4Host("*", routeType, "*");
	         
	      }
	      else{
	        redisKey =
	            RouteUtil.getPrefixedKey("[^h]*", routeType, "*");
	       
	      }
	    
	    return redisKey;
	  }
	
	 /** 
	  * @Title getRouteNameByns 
	  * @Description TODO(根据服务名和命名空间拆分服务路由名) 
	  * @param serviceName
	  * @param namespace
	  * @return      
	  * @return String    
	  */
	public static String getRouteNameByns(String consul_serviceName,String namespace){
	    String serviceName=consul_serviceName;
	    if(StringUtils.isNotBlank(namespace)){
	      if(consul_serviceName.endsWith("-"+namespace)){
	          serviceName=consul_serviceName.substring(0,consul_serviceName.length()-namespace.length()-1);
	        }  
	    }    
	    
	    return serviceName;
	  }
	
	public static String getVisualRangeByRouter(String visualRange){
	  String[] rangs = StringUtils.split(visualRange, "|");
	  if(rangs.length>1){
	    String visualRangeMatches=ConfigUtil.getInstance().getVisualRangeMatches();
	    if(StringUtils.split(visualRangeMatches, "|").length>1){
	      return "0";
	    }
	    else{
	      return visualRangeMatches;
	    }
	  }
	  else{
	    return visualRange;
	  }
	  
	}

 

}
