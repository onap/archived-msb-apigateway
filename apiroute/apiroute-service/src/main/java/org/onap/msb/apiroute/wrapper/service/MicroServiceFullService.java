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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.wrapper.dao.DAOFactory;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.dao.service.IServiceDAO;
import org.onap.msb.apiroute.wrapper.dao.service.bean.Metadata;
import org.onap.msb.apiroute.wrapper.dao.service.bean.ServiceInfo;
import org.onap.msb.apiroute.wrapper.dao.service.bean.Spec;
import org.onap.msb.apiroute.wrapper.util.MicroServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

public class MicroServiceFullService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MicroServiceFullService.class);

    private static MicroServiceFullService instance = new MicroServiceFullService();

    private IServiceDAO serviceDAO = DAOFactory.getServiceDAO();

    private MicroServiceFullService() {
    }

    public static MicroServiceFullService getInstance() {
        return instance;
    }

    public List<MicroServiceFullInfo> getAllMicroServiceInstances() throws Exception {
        String serviceKeyPattern = MicroServiceUtil.getPrefixedKey("*");

        List<MicroServiceFullInfo> microServiceFullInfoList = new ArrayList<>();
        List<ServiceInfo> serviceInfoList = serviceDAO.queryMultiService(serviceKeyPattern);
        for (ServiceInfo serviceInfo : serviceInfoList) {
            if (serviceInfo != null) {
                MicroServiceFullInfo microServiceFullInfo = MicroServiceFullAdapter.fromServiceInfo(serviceInfo);
                ;
                microServiceFullInfoList.add(microServiceFullInfo);
            }
        }
        return microServiceFullInfoList;
    }

    public Set<String> getAllMicroServiceKey() throws Exception {
        final Set<String> builder = new HashSet<String>();

        String serviceKeyPattern = MicroServiceUtil.getPrefixedKey("*");
        Set<String> serviceKeySet = RedisAccessWrapper.filterKeys(serviceKeyPattern);

        Pattern serviceKeyRegexPattern = MicroServiceUtil.getServiceKeyRegexPattern();
        for (String serviceKey : serviceKeySet) {
            Matcher matcher = serviceKeyRegexPattern.matcher(serviceKey);
            if (matcher.matches()) {
                builder.add(matcher.group("servicename"));
            }
        }
        return builder;
    }

    public void saveMicroServiceInfo2Redis(MicroServiceFullInfo microServiceFullInfo) throws Exception {
        if(microServiceFullInfo ==null){
            throw new Exception("input microServiceInfo to be saved is null!");
        }
        ServiceInfo serviceInfo = MicroServiceFullAdapter.toServiceInfo(microServiceFullInfo);
        String serviceKey = MicroServiceUtil.getServiceKey(microServiceFullInfo.getServiceName(),microServiceFullInfo.getVersion());
        serviceDAO.saveService(serviceKey,serviceInfo);
    }

    public void updateMicroServiceStatus(String serviceName, String version, String status)
            throws Exception {
        if (null == version || "null".equals(version)) {
            version = "";
        }
        String serviceKey = MicroServiceUtil.getServiceKey(serviceName, version);
        ServiceInfo serviceInfo = serviceDAO.queryService(serviceKey);
        if(serviceInfo != null){
            serviceInfo.setStatus(status);
            serviceDAO.saveService(serviceKey,serviceInfo);
        }
    }

    public boolean existsMicroServiceInstance(String serviceName, String version)
            throws Exception {
        if (null == version || "null".equals(version)) {
            version = "";
        }
        String serviceKey = MicroServiceUtil.getServiceKey(serviceName, version);
        return RedisAccessWrapper.isExist(serviceKey);
    }

    public MicroServiceFullInfo getMicroServiceInstance(String serviceName, String version)
            throws Exception {
        if (null == version || "null".equals(version)) {
            version = "";
        }
        String serviceKey = MicroServiceUtil.getServiceKey(serviceName, version);

        MicroServiceFullInfo microServiceInfo = null;

        ServiceInfo serviceInfo = null;
        serviceInfo = serviceDAO.queryService(serviceKey);
        if(serviceInfo!=null) {
            microServiceInfo = MicroServiceFullAdapter.fromServiceInfo(serviceInfo);
        }
        return microServiceInfo;
    }

    /**
     * query all the versions of the given ServiceName
     * @param serviceName
     * @return
     * @throws Exception
     */
    public List<MicroServiceFullInfo> getAllVersionsOfTheService(String serviceName) throws Exception {
        String serviceKeyPattern = MicroServiceUtil.getPrefixedKey(serviceName, "*");

        List<MicroServiceFullInfo> microServiceFullInfoList = new ArrayList<>();
        List<ServiceInfo> serviceInfoList = serviceDAO.queryMultiService(serviceKeyPattern);
        for (ServiceInfo serviceInfo : serviceInfoList) {
            if (serviceInfo != null) {
                MicroServiceFullInfo microServiceFullInfo = MicroServiceFullAdapter.fromServiceInfo(serviceInfo);
                microServiceFullInfoList.add(microServiceFullInfo);
            }
        }
        return microServiceFullInfoList;
    }

    public void deleteMicroService(String serviceName, String version) throws Exception {
        if (null == version || "null".equals(version)) {
            version = "";
        }
        String serviceKey = MicroServiceUtil.getServiceKey(serviceName, version);
        serviceDAO.deleteService(serviceKey);
    }

    public long deleteMultiMicroService(String keyPattern) throws Exception {
        return serviceDAO.deleteMultiService(keyPattern);
    }
}

class MicroServiceFullAdapter {
    public static ServiceInfo toServiceInfo(MicroServiceFullInfo microServiceFullInfo) {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setApiVersion(microServiceFullInfo.getVersion());
        serviceInfo.setStatus(microServiceFullInfo.getStatus());


        Spec spec = new Spec();
        spec.setVisualRange(microServiceFullInfo.getVisualRange());
        spec.setUrl(microServiceFullInfo.getUrl());
        spec.setPublish_port(microServiceFullInfo.getPublish_port());
        spec.setHost(microServiceFullInfo.getHost());
        spec.setProtocol(microServiceFullInfo.getProtocol());
        spec.setLb_policy(microServiceFullInfo.getLb_policy());
        spec.setEnable_ssl(microServiceFullInfo.isEnable_ssl());
        Set<org.onap.msb.apiroute.api.Node> nodeSet = microServiceFullInfo.getNodes();
        List<org.onap.msb.apiroute.wrapper.dao.service.bean.Node> serviceNodeList = new ArrayList<>();
        for (org.onap.msb.apiroute.api.Node node : nodeSet) {
            org.onap.msb.apiroute.wrapper.dao.service.bean.Node serviceNode = new org.onap.msb.apiroute.wrapper.dao.service.bean.Node();
            serviceNode.setIp(node.getIp());
            serviceNode.setPort(node.getPort());
            serviceNode.setTtl(node.getTtl());
            serviceNodeList.add(serviceNode);
        }
        spec.setNodes(serviceNodeList.toArray(new org.onap.msb.apiroute.wrapper.dao.service.bean.Node[]{}));
        serviceInfo.setSpec(spec);

        Metadata metadata = new Metadata();
        metadata.setName(microServiceFullInfo.getServiceName());
        metadata.setNamespace(microServiceFullInfo.getNamespace());
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);
        metadata.setUpdateTimestamp(now.getTime());
        serviceInfo.setMetadata(metadata);

        return serviceInfo;
    }

    public static MicroServiceFullInfo fromServiceInfo(ServiceInfo serviceInfo) {
        MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();

        microServiceFullInfo.setVersion(serviceInfo.getApiVersion());
        microServiceFullInfo.setStatus(serviceInfo.getStatus());

        Spec spec = serviceInfo.getSpec();
        microServiceFullInfo.setVisualRange(spec.getVisualRange());
        microServiceFullInfo.setUrl(spec.getUrl());
        microServiceFullInfo.setPath(spec.getPath());
        microServiceFullInfo.setPublish_port(spec.getPublish_port());
        microServiceFullInfo.setHost(spec.getHost());
        microServiceFullInfo.setProtocol(spec.getProtocol());
        microServiceFullInfo.setLb_policy(spec.getLb_policy());
        microServiceFullInfo.setEnable_ssl(spec.isEnable_ssl());
        org.onap.msb.apiroute.wrapper.dao.service.bean.Node[] serviceNodes = spec.getNodes();
        List<org.onap.msb.apiroute.api.Node> nodeList = new ArrayList<>();
        for (org.onap.msb.apiroute.wrapper.dao.service.bean.Node serviceNode : serviceNodes) {
            org.onap.msb.apiroute.api.Node node = new org.onap.msb.apiroute.api.Node();
            node.setIp(serviceNode.getIp());
            node.setPort(String.valueOf(serviceNode.getPort()));
            node.setTtl(serviceNode.getTtl());
            nodeList.add(node);
        }
        microServiceFullInfo.setNodes(new HashSet<org.onap.msb.apiroute.api.Node>(nodeList));

        Metadata metadata = serviceInfo.getMetadata();
        microServiceFullInfo.setServiceName(metadata.getName());
        microServiceFullInfo.setNamespace(metadata.getNamespace());

        return microServiceFullInfo;
    }
}
