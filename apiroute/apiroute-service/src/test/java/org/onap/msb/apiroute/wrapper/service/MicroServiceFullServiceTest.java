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
package org.onap.msb.apiroute.wrapper.service;

import com.fiftyonred.mock_jedis.MockJedisPool;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.api.Node;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.service.MicroServiceFullService;
import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.onap.msb.apiroute.wrapper.util.MicroServiceUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JedisUtil.class,RedisAccessWrapper.class})
@PowerMockIgnore( {"javax.management.*"})
public class MicroServiceFullServiceTest {
    private static MicroServiceFullService microServiceFullService = null;
    private static Comparator<MicroServiceFullInfo> serviceComparator = null;
    @BeforeClass
    public static void setUp() throws Exception{
        microServiceFullService = MicroServiceFullService.getInstance();
        serviceComparator = new Comparator<MicroServiceFullInfo>() {
            @Override
            public int compare(MicroServiceFullInfo o1, MicroServiceFullInfo o2) {
                if (!o1.getServiceName().equals(o2.getServiceName()))
                    return (o1.getServiceName()).compareTo(o2.getServiceName());
                if (!o1.getVersion().equals(o2.getVersion()))
                    return (o1.getVersion()).compareTo(o2.getVersion());
                return 0;
            }
        };
    }
    @Before
    public void setUpBeforeTest() throws Exception {
        final JedisPool mockJedisPool = new MockJedisPool(new JedisPoolConfig(), "localhost");
        PowerMockito.mockStatic(JedisUtil.class);
        JedisUtil jedisUtil=PowerMockito.mock(JedisUtil.class);
        when(jedisUtil.borrowJedisInstance()).thenReturn(mockJedisPool.getResource());

        PowerMockito.replace(PowerMockito.method(RedisAccessWrapper.class, "filterKeys")).with(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return mockJedisPool.getResource().keys((String) args[0]);
            }
        });
    }

    @Test
    public void testExistsMicroServiceInstance_notExist(){
        try {
            assertFalse(microServiceFullService.existsMicroServiceInstance("notExist", "v1"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }
    @Test
    public void testExistsMicroServiceInstance_Exist(){
        MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
        microServiceFullInfo.setServiceName("testService");
        microServiceFullInfo.setVersion("v1");
        microServiceFullInfo.setStatus("1");
        microServiceFullInfo.setUrl("/testService/v1");
        microServiceFullInfo.setVisualRange("0");
        microServiceFullInfo.setProtocol("http");
        microServiceFullInfo.setEnable_ssl(false);
        Set<Node> nodeSet = new HashSet<>();
        nodeSet.add(new Node("10.74.148.88","8080"));
        microServiceFullInfo.setNodes(nodeSet);
        try {
            assertFalse(microServiceFullService.existsMicroServiceInstance("testService", "v1"));
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo);
            assertTrue(microServiceFullService.existsMicroServiceInstance("testService", "v1"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }


    @Test
    public void testSaveMicroServiceInfo2Redis(){
        MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
        microServiceFullInfo.setServiceName("testService");
        microServiceFullInfo.setVersion("v1");
        microServiceFullInfo.setStatus("1");
        microServiceFullInfo.setUrl("/testService/v1");
        microServiceFullInfo.setVisualRange("0");
        microServiceFullInfo.setProtocol("http");
        microServiceFullInfo.setEnable_ssl(false);
        Set<Node> nodeSet = new HashSet<>();
        nodeSet.add(new Node("10.74.148.88","8080"));
        microServiceFullInfo.setNodes(nodeSet);
        try {
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo);
            MicroServiceFullInfo actual = microServiceFullService.getMicroServiceInstance("testService", "v1");
            assertEquals(microServiceFullInfo, actual);
        } catch (Exception e) {
            e.printStackTrace();
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testDeleteMicroService(){
        MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
        microServiceFullInfo.setServiceName("testService");
        microServiceFullInfo.setVersion("v1");
        microServiceFullInfo.setStatus("1");
        microServiceFullInfo.setUrl("/testService/v1");
        microServiceFullInfo.setVisualRange("0");
        microServiceFullInfo.setProtocol("http");
        microServiceFullInfo.setEnable_ssl(false);
        Set<Node> nodeSet = new HashSet<>();
        nodeSet.add(new Node("10.74.148.88","8080"));
        microServiceFullInfo.setNodes(nodeSet);
        try {
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo);
            assertTrue(microServiceFullService.existsMicroServiceInstance("testService", "v1"));
            microServiceFullService.deleteMicroService("testService","v1");
            assertFalse(microServiceFullService.existsMicroServiceInstance("testService", "v1"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }


    @Test
    public void testUpdateMicroServiceStatus(){
        MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
        microServiceFullInfo.setServiceName("testService");
        microServiceFullInfo.setVersion("v1");
        microServiceFullInfo.setStatus("1");
        microServiceFullInfo.setUrl("/testService/v1");
        microServiceFullInfo.setVisualRange("0");
        microServiceFullInfo.setProtocol("http");
        microServiceFullInfo.setEnable_ssl(false);
        Set<Node> nodeSet = new HashSet<>();
        nodeSet.add(new Node("10.74.148.88","8080"));
        microServiceFullInfo.setNodes(nodeSet);
        try {
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo);
            assertEquals("1", microServiceFullService.getMicroServiceInstance("testService","v1").getStatus());
            microServiceFullService.updateMicroServiceStatus("testService", "v1", "0");
            assertEquals("0", microServiceFullService.getMicroServiceInstance("testService", "v1").getStatus());
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testGetAllMicroServiceKey(){
        MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
        microServiceFullInfo.setServiceName("testService");
        microServiceFullInfo.setVersion("v1");
        microServiceFullInfo.setStatus("1");
        microServiceFullInfo.setUrl("/testService/v1");
        microServiceFullInfo.setVisualRange("0");
        microServiceFullInfo.setProtocol("http");
        microServiceFullInfo.setEnable_ssl(false);
        Set<Node> nodeSet = new HashSet<>();
        nodeSet.add(new Node("10.74.148.88","8080"));
        microServiceFullInfo.setNodes(nodeSet);

        MicroServiceFullInfo microServiceFullInfo2 = new MicroServiceFullInfo();
        microServiceFullInfo2.setServiceName("testService2");
        microServiceFullInfo2.setVersion("");
        microServiceFullInfo2.setStatus("1");
        microServiceFullInfo2.setUrl("/testService2");
        microServiceFullInfo2.setVisualRange("0");
        microServiceFullInfo2.setProtocol("http");
        microServiceFullInfo2.setEnable_ssl(false);
        Set<Node> nodeSet2 = new HashSet<>();
        nodeSet2.add(new Node("10.74.148.88","8081"));
        microServiceFullInfo2.setNodes(nodeSet2);

        MicroServiceFullInfo microServiceFullInfo3 = new MicroServiceFullInfo();
        microServiceFullInfo3.setServiceName("testService");
        microServiceFullInfo3.setVersion("v2");
        microServiceFullInfo3.setStatus("1");
        microServiceFullInfo3.setUrl("/testService/v2");
        microServiceFullInfo3.setVisualRange("0");
        microServiceFullInfo3.setProtocol("http");
        microServiceFullInfo3.setEnable_ssl(false);
        Set<Node> nodeSet3 = new HashSet<>();
        nodeSet3.add(new Node("10.74.148.89","8080"));
        microServiceFullInfo3.setNodes(nodeSet3);

        try {
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo);
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo2);
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo3);

            Set<String> result = microServiceFullService.getAllMicroServiceKey();
            final Set<String> expected =new HashSet<String>();
            expected.add("testService");
            expected.add("testService2");

            assertEquals(expected, result);
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }

    }

    @Test
    public void testGetAllVersionsOfTheService(){
        MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
        microServiceFullInfo.setServiceName("testService");
        microServiceFullInfo.setVersion("v1");
        microServiceFullInfo.setStatus("1");
        microServiceFullInfo.setUrl("/testService/v1");
        microServiceFullInfo.setVisualRange("0");
        microServiceFullInfo.setProtocol("http");
        microServiceFullInfo.setEnable_ssl(false);
        Set<Node> nodeSet = new HashSet<>();
        nodeSet.add(new Node("10.74.148.88","8080"));
        microServiceFullInfo.setNodes(nodeSet);

        MicroServiceFullInfo microServiceFullInfo2 = new MicroServiceFullInfo();
        microServiceFullInfo2.setServiceName("testService2");
        microServiceFullInfo2.setVersion("");
        microServiceFullInfo2.setStatus("1");
        microServiceFullInfo2.setUrl("/testService2");
        microServiceFullInfo2.setVisualRange("0");
        microServiceFullInfo2.setProtocol("http");
        microServiceFullInfo2.setEnable_ssl(false);
        Set<Node> nodeSet2 = new HashSet<>();
        nodeSet2.add(new Node("10.74.148.88","8081"));
        microServiceFullInfo2.setNodes(nodeSet2);

        MicroServiceFullInfo microServiceFullInfo3 = new MicroServiceFullInfo();
        microServiceFullInfo3.setServiceName("testService");
        microServiceFullInfo3.setVersion("v2");
        microServiceFullInfo3.setStatus("1");
        microServiceFullInfo3.setUrl("/testService/v2");
        microServiceFullInfo3.setVisualRange("0");
        microServiceFullInfo3.setProtocol("http");
        microServiceFullInfo3.setEnable_ssl(false);
        Set<Node> nodeSet3 = new HashSet<>();
        nodeSet3.add(new Node("10.74.148.89","8080"));
        microServiceFullInfo3.setNodes(nodeSet3);

        try {
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo);
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo3);
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo2);


            List<MicroServiceFullInfo> result = microServiceFullService.getAllVersionsOfTheService("testService");

            List<MicroServiceFullInfo> expected = new ArrayList<>();
            expected.add(microServiceFullInfo);
            expected.add(microServiceFullInfo3);

            Collections.sort(expected,serviceComparator);
            Collections.sort(result,serviceComparator);
            assertEquals(expected, result);
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }

    }

    @Test
    public void testGetAllMicroServicesInstances(){
        MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
        microServiceFullInfo.setServiceName("testService");
        microServiceFullInfo.setVersion("v1");
        microServiceFullInfo.setStatus("1");
        microServiceFullInfo.setUrl("/testService/v1");
        microServiceFullInfo.setVisualRange("0");
        microServiceFullInfo.setProtocol("http");
        microServiceFullInfo.setEnable_ssl(false);
        Set<Node> nodeSet = new HashSet<>();
        nodeSet.add(new Node("10.74.148.88","8080"));
        microServiceFullInfo.setNodes(nodeSet);

        MicroServiceFullInfo microServiceFullInfo2 = new MicroServiceFullInfo();
        microServiceFullInfo2.setServiceName("testService2");
        microServiceFullInfo2.setVersion("");
        microServiceFullInfo2.setStatus("1");
        microServiceFullInfo2.setUrl("/testService/v1");
        microServiceFullInfo2.setVisualRange("0");
        microServiceFullInfo2.setProtocol("http");
        microServiceFullInfo2.setEnable_ssl(true);
        Set<Node> nodeSet2 = new HashSet<>();
        nodeSet2.add(new Node("10.74.148.89","8080"));
        microServiceFullInfo2.setNodes(nodeSet2);

        try {
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo);
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo2);

            List<MicroServiceFullInfo> expected = new ArrayList<MicroServiceFullInfo>();
            expected.add(microServiceFullInfo);
            expected.add(microServiceFullInfo2);
            List<MicroServiceFullInfo> result = microServiceFullService.getAllMicroServiceInstances();
            Collections.sort(expected, serviceComparator);
            Collections.sort(result,serviceComparator );
            assertEquals(expected, result);
        } catch (Exception e) {
            e.printStackTrace();
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }


    @Test
    public void testDeleteMultiMicroService(){
        MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
        microServiceFullInfo.setServiceName("testService");
        microServiceFullInfo.setVersion("v1");
        microServiceFullInfo.setStatus("1");
        microServiceFullInfo.setUrl("/testService/v1");
        microServiceFullInfo.setVisualRange("0");
        microServiceFullInfo.setProtocol("http");
        microServiceFullInfo.setEnable_ssl(false);
        Set<Node> nodeSet = new HashSet<>();
        nodeSet.add(new Node("10.74.148.88","8080"));
        microServiceFullInfo.setNodes(nodeSet);


        MicroServiceFullInfo microServiceFullInfo3 = new MicroServiceFullInfo();
        microServiceFullInfo3.setServiceName("testService");
        microServiceFullInfo3.setVersion("v2");
        microServiceFullInfo3.setStatus("1");
        microServiceFullInfo3.setUrl("/testService/v2");
        microServiceFullInfo3.setVisualRange("0");
        microServiceFullInfo3.setProtocol("http");
        microServiceFullInfo3.setEnable_ssl(false);
        Set<Node> nodeSet3 = new HashSet<>();
        nodeSet3.add(new Node("10.74.148.89","8080"));
        microServiceFullInfo3.setNodes(nodeSet3);

        try {
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo);
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo3);
            //two versions of testservice exist
            assertEquals(2,microServiceFullService.getAllVersionsOfTheService("testService").size());
            //delete all versions of testservice
            long size = microServiceFullService.deleteMultiMicroService(MicroServiceUtil.getPrefixedKey("testService","*"));
            //after delete,no version exist
            assertEquals(0,microServiceFullService.getAllVersionsOfTheService("testService").size());
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void tesGetMicroServiceInstance_notExist(){
        try {
            assertNull(microServiceFullService.getMicroServiceInstance("notExist","v1"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }

    }
    @Test
    public void tesExistsGetUpdateDeleteMicroServiceStatus_versionNull(){
        MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
        microServiceFullInfo.setServiceName("testService");
        microServiceFullInfo.setVersion("");
        microServiceFullInfo.setStatus("1");
        microServiceFullInfo.setUrl("/testService/v1");
        microServiceFullInfo.setVisualRange("0");
        microServiceFullInfo.setProtocol("http");
        microServiceFullInfo.setEnable_ssl(false);
        Set<Node> nodeSet = new HashSet<>();
        nodeSet.add(new Node("10.74.148.88","8080"));
        microServiceFullInfo.setNodes(nodeSet);
        try {
            //test null
            assertFalse(microServiceFullService.existsMicroServiceInstance("testService", "null"));
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo);
            assertEquals("1", microServiceFullService.getMicroServiceInstance("testService","null").getStatus());
            microServiceFullService.updateMicroServiceStatus("testService", "null", "0");
            assertEquals("0", microServiceFullService.getMicroServiceInstance("testService", "null").getStatus());
            microServiceFullService.deleteMicroService("testService","null");
            assertFalse(microServiceFullService.existsMicroServiceInstance("testService", "null"));


            //test String "null"
            assertFalse(microServiceFullService.existsMicroServiceInstance("testService", null));
            microServiceFullService.saveMicroServiceInfo2Redis(microServiceFullInfo);
            assertEquals("1", microServiceFullService.getMicroServiceInstance("testService",null).getStatus());
            microServiceFullService.updateMicroServiceStatus("testService", null, "0");
            assertEquals("0", microServiceFullService.getMicroServiceInstance("testService", null).getStatus());
            microServiceFullService.deleteMicroService("testService",null);
            assertFalse(microServiceFullService.existsMicroServiceInstance("testService", null));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test(expected = Exception.class)
    public void tesSaveMicroService_null() throws Exception {
        microServiceFullService.saveMicroServiceInfo2Redis(null);
    }
}
