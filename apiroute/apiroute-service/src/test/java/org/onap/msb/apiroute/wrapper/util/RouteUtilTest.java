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

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.api.Node;
import org.onap.msb.apiroute.api.RouteInfo;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.api.exception.UnprocessableEntityException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigUtil.class})
public class RouteUtilTest {

  @Test
  public void test_getPrefixedKey() {
    Assert.assertEquals("msb:routing:test:v1", RouteUtil.getPrefixedKey("", "test", "v1"));
    Assert.assertEquals("msb:5656:test:v1", RouteUtil.getPrefixedKey("5656", "test", "v1"));

  }

  @Test
  public void test_getPrefixedKey4Host() {
    Assert.assertEquals("msb:host:test:v1", RouteUtil.getPrefixedKey4Host("test", "v1"));

  }



  @Test
  public void test_checkRouteWay() {
    try {
      RouteUtil.checkRouteWay("ipp");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }

  }

  @Test
  public void test_checkServiceNameAndVersion() {
    try {
      RouteUtil.checkServiceNameAndVersion("","v1");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
    
    try {
      RouteUtil.checkServiceNameAndVersion("test","ve1");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  @Test
  public void test_checkServiceStatus() {
    try {
      RouteUtil.checkServiceStatus("2");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  @Test
  public void test_checkRouterInfoFormat() {
    RouteInfo routeInfo=new RouteInfo();
    
    try {
      RouteUtil.checkRouterInfoFormat(routeInfo);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  @Test
  public void test_checkMicroServiceInfoFormat() {
    MicroServiceFullInfo microServiceInfo=new MicroServiceFullInfo();
    
    try {
      RouteUtil.checkMicroServiceInfoFormat(microServiceInfo,"");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  @Test
  public void test_checkMicroServiceInfoFormat_ip() {
    MicroServiceFullInfo microServiceInfo=new MicroServiceFullInfo();
    microServiceInfo.setServiceName("name");
    microServiceInfo.setProtocol("REST");
    Set<Node> nodeSet = new HashSet<>();
    nodeSet.add(new Node("10.74.148.88.22","8080"));
    microServiceInfo.setNodes(nodeSet);
    
    try {
      RouteUtil.checkMicroServiceInfoFormat(microServiceInfo,"");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  @Test
  public void test_checkMicroServiceInfoFormat_port() {
    MicroServiceFullInfo microServiceInfo=new MicroServiceFullInfo();
    microServiceInfo.setServiceName("name");
    microServiceInfo.setProtocol("REST");
    Set<Node> nodeSet = new HashSet<>();
    nodeSet.add(new Node("10.74.148.88.22","808770"));
    microServiceInfo.setNodes(nodeSet);
    
    try {
      RouteUtil.checkMicroServiceInfoFormat(microServiceInfo,"");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  @Test
  public void test_checkMicroServiceInfoFormat_version() {
    MicroServiceFullInfo microServiceInfo=new MicroServiceFullInfo();
    microServiceInfo.setServiceName("name");
    microServiceInfo.setProtocol("REST");
    Set<Node> nodeSet = new HashSet<>();
    nodeSet.add(new Node("","8089"));
    microServiceInfo.setNodes(nodeSet);
    microServiceInfo.setVersion("cv2");
    
    try {
      RouteUtil.checkMicroServiceInfoFormat(microServiceInfo,"10.74.55.36");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  @Test
  public void test_checkMicroServiceInfoFormat_url() {
    MicroServiceFullInfo microServiceInfo=new MicroServiceFullInfo();
    microServiceInfo.setServiceName("name");
    microServiceInfo.setProtocol("REST");
    Set<Node> nodeSet = new HashSet<>();
    nodeSet.add(new Node("","8089"));
    microServiceInfo.setNodes(nodeSet);
    microServiceInfo.setVersion("v2");
    microServiceInfo.setUrl("url");
    
    try {
      RouteUtil.checkMicroServiceInfoFormat(microServiceInfo,"10.74.55.36");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  @Test
  public void test_checkMicroServiceInfoFormat_protocol() {
    MicroServiceFullInfo microServiceInfo=new MicroServiceFullInfo();
    microServiceInfo.setServiceName("name");
    microServiceInfo.setProtocol("REST2");
    Set<Node> nodeSet = new HashSet<>();
    nodeSet.add(new Node("","8089"));
    microServiceInfo.setNodes(nodeSet);
    microServiceInfo.setVersion("v2");
    microServiceInfo.setUrl("/url");
    
    try {
      RouteUtil.checkMicroServiceInfoFormat(microServiceInfo,"10.74.55.36");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  @Test
  public void test_getAPIRedisPrefixedKey() {
    Assert.assertEquals("msb:20081:api:testApi:v1", RouteUtil.getAPIRedisPrefixedKey("testApi", "v1", "testHost","20081","ip"));
    Assert.assertEquals("msb:routing:api:testApi:v1", RouteUtil.getAPIRedisPrefixedKey("testApi", "v1", "testHost","","ip"));
    Assert.assertEquals("msb:host:testHost:api:testApi:v1", RouteUtil.getAPIRedisPrefixedKey("testApi", "v1", "testHost","20081","domain"));
  }
  
  @Test
  public void test_getRedisPrefixedKey() {
    Assert.assertEquals("msb:20081:custom:/testName/v1", RouteUtil.getRedisPrefixedKey(RouteUtil.CUSTOMROUTE,"/testName/v1", "testHost","20081","ip"));
    Assert.assertEquals("msb:routing:custom:/testName/v1", RouteUtil.getRedisPrefixedKey(RouteUtil.CUSTOMROUTE,"/testName/v1", "testHost","","ip"));
    Assert.assertEquals("msb:host:testHost:custom:/testName/v1", RouteUtil.getRedisPrefixedKey(RouteUtil.CUSTOMROUTE,"/testName/v1", "testHost","20081","domain"));
  
    Assert.assertEquals("msb:20081:iui:testName", RouteUtil.getRedisPrefixedKey(RouteUtil.IUIROUTE,"testName", "testHost","20081","ip"));
    Assert.assertEquals("msb:routing:iui:testName", RouteUtil.getRedisPrefixedKey(RouteUtil.IUIROUTE,"testName", "testHost","","ip"));
    Assert.assertEquals("msb:host:testHost:iui:testName", RouteUtil.getRedisPrefixedKey(RouteUtil.IUIROUTE,"testName", "testHost","20081","domain"));
  }
  
  @Test
  public void test_getMutiRedisKey() {
    Assert.assertEquals("msb:[^h]*:api:*", RouteUtil.getMutiRedisKey(RouteUtil.APIROUTE,"ip"));
    Assert.assertEquals("msb:[^h]*:iui:*", RouteUtil.getMutiRedisKey(RouteUtil.IUIROUTE,"ip"));
    Assert.assertEquals("msb:[^h]*:custom:*", RouteUtil.getMutiRedisKey(RouteUtil.CUSTOMROUTE,"ip"));

    Assert.assertEquals("msb:host:*:api:*", RouteUtil.getMutiRedisKey(RouteUtil.APIROUTE,"domain"));
    Assert.assertEquals("msb:host:*:iui:*", RouteUtil.getMutiRedisKey(RouteUtil.IUIROUTE,"domain"));
    Assert.assertEquals("msb:host:*:custom:*", RouteUtil.getMutiRedisKey(RouteUtil.CUSTOMROUTE,"domain"));
  }
  
  @Test
  public void test_getRouteNameByns() {
    Assert.assertEquals("serviceName", RouteUtil.getRouteNameByns("serviceName",""));
    Assert.assertEquals("serviceName", RouteUtil.getRouteNameByns("serviceName-ns","ns"));
    Assert.assertEquals("serviceName-ns", RouteUtil.getRouteNameByns("serviceName-ns-ns","ns"));
    Assert.assertEquals("serviceName", RouteUtil.getRouteNameByns("serviceName","default"));
  }
  
  @Test
  public void test_getVisualRangeByRouter(){
    Assert.assertEquals("0", RouteUtil.getVisualRangeByRouter("0|1"));
    Assert.assertEquals("1", RouteUtil.getVisualRangeByRouter("1"));
    Assert.assertEquals("0", RouteUtil.getVisualRangeByRouter("0"));
    
   
  }
  
  @Test
  public void test_getVisualRangeByRouter_muti(){
    PowerMockito.mockStatic(System.class);
    PowerMockito.when(System.getenv("ROUTE_LABELS")).thenReturn("lab1:val,visualRange:0|1");
    ConfigUtil.getInstance().initRouteLabelsMatches();
    Assert.assertEquals("0", RouteUtil.getVisualRangeByRouter("0|1"));
  }
  
  @Test
  public void test_checkRouterInfoFormat_url() {
    RouteInfo routeInfo=new RouteInfo();
    routeInfo.setServiceName("name");
    routeInfo.setUrl("url");
    routeInfo.setServers( new RouteServer[]{new RouteServer("10.74.148.88","8080")});
    
    try {
      RouteUtil.checkRouterInfoFormat(routeInfo);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  @Test
  public void test_checkRouterInfoFormat_visualRangeRange() {
    RouteInfo routeInfo=new RouteInfo();
    routeInfo.setServiceName("name");
    routeInfo.setUrl("/url");
    routeInfo.setServers( new RouteServer[]{new RouteServer("10.74.148.88","8080")});
    routeInfo.setVisualRange("2");
    
    try {
      RouteUtil.checkRouterInfoFormat(routeInfo);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  
  @Test
  public void test_checkRouterInfoFormat_controlRangeMatches() {
    RouteInfo routeInfo=new RouteInfo();
    routeInfo.setServiceName("name");
    routeInfo.setUrl("/url");
    routeInfo.setServers( new RouteServer[]{new RouteServer("10.74.148.88","8080")});
    routeInfo.setVisualRange("0");
    routeInfo.setControl("3");
    
    try {
      RouteUtil.checkRouterInfoFormat(routeInfo);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  @Test
  public void test_checkRouterInfoFormat_statusRangeMatches() {
    RouteInfo routeInfo=new RouteInfo();
    routeInfo.setServiceName("name");
    routeInfo.setUrl("/url");
    routeInfo.setServers( new RouteServer[]{new RouteServer("10.74.148.88","8080")});
    routeInfo.setVisualRange("0");
    routeInfo.setControl("0");
    routeInfo.setStatus("3");
    
    try {
      RouteUtil.checkRouterInfoFormat(routeInfo);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  
  @Test
  public void test_checkRouterInfoFormat_useOwnUpstreamRangeMatches() {
    RouteInfo routeInfo=new RouteInfo();
    routeInfo.setServiceName("name");
    routeInfo.setUrl("/url");
    routeInfo.setServers( new RouteServer[]{new RouteServer("10.74.148.88","8080")});
    routeInfo.setVisualRange("0");
    routeInfo.setControl("0");
    routeInfo.setStatus("0");
    routeInfo.setUseOwnUpstream("3");
    
    try {
      RouteUtil.checkRouterInfoFormat(routeInfo);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  @Test
  public void test_checkRouterInfoFormat_ip() {
    RouteInfo routeInfo=new RouteInfo();
    routeInfo.setServiceName("name");
    routeInfo.setUrl("/url");
    routeInfo.setServers( new RouteServer[]{new RouteServer("10.74.148.88.6","8080")});
    routeInfo.setVisualRange("0");
    routeInfo.setControl("0");
    routeInfo.setStatus("0");
    routeInfo.setUseOwnUpstream("1");
    
    try {
      RouteUtil.checkRouterInfoFormat(routeInfo);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  
  @Test
  public void test_checkRouterInfoFormat_port() {
    RouteInfo routeInfo=new RouteInfo();
    routeInfo.setServiceName("name");
    routeInfo.setUrl("/url");
    routeInfo.setServers( new RouteServer[]{new RouteServer("10.74.148.88.6","757577")});
    routeInfo.setVisualRange("0");
    routeInfo.setControl("0");
    routeInfo.setStatus("0");
    routeInfo.setUseOwnUpstream("1");
    
    try {
      RouteUtil.checkRouterInfoFormat(routeInfo);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof UnprocessableEntityException);
    }
  }
  
  

  
  
 


}
