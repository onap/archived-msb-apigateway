/**
 * Copyright 2016 2015-2016 ZTE, Inc. and others. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openo.msb;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.openo.msb.api.MicroServiceFullInfo;
import org.openo.msb.api.MicroServiceInfo;
import org.openo.msb.api.Node;
import org.openo.msb.wrapper.MicroServiceWrapper;
import org.openo.msb.wrapper.consul.CatalogClient;
import org.openo.msb.wrapper.consul.Consul;
import org.openo.msb.wrapper.consul.HealthClient;
import org.openo.msb.wrapper.consul.cache.CatalogCache;
import org.openo.msb.wrapper.consul.cache.ConsulCache;
import org.openo.msb.wrapper.consul.cache.ConsulCache4Map;
import org.openo.msb.wrapper.consul.cache.HealthCache;
import org.openo.msb.wrapper.consul.cache.ServiceCache;
import org.openo.msb.wrapper.consul.model.catalog.CatalogService;
import org.openo.msb.wrapper.consul.model.catalog.ServiceInfo;
import org.openo.msb.wrapper.consul.model.health.Service;
import org.openo.msb.wrapper.consul.model.health.ServiceHealth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulClientApp {

    private final Consul consul;
    private final CatalogClient catalogClient;
    private final HealthClient healthClient;
    private AtomicReference<List<HealthCache>> cacheList = new AtomicReference<List<HealthCache>>(
            new ArrayList<HealthCache>());


    private static final Logger LOGGER = LoggerFactory.getLogger(ConsulClientApp.class);

    public ConsulClientApp(String ip, int port) {
        URL url = null;
        try {
            url = new URL("http", ip, port, "");
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            LOGGER.error("start  ConsulClientApp throw exception", e1);
            throw new RuntimeException(e1);
        }
        this.consul = Consul.builder().withUrl(url).build(); // connect to Consul on localhost
        this.catalogClient = consul.catalogClient();
        this.healthClient = consul.healthClient();
    }

    public Consul getConsul() {
        return consul;
    }

    public CatalogClient getCatalogClient() {
        return catalogClient;
    }

    private void stopNodeListen(String serviceName) {
        try {

            ListIterator<HealthCache> cacheListLit = cacheList.get().listIterator();
            while (cacheListLit.hasNext()) {
                HealthCache cache = (HealthCache) cacheListLit.next();
                if (cache.getServiceName().equals(serviceName)) {

                    cache.stop();
                    cacheListLit.remove();
                    LOGGER.info(cache.getServiceName() + " NodeListen stoped");
                    break;
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOGGER.error("stop Node:[" + serviceName + "] Listen throw exception", e);
        }


    }

    /**
     * @Title startServiceListen
     * @Description TODO(Open the consul registration services to monitor)
     * @return void
     */
    public void startServiceListen() {
        final ServiceCache serviceCache = ServiceCache.newCache(catalogClient, 30);
        serviceCache.addListener(new ConsulCache4Map.Listener<String, Map<String, List<String>>>() {
            @Override
            public void notify(List<ServiceInfo> oldValues, List<ServiceInfo> newValues) {
                // do Something with updated server List
                LOGGER.info("--new service notify--");

                List<ServiceInfo> deRegisterServiceList = getDiffrent(oldValues, newValues);


                for (ServiceInfo serviceInfo : deRegisterServiceList) {
                    try {
                       
                            MicroServiceWrapper.getInstance().deleteMicroService(
                                    serviceInfo.getServiceName(), serviceInfo.getVersion());
                        

                        stopNodeListen(serviceInfo.getServiceName());
                        LOGGER.info("Cancel MicroServiceInfo and stop node listen successs:"
                                + serviceInfo.getServiceName());
                    } catch (Exception e) {
                        LOGGER.error("Cancel MicroServiceInfo and stop node listen FAIL : ", e);

                    }

                }


                List<ServiceInfo> registerServiceList = getDiffrent(newValues, oldValues);
                for (ServiceInfo serviceInfo : registerServiceList) {

                    // if (deRegisterServiceList.contains(serviceInfo)) continue;


                    LOGGER.info(" new serviceName:" + serviceInfo.getServiceName() + "  version:"
                            + serviceInfo.getVersion());
                    // Open Node to monitor new registration service
                    startHealthNodeListen(serviceInfo.getServiceName(), serviceInfo.getVersion());

                }


            }

        });

        try {
            LOGGER.info("start...consul ... service..Listening.");
            serviceCache.start();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOGGER.error("start...service..Listen throw exception", e);
        }
    }


    /**
     * @Title startHealthNodeListen
     * @Description TODO(Open a service node changes to monitor, only to return to health service)
     * @param serviceName
     * @return
     * @return HealthCache
     */
    private HealthCache startHealthNodeListen(final String serviceName, final String version) {
        final HealthCache healthCache = HealthCache.newCache(healthClient, serviceName, 30);
        healthCache.addListener(new HealthCache.Listener<String, ServiceHealth>() {
            @Override
            public void notify(Map<String, ServiceHealth> newValues) {
                // do Something with updated server map
                LOGGER.info(serviceName + "--new node notify--");

                if (newValues.isEmpty()) {
                    LOGGER.info(serviceName + "--nodeList is Empty--");

                  
                        MicroServiceWrapper.getInstance().deleteMicroService(serviceName, version);
                  
                    // try {
                    // healthCache.stop();
                    // } catch (Exception e) {
                    // LOGGER.equals(serviceName+"-- stop Node error:"+e.getMessage());
                    // }

                } else {

                    MicroServiceInfo microServiceInfo = new MicroServiceInfo();
                    HashSet<Node> nodes = new HashSet<Node>();
                    String url = "";
                    String version = "", visualRange = "", protocol = "",lb_policy="";

                    for (Map.Entry<String, ServiceHealth> entry : newValues.entrySet()) {
                        String nodeName = entry.getKey().toString();
                        ServiceHealth value = (ServiceHealth) entry.getValue();

                        Node node = new Node();
                        Service service = value.getService();
                        node.setIp(service.getAddress());
                        node.setPort(String.valueOf(service.getPort()));


                        try {
                            List<String> tagList = service.getTags();
                            for (String tag : tagList) {
                                if (tag.startsWith("url")) {
                                    if (tag.split(":").length == 2) {
                                        url = tag.split(":")[1];
                                    } else {
                                        url = "";
                                    }


                                    continue;
                                }
                                if (tag.startsWith("version")) {
                                    if (tag.split(":").length == 2) {
                                        version = tag.split(":")[1];
                                    } else {
                                        version = "";
                                    }
                                    continue;
                                }
                                if (tag.startsWith("protocol")) {
                                    protocol = tag.split(":")[1];
                                    continue;
                                }
                                if (tag.startsWith("visualRange")) {
                                    visualRange = tag.split(":")[1];
                                    continue;
                                }
                                
                                if (tag.startsWith("lb_policy")) {
                                    lb_policy = tag.split(":")[1];
                                    continue;
                                }

                            }


                        } catch (Exception e) {
                            LOGGER.error(serviceName + " read tag  throw exception", e);
                            System.out.println(serviceName + " read tag  throw exception");
                        }

                        nodes.add(node);
                    }

                    microServiceInfo.setNodes(nodes);
                    microServiceInfo.setProtocol(protocol);
                    microServiceInfo.setUrl(url);
                    microServiceInfo.setServiceName(serviceName);
                    microServiceInfo.setLb_policy(lb_policy);
                    if (!visualRange.isEmpty()) {
                        microServiceInfo.setVisualRange(visualRange);
                    }
                    microServiceInfo.setVersion(version);

                    try {
                        MicroServiceFullInfo microServiceFullInfo =
                                MicroServiceWrapper.getInstance().saveMicroServiceInstance(
                                        microServiceInfo, false, "", "");
                        LOGGER.info("register MicroServiceInfo successs:"
                                + microServiceFullInfo.getServiceName());
                    } catch (Exception e) {
                        LOGGER.error("register MicroServiceInfo FAIL : " + serviceName, e);

                    }
                }
            }
        });
        try {
            LOGGER.info(serviceName + " Node Listen start");
            cacheList.get().add(healthCache);
            healthCache.start();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOGGER.error(serviceName + " Node Listen start throw exception", e);
        }

        return healthCache;
    }

    /**
     * @Title startNodeListen
     * @Description TODO(Open a service node changes to monitor)
     * @param serviceName
     * @return
     * @return CatalogCache
     */
    @Deprecated
    private CatalogCache startNodeListen(final String serviceName) {
        final CatalogCache catalogCache = CatalogCache.newCache(catalogClient, serviceName, 30);
        catalogCache.addListener(new ConsulCache.Listener<String, CatalogService>() {
            @Override
            public void notify(Map<String, CatalogService> newValues) {
                // do Something with updated server map
                System.out.println(serviceName + "--new node notify--");
                LOGGER.info(serviceName + "--new node notify--");

                if (newValues.isEmpty()) {
                    System.out.println(serviceName + "-- nodeList is Empty--");
                    LOGGER.info(serviceName + "--nodeList is Empty-stop service[" + serviceName
                            + "] listen-");
                    try {
                        catalogCache.stop();
                    } catch (Exception e) {
                        LOGGER.equals(serviceName + "-- stop Node error:" + e.getMessage());
                    }

                } else {

                    MicroServiceInfo microServiceInfo = new MicroServiceInfo();
                    HashSet<Node> nodes = new HashSet<Node>();
                    String url = "";
                    String version = "", visualRange = "", protocol = "";

                    for (Map.Entry<String, CatalogService> entry : newValues.entrySet()) {
                        String nodeName = entry.getKey().toString();
                        CatalogService value = (CatalogService) entry.getValue();

                        Node node = new Node();
                        node.setIp(value.getServiceAddress());
                        node.setPort(String.valueOf(value.getServicePort()));


                        try {
                            List<String> tagList = value.getServiceTags();
                            for (String tag : tagList) {
                                if (tag.startsWith("url")) {
                                    if (tag.split(":").length == 2) {
                                        url = tag.split(":")[1];
                                    } else {
                                        url = "";
                                    }


                                    continue;
                                }
                                if (tag.startsWith("version")) {
                                    if (tag.split(":").length == 2) {
                                        version = tag.split(":")[1];
                                    } else {
                                        version = "";
                                    }
                                    continue;
                                }
                                if (tag.startsWith("protocol")) {
                                    protocol = tag.split(":")[1];
                                    continue;
                                }
                                if (tag.startsWith("visualRange")) {
                                    visualRange = tag.split(":")[1];
                                    continue;
                                }
                                if (tag.startsWith("ttl")) {
                                    int ttl = Integer.parseInt(tag.split(":")[1]);
                                    node.setTtl(ttl);
                                    continue;
                                }
                            }


                        } catch (Exception e) {
                            LOGGER.error(serviceName + " read tag  throw exception", e);
                            System.out.println(serviceName + " read tag  throw exception");
                        }

                        nodes.add(node);


                        System.out.println(nodeName + ":" + value.getServiceAddress() + " "
                                + value.getServicePort() + " " + value.getServiceTags());
                    }

                    microServiceInfo.setNodes(nodes);
                    microServiceInfo.setProtocol(protocol);
                    microServiceInfo.setUrl(url);
                    microServiceInfo.setServiceName(serviceName);
                    if (!visualRange.isEmpty()) {
                        microServiceInfo.setVisualRange(visualRange);
                    }
                    microServiceInfo.setVersion(version);

                    try {
                        MicroServiceFullInfo microServiceFullInfo =
                                MicroServiceWrapper.getInstance().saveMicroServiceInstance(
                                        microServiceInfo, false, "", "");
                        LOGGER.info("register MicroServiceInfo successs:" + microServiceFullInfo);
                        System.out.println("register MicroServiceInfo successs:" + serviceName);
                    } catch (Exception e) {
                        LOGGER.error("register MicroServiceInfo FAIL : ", e);

                    }
                }
            }
        });
        try {
            System.out.println(serviceName + " Node Listen start");
            LOGGER.info(serviceName + " Node Listen start");
            catalogCache.start();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOGGER.error(serviceName + " Node Listen start throw exception", e);
        }

        return catalogCache;
    }


    /**
     * @Title getDiffrent
     * @Description TODO(Extract the list1 and list2 different data sets)
     * @param list1
     * @param list2
     * @return
     * @return List<String>
     */
    private List<ServiceInfo> getDiffrent(List<ServiceInfo> list1, List<ServiceInfo> list2) {

        List<ServiceInfo> diff = new ArrayList<ServiceInfo>();



        for (ServiceInfo serviceInfo : list1) {
            if (!list2.contains(serviceInfo)) {
                diff.add(serviceInfo);
            }
        }

        return diff;
    }

    public static void main(String[] args) {
        ConsulClientApp consulTest = new ConsulClientApp("127.0.0.1", 10081);
        consulTest.startServiceListen();


    }


}
