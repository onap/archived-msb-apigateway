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
package org.onap.msb.apiroute.wrapper.util;



import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.ApiRouteAppConfig;
import org.onap.msb.apiroute.api.DiscoverInfo;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigUtil.class})
public class ConfigUtilTest {

    @Test
    public void test_initRootPath() {
        try {
            ConfigUtil.getInstance().initRootPath();
            String iuiRootPath = ConfigUtil.getInstance().getIUI_ROOT_PATH();
            String apiRootPath = ConfigUtil.getInstance().getAPI_ROOT_PATH();
            Assert.assertEquals("iui", iuiRootPath);
            Assert.assertEquals("api", apiRootPath);
        } catch (Exception e) {
            Assert.fail("throw exception means error occured!" + e.getMessage());

        }

    }

    @Test
    public void test_initApiGatewayPort() {

        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("APIGATEWAY_EXPOSE_PORT")).thenReturn(null);
        ConfigUtil.getInstance().initApiGatewayPort();
        Assert.assertEquals("80", ConfigUtil.getInstance().getServerPort());


        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("APIGATEWAY_EXPOSE_PORT")).thenReturn("81");

        ConfigUtil.getInstance().initApiGatewayPort();
        Assert.assertEquals("81", ConfigUtil.getInstance().getServerPort());
    }

    @Test
    public void test_initRouteNameSpaceMatches() {

        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("APIGATEWAY_EXPOSE_PORT")).thenReturn(null);
        ConfigUtil.getInstance().initRouteNameSpaceMatches();
        Assert.assertEquals("all", ConfigUtil.getInstance().getNamespaceMatches());


        PowerMockito.when(System.getenv("NAMESPACE")).thenReturn("net");

        ConfigUtil.getInstance().initRouteNameSpaceMatches();
        Assert.assertEquals("net", ConfigUtil.getInstance().getNamespaceMatches());
    }

    @Test
    public void test_initRouteLabelsMatches() {

        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("ROUTE_LABELS")).thenReturn(null);
        ConfigUtil.getInstance().initRouteLabelsMatches();
        Assert.assertEquals("0", ConfigUtil.getInstance().getVisualRangeMatches());
        Assert.assertEquals("net", ConfigUtil.getInstance().getNetwork_plane_typeMatches());
        Assert.assertTrue(ConfigUtil.getInstance().getLabelMapMatches().containsKey("custom-key"));



        PowerMockito.when(System.getenv("ROUTE_LABELS")).thenReturn("visualRange:1,network_plane_type:net,custom:test");

        ConfigUtil.getInstance().initRouteLabelsMatches();
        Assert.assertEquals("1", ConfigUtil.getInstance().getVisualRangeMatches());
        Assert.assertEquals("net", ConfigUtil.getInstance().getNetwork_plane_typeMatches());
        Assert.assertTrue(ConfigUtil.getInstance().getLabelMapMatches().containsKey("custom"));

    }

    @Test
    public void test_initRouteWay() {
        PowerMockito.mockStatic(System.class);

        PowerMockito.when(System.getenv("ROUTE_WAY")).thenReturn(null);
        ConfigUtil.getInstance().initRouteWay();
        String[] ip_routeWay = {"ip"};
        Assert.assertArrayEquals(ip_routeWay, ConfigUtil.getInstance().getRouteWay());

        PowerMockito.when(System.getenv("ROUTE_WAY")).thenReturn("ip|domain");

        ConfigUtil.getInstance().initRouteWay();
        String[] routeWay = {"ip", "domain"};
        Assert.assertArrayEquals(routeWay, ConfigUtil.getInstance().getRouteWay());
    }

    @Test
    public void test_initDiscoverInfo() {
        PowerMockito.mockStatic(System.class);


        ApiRouteAppConfig configuration = new ApiRouteAppConfig();

        DiscoverInfo discoverInfo = new DiscoverInfo();
        discoverInfo.setEnabled(true);
        discoverInfo.setIp("127.0.0.1");
        discoverInfo.setPort(10081);

        configuration.setDiscoverInfo(discoverInfo);
        PowerMockito.when(System.getenv("SDCLIENT_IP")).thenReturn(null);
        ConfigUtil.getInstance().initDiscoverInfo(configuration);
        Assert.assertEquals("127.0.0.1:10081", ConfigUtil.getInstance().getDiscoverInfo().toString());

        PowerMockito.when(System.getenv("SDCLIENT_IP")).thenReturn("10.74.44.86");
        ConfigUtil.getInstance().initDiscoverInfo(configuration);
        Assert.assertEquals("10.74.44.86:10081", ConfigUtil.getInstance().getDiscoverInfo().toString());
    }

    @Test
    public void test_initNodeMeta() {

        // CONSUL_REGISTER_MODE not catalog
        ConfigUtil util = ConfigUtil.getInstance();
        util.initNodeMetaQueryParam();
        System.out.println(util.getNodeMetaQueryParam());
        Assert.assertEquals(util.getNodeMetaQueryParam(), "");

        // CONSUL_REGISTER_MODE catalog
        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("CONSUL_REGISTER_MODE")).thenReturn("agnet");
        util.initNodeMetaQueryParam();
        System.out.println(util.getNodeMetaQueryParam());
        Assert.assertEquals(util.getNodeMetaQueryParam(), "");


        // CONSUL_REGISTER_MODE catalog
        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("CONSUL_REGISTER_MODE")).thenReturn("catalog");
        try {
            Field visualRangeField = util.getClass().getDeclaredField("visualRangeMatches");
            visualRangeField.setAccessible(true);

            Field namespaceField = util.getClass().getDeclaredField("namespaceMatches");
            namespaceField.setAccessible(true);

            // 0:default;
            visualRangeField.set(util, "0");
            namespaceField.set(util, "default");

            util.initNodeMetaQueryParam();
            System.out.println(util.getNodeMetaQueryParam());
            Assert.assertEquals(util.getNodeMetaQueryParam(), "node-meta=external:true&node-meta=ns:default");

            // 1:default;
            visualRangeField.set(util, "1");
            util.initNodeMetaQueryParam();
            System.out.println(util.getNodeMetaQueryParam());
            Assert.assertEquals(util.getNodeMetaQueryParam(), "node-meta=internal:true&node-meta=ns:default");

            // 0|1:default
            visualRangeField.set(util, "0|1");
            util.initNodeMetaQueryParam();
            System.out.println(util.getNodeMetaQueryParam());
            Assert.assertEquals(util.getNodeMetaQueryParam(), "node-meta=ns:default");

            // 0|1:all
            namespaceField.set(util, "all");
            util.initNodeMetaQueryParam();
            System.out.println(util.getNodeMetaQueryParam());
            Assert.assertEquals(util.getNodeMetaQueryParam(), "");

            ///////////////////////////////////////////////////////////////////////////
            // 1:all
            visualRangeField.set(util, "1");
            namespaceField.set(util, "all");
            util.initNodeMetaQueryParam();
            System.out.println(util.getNodeMetaQueryParam());
            Assert.assertEquals(util.getNodeMetaQueryParam(), "node-meta=internal:true");

            // 1:!
            namespaceField.set(util, "!default");
            util.initNodeMetaQueryParam();
            System.out.println(util.getNodeMetaQueryParam());
            Assert.assertEquals(util.getNodeMetaQueryParam(), "node-meta=internal:true");

            // 1:&
            namespaceField.set(util, "tenant1&tenant2");
            util.initNodeMetaQueryParam();
            System.out.println(util.getNodeMetaQueryParam());
            Assert.assertEquals(util.getNodeMetaQueryParam(), "node-meta=internal:true");

            // 1:|
            namespaceField.set(util, "tenant1|tenant2");
            util.initNodeMetaQueryParam();
            System.out.println(util.getNodeMetaQueryParam());
            Assert.assertEquals(util.getNodeMetaQueryParam(), "node-meta=internal:true");

        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
