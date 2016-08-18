/**
 * Copyright 2016 ZTE, Inc. and others.
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

package org.openo.msb.wrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openo.msb.api.ServiceAccessInfo;
import org.openo.msb.wrapper.util.JedisUtil;
import org.openo.msb.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class ServiceAccessWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAccessWrapper.class);

    private static ServiceAccessWrapper instance = new ServiceAccessWrapper();

    private ServiceAccessWrapper() {}

    public static ServiceAccessWrapper getInstance() {
        return instance;
    }

    public ServiceAccessInfo getApiServiceAccessAddr(String serviceName, String version, String host) {

        ServiceAccessInfo apiRouteAccessInfo = null;

        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new Exception("fetch from jedis pool failed,null object!");
            }

            if ("null".equals(version)) {
                version = "";
            }

            String routekey =
                    RouteUtil.getPrefixedKey("",RouteUtil.APIROUTE, serviceName, version,
                            RouteUtil.ROUTE_PATH_INFO);
            Boolean isExist = jedis.exists(routekey);
            if (isExist) {
                apiRouteAccessInfo = new ServiceAccessInfo();
                apiRouteAccessInfo.setServiceName(serviceName);
                apiRouteAccessInfo.setVersion(version);
                String accessAddr = "http://" + host + "/api/" + serviceName + "/" + version;
                apiRouteAccessInfo.setAccessAddr(accessAddr);
            }
        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
        return apiRouteAccessInfo;

    }

    public List<ServiceAccessInfo> getApiRouteAccessAddr(String serviceType, String serviceName,
            String version, String host) {
        List<ServiceAccessInfo> serviceList = new ArrayList<ServiceAccessInfo>();
        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new Exception("fetch from jedis pool failed,null object!");
            }

            String keyPattern = this.getRedisSearchPattern(serviceType, serviceName, version);
            Set<String> infoKeys = jedis.keys(keyPattern);
            Pattern pattern = this.getKeyPattern();
            for (Iterator<String> iterator = infoKeys.iterator(); iterator.hasNext();) {
                String infoKey = (String) iterator.next();
                Matcher matcher = pattern.matcher(infoKey);
                if (matcher.matches()) {
                    ServiceAccessInfo serviceAccessInfo = new ServiceAccessInfo();
                    serviceAccessInfo.setServiceType(matcher.group("servicetype"));
                    serviceAccessInfo.setServiceName(matcher.group("servicename"));
                    serviceAccessInfo.setVersion(matcher.group("version"));
                    this.buildServiceAccessAddr(serviceAccessInfo, host, infoKey, jedis);
                    serviceList.add(serviceAccessInfo);
                }
            }
        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
        return serviceList;

    }

    private void buildServiceAccessAddr(ServiceAccessInfo serviceAccessInfo, String host,
            String infoKey, Jedis jedis) {
        String serviceType = serviceAccessInfo.getServiceType();
        StringBuffer accessAddr = new StringBuffer();
        switch (serviceType) {
            case RouteUtil.APIROUTE:
                accessAddr.append("http://").append(host).append(":").append(JedisUtil.serverPort)
                        .append("/").append(serviceAccessInfo.getServiceType()).append("/")
                        .append(serviceAccessInfo.getServiceName()).append("/")
                        .append(serviceAccessInfo.getVersion());
                serviceAccessInfo.setAccessAddr(accessAddr.toString());
                break;
            case RouteUtil.IUIROUTE:
                accessAddr.append("http://").append(host).append(":").append(JedisUtil.serverPort)
                        .append("/").append(serviceAccessInfo.getServiceType()).append("/")
                        .append(serviceAccessInfo.getServiceName());
                serviceAccessInfo.setAccessAddr(accessAddr.toString());
                break;
            case RouteUtil.CUSTOMROUTE:
                accessAddr.append("http://").append(host).append(":").append(JedisUtil.serverPort)
                        .append(serviceAccessInfo.getServiceName());
                serviceAccessInfo.setAccessAddr(accessAddr.toString());
                break;
            case RouteUtil.P2PROUTE:
                accessAddr.append(jedis.hget(infoKey, "url"));
                serviceAccessInfo.setAccessAddr(accessAddr.toString());
                break;
            default:
                serviceAccessInfo.setAccessAddr("not supported now");
                break;
        }
    }

    private String getRedisSearchPattern(String serviceType, String serviceName, String version) {
        StringBuffer sb = new StringBuffer();
        sb.append(RouteUtil.ROUTE_PATH);
        if (null != serviceType && !"".equals(serviceType)) {
            sb.append(":").append(serviceType);
        } else {
            sb.append(":").append("*");
        }
        sb.append(":").append(serviceName);
        if (null != version && !"".equals(version)) {
            sb.append(":");
            sb.append(version);
            sb.append(":");
        } else {
            sb.append(":*");
        }
        sb.append(RouteUtil.ROUTE_PATH_INFO);
        return sb.toString();
    }

    private Pattern getKeyPattern() {
        String pStr =
                "conductor:routing:(?<servicetype>api|iui|custom|p2p):(?<servicename>[^:]+)(:(?<version>[^:]*))?:info";
        return Pattern.compile(pStr);
    }
}