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
package org.onap.msb.apiroute.wrapper.serviceListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.ApiRouteAppConfig;
import org.onap.msb.apiroute.api.ApiRouteInfo;
import org.onap.msb.apiroute.api.CustomRouteInfo;
import org.onap.msb.apiroute.api.DiscoverInfo;
import org.onap.msb.apiroute.api.IuiRouteInfo;
import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.api.Node;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.api.exception.ExtendedNotFoundException;
import org.onap.msb.apiroute.wrapper.ApiRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.CustomRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.InitRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.IuiRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
import org.onap.msb.apiroute.wrapper.util.HttpClientUtil;
import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fiftyonred.mock_jedis.MockJedisPool;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JedisUtil.class, ConfigUtil.class, HttpClientUtil.class, RedisAccessWrapper.class,})
@PowerMockIgnore({"javax.management.*"})
public class MicroServiceChangeListenerTest {
    private static RouteNotify routeInstance;
    private static ApiRouteServiceWrapper apiRouteServiceWrapper;
    private static IuiRouteServiceWrapper iuiRouteServiceWrapper;
    private static CustomRouteServiceWrapper customRouteServiceWrapper;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        InitRouteServiceWrapper.getInstance().registerServiceChangeListener();
        routeInstance = RouteNotify.getInstance();
        apiRouteServiceWrapper = ApiRouteServiceWrapper.getInstance();
        iuiRouteServiceWrapper = IuiRouteServiceWrapper.getInstance();
        customRouteServiceWrapper = CustomRouteServiceWrapper.getInstance();

        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("ROUTE_WAY")).thenReturn("ip|domain");
        ConfigUtil.getInstance().initRouteWay();
    }

    @Before
    public void initReidsMock() throws Exception {
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
    public void test_noticeRouteListener4Update_api() {
        try {
            routeInstance.noticeRouteListener4Update("apiTest-ns", "v1", buildMicroServiceFullInfo4API());
            ApiRouteInfo apiRouteInfo =
                            apiRouteServiceWrapper.getApiRouteInstance("apiTest-ns", "v1", "host", "20081", "ip");

            Assert.assertNotNull(apiRouteInfo);
            Assert.assertEquals("1", apiRouteInfo.getStatus());

            routeInstance.noticeUpdateStatusListener(buildMicroServiceFullInfo4API(), "0");
            apiRouteInfo = apiRouteServiceWrapper.getApiRouteInstance("apiTest-ns", "v1", "host", "20081", "ip");
            Assert.assertEquals("0", apiRouteInfo.getStatus());

        } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());
        }
    }

    /*
     * @Test
     *
     * public void test_noticeRouteListener4Update_iui() { try {
     * routeInstance.noticeRouteListener4Update("iuiTest-ns", "v1",
     * buildMicroServiceFullInfo4IUI()); IuiRouteInfo iuiRouteInfo =
     * iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest-ns", "host", "20081", "ip");
     * 
     * Assert.assertNotNull(iuiRouteInfo); Assert.assertEquals("1", iuiRouteInfo.getStatus());
     * 
     * routeInstance.noticeUpdateStatusListener(buildMicroServiceFullInfo4IUI(), "0"); iuiRouteInfo
     * = iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest-ns", "host", "20081", "ip");
     * Assert.assertEquals("0", iuiRouteInfo.getStatus());
     * 
     * } catch (Exception e) { Assert.fail("throw exception means error occured!" + e.getMessage());
     * } }
     */

    @Test
    public void test_noticeRouteListener4Update_http() {
        try {
            routeInstance.noticeRouteListener4Update("httpTest-ns", "v1", buildMicroServiceFullInfo4HTTP());
            CustomRouteInfo customRouteInfo =
                            customRouteServiceWrapper.getCustomRouteInstance("/httpTest-ns", "host", "20081", "ip");
            Assert.assertNotNull(customRouteInfo);
            Assert.assertEquals("1", customRouteInfo.getStatus());

            routeInstance.noticeUpdateStatusListener(buildMicroServiceFullInfo4HTTP(), "0");
            customRouteInfo = customRouteServiceWrapper.getCustomRouteInstance("/httpTest-ns", "host", "20081", "ip");
            Assert.assertEquals("0", customRouteInfo.getStatus());
        } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());
        }
    }

    /*
     * @Test public void test_noticeRouteListener4Add_del_api() { try { MicroServiceFullInfo
     * microServiceInfo = buildMicroServiceFullInfo4API();
     * routeInstance.noticeRouteListener4Add(microServiceInfo);
     * Assert.assertNotNull(apiRouteServiceWrapper.getApiRouteInstance("apiTest", "v1", "", "20081",
     * "ip")); Assert.assertNotNull(customRouteServiceWrapper.getCustomRouteInstance("/",
     * "apitest-ns", "", "domain"));
     * 
     * routeInstance.noticeRouteListener4Delete(microServiceInfo);
     * 
     * } catch (Exception e) { Assert.fail("throw exception means error occured!" + e.getMessage());
     * }
     * 
     * try { apiRouteServiceWrapper.getApiRouteInstance("apiTest", "v1", "", "20081", "ip");
     * Assert.fail("should not process to here."); } catch (Exception e) { Assert.assertTrue(e
     * instanceof ExtendedNotFoundException); }
     * 
     * try { apiRouteServiceWrapper.getApiRouteInstance("apiTest", "v1", "apitest-ns", "",
     * "domain"); Assert.fail("should not process to here."); } catch (Exception e) {
     * Assert.assertTrue(e instanceof ExtendedNotFoundException); }
     * 
     * 
     * }
     */

    @Test
    public void test_noticeRouteListener4Add_del_api_path() {
        try {
            MicroServiceFullInfo microServiceInfo = buildMicroServiceFullInfo4API_path();
            routeInstance.noticeRouteListener4Add(microServiceInfo);
            Assert.assertNotNull(apiRouteServiceWrapper.getApiRouteInstance("apiTest4Path", "v1", "", "10081", "ip"));
            Assert.assertNotNull(apiRouteServiceWrapper.getApiRouteInstance("apiTest4Path", "v1", "", "10082", "ip"));
            Assert.assertNotNull(
                            apiRouteServiceWrapper.getApiRouteInstance("apiTest4Path", "v1", "host", "", "domain"));

            routeInstance.noticeRouteListener4Delete(microServiceInfo);

        } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());
        }

        try {
            apiRouteServiceWrapper.getApiRouteInstance("apiTest4Path", "v1", "", "10081", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            apiRouteServiceWrapper.getApiRouteInstance("apiTest4Path", "v1", "", "10082", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            apiRouteServiceWrapper.getApiRouteInstance("apiTest4Path", "v1", "host", "", "domain");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

    }

    @Test
    public void test_noticeRouteListener4Add_del_api_mutiPort() {
        try {
            MicroServiceFullInfo microServiceInfo = buildMicroServiceFullInfo4API_path();
            microServiceInfo.setPath("");
            microServiceInfo.setHost("");

            routeInstance.noticeRouteListener4Add(microServiceInfo);
            Assert.assertNotNull(apiRouteServiceWrapper.getApiRouteInstance("apiTest", "v1", "", "10081", "ip"));
            Assert.assertNotNull(apiRouteServiceWrapper.getApiRouteInstance("apiTest", "v1", "", "10082", "ip"));
            Assert.assertNotNull(customRouteServiceWrapper.getCustomRouteInstance("/", "apitest", "", "domain"));

            routeInstance.noticeRouteListener4Delete(microServiceInfo);

        } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());
        }

        try {
            apiRouteServiceWrapper.getApiRouteInstance("apiTest", "v1", "", "10081", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            apiRouteServiceWrapper.getApiRouteInstance("apiTest", "v1", "", "10082", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            apiRouteServiceWrapper.getApiRouteInstance("apiTest", "v1", "apitest", "", "domain");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

    }


    @Test
    public void test_noticeRouteListener4Add_del_iui() throws Exception {

        MicroServiceFullInfo microServiceInfo = buildMicroServiceFullInfo4IUI();
        routeInstance.noticeRouteListener4Add(microServiceInfo);
        iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest", "", "20081", "ip");
        // Assert.assertNotNull(customRouteServiceWrapper.getCustomRouteInstance("/", "iuitest-ns",
        // "", "domain"));

        routeInstance.noticeRouteListener4Delete(microServiceInfo);


        try {
            iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest", "", "20081", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest", "iuitest-ns", "", "domain");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

    }


    @Test
    public void test_noticeRouteListener4Add_del_iui_path() {
        try {
            MicroServiceFullInfo microServiceInfo = buildMicroServiceFullInfo4IUI_path();
            routeInstance.noticeRouteListener4Add(microServiceInfo);
            Assert.assertNotNull(iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest4Path", "", "10081", "ip"));
            Assert.assertNotNull(iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest4Path", "", "10082", "ip"));
            Assert.assertNotNull(iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest4Path", "host", "", "domain"));

            routeInstance.noticeRouteListener4Delete(microServiceInfo);
        } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());
        }

        try {
            iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest4Path", "", "10081", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest4Path", "", "10082", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest4Path", "host", "", "domain");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

    }


    @Test
    public void test_noticeRouteListener4Add_del_iui_mutiPort() {
        try {
            MicroServiceFullInfo microServiceInfo = buildMicroServiceFullInfo4IUI_path();
            microServiceInfo.setPath("");
            microServiceInfo.setHost("");

            routeInstance.noticeRouteListener4Add(microServiceInfo);
            Assert.assertNotNull(iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest", "", "10081", "ip"));
            Assert.assertNotNull(iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest", "", "10082", "ip"));
            Assert.assertNotNull(customRouteServiceWrapper.getCustomRouteInstance("/", "iuitest", "", "domain"));

            routeInstance.noticeRouteListener4Delete(microServiceInfo);
        } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());
        }

        try {
            iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest", "", "10081", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            iuiRouteServiceWrapper.getIuiRouteInstance("iuiTest", "", "10082", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            customRouteServiceWrapper.getCustomRouteInstance("/", "iuitest", "", "domain");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

    }

    /*
     * @Test public void test_noticeRouteListener4Add_del_http() { try { MicroServiceFullInfo
     * microServiceInfo = buildMicroServiceFullInfo4HTTP();
     * routeInstance.noticeRouteListener4Add(microServiceInfo);
     * Assert.assertNotNull(customRouteServiceWrapper.getCustomRouteInstance("/httpTest/v1", "",
     * "20081", "ip"));
     * Assert.assertNotNull(customRouteServiceWrapper.getCustomRouteInstance("/httpTest/v1",
     * "httptest-ns", "", "domain"));
     * 
     * routeInstance.noticeRouteListener4Delete(microServiceInfo); } catch (Exception e) {
     * Assert.fail("throw exception means error occured!" + e.getMessage()); }
     * 
     * try { customRouteServiceWrapper.getCustomRouteInstance("/httpTest/v1", "", "20081", "ip");
     * Assert.fail("should not process to here."); } catch (Exception e) { Assert.assertTrue(e
     * instanceof ExtendedNotFoundException); }
     * 
     * try { customRouteServiceWrapper.getCustomRouteInstance("/httpTest", "httptest-ns", "",
     * "domain"); Assert.fail("should not process to here."); } catch (Exception e) {
     * Assert.assertTrue(e instanceof ExtendedNotFoundException); }
     * 
     * }
     */

    @Test
    public void test_noticeRouteListener4Add_del_http_path() {
        try {
            MicroServiceFullInfo microServiceInfo = buildMicroServiceFullInfo4HTTP_path();
            routeInstance.noticeRouteListener4Add(microServiceInfo);
            Assert.assertNotNull(customRouteServiceWrapper.getCustomRouteInstance("/httpTest4Path", "", "10081", "ip"));
            Assert.assertNotNull(customRouteServiceWrapper.getCustomRouteInstance("/httpTest4Path", "", "10082", "ip"));
            Assert.assertNotNull(
                            customRouteServiceWrapper.getCustomRouteInstance("/httpTest4Path", "host", "", "domain"));

            routeInstance.noticeRouteListener4Delete(microServiceInfo);
        } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());
        }

        try {
            customRouteServiceWrapper.getCustomRouteInstance("/httpTest4Path", "", "10081", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            customRouteServiceWrapper.getCustomRouteInstance("/httpTest4Path", "", "10082", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            customRouteServiceWrapper.getCustomRouteInstance("/httpTest4Path", "host", "", "domain");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

    }


    @Test
    public void test_noticeRouteListener4Add_del_http_mutiPort() {
        try {
            MicroServiceFullInfo microServiceInfo = buildMicroServiceFullInfo4HTTP_path();
            microServiceInfo.setPath("");
            microServiceInfo.setHost("");

            routeInstance.noticeRouteListener4Add(microServiceInfo);
            Assert.assertNotNull(customRouteServiceWrapper.getCustomRouteInstance("/httpTest/v1", "", "10081", "ip"));
            Assert.assertNotNull(customRouteServiceWrapper.getCustomRouteInstance("/httpTest/v1", "", "10082", "ip"));
            Assert.assertNotNull(
                            customRouteServiceWrapper.getCustomRouteInstance("/httpTest/v1", "httptest", "", "domain"));

            routeInstance.noticeRouteListener4Delete(microServiceInfo);
        } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());
        }

        try {
            customRouteServiceWrapper.getCustomRouteInstance("/httpTest/v1", "", "10081", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            customRouteServiceWrapper.getCustomRouteInstance("/httpTest/v1", "", "10082", "ip");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

        try {
            customRouteServiceWrapper.getCustomRouteInstance("/httpTest", "httptest", "", "domain");
            Assert.fail("should not process to here.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ExtendedNotFoundException);
        }

    }


    @Test
    public void test_noticeRouteListener4Add_portal() {
        try {
            PowerMockito.mockStatic(System.class);
            PowerMockito.when(System.getenv("SDCLIENT_IP")).thenReturn("127.0.0.1");
            ApiRouteAppConfig configuration = new ApiRouteAppConfig();

            DiscoverInfo discoverInfo = new DiscoverInfo();
            discoverInfo.setEnabled(true);
            discoverInfo.setIp("127.0.0.2");
            discoverInfo.setPort(10081);
            configuration.setDiscoverInfo(discoverInfo);
            ConfigUtil.getInstance().initDiscoverInfo(configuration);


            PowerMockito.mockStatic(HttpClientUtil.class);
            String publishUrl =
                            "http://127.0.0.1:10081/api/microservices/v1/services/portalTest/version/v1/allpublishaddress?namespace=&visualRange=0";
            String resultJson =
                            "[{\"domain\":\"opapi.openpalette.zte.com.cn\",\"port\":\"443\",\"publish_url\":\"/api\",\"visualRange\":\"0\",\"publish_protocol\":\"https\"},{\"ip\":\"10.74.165.246\",\"port\":\"443\",\"publish_url\":\"/opapi\",\"visualRange\":\"0\",\"publish_protocol\":\"https\"},{\"ip\":\"10.74.165.246\",\"port\":\"80\",\"publish_url\":\"/opapi\",\"visualRange\":\"0\",\"publish_protocol\":\"http\"}]";
            PowerMockito.when(HttpClientUtil.httpGet(publishUrl)).thenReturn(resultJson);

            MicroServiceFullInfo microServiceInfo = buildMicroServiceFullInfo4PORTAL();

            routeInstance.noticeRouteListener4Add(microServiceInfo);

            CustomRouteInfo routeInfo_ip =
                            customRouteServiceWrapper.getCustomRouteInstance("/portalTest/v1", "", "10088", "ip");
            RouteServer[] servers_ip = new RouteServer[] {new RouteServer("10.74.148.99", "8080")};
            Assert.assertArrayEquals(servers_ip, routeInfo_ip.getServers());

            CustomRouteInfo routeInfo_domain =
                            customRouteServiceWrapper.getCustomRouteInstance("/portalTest/v1", "host", "", "domain");
            RouteServer[] servers_domain = new RouteServer[] {new RouteServer("10.74.165.246", "443")};

            Assert.assertArrayEquals(servers_domain, routeInfo_domain.getServers());

        } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());
        }

    }


    private MicroServiceFullInfo buildMicroServiceFullInfo4API() {
        MicroServiceFullInfo microServiceInfo = new MicroServiceFullInfo();
        microServiceInfo.setServiceName("apiTest-ns");
        microServiceInfo.setVersion("v1");
        microServiceInfo.setEnable_ssl(false);
        microServiceInfo.setPublish_port("20081");
        microServiceInfo.setProtocol("REST");
        microServiceInfo.setUrl("/api/apiTest/v1");
        microServiceInfo.setVisualRange("1");
        microServiceInfo.setStatus("1");
        microServiceInfo.setNamespace("ns");
        Set<Node> nodes = new HashSet<Node>();
        nodes.add(new Node("10.74.148.88", "8080"));
        nodes.add(new Node("10.74.148.89", "8080"));
        microServiceInfo.setNodes(nodes);

        return microServiceInfo;
    }

    private MicroServiceFullInfo buildMicroServiceFullInfo4API_path() {
        MicroServiceFullInfo microServiceInfo = new MicroServiceFullInfo();
        microServiceInfo.setServiceName("apiTest");
        microServiceInfo.setVersion("v1");
        microServiceInfo.setEnable_ssl(true);
        microServiceInfo.setHost("host");
        microServiceInfo.setPath("/api/apiTest4Path/v1");
        microServiceInfo.setPublish_port("10081|10082");
        microServiceInfo.setProtocol("REST");
        microServiceInfo.setUrl("/api/apiTest/v1");
        microServiceInfo.setVisualRange("0");
        microServiceInfo.setLb_policy("ip_hash");
        microServiceInfo.setStatus("1");
        Set<Node> nodes = new HashSet<Node>();
        nodes.add(new Node("10.74.148.88", "8080"));
        nodes.add(new Node("10.74.148.89", "8080"));
        microServiceInfo.setNodes(nodes);

        return microServiceInfo;
    }


    private MicroServiceFullInfo buildMicroServiceFullInfo4PORTAL() {

        MicroServiceFullInfo microServiceInfo = new MicroServiceFullInfo();
        microServiceInfo.setServiceName("portalTest");
        microServiceInfo.setVersion("v1");
        microServiceInfo.setEnable_ssl(true);
        microServiceInfo.setHost("host");
        microServiceInfo.setPublish_port("10088");
        microServiceInfo.setProtocol("HTTP");
        microServiceInfo.setUrl("/portalTestUrl/v1");
        microServiceInfo.setVisualRange("0");
        microServiceInfo.setLb_policy("ip_hash");
        microServiceInfo.setStatus("1");
        microServiceInfo.setCustom(RouteUtil.CUSTOM_PORTAL);
        Set<Node> nodes = new HashSet<Node>();
        nodes.add(new Node("10.74.148.99", "8080"));
        microServiceInfo.setNodes(nodes);

        return microServiceInfo;
    }

    private MicroServiceFullInfo buildMicroServiceFullInfo4IUI() {
        MicroServiceFullInfo microServiceInfo = new MicroServiceFullInfo();
        microServiceInfo.setServiceName("iuiTest");
        microServiceInfo.setNamespace("ns");
        microServiceInfo.setVersion("v1");
        microServiceInfo.setEnable_ssl(false);
        microServiceInfo.setPublish_port("20081");
        microServiceInfo.setProtocol("UI");
        microServiceInfo.setUrl("/iui/iuiTest");
        microServiceInfo.setVisualRange("1");
        microServiceInfo.setStatus("1");
        Set<Node> nodes = new HashSet<Node>();
        nodes.add(new Node("10.74.148.88", "8080"));
        nodes.add(new Node("10.74.148.89", "8080"));
        microServiceInfo.setNodes(nodes);

        return microServiceInfo;
    }

    private MicroServiceFullInfo buildMicroServiceFullInfo4IUI_path() {
        MicroServiceFullInfo microServiceInfo = new MicroServiceFullInfo();
        microServiceInfo.setServiceName("iuiTest");
        microServiceInfo.setVersion("v1");
        microServiceInfo.setEnable_ssl(true);
        microServiceInfo.setHost("host");
        microServiceInfo.setProtocol("UI");
        microServiceInfo.setUrl("/iui/iuiTest");
        microServiceInfo.setLb_policy("ip_hash");
        microServiceInfo.setPublish_port("10081|10082");
        microServiceInfo.setPath("/iui/iuiTest4Path");
        microServiceInfo.setVisualRange("0");
        microServiceInfo.setStatus("1");
        Set<Node> nodes = new HashSet<Node>();
        nodes.add(new Node("10.74.148.88", "8080"));
        nodes.add(new Node("10.74.148.89", "8080"));
        microServiceInfo.setNodes(nodes);

        return microServiceInfo;
    }

    private MicroServiceFullInfo buildMicroServiceFullInfo4HTTP() {
        MicroServiceFullInfo microServiceInfo = new MicroServiceFullInfo();
        microServiceInfo.setServiceName("httpTest-ns");
        microServiceInfo.setNamespace("ns");
        microServiceInfo.setVersion("v1");
        microServiceInfo.setEnable_ssl(false);
        microServiceInfo.setPublish_port("20081");
        microServiceInfo.setProtocol("HTTP");
        microServiceInfo.setUrl("/httpTest");
        microServiceInfo.setVisualRange("1");
        microServiceInfo.setStatus("1");
        Set<Node> nodes = new HashSet<Node>();
        nodes.add(new Node("10.74.148.88", "8080"));
        nodes.add(new Node("10.74.148.89", "8080"));
        microServiceInfo.setNodes(nodes);

        return microServiceInfo;
    }

    private MicroServiceFullInfo buildMicroServiceFullInfo4HTTP_path() {
        MicroServiceFullInfo microServiceInfo = new MicroServiceFullInfo();
        microServiceInfo.setServiceName("httpTest");
        microServiceInfo.setVersion("v1");
        microServiceInfo.setEnable_ssl(true);
        microServiceInfo.setHost("host");
        microServiceInfo.setPublish_port("20081");
        microServiceInfo.setProtocol("HTTP");
        microServiceInfo.setUrl("/httpTest");
        microServiceInfo.setVisualRange("0");
        microServiceInfo.setStatus("1");
        microServiceInfo.setLb_policy("ip_hash");
        microServiceInfo.setPublish_port("10081|10082");
        microServiceInfo.setPath("/httpTest4Path");
        Set<Node> nodes = new HashSet<Node>();
        nodes.add(new Node("10.74.148.88", "8080"));
        nodes.add(new Node("10.74.148.89", "8080"));
        microServiceInfo.setNodes(nodes);

        return microServiceInfo;
    }


}
