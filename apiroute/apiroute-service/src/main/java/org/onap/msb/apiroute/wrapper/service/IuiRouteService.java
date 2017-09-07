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
package org.onap.msb.apiroute.wrapper.service;

import org.onap.msb.apiroute.api.IuiRouteInfo;
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


public class IuiRouteService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRouteService.class);

    private static final IuiRouteService instance = new IuiRouteService();
    private IRouteDAO routeDAO = DAOFactory.getRouteDAO();

    private IuiRouteService() {
    }

    public static IuiRouteService getInstance() {
        return instance;
    }

    public void saveIuiRouteService2Redis(IuiRouteInfo iuiRouteInfo, String routeKey) throws Exception {
        if(iuiRouteInfo ==null){
            throw new Exception("input apiRouteInfo to be saved is null!");
        }
        RouteInfo routeInfo = IuiRouteAdapter.toRouteInfo(iuiRouteInfo);
        routeDAO.saveRoute(routeKey, routeInfo);
    }

    public long deleteIuiRouteService2Redis(String routeKey) throws Exception {
        return routeDAO.deleteRoute(routeKey);
    }

    public long deleteMultiIuiRouteService2Redis(String routeKeyPattern) throws Exception {
        return routeDAO.deleteMultiRoute(routeKeyPattern);
    }

    public IuiRouteInfo getIuiRouteInstance(String routeKey) throws Exception {
        IuiRouteInfo iuiRouteInfo = null;
        RouteInfo routeInfo = null;
        routeInfo = routeDAO.queryRoute(routeKey);
        if(routeInfo!=null) {
            iuiRouteInfo = IuiRouteAdapter.fromRouteInfo(routeInfo);
        }
        return iuiRouteInfo;
    }

    public List<IuiRouteInfo> getMultiIuiRouteInstances(String apiRedisKeyPattern) throws Exception {
        List<IuiRouteInfo> iuiRouteList = new ArrayList<>();
        List<RouteInfo> routeInfoList = routeDAO.queryMultiRoute(apiRedisKeyPattern);
        for (RouteInfo routeInfo : routeInfoList) {
            if (routeInfo != null) {
                IuiRouteInfo iuiRouteInfo = IuiRouteAdapter.fromRouteInfo(routeInfo);;
                iuiRouteList.add(iuiRouteInfo);
            }
        }
        return iuiRouteList;
    }

    public void updateIuiRouteStatus2Redis(String routeKey,String status) throws Exception {
        RouteInfo routeInfo = routeDAO.queryRoute(routeKey);
        if(routeInfo != null){
            routeInfo.setStatus(status);
            routeDAO.saveRoute(routeKey,routeInfo);
        }else{
            throw new Exception("service to be updated is not exist! Update failed");
        }
    }

}

class IuiRouteAdapter {
    public static RouteInfo toRouteInfo(IuiRouteInfo iuiRouteInfo) {
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setStatus(iuiRouteInfo.getStatus());


        Spec spec = new Spec();
        spec.setVisualRange(iuiRouteInfo.getVisualRange());
        spec.setUrl(iuiRouteInfo.getUrl().trim());
        spec.setPublish_port(iuiRouteInfo.getPublish_port());
        spec.setHost(iuiRouteInfo.getHost());
        spec.setConsulServiceName(iuiRouteInfo.getConsulServiceName());
        spec.setUseOwnUpstream(iuiRouteInfo.getUseOwnUpstream());
        spec.setPublish_protocol(iuiRouteInfo.getPublishProtocol());
        spec.setEnable_ssl(iuiRouteInfo.isEnable_ssl());
        spec.setControl(iuiRouteInfo.getControl());
        RouteServer[] routeServers = iuiRouteInfo.getServers();
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
        metadata.setName(iuiRouteInfo.getServiceName());
        metadata.setNamespace(iuiRouteInfo.getNamespace());
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);
        metadata.setUpdateTimestamp(now.getTime());
        routeInfo.setMetadata(metadata);
        return routeInfo;
    }

    public static IuiRouteInfo fromRouteInfo(RouteInfo routeInfo) {
        IuiRouteInfo iuiRouteInfo = new IuiRouteInfo();
        iuiRouteInfo.setStatus(routeInfo.getStatus());

        Spec spec = routeInfo.getSpec();
        iuiRouteInfo.setVisualRange(spec.getVisualRange());
        iuiRouteInfo.setUrl(spec.getUrl());
        iuiRouteInfo.setPublish_port(spec.getPublish_port());
        iuiRouteInfo.setHost(spec.getHost());
        iuiRouteInfo.setConsulServiceName(spec.getConsulServiceName());
        iuiRouteInfo.setUseOwnUpstream(spec.getUseOwnUpstream());
        iuiRouteInfo.setPublishProtocol(spec.getPublish_protocol());
        iuiRouteInfo.setEnable_ssl(spec.isEnable_ssl());
        iuiRouteInfo.setControl(spec.getControl());
        Node[] nodes = spec.getNodes();
        List<RouteServer> routeServerList = new ArrayList<>();
        for (Node node: nodes){
            RouteServer routeServer = new RouteServer();
            routeServer.setIp(node.getIp());
            routeServer.setPort(String.valueOf(node.getPort()));
            routeServer.setWeight(node.getWeight());
            routeServerList.add(routeServer);
        }
        iuiRouteInfo.setServers(routeServerList.toArray(new RouteServer[]{}));

        Metadata metadata = routeInfo.getMetadata();
        iuiRouteInfo.setServiceName(metadata.getName());
        iuiRouteInfo.setNamespace(metadata.getNamespace());

        return iuiRouteInfo;
    }
}
