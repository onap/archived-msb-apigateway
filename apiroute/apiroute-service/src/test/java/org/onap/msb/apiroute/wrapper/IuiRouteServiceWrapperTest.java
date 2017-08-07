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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.api.IuiRouteInfo;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.api.exception.ExtendedNotFoundException;
import org.onap.msb.apiroute.wrapper.IuiRouteServiceWrapper;
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
public class IuiRouteServiceWrapperTest {
    private static IuiRouteServiceWrapper iuiRouteServiceWrapper;
    private static Comparator<IuiRouteInfo> iuiRouteComparator = null;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        iuiRouteServiceWrapper=IuiRouteServiceWrapper.getInstance();
        iuiRouteComparator = new Comparator<IuiRouteInfo>() {
          @Override
          public int compare(IuiRouteInfo o1, IuiRouteInfo o2) {
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
    public void test_getIuiRouteInstance_not_exist(){
      try {     
        iuiRouteServiceWrapper.getIuiRouteInstance("testForJunit","","","ip");
        Assert.fail("should not process to here.");          
        } 
        catch(Exception e){
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
           
        }
       
    }
    
    @Test
    public void test_getIuiRouteInstance(){
              
      IuiRouteInfo iuirouteInfo  = buildIuiRouteInfo();
          try {
              iuiRouteServiceWrapper.saveIuiRouteInstance4Rest(iuirouteInfo, "ip");
              IuiRouteInfo dbIuiRouteInfo=iuiRouteServiceWrapper.getIuiRouteInstance("testiui", "", "", "ip");
              Assert.assertEquals(iuirouteInfo,dbIuiRouteInfo );
          } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());
          }
       
    }
    
    @Test
    public void test_getAllIuiRouteInstances(){
      IuiRouteInfo iuirouteInfo = buildIuiRouteInfo();
      IuiRouteInfo iuirouteInfo2 = buildIuiRouteInfo2();
      List<IuiRouteInfo> expected = new ArrayList<>();
      expected.add(iuirouteInfo);
      expected.add(iuirouteInfo2);
      Collections.sort(expected, iuiRouteComparator);
      
      try {
        iuiRouteServiceWrapper.saveIuiRouteInstance4Rest(iuirouteInfo, "ip");
        iuiRouteServiceWrapper.saveIuiRouteInstance4Rest(iuirouteInfo2, "ip");

        
        
        PowerMockito.mockStatic(RouteUtil.class);
        PowerMockito.when(RouteUtil.getMutiRedisKey(RouteUtil.IUIROUTE, "ip")).thenReturn("msb:routing:iui:*");
        List<IuiRouteInfo> iuiRouterList=iuiRouteServiceWrapper.getAllIuiRouteInstances("ip");
        Collections.sort(iuiRouterList, iuiRouteComparator);
        
        Assert.assertEquals(expected,iuiRouterList);
        
      } catch (Exception e) {
        Assert.fail("throw exception means error occured!" + e.getMessage());
      }
      
    }
    
    @Test
    public void test_updateIuiRouteStatus(){
      IuiRouteInfo iuirouteInfo = buildIuiRouteInfo();
      try {
          iuiRouteServiceWrapper.saveIuiRouteInstance4Rest(iuirouteInfo, "ip");
          IuiRouteInfo dbIuirouteInfo=iuiRouteServiceWrapper.getIuiRouteInstance("testiui", "", "", "ip");
          Assert.assertEquals("1",dbIuirouteInfo.getStatus() );
          iuiRouteServiceWrapper.updateIuiRouteStatus("testiui","","","0", "ip");
          dbIuirouteInfo=iuiRouteServiceWrapper.getIuiRouteInstance("testiui", "", "", "ip");
          Assert.assertEquals("0",dbIuirouteInfo.getStatus() );
      } catch (Exception e) {
        Assert.fail("throw exception means error occured!" + e.getMessage());
      }
      
    }
    
   
    
    @Test
    public void test_deleteIuiRoute(){
      IuiRouteInfo iuirouteInfo2 = buildIuiRouteInfo2();
      try {
          iuiRouteServiceWrapper.saveIuiRouteInstance4Rest(iuirouteInfo2, "ip");
          IuiRouteInfo dbIuirouteInfo=iuiRouteServiceWrapper.getIuiRouteInstance("testiui2","","","ip");
          Assert.assertNotNull(dbIuirouteInfo);          
          
      } catch (Exception e) {
        Assert.fail("throw exception means error occured!" + e.getMessage());
      }
      try {
        iuiRouteServiceWrapper.deleteIuiRoute("testiui2","","","ip");
        iuiRouteServiceWrapper.getIuiRouteInstance("testiui2","","","ip");
      }
      catch(Exception e){
        Assert.assertTrue(e instanceof ExtendedNotFoundException);       
      }
    }
 
    
    private IuiRouteInfo buildIuiRouteInfo(){
      IuiRouteInfo iuirouteInfo = new IuiRouteInfo();
      iuirouteInfo.setServiceName("testiui");
      iuirouteInfo.setStatus("1");
      iuirouteInfo.setUrl("/iui/testiui");
      iuirouteInfo.setUseOwnUpstream("0");
      iuirouteInfo.setVisualRange("0");
      iuirouteInfo.setEnable_ssl(false);
      RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.88","8080")};
      iuirouteInfo.setServers(servers);
      return iuirouteInfo;
    }
    
    private IuiRouteInfo buildIuiRouteInfo2(){
      IuiRouteInfo iuirouteInfo = new IuiRouteInfo();
      iuirouteInfo.setServiceName("testiui2");
      iuirouteInfo.setStatus("1");
      iuirouteInfo.setUrl("/iui/testiui");
      iuirouteInfo.setUseOwnUpstream("0");
      iuirouteInfo.setVisualRange("1");
      iuirouteInfo.setEnable_ssl(true);
      RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.89","8080")};
      iuirouteInfo.setServers(servers);
      return iuirouteInfo;
    }
   
    
}
