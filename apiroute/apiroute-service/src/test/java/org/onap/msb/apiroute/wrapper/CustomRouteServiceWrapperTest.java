/**
 * Copyright 2016 ZTE, Inc. and others.
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.api.CustomRouteInfo;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.api.exception.ExtendedNotFoundException;
import org.onap.msb.apiroute.wrapper.CustomRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
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
public class CustomRouteServiceWrapperTest {

  private static CustomRouteServiceWrapper customRouteServiceWrapper;
  private static Comparator<CustomRouteInfo> customRouteComparator = null;
  
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        customRouteServiceWrapper=CustomRouteServiceWrapper.getInstance();
        customRouteComparator = new Comparator<CustomRouteInfo>() {
          @Override
          public int compare(CustomRouteInfo o1, CustomRouteInfo o2) {
              if (!o1.getServiceName().equals(o2.getServiceName()))
                  return (o1.getServiceName()).compareTo(o2.getServiceName());            
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
    public void test_getCustomRouteInstance_not_exist(){
      try {     
        customRouteServiceWrapper.getCustomRouteInstance("/testForJunit","","","ip");
        Assert.fail("should not process to here.");          
        } 
        catch(Exception e){
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
           
        }
       
    }
    
    @Test
    public void test_getCustomRouteInstance(){
              
      CustomRouteInfo customrouteInfo = buildCustomRouteInfo();
          try {
            customRouteServiceWrapper.saveCustomRouteInstance4Rest(customrouteInfo, "ip");
            CustomRouteInfo dbCustomRouteInfo=customRouteServiceWrapper.getCustomRouteInstance("/testcustom", "", "", "ip");
              Assert.assertEquals(customrouteInfo,dbCustomRouteInfo );
          } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());
          }
       
    }

    @Test
    public void test_getAllCustomRouteInstances(){
      CustomRouteInfo customrouteInfo = buildCustomRouteInfo();
      CustomRouteInfo customrouteInfo2 = buildCustomRouteInfo2();
      List<CustomRouteInfo> expected = new ArrayList<>();
      expected.add(customrouteInfo);
      expected.add(customrouteInfo2);
      Collections.sort(expected, customRouteComparator);
      
      try {
        customRouteServiceWrapper.saveCustomRouteInstance4Rest(customrouteInfo, "ip");
        customRouteServiceWrapper.saveCustomRouteInstance4Rest(customrouteInfo2, "ip");
        
        PowerMockito.mockStatic(RouteUtil.class);
        PowerMockito.when(RouteUtil.getMutiRedisKey(RouteUtil.CUSTOMROUTE, "ip")).thenReturn("msb:routing:custom:*");
        List<CustomRouteInfo> customRouterList=customRouteServiceWrapper.getAllCustomRouteInstances("ip");
        Collections.sort(customRouterList, customRouteComparator);
        
        Assert.assertEquals(expected,customRouterList);
        
      } catch (Exception e) {
        Assert.fail("throw exception means error occured!" + e.getMessage());
      }
      
    }
    
    @Test
    public void test_updateCustomRouteStatus(){
      CustomRouteInfo customrouteInfo = buildCustomRouteInfo();
      try {
          customRouteServiceWrapper.saveCustomRouteInstance4Rest(customrouteInfo, "ip");
          CustomRouteInfo dbCustomrouteInfo=customRouteServiceWrapper.getCustomRouteInstance("/testcustom", "", "", "ip");
          Assert.assertEquals("1",dbCustomrouteInfo.getStatus() );
          customRouteServiceWrapper.updateCustomRouteStatus("/testcustom","","","0", "ip");
          dbCustomrouteInfo=customRouteServiceWrapper.getCustomRouteInstance("/testcustom", "", "", "ip");
          Assert.assertEquals("0",dbCustomrouteInfo.getStatus() );
      } catch (Exception e) {
        Assert.fail("throw exception means error occured!" + e.getMessage());
      }
      
    }
    
   
    
    @Test
    public void test_deleteCustomRoute(){
      CustomRouteInfo customrouteInfo2 = buildCustomRouteInfo2();
      try {
          customRouteServiceWrapper.saveCustomRouteInstance4Rest(customrouteInfo2, "ip");
          CustomRouteInfo dbCustomrouteInfo=customRouteServiceWrapper.getCustomRouteInstance("/testcustom2","","","ip");
          Assert.assertNotNull(dbCustomrouteInfo);          
          
      } catch (Exception e) {
        Assert.fail("throw exception means error occured!" + e.getMessage());
      }
      try {
        customRouteServiceWrapper.deleteCustomRoute("/testcustom2","","","ip");
        customRouteServiceWrapper.getCustomRouteInstance("/testcustom2","","","ip");
      }
      catch(Exception e){
        Assert.assertTrue(e instanceof ExtendedNotFoundException);       
      }
    }
 
    
    private CustomRouteInfo buildCustomRouteInfo(){
      CustomRouteInfo customrouteInfo = new CustomRouteInfo();
      customrouteInfo.setServiceName("/testcustom");
      customrouteInfo.setStatus("1");
      customrouteInfo.setUrl("/custom/testcustom");
      customrouteInfo.setUseOwnUpstream("0");
      customrouteInfo.setVisualRange("0");
      customrouteInfo.setEnable_ssl(false);
      RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.88","8080")};
      customrouteInfo.setServers(servers);
      return customrouteInfo;
    }
    
    private CustomRouteInfo buildCustomRouteInfo2(){
      CustomRouteInfo customrouteInfo = new CustomRouteInfo();
      customrouteInfo.setServiceName("/testcustom2");
      customrouteInfo.setStatus("1");
      customrouteInfo.setUrl("/custom/testcustom");
      customrouteInfo.setUseOwnUpstream("0");
      customrouteInfo.setVisualRange("1");
      customrouteInfo.setEnable_ssl(true);
      RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.89","8080")};
      customrouteInfo.setServers(servers);
      return customrouteInfo;
    }
   
}
