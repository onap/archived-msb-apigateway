package org.onap.msb.apiroute.wrapper.service;

import com.fiftyonred.mock_jedis.MockJedisPool;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.api.IuiRouteInfo;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.service.IuiRouteService;
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
public class IuiRouteServiceTest {
    private static IuiRouteService iuiRouteService = null;
    private static Comparator<IuiRouteInfo> iuiRouteComparator = null;
    @BeforeClass
    public static void setUp() throws Exception{
        iuiRouteService = IuiRouteService.getInstance();
        iuiRouteComparator = new Comparator<IuiRouteInfo>() {
            @Override
            public int compare(IuiRouteInfo o1, IuiRouteInfo o2) {
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
    public void testGetIuiRouteInstance_key_not_exist(){
        try {
            assertNull(iuiRouteService.getIuiRouteInstance("msb:routing:iui:notexistservice:v1"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testGetIuiRouteInstance_key_exist(){
        IuiRouteInfo iuirouteInfo = new IuiRouteInfo();
        iuirouteInfo.setServiceName("testiui");
        iuirouteInfo.setStatus("1");
        iuirouteInfo.setUrl("/iui/testiui");
        iuirouteInfo.setUseOwnUpstream("0");
        iuirouteInfo.setVisualRange("0");
        iuirouteInfo.setEnable_ssl(false);
        RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.88","8080")};
        iuirouteInfo.setServers(servers);
        try {
            iuiRouteService.saveIuiRouteService2Redis(iuirouteInfo,"msb:routing:iui:testiui");
            assertEquals(iuirouteInfo, iuiRouteService.getIuiRouteInstance("msb:routing:iui:testiui"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testSaveIuiRouteService2Redis(){
        IuiRouteInfo iuirouteInfo = new IuiRouteInfo();
        iuirouteInfo.setServiceName("testiui");
        iuirouteInfo.setStatus("1");
        iuirouteInfo.setUrl("/iui/testiui/v1");
        iuirouteInfo.setUseOwnUpstream("0");
        iuirouteInfo.setVisualRange("0");
        iuirouteInfo.setEnable_ssl(true);
        RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.88","8080")};
        iuirouteInfo.setServers(servers);
        try {
            iuiRouteService.saveIuiRouteService2Redis(iuirouteInfo,"msb:routing:iui:testiui");
            assertEquals(iuirouteInfo, iuiRouteService.getIuiRouteInstance("msb:routing:iui:testiui"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testDeleteIuiRouteService2Redis(){
        IuiRouteInfo iuirouteInfo = new IuiRouteInfo();
        iuirouteInfo.setServiceName("testiui");
        iuirouteInfo.setStatus("1");
        iuirouteInfo.setUrl("/iui/testiui/v1");
        iuirouteInfo.setUseOwnUpstream("0");
        iuirouteInfo.setVisualRange("0");
        iuirouteInfo.setEnable_ssl(false);
        RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.88","8080")};
        iuirouteInfo.setServers(servers);
        try {
            iuiRouteService.saveIuiRouteService2Redis(iuirouteInfo,"msb:routing:iui:testiui");
            assertNotNull(iuiRouteService.getIuiRouteInstance("msb:routing:iui:testiui"));
            iuiRouteService.deleteIuiRouteService2Redis("msb:routing:iui:testiui");
            assertNull(iuiRouteService.getIuiRouteInstance("msb:routing:iui:testiui"));
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testUpdateIuiRouteStatus2Redis(){
        IuiRouteInfo iuirouteInfo = new IuiRouteInfo();
        iuirouteInfo.setServiceName("testiui");
        iuirouteInfo.setStatus("1");
        iuirouteInfo.setUrl("/iui/testiui/v1");
        iuirouteInfo.setUseOwnUpstream("0");
        iuirouteInfo.setVisualRange("0");
        iuirouteInfo.setEnable_ssl(true);
        RouteServer[] servers = new RouteServer[]{new RouteServer("10.74.148.88","8080")};
        iuirouteInfo.setServers(servers);
        try {
            iuiRouteService.saveIuiRouteService2Redis(iuirouteInfo,"msb:routing:iui:testiui");
            assertEquals("1", iuiRouteService.getIuiRouteInstance("msb:routing:iui:testiui").getStatus());
            iuiRouteService.updateIuiRouteStatus2Redis("msb:routing:iui:testiui","0");
            assertEquals("0", iuiRouteService.getIuiRouteInstance("msb:routing:iui:testiui").getStatus());
        } catch (Exception e) {
            assert false:"throw exception means error occured!"+e.getMessage();
        }
    }

    @Test
    public void testGetMultiIuiRouteInstances() throws Exception {
        IuiRouteInfo iuirouteInfo = new IuiRouteInfo();
        iuirouteInfo.setServiceName("testiui");
        iuirouteInfo.setStatus("1");
        iuirouteInfo.setUrl("/iui/testiui");
        iuirouteInfo.setUseOwnUpstream("0");
        iuirouteInfo.setVisualRange("0");
        iuirouteInfo.setEnable_ssl(false);
        iuirouteInfo.setServers(new RouteServer[]{new RouteServer("10.74.148.88","8080")});

        IuiRouteInfo iuirouteInfo2 = new IuiRouteInfo();
        iuirouteInfo2.setServiceName("testiui2");
        iuirouteInfo2.setStatus("0");
        iuirouteInfo2.setUrl("/iui/testiui2");
        iuirouteInfo2.setUseOwnUpstream("0");
        iuirouteInfo2.setVisualRange("0");;
        iuirouteInfo.setEnable_ssl(true);
        iuirouteInfo2.setServers(new RouteServer[]{new RouteServer("10.74.148.88","8088")});

        iuiRouteService.saveIuiRouteService2Redis(iuirouteInfo,"msb:routing:iui:testiui");
        iuiRouteService.saveIuiRouteService2Redis(iuirouteInfo2,"msb:routing:iui:testiui2");

        List<IuiRouteInfo> expected = new ArrayList<>();
        expected.add(iuirouteInfo);
        expected.add(iuirouteInfo2);
        Collections.sort(expected, iuiRouteComparator);

        List<IuiRouteInfo> result = iuiRouteService.getMultiIuiRouteInstances("msb:routing:iui:*");

        Collections.sort(result, iuiRouteComparator);
        assertEquals(expected, result);
    }

    @Test
    public void testDeleteMultiIuiRouteInstances() throws Exception {
        IuiRouteInfo iuirouteInfo = new IuiRouteInfo();
        iuirouteInfo.setServiceName("testiui");
        iuirouteInfo.setStatus("1");
        iuirouteInfo.setUrl("/iui/testiui");
        iuirouteInfo.setUseOwnUpstream("0");
        iuirouteInfo.setVisualRange("0");
        iuirouteInfo.setEnable_ssl(false);
        iuirouteInfo.setServers(new RouteServer[]{new RouteServer("10.74.148.88","8080")});

        IuiRouteInfo iuirouteInfo2 = new IuiRouteInfo();
        iuirouteInfo2.setServiceName("testiui2");
        iuirouteInfo2.setStatus("0");
        iuirouteInfo2.setUrl("/iui/testiui2");
        iuirouteInfo2.setUseOwnUpstream("0");
        iuirouteInfo2.setVisualRange("0");;
        iuirouteInfo.setEnable_ssl(true);
        iuirouteInfo2.setServers(new RouteServer[]{new RouteServer("10.74.148.88","8088")});
        iuiRouteService.saveIuiRouteService2Redis(iuirouteInfo,"msb:routing:iui:testiui");
        iuiRouteService.saveIuiRouteService2Redis(iuirouteInfo2,"msb:routing:iui:testiui2");

        assertEquals(2,iuiRouteService.getMultiIuiRouteInstances("msb:routing:iui:*").size());
        assertEquals(2,iuiRouteService.deleteMultiIuiRouteService2Redis("msb:routing:iui:*"));
        assertEquals(0,iuiRouteService.getMultiIuiRouteInstances("msb:routing:iui:*").size());
    }

    @Test(expected = Exception.class)
    public void testUpdateIuiRouteStatus2Redis_keyNotExist() throws Exception {
        iuiRouteService.updateIuiRouteStatus2Redis("msb:routing:iui:notexistservice","0");
    }

    @Test(expected = Exception.class)
    public void testSaveIuiRouteService2Redis_null() throws Exception {
        iuiRouteService.saveIuiRouteService2Redis(null,"msb:routing:iui:null");
    }

}
