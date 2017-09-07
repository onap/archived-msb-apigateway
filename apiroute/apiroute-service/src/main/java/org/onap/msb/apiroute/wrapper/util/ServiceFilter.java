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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.api.Node;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.Service;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.model.health.HealthCheck;



public class ServiceFilter {
    private static ServiceFilter instance = new ServiceFilter();

    private ServiceFilter() {}

    public static ServiceFilter getInstance() {
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceFilter.class);


    /**
     * Determine whether the service needs to send a notification TODO: filter according to the
     * agreement, the only notice of agreement for REST \HTTP\ UI interface MSB - REST
     * 
     * @param protocol
     * @return
     */
    public boolean isNeedNotifyByProtocol(String protocol) {
        return CommonUtil.contain(RouteUtil.FILTER_PROTOCOLS, protocol.trim());
    }

    /**
     * Determine whether the service needs to send a notification TODO: according to the visual
     * range filter conditions Regular language: all 、 default 、 !default 、 A、 |A 、 A|B、 !A&!B
     * 
     * @param visualRange
     * @return
     */
    public boolean isNeedNotifyByNameSpace(String nameSpace) {

        String namespaceMatches = ConfigUtil.getInstance().getNamespaceMatches();
        String[] namespaceArray = StringUtils.split(namespaceMatches, "|");

        if (CommonUtil.contain(namespaceArray, "all")) {
            return true;
        }

        if (CommonUtil.contain(namespaceArray, "default")) {
            if (StringUtils.isEmpty(nameSpace) || "default".equals(nameSpace)) {
                return true;
            } else {
                return false;
            }
        }

        if (CommonUtil.contain(namespaceArray, "!default")) {
            if (StringUtils.isNotEmpty(nameSpace) && !"default".equals(nameSpace)) {
                return true;
            } else {
                return false;
            }
        }
        try {
            String namespaceReg;
            if (namespaceMatches.contains("!")) {
                namespaceReg = "^" + namespaceMatches.replaceAll("!", "").replaceAll("&", "|") + "$";
                return !Pattern.matches(namespaceReg, nameSpace);
            } else {
                namespaceReg = "^" + namespaceMatches + "$";
                return Pattern.matches(namespaceReg, nameSpace);
            }

        } catch (Exception e) {
            LOGGER.error(" Regular " + namespaceMatches + " throw exception:" + e.getMessage());
            return false;
        }
    }

    public boolean isNeedNotifyByVisualRange(String visualRange) {

        String[] routeVisualRangeArray = StringUtils.split(ConfigUtil.getInstance().getVisualRangeMatches(), "|");

        String[] serviceVisualRangeArray = StringUtils.split(visualRange, "|");

        if (CommonUtil.contain(serviceVisualRangeArray, routeVisualRangeArray)) {
            return true;
        }

        return false;

    }

    public boolean isNeedNotifyByNetwork_plane_typeMatches(String network_plane_type) {

        String network_plane_typeMatches = ConfigUtil.getInstance().getNetwork_plane_typeMatches();
        if (StringUtils.isBlank(network_plane_typeMatches))
            return true;

        String[] routeNetwork_plane_typeArray = StringUtils.split(network_plane_typeMatches, "|");

        String[] serviceVisualRangeArray = StringUtils.split(network_plane_type, "|");

        if (CommonUtil.contain(serviceVisualRangeArray, routeNetwork_plane_typeArray)) {
            return true;
        }

        return false;

    }

    /**
     * Determine whether the service needs to send a notification TODO: according to the visual
     * range filter conditions
     * 
     * @param visualRange
     * @return
     */
    public boolean isNeedNotifyByRouteLabels(Map<String, String> labelMap) {

        Map<String, String> labelMapMatches = ConfigUtil.getInstance().getLabelMapMatches();

        if (labelMapMatches == null || labelMapMatches.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, String> entry : labelMapMatches.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Multiple values match

            if (StringUtils.isBlank(labelMap.get(key))) {
                continue;
            }

            String[] routeLalelsArray = StringUtils.split(value, "|");

            String[] serviceLabelsArray = StringUtils.split(labelMap.get(key), "|");

            if (CommonUtil.contain(routeLalelsArray, serviceLabelsArray)) {
                return true;
            }

        }

        return false;
    }



    /*
     * public boolean isNeedNotifyByRoute(String protocol, String namespace, String visualRange,
     * String network_plane_type, Map<String, String> labelMap) {
     * 
     * return isNeedNotifyByProtocol(protocol) && isNeedNotifyByNameSpace(namespace) &&
     * isNeedNotifyByVisualRange(visualRange) && isNeedNotifyByRouteLabels(labelMap) &&
     * isNeedNotifyByNetwork_plane_typeMatches(network_plane_type);
     * 
     * }
     */

    public boolean isFilterCheck(ServiceHealth health) {
        return isFilterHealthCheck(health.getChecks()) && isFilterService(health.getService().getTags());
    }

    /**
     * @Title isFilterHealthCheck
     * @Description TODO(判断服务实例的健康检查信息，全部为passing表示健康检查有效)
     * @param List<HealthCheck>
     * @return boolean checkList示例——"Checks" : [{ "Node" : "server", "CheckID" : "serfHealth",
     *         "Name" : "Serf Health Status", "Status" : "passing", "Notes" : "", "Output" : "Agent
     *         alive and reachable", "ServiceID" : "", "ServiceName" : "", "CreateIndex" : 65536,
     *         "ModifyIndex" : 65536 }, { "Node" : "server", "CheckID" :
     *         "service:_tcp_roundrobin_1_10.74.151.26_22", "Name" : "Service 'tcp_roundrobin_1'
     *         check", "Status" : "critical", "Notes" : "", "Output" : "dial tcp: missing port in
     *         address ok", "ServiceID" : "_tcp_roundrobin_1_10.74.151.26_22", "ServiceName" :
     *         "tcp_roundrobin_1", "CreateIndex" : 75988, "ModifyIndex" : 76173 } ]
     */
    public boolean isFilterHealthCheck(List<HealthCheck> checkList) {
        if (checkList.isEmpty()) {
            return true;
        }

        for (HealthCheck check : checkList) {
            if (!RouteUtil.HEALTH_CHECK_PASSING.equals(check.getStatus())) {
                return false;
            }
        }

        return true;
    }



    /**
     * @Title isFilterService
     * @Description TODO(判断来自consul的服务信息是否需要过滤)
     * @param List<String>
     * @return boolean tagList示例—— [
     *         "\"base\":{\"protocol\":\"REST\",\"is_manual\":\"true\",\"version\":\"v1\",\"url\":\"/api/msbtest/v1\"}"
     *         , "\"ns\":{\"namespace\":\"nsName\"}",
     *         "\"labels\":{\"visualRange\":\"0\",\"network_plane_type\":\"net\",\"customLabel\":\"custom\"}"
     *         ]
     */
    @SuppressWarnings("unchecked")
    public boolean isFilterService(List<String> tagList) {

        if (tagList == null || tagList.size() == 0)
            return false;

        String visualRange = "", network_plane_type = "", protocol = "", namespace = "";

        // 针对多版本不同属性的tag会有多个，只要其中一个匹配即通过过滤,默认不通过
        boolean visualRangeFilter = false, protocolFilter = false, namespaceFilter = false;
        boolean hasnamespace = false;

        try {

            for (String tag : tagList) {

                // 提取基础属性tag
                if (!protocolFilter && tag.startsWith("\"base\"")) {
                    String ms_base_json = tag.split("\"base\":")[1];

                    Map<String, String> baseMap =
                                    (Map<String, String>) JacksonJsonUtil.jsonToBean(ms_base_json, Map.class);

                    if (baseMap.get("protocol") != null) {
                        protocol = baseMap.get("protocol");
                        if ("PORTAL".equalsIgnoreCase(protocol)) {
                            protocol = "HTTP";
                        }

                        if (isNeedNotifyByProtocol(protocol)) {
                            protocolFilter = true;
                        }

                    }



                    continue;
                }

                // 提取命名空间属性tag
                if (!namespaceFilter && tag.startsWith("\"ns\"")) {
                    String ms_ns_json = tag.split("\"ns\":")[1];
                    Map<String, String> nsMap = (Map<String, String>) JacksonJsonUtil.jsonToBean(ms_ns_json, Map.class);

                    if (nsMap.get("namespace") != null) {
                        namespace = nsMap.get("namespace");
                        hasnamespace = true;

                        if (isNeedNotifyByNameSpace(namespace)) {
                            namespaceFilter = true;
                        }
                    }


                    continue;
                }

                // 提取Label属性tag
                if (tag.startsWith("\"labels\"")) {
                    String ms_labels_json = "{" + tag.split("\"labels\":\\{")[1];
                    // 自定义label标签属性
                    Map<String, String> labelMap =
                                    (Map<String, String>) JacksonJsonUtil.jsonToBean(ms_labels_json, Map.class);



                    if (!visualRangeFilter && labelMap.get("visualRange") != null) {
                        visualRange = labelMap.get("visualRange");
                        labelMap.remove("visualRange"); // 自定义标签排除可见范围和网络平面

                        if (isNeedNotifyByVisualRange(visualRange)) {
                            visualRangeFilter = true;
                        }
                    }


                    if (labelMap.get("network_plane_type") != null) {
                        network_plane_type = labelMap.get("network_plane_type");
                        labelMap.remove("network_plane_type");
                    }
                    if (!isNeedNotifyByNetwork_plane_typeMatches(network_plane_type)) {
                        return false;
                    }

                    if (!isNeedNotifyByRouteLabels(labelMap)) {
                        return false;
                    }

                    continue;
                }

            }

            // 针对无命名空间的服务判断是否过滤
            if (!hasnamespace && isNeedNotifyByNameSpace(namespace)) {
                namespaceFilter = true;
            }

            return visualRangeFilter && protocolFilter && namespaceFilter;


        } catch (Exception e) {
            LOGGER.error(" read tag  throw exception", e);
            return false;
        }


    }



    @SuppressWarnings("unchecked")
    public Map<String, MicroServiceFullInfo> transMicroServiceInfoFromConsul(List<ServiceHealth> serviceNodeList) {
        // 同名多版本服务MAP
        Map<String, MicroServiceFullInfo> microServiceInfo4version = new HashMap<String, MicroServiceFullInfo>();


        for (ServiceHealth serviceNode : serviceNodeList) {

            MicroServiceFullInfo microServiceInfo = new MicroServiceFullInfo();
            String url = "";
            String version = "", visualRange = "", protocol = "", lb_policy = "", namespace = "", host = "", path = "",
                            publish_port = "";
            boolean enable_ssl = false;

            HashSet<Node> nodes = new HashSet<Node>();

            Service service = serviceNode.getService();
            String serviceName = service.getService();

            try {
                List<String> tagList = service.getTags();

                for (String tag : tagList) {

                    if (tag.startsWith("\"base\"")) {
                        String ms_base_json = tag.split("\"base\":")[1];


                        Map<String, String> baseMap =
                                        (Map<String, String>) JacksonJsonUtil.jsonToBean(ms_base_json, Map.class);
                        if (baseMap.get("url") != null) {
                            url = baseMap.get("url");
                        }

                        if (baseMap.get("version") != null) {
                            version = baseMap.get("version");
                        }

                        if (baseMap.get("protocol") != null) {
                            protocol = baseMap.get("protocol");
                        }

                        if (baseMap.get("host") != null) {
                            host = baseMap.get("host");
                        }

                        if (baseMap.get("path") != null) {
                            path = baseMap.get("path");
                        }

                        if (baseMap.get("publish_port") != null) {
                            publish_port = baseMap.get("publish_port");
                        }


                        if (baseMap.get("enable_ssl") != null) {
                            enable_ssl = Boolean.valueOf(baseMap.get("enable_ssl"));
                        }

                        continue;
                    }



                    if (tag.startsWith("\"ns\"")) {
                        String ms_ns_json = tag.split("\"ns\":")[1];
                        Map<String, String> nsMap =
                                        (Map<String, String>) JacksonJsonUtil.jsonToBean(ms_ns_json, Map.class);

                        if (nsMap.get("namespace") != null) {
                            namespace = nsMap.get("namespace");
                        }

                        continue;
                    }

                    if (tag.startsWith("\"labels\"")) {
                        String ms_labels_json = "{" + tag.split("\"labels\":\\{")[1];
                        Map<String, String> labelMap =
                                        (Map<String, String>) JacksonJsonUtil.jsonToBean(ms_labels_json, Map.class);


                        if (labelMap.get("visualRange") != null) {
                            visualRange = labelMap.get("visualRange");
                        }

                        /*
                         * if (labelMap.get("network_plane_type") != null) { network_plane_type =
                         * labelMap.get("network_plane_type"); }
                         */

                        continue;
                    }

                    if (tag.startsWith("\"lb\"")) {
                        String ms_lb_json = tag.split("\"lb\":")[1];
                        Map<String, String> lbMap =
                                        (Map<String, String>) JacksonJsonUtil.jsonToBean(ms_lb_json, Map.class);

                        if (lbMap.get("lb_policy") != null) {
                            lb_policy = lbMap.get("lb_policy");
                        }
                        continue;
                    }

                }



            } catch (Exception e) {
                LOGGER.error(serviceName + " read tag  throw exception", e);
            }

            if (!microServiceInfo4version.containsKey(version)) {

                if ("PORTAL".equalsIgnoreCase(protocol)) {
                    protocol = "HTTP";
                    microServiceInfo.setCustom(RouteUtil.CUSTOM_PORTAL);
                }

                microServiceInfo.setProtocol(protocol);
                microServiceInfo.setUrl(url);
                microServiceInfo.setServiceName(serviceName);
                microServiceInfo.setLb_policy(lb_policy);
                microServiceInfo.setVisualRange(visualRange);

                microServiceInfo.setEnable_ssl(enable_ssl);
                microServiceInfo.setVersion(version);
                microServiceInfo.setNamespace(namespace);
                microServiceInfo.setHost(host);
                microServiceInfo.setPath(path);
                // 系统间apigateway 保存publish_port
                if ("0".equals(ConfigUtil.getInstance().getVisualRangeMatches())) {
                    microServiceInfo.setPublish_port(publish_port);
                }

                nodes.add(new Node(service.getAddress(), String.valueOf(service.getPort())));
                microServiceInfo.setNodes(nodes);

                microServiceInfo4version.put(version, microServiceInfo);
            } else {

                Set<Node> newNodes = microServiceInfo4version.get(version).getNodes();
                // 默认node是注册信息的IP和port
                newNodes.add(new Node(service.getAddress(), String.valueOf(service.getPort())));

                // 同名多版本同步
                microServiceInfo4version.get(version).setNodes(newNodes);

            }


            /*
             * // 健康检查信息 List<Check> checks = value.getChecks(); node.setStatus("passing"); for
             * (Check check : checks) { if (!"passing".equals(check.getStatus())) {
             * node.setStatus(check.getStatus()); break; } }
             */



        }

        return microServiceInfo4version;

    }

}
