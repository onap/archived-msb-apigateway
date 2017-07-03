package org.onap.msb.apiroute.wrapper.service;

import com.fiftyonred.mock_jedis.MockJedisPool;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.api.CustomRouteInfo;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.service.CustomRouteService;
import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JedisUtil.class,RedisAccessWrapper.class})
@PowerMockIgnore( {"javax.management.*"})
public class CustomRouteServiceTest {
    private static CustomRouteService customRouteService = null;
    private static Comparator<CustomRouteInfo> customRouteComparator = null;
    @BeforeClass
    public static void setUp() throws Exception{
        customRouteService = CustomRouteService.getInstance();
        customRouteComparator = new Comparator<CustomRouteInfo>() {
            @Override
            public int compare(CustomRouteInfo o1, CustomRouteInfo o2) {
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
    public void testGetCustomRouteInstance_key_not_exist(){
        try {
            assertNull(customRouteService.getCustomRouteInstance("msb:routing:custom:notexistservice:v1"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testGetCustomRouteInstance_key_exist(){
        CustomRouteInfo customrouteInfo = new CustomRouteInfo();
        customrouteInfo.setServiceName("testcustom");
        customrouteInfo.setStatus("1");
        customrouteInfo.setUrl("/custom/testcustom");
        customrouteInfo.setUseOwnUpstream("0");
        customrouteInfo.setVisualRange("0");
        customrouteInfo.setEnable_ssl(false);
        RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.88","8080")};
        customrouteInfo.setServers(servers);
        try {
            customRouteService.saveCustomRouteService2Redis(customrouteInfo, "msb:routing:custom:testcustom");
            assertEquals(customrouteInfo, customRouteService.getCustomRouteInstance("msb:routing:custom:testcustom"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testSaveCustomRouteService2Redis(){
        CustomRouteInfo customrouteInfo = new CustomRouteInfo();
        customrouteInfo.setServiceName("testcustom");
        customrouteInfo.setStatus("1");
        customrouteInfo.setUrl("/custom/testcustom/v1");
        customrouteInfo.setUseOwnUpstream("0");
        customrouteInfo.setVisualRange("0");
        customrouteInfo.setEnable_ssl(true);
        RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.88","8080")};
        customrouteInfo.setServers(servers);
        try {
            customRouteService.saveCustomRouteService2Redis(customrouteInfo, "msb:routing:custom:testcustom");
            assertEquals(customrouteInfo, customRouteService.getCustomRouteInstance("msb:routing:custom:testcustom"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testSaveCustomRouteService2Redis_urlIsSlash(){
        CustomRouteInfo customrouteInfo = new CustomRouteInfo();
        customrouteInfo.setServiceName("testcustom");
        customrouteInfo.setStatus("1");
        customrouteInfo.setUrl("/");
        customrouteInfo.setUseOwnUpstream("0");
        customrouteInfo.setVisualRange("0");
        customrouteInfo.setEnable_ssl(true);
        RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.88","8080")};
        customrouteInfo.setServers(servers);
        try {
            customRouteService.saveCustomRouteService2Redis(customrouteInfo, "msb:routing:custom:testcustom");
            customrouteInfo.setUrl("");
            assertEquals(customrouteInfo, customRouteService.getCustomRouteInstance("msb:routing:custom:testcustom"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testDeleteCustomRouteService2Redis(){
        CustomRouteInfo customrouteInfo = new CustomRouteInfo();
        customrouteInfo.setServiceName("testcustom");
        customrouteInfo.setStatus("1");
        customrouteInfo.setUrl("/custom/testcustom/v1");
        customrouteInfo.setUseOwnUpstream("0");
        customrouteInfo.setVisualRange("0");
        customrouteInfo.setEnable_ssl(false);
        RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.88","8080")};
        customrouteInfo.setServers(servers);
        try {
            customRouteService.saveCustomRouteService2Redis(customrouteInfo, "msb:routing:custom:testcustom");
            assertNotNull(customRouteService.getCustomRouteInstance("msb:routing:custom:testcustom"));
            customRouteService.deleteCustomRouteService2Redis("msb:routing:custom:testcustom");
            assertNull(customRouteService.getCustomRouteInstance("msb:routing:custom:testcustom"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testUpdateCustomRouteStatus2Redis(){
        CustomRouteInfo customrouteInfo = new CustomRouteInfo();
        customrouteInfo.setServiceName("testcustom");
        customrouteInfo.setStatus("1");
        customrouteInfo.setUrl("/custom/testcustom/v1");
        customrouteInfo.setUseOwnUpstream("0");
        customrouteInfo.setVisualRange("0");
        customrouteInfo.setEnable_ssl(true);
        RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.88","8080")};
        customrouteInfo.setServers(servers);
        try {
            customRouteService.saveCustomRouteService2Redis(customrouteInfo, "msb:routing:custom:testcustom");
            assertEquals("1", customRouteService.getCustomRouteInstance("msb:routing:custom:testcustom").getStatus());
            customRouteService.updateCustomRouteStatus2Redis("msb:routing:custom:testcustom", "0");
            assertEquals("0", customRouteService.getCustomRouteInstance("msb:routing:custom:testcustom").getStatus());
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testGetMultiCustomRouteInstances() throws Exception {
        CustomRouteInfo customrouteInfo = new CustomRouteInfo();
        customrouteInfo.setServiceName("testcustom");
        customrouteInfo.setStatus("1");
        customrouteInfo.setUrl("/custom/testcustom");
        customrouteInfo.setUseOwnUpstream("0");
        customrouteInfo.setVisualRange("0");
        customrouteInfo.setEnable_ssl(false);
        customrouteInfo.setServers(new RouteServer[]{new RouteServer("10.74.148.88","8080")});

        CustomRouteInfo customrouteInfo2 = new CustomRouteInfo();
        customrouteInfo2.setServiceName("testcustom2");
        customrouteInfo2.setStatus("0");
        customrouteInfo2.setUrl("/custom/testcustom2");
        customrouteInfo2.setUseOwnUpstream("0");
        customrouteInfo2.setVisualRange("0");;
        customrouteInfo.setEnable_ssl(true);
        customrouteInfo2.setServers(new RouteServer[]{new RouteServer("10.74.148.88","8088")});

        customRouteService.saveCustomRouteService2Redis(customrouteInfo, "msb:routing:custom:testcustom");
        customRouteService.saveCustomRouteService2Redis(customrouteInfo2, "msb:routing:custom:testcustom2");

        List<CustomRouteInfo> expected = new ArrayList<>();
        expected.add(customrouteInfo);
        expected.add(customrouteInfo2);
        Collections.sort(expected, customRouteComparator);

        List<CustomRouteInfo> result = customRouteService.getMultiCustomRouteInstances("msb:routing:custom:*");

        Collections.sort(result, customRouteComparator);
        assertEquals(expected, result);
    }

    @Test
    public void testDeleteMultiCustomRouteInstances() throws Exception {
        CustomRouteInfo customrouteInfo = new CustomRouteInfo();
        customrouteInfo.setServiceName("testcustom");
        customrouteInfo.setStatus("1");
        customrouteInfo.setUrl("/custom/testcustom");
        customrouteInfo.setUseOwnUpstream("0");
        customrouteInfo.setVisualRange("0");
        customrouteInfo.setEnable_ssl(false);
        customrouteInfo.setServers(new RouteServer[]{new RouteServer("10.74.148.88","8080")});

        CustomRouteInfo customrouteInfo2 = new CustomRouteInfo();
        customrouteInfo2.setServiceName("testcustom2");
        customrouteInfo2.setStatus("0");
        customrouteInfo2.setUrl("/custom/testcustom2");
        customrouteInfo2.setUseOwnUpstream("0");
        customrouteInfo2.setVisualRange("0");;
        customrouteInfo.setEnable_ssl(true);
        customrouteInfo2.setServers(new RouteServer[]{new RouteServer("10.74.148.88","8088")});
        customRouteService.saveCustomRouteService2Redis(customrouteInfo, "msb:routing:custom:testcustom");
        customRouteService.saveCustomRouteService2Redis(customrouteInfo2, "msb:routing:custom:testcustom2");

        assertEquals(2,customRouteService.getMultiCustomRouteInstances("msb:routing:custom:*").size());
        assertEquals(2,customRouteService.deleteMultiCustomRouteService2Redis("msb:routing:custom:*"));
        assertEquals(0, customRouteService.getMultiCustomRouteInstances("msb:routing:custom:*").size());
    }

    @Test(expected = Exception.class)
    public void testUpdateCustomRouteStatus2Redis_keyNotExist() throws Exception {
        customRouteService.updateCustomRouteStatus2Redis("msb:routing:custom:notexistservice", "0");
    }

    @Test(expected = Exception.class)
    public void testSaveCustomRouteService2Redis_null() throws Exception {
        customRouteService.saveCustomRouteService2Redis(null, "msb:routing:custom:null");
    }

}
