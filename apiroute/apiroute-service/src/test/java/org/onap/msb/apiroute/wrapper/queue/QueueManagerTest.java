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
package org.onap.msb.apiroute.wrapper.queue;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.SyncDataManager;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableService;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableServiceHealth;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.Service;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.onap.msb.apiroute.wrapper.consulextend.util.HttpTest;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.queue.QueueManager;
import org.onap.msb.apiroute.wrapper.queue.ServiceConsumer;
import org.onap.msb.apiroute.wrapper.queue.ServiceData;
import org.onap.msb.apiroute.wrapper.queue.ServiceListConsumer;
import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.fiftyonred.mock_jedis.MockJedisPool;
import com.orbitz.consul.model.health.ImmutableNode;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JedisUtil.class,RouteUtil.class,RedisAccessWrapper.class})
@PowerMockIgnore( {"javax.management.*"})
public class QueueManagerTest {
  private static QueueManager queueManager;
  
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    queueManager=QueueManager.getInstance();
    putInServiceListQueue();
    putInServiceQueue4Update();
    
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
  
  

  public void test_ServiceConsumer(){
    
    //start ServiceListConsumer
//    new Thread(new ServiceListConsumer(this),"ServiceListConsumerThread").start();
    
    //start Service Consumer
    int serviceQueneNum=RouteUtil.SERVICE_DATA_QUEUE_NUM;
    for(int i=0;i<serviceQueneNum;i++){
      new Thread(new ServiceConsumer(i),"ServiceConsumerThread"+i).start();
    }
    
  }
  

  public void test_ServiceListConsumer(){
    
    //start ServiceListConsumer
    new Thread(new ServiceListConsumer(),"ServiceListConsumerThread").start();
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    putInServiceQueue4Delete();
  }
  
  

  private static  void putInServiceListQueue(){
    ServiceData<HttpEntity> data=new ServiceData<HttpEntity>();
    data.setDataType(ServiceData.DataType.service_list);
        
    BasicHttpEntity entity = new BasicHttpEntity();
    InputStream content = HttpTest.class.getResourceAsStream("serviceslist.json");
    entity.setContent(content);
    data.setData(entity);
    
    try {
      queueManager.putIn(data);
    } catch (Exception e) {      
      Assert.assertTrue(e instanceof InterruptedException);
    }
  }
  
  private static void putInServiceQueue4Update(){
    ServiceData<List<ServiceHealth>> data=new ServiceData<List<ServiceHealth>>();
    data.setDataType(ServiceData.DataType.service);
    data.setOperate(ServiceData.Operate.delete);
    
    List<String> tagList = new ArrayList<String>();
    tagList.add("\"base\":{\"protocol\":\"REST\",\"version\":\"v1\",\"url\":\"/api/msbtest/v1\"}");
    tagList
        .add("\"labels\":{\"visualRange\":\"0\",\"network_plane_type\":\"net\",\"customLabel\":\"custom\"}");
    tagList.add("\"ns\":{\"namespace\":\"ns1\"}");

    Service service =
        ImmutableService.builder().id("id").port(8686).address("10.74.165.246").service("msbtest")
            .addAllTags(tagList).createIndex(0).modifyIndex(0).build();
    ServiceHealth serviceHealth =
        ImmutableServiceHealth.builder().service(service)
            .node(ImmutableNode.builder().node("server").address("192.168.1.98").build()).build();
    List<ServiceHealth> serviceHealthList = new ArrayList<ServiceHealth>();
    serviceHealthList.add(serviceHealth);
    
    data.setData(serviceHealthList);
    
    try {
      queueManager.putIn(data);
    } catch (Exception e) {      
      Assert.assertTrue(e instanceof InterruptedException);
    }
  }
  
  private static void putInServiceQueue4Delete(){
    ServiceData<List<ServiceHealth>> data=new ServiceData<List<ServiceHealth>>();
    data.setDataType(ServiceData.DataType.service);
    data.setOperate(ServiceData.Operate.update);
    
    List<String> tagList = new ArrayList<String>();
    tagList.add("\"base\":{\"protocol\":\"REST\",\"version\":\"v1\",\"url\":\"/api/msbtest/v1\"}");
    tagList
        .add("\"labels\":{\"visualRange\":\"0\",\"network_plane_type\":\"net\",\"customLabel\":\"custom\"}");
    tagList.add("\"ns\":{\"namespace\":\"ns1\"}");

    Service service =
        ImmutableService.builder().id("id").port(8686).address("10.74.165.246").service("msbtest")
            .addAllTags(tagList).createIndex(0).modifyIndex(0).build();
    ServiceHealth serviceHealth =
        ImmutableServiceHealth.builder().service(service)
            .node(ImmutableNode.builder().node("server").address("192.168.1.98").build()).build();
    List<ServiceHealth> serviceHealthList = new ArrayList<ServiceHealth>();
    serviceHealthList.add(serviceHealth);
    
    data.setData(serviceHealthList);
    
    try {
      queueManager.putIn(data);
    } catch (Exception e) {      
      Assert.assertTrue(e instanceof InterruptedException);
    }
  }
  
  
}
