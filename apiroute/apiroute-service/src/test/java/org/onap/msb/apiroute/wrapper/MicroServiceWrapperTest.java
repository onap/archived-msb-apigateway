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
 *  limitations under the License.
 ******************************************************************************/
package org.onap.msb.apiroute.wrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.api.Node;
import org.onap.msb.apiroute.api.exception.ExtendedNotFoundException;
import org.onap.msb.apiroute.api.exception.UnprocessableEntityException;
import org.onap.msb.apiroute.wrapper.MicroServiceWrapper;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.fiftyonred.mock_jedis.MockJedisPool;


@RunWith(PowerMockRunner.class)
@PrepareForTest({JedisUtil.class,RedisAccessWrapper.class})
@PowerMockIgnore( {"javax.management.*"})
public class MicroServiceWrapperTest {
  private static MicroServiceWrapper microServiceWrapper;
  private static Comparator<MicroServiceFullInfo> microServiceComparator = null;

  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    microServiceWrapper=MicroServiceWrapper.getInstance();
    microServiceComparator = new Comparator<MicroServiceFullInfo>() {
      @Override
      public int compare(MicroServiceFullInfo o1, MicroServiceFullInfo o2) {
          if (!o1.getServiceName().equals(o2.getServiceName()))
              return (o1.getServiceName()).compareTo(o2.getServiceName());            
          return 0;
      }
  };
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
  public void test_getMicroServiceFullInfo_not_exist(){
    try {     
      microServiceWrapper.getMicroServiceInstance("testForJunit","v1");
      Assert.fail("should not process to here.");          
      } 
      catch(Exception e){
          Assert.assertTrue(e instanceof ExtendedNotFoundException);
         
      }
     
  }
  
  @Test
  public void test_getMicroServiceFullInfo(){
            
    MicroServiceFullInfo microServiceFullInfo = buildMicroServiceFullInfo();
        try {
          microServiceWrapper.saveMicroServiceInstance(microServiceFullInfo, false, "", "");
          MicroServiceFullInfo dbMicroServiceFullInfo=microServiceWrapper.getMicroServiceInstance("testService", "v1");
            Assert.assertEquals(microServiceFullInfo,dbMicroServiceFullInfo );
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
     
  }
  
  @Test
  public void test_getAllMicroServiceInstances(){
    MicroServiceFullInfo microService = buildMicroServiceFullInfo();
    MicroServiceFullInfo microService2 = buildMicroServiceFullInfo2();
    List<MicroServiceFullInfo> expected = new ArrayList<>();
    expected.add(microService);
    expected.add(microService2);
    Collections.sort(expected, microServiceComparator);
    
    try {
      microServiceWrapper.saveMicroServiceInstance(microService, false, "", "");
      microServiceWrapper.saveMicroServiceInstance(microService2, false, "", "");
      
      List<MicroServiceFullInfo> microServiceList=microServiceWrapper.getAllMicroServiceInstances();
      Collections.sort(microServiceList, microServiceComparator);
      
      Assert.assertEquals(expected,microServiceList);
      
    } catch (Exception e) {
      Assert.fail("throw exception means error occured!" + e.getMessage());
    }
    
  }
  
  @Test
  public void test_updateMicroServiceStatus(){
    MicroServiceFullInfo microService = buildMicroServiceFullInfo();

    try {
      microServiceWrapper.saveMicroServiceInstance(microService, false, "", "");
      MicroServiceFullInfo dbMicroServiceFullInfo=microServiceWrapper.getMicroServiceInstance("testService", "v1");
        Assert.assertEquals("1",dbMicroServiceFullInfo.getStatus() );
        microServiceWrapper.updateMicroServiceStatus("testService","v1","0");
        dbMicroServiceFullInfo=microServiceWrapper.getMicroServiceInstance("testService", "v1");
        Assert.assertEquals("0",dbMicroServiceFullInfo.getStatus() );
    } catch (Exception e) {
      Assert.fail("throw exception means error occured!" + e.getMessage());
    }
    
  }
  
 
  
  @Test
  public void test_deleteMicroService(){
    MicroServiceFullInfo microService = buildMicroServiceFullInfo2();
    try {
        microServiceWrapper.saveMicroServiceInstance(microService, false, "", "");
        MicroServiceFullInfo dbMicroServiceFullInfo=microServiceWrapper.getMicroServiceInstance("testService2", "v1");
        Assert.assertNotNull(dbMicroServiceFullInfo);          
        
    } catch (Exception e) {
      Assert.fail("throw exception means error occured!" + e.getMessage());
    }
    try {
      microServiceWrapper.deleteMicroService("testService2","v1");
      microServiceWrapper.getMicroServiceInstance("testService2", "v1");
    }
    catch(Exception e){
      Assert.assertTrue(e instanceof ExtendedNotFoundException);       
    }
  }
  
  @Test
  public void test_deleteMicroServiceInstance(){
    
  
    //添加多版本服务
    MicroServiceFullInfo microService4v2 = buildMicroServiceFullInfo4version2();
    try {
        microServiceWrapper.saveMicroServiceInstance(microService4v2, false, "", "");       
    } catch (Exception e) {
      Assert.fail("throw exception means error occured!" + e.getMessage());
    }
    
    //删除不存在实例
    try {
      microServiceWrapper.deleteMicroServiceInstance("testService","v2","127.0.0.1","8989");
    }
    catch(Exception e){
      Assert.assertTrue(e instanceof ExtendedNotFoundException);       
    }
    
    try {
      //删除其中一个实例
      microServiceWrapper.deleteMicroServiceInstance("testService","v2","10.74.148.87","8080");
      MicroServiceFullInfo microService =microServiceWrapper.getMicroServiceInstance("testService", "v2");
      
      Set<Node> nodeSet=new HashSet<Node>();
      nodeSet.add(new Node("10.74.148.86","8080"));
      Assert.assertEquals(nodeSet, microService.getNodes());
      
      //删除服务
      microServiceWrapper.deleteMicroServiceInstance("testService","v2","10.74.148.86","8080");
    }
    catch(Exception e){
      Assert.fail("throw exception means error occured!" + e.getMessage());     
    }
    
    try {     
      microServiceWrapper.getMicroServiceInstance("testService","v2");
      Assert.fail("should not process to here.");          
      } 
      catch(Exception e){
          Assert.assertTrue(e instanceof ExtendedNotFoundException);
         
      }
  }
  
  @Test
  public void test_getAllVersion(){
    try {
      microServiceWrapper.saveMicroServiceInstance(buildMicroServiceFullInfo(), false, "", "");       
      microServiceWrapper.saveMicroServiceInstance(buildMicroServiceFullInfo4version2(), false, "", "");
      Set<String> versionSet=new HashSet<String>();
      versionSet.add("v1");
      versionSet.add("v2");
      Assert.assertEquals(versionSet,microServiceWrapper.getAllVersion("testService"));
      
    
    } catch (Exception e) {
    Assert.fail("throw exception means error occured!" + e.getMessage());
    }
    try {     
      microServiceWrapper.deleteMicroService4AllVersion("testService");
      Assert.assertEquals(0,microServiceWrapper.getAllVersion("testService").size());         
      } 
      catch(Exception e){
          Assert.assertTrue(e instanceof ExtendedNotFoundException);
         
      }
    
  }

  @Test
  public void test_getAllMicroServiceKey(){
    microServiceWrapper.saveMicroServiceInstance(buildMicroServiceFullInfo(), false, "", ""); 
    microServiceWrapper.saveMicroServiceInstance(buildMicroServiceFullInfo2(), false, "", ""); 
   Set<String> builder = new HashSet<String>();
    builder.add("testService");
    builder.add("testService2");
    Assert.assertEquals(builder,microServiceWrapper.getAllMicroServiceKey());

   
  }
  
  private MicroServiceFullInfo buildMicroServiceFullInfo(){
    MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
    microServiceFullInfo.setServiceName("testService");
    microServiceFullInfo.setVersion("v1");
    microServiceFullInfo.setStatus("1");
    microServiceFullInfo.setUrl("/testService/v1");
    microServiceFullInfo.setVisualRange("0");
    microServiceFullInfo.setProtocol("HTTP");
    microServiceFullInfo.setEnable_ssl(false);
    Set<Node> nodeSet = new HashSet<>();
    nodeSet.add(new Node("10.74.148.88","8080"));
    microServiceFullInfo.setNodes(nodeSet);
    
    return microServiceFullInfo;
  }
  
  private MicroServiceFullInfo buildMicroServiceFullInfo4version2(){
    MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
    microServiceFullInfo.setServiceName("testService");
    microServiceFullInfo.setVersion("v2");
    microServiceFullInfo.setStatus("1");
    microServiceFullInfo.setUrl("/testService/v1");
    microServiceFullInfo.setVisualRange("0");
    microServiceFullInfo.setProtocol("HTTP");
    microServiceFullInfo.setEnable_ssl(false);
    Set<Node> nodeSet = new HashSet<>();
    nodeSet.add(new Node("10.74.148.87","8080"));
    nodeSet.add(new Node("10.74.148.86","8080"));
    microServiceFullInfo.setNodes(nodeSet);
    
    return microServiceFullInfo;
  }
  
  private MicroServiceFullInfo buildMicroServiceFullInfo2(){
    MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
    microServiceFullInfo.setServiceName("testService2");
    microServiceFullInfo.setVersion("v1");
    microServiceFullInfo.setStatus("1");
    microServiceFullInfo.setUrl("/api/testService/v1");
    microServiceFullInfo.setVisualRange("1");
    microServiceFullInfo.setProtocol("REST");
    microServiceFullInfo.setEnable_ssl(true);
    Set<Node> nodeSet = new HashSet<>();
    nodeSet.add(new Node("10.74.148.89","8080"));
    microServiceFullInfo.setNodes(nodeSet);
    
    return microServiceFullInfo;
  }
  
  
}
