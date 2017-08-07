/**
 * Copyright 2016-2017 ZTE, Inc. and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.msb.apiroute.wrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.api.ApiRouteInfo;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.api.exception.ExtendedNotFoundException;
import org.onap.msb.apiroute.wrapper.ApiRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
import org.onap.msb.apiroute.wrapper.util.JacksonJsonUtil;
import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.fiftyonred.mock_jedis.MockJedisPool;


@RunWith(PowerMockRunner.class)
@PrepareForTest({JedisUtil.class,RouteUtil.class,RedisAccessWrapper.class})
@PowerMockIgnore( {"javax.management.*"})
public class ApiRouteServiceWrapperTest {
    private static ApiRouteServiceWrapper apiRouteServiceWrapper;
    private static Comparator<ApiRouteInfo> apiRouteComparator = null;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        apiRouteServiceWrapper=ApiRouteServiceWrapper.getInstance();
        apiRouteComparator = new Comparator<ApiRouteInfo>() {
          @Override
          public int compare(ApiRouteInfo o1, ApiRouteInfo o2) {
              if (!o1.getServiceName().equals(o2.getServiceName()))
                  return (o1.getServiceName()).compareTo(o2.getServiceName());
              if (!o1.getVersion().equals(o2.getVersion()))
                  return (o1.getVersion()).compareTo(o2.getVersion());
              return 0;
          }
      };
      
      PowerMockito.mockStatic(System.class);        
      PowerMockito.when(System.getenv("ROUTE_WAY")).thenReturn(null);      
      PowerMockito.when(System.getenv("ROUTE_WAY")).thenReturn("ip|domain");
      ConfigUtil.getInstance().initRouteWay();
    }
    
    @Before
    public void setUpBeforeTest() throws Exception {
        final JedisPool mockJedisPool = new MockJedisPool(new JedisPoolConfig(), "localhost");
        PowerMockito.mockStatic(JedisUtil.class);
        JedisUtil jedisUtil=PowerMockito.mock(JedisUtil.class);
        PowerMockito.when(jedisUtil.borrowJedisInstance()).thenReturn(mockJedisPool.getResource());

        PowerMockito.replace(PowerMockito.method(RedisAccessWrapper.class, "filterKeys")).with(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return mockJedisPool.getResource().keys((String) args[0]);
            }
        });
    }
    
    

    @Test
    public void test_getApiRouteInstance_not_exist(){
      try {     
        apiRouteServiceWrapper.getApiRouteInstance("testForJunit", "v1","","","ip");
        Assert.fail("should not process to here.");          
        } 
        catch(Exception e){
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }
       
    }
    
    @Test
    public void test_getApiRouteInstance(){
              
          ApiRouteInfo apirouteInfo = buildApiRouteInfo();
          try {
              apiRouteServiceWrapper.saveApiRouteInstance4Rest(apirouteInfo, "ip");
              ApiRouteInfo dbApirouteInfo=apiRouteServiceWrapper.getApiRouteInstance("testapi", "v1", "", "", "ip");
              Assert.assertEquals(apirouteInfo,dbApirouteInfo );
          } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());
          }
       
    }
    
    
    
    @Test
    public void test_getAllApiRouteInstances(){
      ApiRouteInfo apirouteInfo = buildApiRouteInfo();
      ApiRouteInfo apirouteInfo2 = buildApiRouteInfo2();
      List<ApiRouteInfo> expected = new ArrayList<>();
      expected.add(apirouteInfo);
      expected.add(apirouteInfo2);
      Collections.sort(expected, apiRouteComparator);
      
      try {
        apiRouteServiceWrapper.saveApiRouteInstance4Rest(apirouteInfo, "ip");
        apiRouteServiceWrapper.saveApiRouteInstance4Rest(apirouteInfo2, "ip");

        
        PowerMockito.mockStatic(RouteUtil.class);
        PowerMockito.when(RouteUtil.getMutiRedisKey(RouteUtil.APIROUTE, "ip")).thenReturn("msb:routing:api:*");
        List<ApiRouteInfo> apiRouterList=apiRouteServiceWrapper.getAllApiRouteInstances("ip");
        Collections.sort(apiRouterList, apiRouteComparator);
        
        Assert.assertEquals(expected,apiRouterList);
        
      } catch (Exception e) {
        Assert.fail("throw exception means error occured!" + e.getMessage());
      }
      
    }
    
    @Test
    public void test_updateApiRouteStatus(){
      ApiRouteInfo apirouteInfo = buildApiRouteInfo();
      try {
          apiRouteServiceWrapper.saveApiRouteInstance4Rest(apirouteInfo, "ip");
          ApiRouteInfo dbApirouteInfo=apiRouteServiceWrapper.getApiRouteInstance("testapi", "v1", "", "", "ip");
          Assert.assertEquals("1",dbApirouteInfo.getStatus() );
          apiRouteServiceWrapper.updateApiRouteStatus("testapi","v1","","","0", "ip");
          dbApirouteInfo=apiRouteServiceWrapper.getApiRouteInstance("testapi", "v1", "", "", "ip");
          Assert.assertEquals("0",dbApirouteInfo.getStatus() );
      } catch (Exception e) {
        Assert.fail("throw exception means error occured!" + e.getMessage());
      }
      
    }
    
   
    
    @Test
    public void test_deleteApiRoute(){
      ApiRouteInfo apirouteInfo2 = buildApiRouteInfo2();
      try {
          apiRouteServiceWrapper.saveApiRouteInstance4Rest(apirouteInfo2, "ip");
          ApiRouteInfo dbApirouteInfo=apiRouteServiceWrapper.getApiRouteInstance("testapi2", "null","","","ip");
          Assert.assertNotNull(dbApirouteInfo);          
          
      } catch (Exception e) {
        Assert.fail("throw exception means error occured!" + e.getMessage());
      }
      try {
        apiRouteServiceWrapper.deleteApiRoute("testapi2", "null","","","ip");
        apiRouteServiceWrapper.getApiRouteInstance("testapi2", "","","","ip");
      }
      catch(Exception e){
        Assert.assertTrue(e instanceof ExtendedNotFoundException);       
      }
    }
    
    @Test
    public void test_getAllApiDocs(){
      String[] paths=apiRouteServiceWrapper.getAllApiDocs();
      String[] expecteds_paths={"api-doc1.json","api-doc2.json"};
      Arrays.sort(expecteds_paths);
      Arrays.sort(paths);
      Assert.assertArrayEquals(expecteds_paths, paths);
    }
    
    @Test
    public void test_getAllrouteByJson(){
      ApiRouteInfo apirouteInfo = buildApiRouteInfo();
      try {
          apiRouteServiceWrapper.saveApiRouteInstance4Rest(apirouteInfo, "ip");
          
          ApiRouteInfo[] apirouteList={apirouteInfo};
          String expected_routeJson=JacksonJsonUtil.beanToJson(apirouteList);
          
          PowerMockito.mockStatic(RouteUtil.class);
          PowerMockito.when(RouteUtil.getMutiRedisKey(RouteUtil.APIROUTE, "ip")).thenReturn("msb:routing:api:*");
          PowerMockito.when(RouteUtil.getMutiRedisKey(RouteUtil.IUIROUTE, "ip")).thenReturn("msb:routing:iui:*");
          PowerMockito.when(RouteUtil.getMutiRedisKey(RouteUtil.CUSTOMROUTE, "ip")).thenReturn("msb:routing:custom:*");

          String allrouteJson= apiRouteServiceWrapper.getAllrouteByJson("ip");       
          Assert.assertEquals(expected_routeJson, allrouteJson);
      } catch (Exception e) {
        Assert.fail("throw exception means error occured!" + e.getMessage());
      }
      
      
    }
    
   
    
    private ApiRouteInfo buildApiRouteInfo(){
      ApiRouteInfo apirouteInfo = new ApiRouteInfo();
      apirouteInfo.setServiceName("testapi");
      apirouteInfo.setVersion("v1");
      apirouteInfo.setStatus("1");
      apirouteInfo.setUrl("/api/testapi/v1");
      apirouteInfo.setUseOwnUpstream("0");
      apirouteInfo.setVisualRange("0");
      apirouteInfo.setEnable_ssl(false);
      RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.88","8080")};
      apirouteInfo.setServers(servers);
      return apirouteInfo;
    }
    
    private ApiRouteInfo buildApiRouteInfo2(){
      ApiRouteInfo apirouteInfo = new ApiRouteInfo();
      apirouteInfo.setServiceName("testapi2");
      apirouteInfo.setVersion("");
      apirouteInfo.setStatus("1");
      apirouteInfo.setUrl("/api/testapi2/v1");
      apirouteInfo.setUseOwnUpstream("0");
      apirouteInfo.setVisualRange("1");
      apirouteInfo.setEnable_ssl(true);
      RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.89","8080")};
      apirouteInfo.setServers(servers);
      return apirouteInfo;
    }
   
    
}
