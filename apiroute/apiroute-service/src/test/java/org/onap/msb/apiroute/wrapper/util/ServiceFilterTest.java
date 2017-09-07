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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.api.Node;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableService;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableServiceHealth;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.Service;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
import org.onap.msb.apiroute.wrapper.util.ServiceFilter;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.orbitz.consul.model.health.ImmutableNode;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigUtil.class})
public class ServiceFilterTest {



    @Test
    public void test_isNeedNotifyByNameSpace() {

        PowerMockito.mockStatic(System.class);

        PowerMockito.when(System.getenv("NAMESPACE")).thenReturn("all");
        ConfigUtil.getInstance().initRouteNameSpaceMatches();
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByNameSpace("test")); // namespaceMatches:all

        PowerMockito.when(System.getenv("NAMESPACE")).thenReturn("default");
        ConfigUtil.getInstance().initRouteNameSpaceMatches();
        Assert.assertFalse(ServiceFilter.getInstance().isNeedNotifyByNameSpace("test"));// namespaceMatches:default
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByNameSpace(""));// namespaceMatches:default
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByNameSpace("default"));// namespaceMatches:default


        PowerMockito.when(System.getenv("NAMESPACE")).thenReturn("!default");
        ConfigUtil.getInstance().initRouteNameSpaceMatches();
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByNameSpace("test"));// namespaceMatches:!default
        Assert.assertFalse(ServiceFilter.getInstance().isNeedNotifyByNameSpace(""));// namespaceMatches:!default

        PowerMockito.when(System.getenv("NAMESPACE")).thenReturn("ns|ns2");
        ConfigUtil.getInstance().initRouteNameSpaceMatches();
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByNameSpace("ns"));
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByNameSpace("ns2"));
        Assert.assertFalse(ServiceFilter.getInstance().isNeedNotifyByNameSpace("ns3"));
        Assert.assertFalse(ServiceFilter.getInstance().isNeedNotifyByNameSpace(""));


        PowerMockito.when(System.getenv("NAMESPACE")).thenReturn("!ns&!ns2");
        ConfigUtil.getInstance().initRouteNameSpaceMatches();
        Assert.assertFalse(ServiceFilter.getInstance().isNeedNotifyByNameSpace("ns"));
        Assert.assertFalse(ServiceFilter.getInstance().isNeedNotifyByNameSpace("ns2"));
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByNameSpace("ns3"));

    }

    /*
     * @Test public void test_isNeedNotifyByVisualRange(){
     * 
     * Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByVisualRange("0"));
     * Assert.assertFalse(ServiceFilter.getInstance().isNeedNotifyByVisualRange("1"));
     * Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByVisualRange("0|1")); }
     */

    @Test
    public void test_isNeedNotifyByProtocol() {
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByProtocol("HTTP"));
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByProtocol("UI"));
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByProtocol("REST"));
        Assert.assertFalse(ServiceFilter.getInstance().isNeedNotifyByProtocol("TCP"));
    }

    @Test
    public void test_isNeedNotifyByNetwork_plane_typeMatches() {

        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("ROUTE_LABELS")).thenReturn("network_plane_type:network");
        ConfigUtil.getInstance().initRouteLabelsMatches();
        Assert.assertFalse(ServiceFilter.getInstance().isNeedNotifyByNetwork_plane_typeMatches("net"));
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByNetwork_plane_typeMatches("network"));

        PowerMockito.when(System.getenv("ROUTE_LABELS")).thenReturn("network_plane_type:net1|net2");
        ConfigUtil.getInstance().initRouteLabelsMatches();
        Assert.assertFalse(ServiceFilter.getInstance().isNeedNotifyByNetwork_plane_typeMatches("net"));
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByNetwork_plane_typeMatches("net1"));
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByNetwork_plane_typeMatches("net2"));

    }

    @Test
    public void test_isNeedNotifyByRouteLabels() {
        Map<String, String> labelMap = new HashMap<String, String>();
        labelMap.put("lab1", "val1");

        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("ROUTE_LABELS")).thenReturn("lab1:val,visualRange:1");
        ConfigUtil.getInstance().initRouteLabelsMatches();
        Assert.assertFalse(ServiceFilter.getInstance().isNeedNotifyByRouteLabels(labelMap));

        PowerMockito.when(System.getenv("ROUTE_LABELS")).thenReturn("lab1:val1");
        ConfigUtil.getInstance().initRouteLabelsMatches();
        Assert.assertTrue(ServiceFilter.getInstance().isNeedNotifyByRouteLabels(labelMap));

    }

    @Test
    public void test_isFilterService() {
        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("NAMESPACE")).thenReturn("ns1");
        ConfigUtil.getInstance().initRouteNameSpaceMatches();

        PowerMockito.when(System.getenv("ROUTE_LABELS"))
                        .thenReturn("visualRange:0,network_plane_type:net,customLabel:custom|custom2");
        ConfigUtil.getInstance().initRouteLabelsMatches();

        List<String> tagList = new ArrayList<String>();
        tagList.add("\"base\":{\"protocol\":\"REST\",\"version\":\"v1\",\"url\":\"/api/msbtest/v1\"}");
        tagList.add("\"labels\":{\"visualRange\":\"0\",\"network_plane_type\":\"net\",\"customLabel\":\"custom\"}");
        tagList.add("\"ns\":{\"namespace\":\"ns1\"}");
        Assert.assertTrue(ServiceFilter.getInstance().isFilterService(tagList));

        tagList.clear();
        tagList.add("\"base\":{\"protocol\":\"TCP\",\"version\":\"v1\",\"url\":\"/api/msbtest/v1\"}");
        Assert.assertFalse(ServiceFilter.getInstance().isFilterService(tagList));

        tagList.clear();
        tagList.add("\"base\":{\"protocol\":\"UI\",\"version\":\"v1\",\"url\":\"/api/msbtest/v1\"}");
        tagList.add("\"ns\":{\"namespace\":\"ns2\"}");
        Assert.assertFalse(ServiceFilter.getInstance().isFilterService(tagList));

        tagList.clear();
        tagList.add("\"base\":{\"protocol\":\"UI\",\"version\":\"v1\",\"url\":\"/api/msbtest/v1\"}");
        tagList.add("\"ns\":{\"namespace\":\"ns1\"}");
        tagList.add("\"labels\":{\"visualRange\":\"1\",\"network_plane_type\":\"net\",\"customLabel\":\"custom\"}");
        Assert.assertFalse(ServiceFilter.getInstance().isFilterService(tagList));

        tagList.clear();
        tagList.add("\"base\":{\"protocol\":\"UI\",\"version\":\"v1\",\"url\":\"/api/msbtest/v1\"}");
        tagList.add("\"ns\":{\"namespace\":\"ns1\"}");
        tagList.add("\"labels\":{\"visualRange\":\"0\",\"network_plane_type\":\"net2\",\"customLabel\":\"custom\"}");
        Assert.assertFalse(ServiceFilter.getInstance().isFilterService(tagList));

        tagList.clear();
        tagList.add("\"base\":{\"protocol\":\"UI\",\"version\":\"v1\",\"url\":\"/api/msbtest/v1\"}");
        tagList.add("\"ns\":{\"namespace\":\"ns1\"}");
        tagList.add("\"labels\":{\"visualRange\":\"0\",\"network_plane_type\":\"net\",\"customLabel\":\"custom3\"}");
        Assert.assertFalse(ServiceFilter.getInstance().isFilterService(tagList));

    }

    @Test
    public void test_transMicroServiceInfoFromConsul() {
        List<String> tagList = new ArrayList<String>();
        tagList.add("\"base\":{\"protocol\":\"REST\",\"version\":\"v1\",\"url\":\"/api/msbtest/v1\"}");
        tagList.add("\"labels\":{\"visualRange\":\"0\",\"network_plane_type\":\"net\",\"customLabel\":\"custom\"}");
        tagList.add("\"ns\":{\"namespace\":\"ns1\"}");

        Service service = ImmutableService.builder().id("id").port(8686).address("10.74.165.246").service("msbtest")
                        .addAllTags(tagList).createIndex(0).modifyIndex(0).build();
        ServiceHealth serviceHealth = ImmutableServiceHealth.builder().service(service)
                        .node(ImmutableNode.builder().node("server").address("192.168.1.98").build()).build();
        List<ServiceHealth> serviceHealthList = new ArrayList<ServiceHealth>();
        serviceHealthList.add(serviceHealth);

        Map<String, MicroServiceFullInfo> serviceMap =
                        ServiceFilter.getInstance().transMicroServiceInfoFromConsul(serviceHealthList);
        Assert.assertTrue(serviceMap.containsKey("v1"));

        MicroServiceFullInfo microService = new MicroServiceFullInfo();
        microService.setServiceName("msbtest");
        microService.setVersion("v1");
        microService.setUrl("/api/msbtest/v1");
        microService.setProtocol("REST");
        microService.setVisualRange("0");
        microService.setNamespace("ns1");

        Set<Node> nodes = new HashSet<Node>();
        nodes.add(new Node("10.74.165.246", "8686"));
        microService.setNodes(nodes);



        Assert.assertEquals(microService, serviceMap.get("v1"));


    }



}
