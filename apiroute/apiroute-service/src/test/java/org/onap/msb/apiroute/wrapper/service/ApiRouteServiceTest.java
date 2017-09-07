/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.onap.msb.apiroute.wrapper.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.api.ApiRouteInfo;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fiftyonred.mock_jedis.MockJedisPool;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JedisUtil.class, RedisAccessWrapper.class})
@PowerMockIgnore({"javax.management.*"})
public class ApiRouteServiceTest {
    private static ApiRouteService apiRouteService = null;
    private static Comparator<ApiRouteInfo> apiRouteComparator = null;

    @BeforeClass
    public static void setUp() throws Exception {
        apiRouteService = ApiRouteService.getInstance();
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
    }

    @Before
    public void setUpBeforeTest() throws Exception {
        final JedisPool mockJedisPool = new MockJedisPool(new JedisPoolConfig(), "localhost");
        PowerMockito.mockStatic(JedisUtil.class);
        JedisUtil jedisUtil = PowerMockito.mock(JedisUtil.class);
        PowerMockito.when(jedisUtil.borrowJedisInstance()).thenReturn(mockJedisPool.getResource());

        PowerMockito.replace(PowerMockito.method(RedisAccessWrapper.class, "filterKeys")).with(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return mockJedisPool.getResource().keys((String) args[0]);
            }
        });
    }

    @Test
    public void testGetApiRouteInstance_key_not_exist() {
        try {
            assertNull(apiRouteService.getApiRouteInstance("msb:routing:api:notexistservice:v1"));
        } catch (Exception e) {
            assert false : "throw exception means error occured!" + e.getMessage();
        }
    }

    @Test
    public void testGetApiRouteInstance_key_exist() {
        ApiRouteInfo apirouteInfo = new ApiRouteInfo();
        apirouteInfo.setServiceName("testapi");
        apirouteInfo.setVersion("v1");
        apirouteInfo.setStatus("1");
        apirouteInfo.setUrl("/api/testapi/v1");
        apirouteInfo.setUseOwnUpstream("0");
        apirouteInfo.setVisualRange("0");
        apirouteInfo.setEnable_ssl(false);
        RouteServer[] servers = new RouteServer[] {new RouteServer("10.74.148.88", "8080")};
        apirouteInfo.setServers(servers);
        try {
            apiRouteService.saveApiRouteService2Redis(apirouteInfo, "msb:routing:api:testapi:v1");
            assertEquals(apirouteInfo, apiRouteService.getApiRouteInstance("msb:routing:api:testapi:v1"));
        } catch (Exception e) {
            assert false : "throw exception means error occured!" + e.getMessage();
        }
    }

    @Test
    public void testSaveApiRouteService2Redis() {
        ApiRouteInfo apirouteInfo = new ApiRouteInfo();
        apirouteInfo.setServiceName("testapi");
        apirouteInfo.setVersion("v1");
        apirouteInfo.setStatus("1");
        apirouteInfo.setUrl("/api/testapi/v1");
        apirouteInfo.setUseOwnUpstream("0");
        apirouteInfo.setVisualRange("0");
        apirouteInfo.setEnable_ssl(true);
        RouteServer[] servers = new RouteServer[] {new RouteServer("10.74.148.88", "8080")};
        apirouteInfo.setServers(servers);
        try {
            apiRouteService.saveApiRouteService2Redis(apirouteInfo, "msb:routing:api:testapi:v1");
            assertEquals(apirouteInfo, apiRouteService.getApiRouteInstance("msb:routing:api:testapi:v1"));
        } catch (Exception e) {
            assert false : "throw exception means error occured!" + e.getMessage();
        }
    }

    @Test
    public void testSaveApiRouteService2Redis2() {
        ApiRouteInfo apirouteInfo = new ApiRouteInfo();
        apirouteInfo.setServiceName("test26msb");
        apirouteInfo.setVersion("v1");
        apirouteInfo.setStatus("1");
        apirouteInfo.setUrl("/api/microservices/v1");
        apirouteInfo.setUseOwnUpstream("0");
        apirouteInfo.setVisualRange("0");
        apirouteInfo.setEnable_ssl(true);
        RouteServer[] servers = new RouteServer[] {new RouteServer("10.74.151.26", "443")};
        apirouteInfo.setServers(servers);
        try {
            apiRouteService.saveApiRouteService2Redis(apirouteInfo, "msb:routing:api:test26msb:v1");
            assertEquals(apirouteInfo, apiRouteService.getApiRouteInstance("msb:routing:api:test26msb:v1"));
        } catch (Exception e) {
            assert false : "throw exception means error occured!" + e.getMessage();
        }
    }

    @Test
    public void testDeleteApiRouteService2Redis() {
        ApiRouteInfo apirouteInfo = new ApiRouteInfo();
        apirouteInfo.setServiceName("testapi");
        apirouteInfo.setVersion("v1");
        apirouteInfo.setStatus("1");
        apirouteInfo.setUrl("/api/testapi/v1");
        apirouteInfo.setUseOwnUpstream("0");
        apirouteInfo.setVisualRange("0");
        apirouteInfo.setEnable_ssl(false);
        RouteServer[] servers = new RouteServer[] {new RouteServer("10.74.148.88", "8080")};
        apirouteInfo.setServers(servers);
        try {
            apiRouteService.saveApiRouteService2Redis(apirouteInfo, "msb:routing:api:testapi:v1");
            assertNotNull(apiRouteService.getApiRouteInstance("msb:routing:api:testapi:v1"));
            apiRouteService.deleteApiRouteService2Redis("msb:routing:api:testapi:v1");
            assertNull(apiRouteService.getApiRouteInstance("msb:routing:api:testapi:v1"));
        } catch (Exception e) {
            assert false : "throw exception means error occured!" + e.getMessage();
        }
    }

    @Test
    public void testUpdateApiRouteStatus2Redis() {
        ApiRouteInfo apirouteInfo = new ApiRouteInfo();
        apirouteInfo.setServiceName("testapi");
        apirouteInfo.setVersion("v1");
        apirouteInfo.setStatus("1");
        apirouteInfo.setUrl("/api/testapi/v1");
        apirouteInfo.setUseOwnUpstream("0");
        apirouteInfo.setVisualRange("0");
        apirouteInfo.setEnable_ssl(true);
        RouteServer[] servers = new RouteServer[] {new RouteServer("10.74.148.88", "8080")};
        apirouteInfo.setServers(servers);
        try {
            apiRouteService.saveApiRouteService2Redis(apirouteInfo, "msb:routing:api:testapi:v1");
            assertEquals("1", apiRouteService.getApiRouteInstance("msb:routing:api:testapi:v1").getStatus());
            apiRouteService.updateApiRouteStatus2Redis("msb:routing:api:testapi:v1", "0");
            assertEquals("0", apiRouteService.getApiRouteInstance("msb:routing:api:testapi:v1").getStatus());
        } catch (Exception e) {
            assert false : "throw exception means error occured!" + e.getMessage();
        }
    }

    @Test
    public void testGetMultiApiRouteInstances() throws Exception {
        ApiRouteInfo apirouteInfo = new ApiRouteInfo();
        apirouteInfo.setServiceName("testapi");
        apirouteInfo.setVersion("v1");
        apirouteInfo.setStatus("1");
        apirouteInfo.setUrl("/api/testapi/v1");
        apirouteInfo.setUseOwnUpstream("0");
        apirouteInfo.setVisualRange("0");
        apirouteInfo.setEnable_ssl(false);
        apirouteInfo.setServers(new RouteServer[] {new RouteServer("10.74.148.88", "8080")});

        ApiRouteInfo apirouteInfo2 = new ApiRouteInfo();
        apirouteInfo2.setServiceName("testapi");
        apirouteInfo2.setVersion("v2");
        apirouteInfo2.setStatus("0");
        apirouteInfo2.setUrl("/api/testapi/v2");
        apirouteInfo2.setUseOwnUpstream("0");
        apirouteInfo2.setVisualRange("0");;
        apirouteInfo.setEnable_ssl(true);
        apirouteInfo2.setServers(new RouteServer[] {new RouteServer("10.74.148.88", "8088")});

        apiRouteService.saveApiRouteService2Redis(apirouteInfo, "msb:routing:api:testapi:v1");
        apiRouteService.saveApiRouteService2Redis(apirouteInfo2, "msb:routing:api:testapi:v2");

        List<ApiRouteInfo> expected = new ArrayList<>();
        expected.add(apirouteInfo);
        expected.add(apirouteInfo2);
        Collections.sort(expected, apiRouteComparator);

        List<ApiRouteInfo> result = apiRouteService.getMultiApiRouteInstances("msb:routing:api:*");

        Collections.sort(result, apiRouteComparator);
        assertEquals(expected, result);
    }

    @Test
    public void testDeleteMultiApiRouteInstances() throws Exception {
        ApiRouteInfo apirouteInfo = new ApiRouteInfo();
        apirouteInfo.setServiceName("testapi");
        apirouteInfo.setVersion("v1");
        apirouteInfo.setStatus("1");
        apirouteInfo.setUrl("/api/testapi/v1");
        apirouteInfo.setUseOwnUpstream("0");
        apirouteInfo.setVisualRange("0");
        apirouteInfo.setEnable_ssl(false);
        apirouteInfo.setServers(new RouteServer[] {new RouteServer("10.74.148.88", "8080")});

        ApiRouteInfo apirouteInfo2 = new ApiRouteInfo();
        apirouteInfo2.setServiceName("testapi");
        apirouteInfo2.setVersion("v2");
        apirouteInfo2.setStatus("0");
        apirouteInfo2.setUrl("/api/testapi/v2");
        apirouteInfo2.setUseOwnUpstream("0");
        apirouteInfo2.setVisualRange("0");;
        apirouteInfo.setEnable_ssl(true);
        apirouteInfo2.setServers(new RouteServer[] {new RouteServer("10.74.148.88", "8088")});
        apiRouteService.saveApiRouteService2Redis(apirouteInfo, "msb:routing:api:testapi:v1");
        apiRouteService.saveApiRouteService2Redis(apirouteInfo2, "msb:routing:api:testapi:v2");

        assertEquals(2, apiRouteService.getMultiApiRouteInstances("msb:routing:api:testapi:*").size());
        assertEquals(2, apiRouteService.deleteMultiApiRouteService2Redis("msb:routing:api:testapi:*"));
        assertEquals(0, apiRouteService.getMultiApiRouteInstances("msb:routing:api:testapi:*").size());
    }

    @Test(expected = Exception.class)
    public void testUpdateApiRouteStatus2Redis_keyNotExist() throws Exception {
        apiRouteService.updateApiRouteStatus2Redis("msb:routing:api:notexistservice:v1", "0");
    }

    @Test(expected = Exception.class)
    public void testSaveApiRouteService2Redis_null() throws Exception {
        apiRouteService.saveApiRouteService2Redis(null, "msb:routing:api:null:v1");
    }

}
