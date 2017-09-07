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

import org.onap.msb.apiroute.api.ApiRouteInfo;
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


public class ApiRouteService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRouteService.class);
    private static final ApiRouteService instance = new ApiRouteService();
    private IRouteDAO routeDAO = DAOFactory.getRouteDAO();

    private ApiRouteService() {
    }

    public static ApiRouteService getInstance() {
        return instance;
    }

    public void saveApiRouteService2Redis(ApiRouteInfo apiRouteInfo, String routeKey) throws Exception {
        if(apiRouteInfo ==null){
            throw new Exception("input apiRouteInfo to be saved is null!");
        }
        RouteInfo routeInfo = APIRouteAdapter.toRouteInfo(apiRouteInfo);
        routeDAO.saveRoute(routeKey, routeInfo);
    }

    public long deleteApiRouteService2Redis(String routeKey) throws Exception {
        return routeDAO.deleteRoute(routeKey);
    }

    public long deleteMultiApiRouteService2Redis(String routeKeyPattern) throws Exception {
        return routeDAO.deleteMultiRoute(routeKeyPattern);
    }

    public ApiRouteInfo getApiRouteInstance(String routeKey) throws Exception {
        ApiRouteInfo apiRouteInfo = null;
        RouteInfo routeInfo = null;
        routeInfo = routeDAO.queryRoute(routeKey);
        if(routeInfo!=null) {
            apiRouteInfo = APIRouteAdapter.fromRouteInfo(routeInfo);
        }
        return apiRouteInfo;
    }

    public List<ApiRouteInfo> getMultiApiRouteInstances(String apiRedisKeyPattern) throws Exception {
        List<ApiRouteInfo> apiRouteList = new ArrayList<>();
        List<RouteInfo> routeInfoList = routeDAO.queryMultiRoute(apiRedisKeyPattern);
        for (RouteInfo routeInfo : routeInfoList) {
            if (routeInfo != null) {
                 ApiRouteInfo apiRouteInfo = APIRouteAdapter.fromRouteInfo(routeInfo);;
                apiRouteList.add(apiRouteInfo);
            }
        }
        return apiRouteList;
    }

    public void updateApiRouteStatus2Redis(String routeKey,String status) throws Exception {
        RouteInfo routeInfo = routeDAO.queryRoute(routeKey);
        if(routeInfo != null){
            routeInfo.setStatus(status);
            routeDAO.saveRoute(routeKey,routeInfo);
        }else{
            throw new Exception("service to be updated is not exist! Update failed");
        }
    }
}

class APIRouteAdapter {
    public static RouteInfo toRouteInfo(ApiRouteInfo apiRouteInfo) {
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setApiVersion(apiRouteInfo.getVersion());
        routeInfo.setStatus(apiRouteInfo.getStatus());


        Spec spec = new Spec();
        spec.setVisualRange(apiRouteInfo.getVisualRange());
        spec.setUrl(apiRouteInfo.getUrl().trim());
        spec.setPublish_port(apiRouteInfo.getPublish_port());
        spec.setHost(apiRouteInfo.getHost());
        spec.setApijson(apiRouteInfo.getApiJson());
        spec.setApijsontype(apiRouteInfo.getApiJsonType());
        spec.setMetricsUrl(apiRouteInfo.getMetricsUrl());
        spec.setConsulServiceName(apiRouteInfo.getConsulServiceName());
        spec.setUseOwnUpstream(apiRouteInfo.getUseOwnUpstream());
        spec.setPublish_protocol(apiRouteInfo.getPublishProtocol());
        spec.setEnable_ssl(apiRouteInfo.isEnable_ssl());
        spec.setControl(apiRouteInfo.getControl());
        RouteServer[] routeServers = apiRouteInfo.getServers();
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
        metadata.setName(apiRouteInfo.getServiceName());
        metadata.setNamespace(apiRouteInfo.getNamespace());
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);
        metadata.setUpdateTimestamp(now.getTime());
        routeInfo.setMetadata(metadata);

        return routeInfo;
    }

    public static ApiRouteInfo fromRouteInfo(RouteInfo routeInfo) {
        ApiRouteInfo apiRouteInfo = new ApiRouteInfo();

        apiRouteInfo.setVersion(routeInfo.getApiVersion());
        apiRouteInfo.setStatus(routeInfo.getStatus());

        Spec spec = routeInfo.getSpec();
        apiRouteInfo.setVisualRange(spec.getVisualRange());
        apiRouteInfo.setUrl(spec.getUrl());
        apiRouteInfo.setPublish_port(spec.getPublish_port());
        apiRouteInfo.setHost(spec.getHost());
        apiRouteInfo.setApiJson(spec.getApijson());
        apiRouteInfo.setApiJsonType(spec.getApijsontype());
        apiRouteInfo.setMetricsUrl(spec.getMetricsUrl());
        apiRouteInfo.setConsulServiceName(spec.getConsulServiceName());
        apiRouteInfo.setUseOwnUpstream(spec.getUseOwnUpstream());
        apiRouteInfo.setPublishProtocol(spec.getPublish_protocol());
        apiRouteInfo.setEnable_ssl(spec.isEnable_ssl());
        apiRouteInfo.setControl(spec.getControl());
        Node[] nodes = spec.getNodes();
        List<RouteServer> routeServerList = new ArrayList<>();
        for (Node node: nodes){
            RouteServer routeServer = new RouteServer();
            routeServer.setIp(node.getIp());
            routeServer.setPort(String.valueOf(node.getPort()));
            routeServer.setWeight(node.getWeight());
            routeServerList.add(routeServer);
        }
        apiRouteInfo.setServers(routeServerList.toArray(new RouteServer[]{}));

        Metadata metadata = routeInfo.getMetadata();
        apiRouteInfo.setServiceName(metadata.getName());
        apiRouteInfo.setNamespace(metadata.getNamespace());

        return apiRouteInfo;
    }
}
