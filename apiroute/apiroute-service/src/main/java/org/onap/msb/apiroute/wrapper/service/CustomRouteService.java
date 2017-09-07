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

import org.onap.msb.apiroute.api.CustomRouteInfo;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.wrapper.dao.DAOFactory;
import org.onap.msb.apiroute.wrapper.dao.route.IRouteDAO;
import org.onap.msb.apiroute.wrapper.dao.route.bean.Metadata;
import org.onap.msb.apiroute.wrapper.dao.route.bean.Node;
import org.onap.msb.apiroute.wrapper.dao.route.bean.RouteInfo;
import org.onap.msb.apiroute.wrapper.dao.route.bean.Spec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CustomRouteService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRouteService.class);

    private static final CustomRouteService instance = new CustomRouteService();
    private IRouteDAO routeDAO = DAOFactory.getRouteDAO();

    private CustomRouteService() {
    }

    public static CustomRouteService getInstance() {
        return instance;
    }

    public void saveCustomRouteService2Redis(CustomRouteInfo customRouteInfo, String routeKey) throws Exception {
        if(customRouteInfo ==null){
            throw new Exception("input customRouteInfo to be saved is null!");
        }
        RouteInfo routeInfo = CustomRouteAdapter.toRouteInfo(customRouteInfo);
        routeDAO.saveRoute(routeKey, routeInfo);
    }

    public long deleteCustomRouteService2Redis(String routeKey) throws Exception {
        return routeDAO.deleteRoute(routeKey);
    }

    public long deleteMultiCustomRouteService2Redis(String routeKeyPattern) throws Exception {
        return routeDAO.deleteMultiRoute(routeKeyPattern);
    }

    public CustomRouteInfo getCustomRouteInstance(String routeKey) throws Exception {
        CustomRouteInfo customRouteInfo = null;
        RouteInfo routeInfo = null;
        routeInfo = routeDAO.queryRoute(routeKey);
        if(routeInfo!=null) {
            customRouteInfo = CustomRouteAdapter.fromRouteInfo(routeInfo);
        }
        return customRouteInfo;
    }

    public List<CustomRouteInfo> getMultiCustomRouteInstances(String customRedisKeyPattern) throws Exception {
        List<CustomRouteInfo> customRouteList = new ArrayList<>();
        List<RouteInfo> routeInfoList = routeDAO.queryMultiRoute(customRedisKeyPattern);
        for (RouteInfo routeInfo : routeInfoList) {
            if (routeInfo != null) {
                CustomRouteInfo customRouteInfo = CustomRouteAdapter.fromRouteInfo(routeInfo);;
                customRouteList.add(customRouteInfo);
            }
        }
        return customRouteList;
    }

    public void updateCustomRouteStatus2Redis(String routeKey,String status) throws Exception {
        RouteInfo routeInfo = routeDAO.queryRoute(routeKey);
        if(routeInfo != null){
            routeInfo.setStatus(status);
            routeDAO.saveRoute(routeKey,routeInfo);
        }else{
            throw new Exception("service to be updated is not exist! Update failed");
        }
    }

}

class CustomRouteAdapter {
    public static RouteInfo toRouteInfo(CustomRouteInfo customRouteInfo) {
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setStatus(customRouteInfo.getStatus());


        Spec spec = new Spec();
        spec.setVisualRange(customRouteInfo.getVisualRange());
        spec.setUrl(customRouteInfo.getUrl().trim());
        spec.setPublish_port(customRouteInfo.getPublish_port());
        spec.setHost(customRouteInfo.getHost());
        spec.setConsulServiceName(customRouteInfo.getConsulServiceName());
        spec.setUseOwnUpstream(customRouteInfo.getUseOwnUpstream());
        spec.setPublish_protocol(customRouteInfo.getPublishProtocol());
        spec.setEnable_ssl(customRouteInfo.isEnable_ssl());
        spec.setControl(customRouteInfo.getControl());
        RouteServer[] routeServers = customRouteInfo.getServers();
        List<Node> nodeList = new ArrayList<>();
        for (RouteServer server: routeServers){
            Node node = new Node();
            node.setIp(server.getIp());
            node.setPort(Integer.parseInt(server.getPort()));
            node.setWeight(server.getWeight());
            nodeList.add(node);
        }
        spec.setNodes(nodeList.toArray(new Node[]{}));
        routeInfo.setSpec(spec);

        Metadata metadata = new Metadata();
        metadata.setName(customRouteInfo.getServiceName());
        metadata.setNamespace(customRouteInfo.getNamespace());
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);
        metadata.setUpdateTimestamp(now.getTime());
        routeInfo.setMetadata(metadata);

        return routeInfo;
    }

    public static CustomRouteInfo fromRouteInfo(RouteInfo routeInfo) {
        CustomRouteInfo customRouteInfo = new CustomRouteInfo();
        customRouteInfo.setStatus(routeInfo.getStatus());

        Spec spec = routeInfo.getSpec();
        customRouteInfo.setVisualRange(spec.getVisualRange());
        customRouteInfo.setUrl(spec.getUrl());
        customRouteInfo.setPublish_port(spec.getPublish_port());
        customRouteInfo.setHost(spec.getHost());
        customRouteInfo.setConsulServiceName(spec.getConsulServiceName());
        customRouteInfo.setUseOwnUpstream(spec.getUseOwnUpstream());
        customRouteInfo.setPublishProtocol(spec.getPublish_protocol());
        customRouteInfo.setEnable_ssl(spec.isEnable_ssl());
        customRouteInfo.setControl(spec.getControl());
        Node[] nodes = spec.getNodes();
        List<RouteServer> routeServerList = new ArrayList<>();
        for (Node node: nodes){
            RouteServer routeServer = new RouteServer();
            routeServer.setIp(node.getIp());
            routeServer.setPort(String.valueOf(node.getPort()));
            routeServer.setWeight(node.getWeight());
            routeServerList.add(routeServer);
        }
        customRouteInfo.setServers(routeServerList.toArray(new RouteServer[]{}));

        Metadata metadata = routeInfo.getMetadata();
        customRouteInfo.setServiceName(metadata.getName());
        customRouteInfo.setNamespace(metadata.getNamespace());

        return customRouteInfo;
    }
}
