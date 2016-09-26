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
package org.openo.msb.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openo.msb.api.CustomRouteInfo;
import org.openo.msb.api.RouteServer;
import org.openo.msb.api.exception.ExtendedInternalServerErrorException;
import org.openo.msb.api.exception.ExtendedNotFoundException;
import org.openo.msb.api.exception.ExtendedNotSupportedException;
import org.openo.msb.wrapper.util.JedisUtil;
import org.openo.msb.wrapper.util.RegExpTestUtil;
import org.openo.msb.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class CustomRouteServiceWrapper {


    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRouteServiceWrapper.class);

    private static CustomRouteServiceWrapper instance = new CustomRouteServiceWrapper();

    private CustomRouteServiceWrapper() {}

    public static CustomRouteServiceWrapper getInstance() {
        return instance;
    }


    
    public CustomRouteInfo[] getAllCustomRouteInstances() {


        Jedis jedis = null;
        CustomRouteInfo[] customRouteList = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new ExtendedInternalServerErrorException(
                        "fetch from jedis pool failed,null object!");
            }

            String routekey =
                    RouteUtil.getPrefixedKey("", RouteUtil.CUSTOMROUTE, "*",
                            RouteUtil.ROUTE_PATH_INFO);
            Set<String> routeSet = jedis.keys(routekey);
            customRouteList = new CustomRouteInfo[routeSet.size()];

            int i = 0;
            for (String routePath : routeSet) {
                String[] routePathArray = routePath.split(":");
                CustomRouteInfo customRoute = getCustomRouteInstance(routePathArray[3], jedis);
                customRouteList[i] = customRoute;
                i++;
            }


        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
            throw new ExtendedInternalServerErrorException("call redis throw exception:"
                    + e.getMessage());

        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }

        return customRouteList;
    }



    public CustomRouteInfo getCustomRouteInstance(String serviceName) {

        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException("serviceName  can't be empty");
        }

        CustomRouteInfo customRouteInfo;

        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new ExtendedInternalServerErrorException(
                        "fetch from jedis pool failed,null object!");
            }

            customRouteInfo = getCustomRouteInstance(serviceName, jedis);


        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
            throw new ExtendedInternalServerErrorException("call redis throw exception:"
                    + e.getMessage());
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }

        if (null == customRouteInfo) {
            String errInfo = "customRouteInfo not found: serviceName-" + serviceName;
            LOGGER.warn(errInfo);
            throw new ExtendedNotFoundException(errInfo);

        }

        return customRouteInfo;

    }

    public CustomRouteInfo getCustomRouteInstance(String serviceName, Jedis jedis) throws Exception {


        CustomRouteInfo customRouteInfo = null;


        String routekey =
                RouteUtil.getPrefixedKey("", RouteUtil.CUSTOMROUTE, serviceName,
                        RouteUtil.ROUTE_PATH_INFO);
        Map<String, String> infomap = jedis.hgetAll(routekey);
        if (!infomap.isEmpty()) {
            customRouteInfo = new CustomRouteInfo();
            customRouteInfo.setServiceName(serviceName);
            customRouteInfo.setUrl(infomap.get("url"));
            customRouteInfo.setControl(infomap.get("control"));
            customRouteInfo.setStatus(infomap.get("status"));
            customRouteInfo.setVisualRange(infomap.get("visualRange"));
            customRouteInfo.setUseOwnUpstream(infomap.get("useOwnUpstream"));


            String serviceLBkey =
                    RouteUtil.getPrefixedKey("", RouteUtil.CUSTOMROUTE, serviceName,
                            RouteUtil.ROUTE_PATH_LOADBALANCE);
            Set<String> serviceLBset = jedis.keys(serviceLBkey + ":*");
            int serverNum = serviceLBset.size();
            RouteServer[] CustomRouteServerList = new RouteServer[serverNum];
            int i = 0;
            for (String serviceInfo : serviceLBset) {
                Map<String, String> serviceLBmap = jedis.hgetAll(serviceInfo);
                RouteServer server = new RouteServer();
                server.setIp(serviceLBmap.get("ip"));
                server.setPort(serviceLBmap.get("port"));
                server.setWeight(Integer.parseInt(serviceLBmap.get("weight")));
                CustomRouteServerList[i] = server;
                i++;
            }

            customRouteInfo.setServers(CustomRouteServerList);
        }


        return customRouteInfo;
    }

    public synchronized CustomRouteInfo updateCustomRouteInstance(String serviceName,
            CustomRouteInfo customRouteInfo, String serverPort) {
        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException("serviceName  can't be empty");
        }

        try {

            if (serviceName.equals(customRouteInfo.getServiceName())) {
                deleteCustomRoute(serviceName, RouteUtil.ROUTE_PATH_LOADBALANCE + "*", serverPort);
            } else {
                deleteCustomRoute(serviceName, "*", serverPort);
            }


            saveCustomRouteInstance(customRouteInfo, serverPort);



        } catch (ExtendedNotSupportedException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("updateCustomRoute throw exception", e);
            throw new ExtendedInternalServerErrorException("update CustomRoute throw exception"
                    + e.getMessage());

        }

        return customRouteInfo;

    }

    public synchronized CustomRouteInfo updateCustomRouteStatus(String serviceName, String status) {

        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException("serviceName  can't be empty");
        }

        if (!RouteUtil.contain(RouteUtil.statusRangeMatches, status)) {
            throw new ExtendedNotSupportedException(
                    "save CustomRouteInfo Status FAIL:status is wrong,value range:("
                            + RouteUtil.show(RouteUtil.statusRangeMatches) + ")");
        }

        CustomRouteInfo new_customRouteInfo = getCustomRouteInstance(serviceName);



        String serviceInfokey =
                RouteUtil.getPrefixedKey("", RouteUtil.CUSTOMROUTE, serviceName,
                        RouteUtil.ROUTE_PATH_INFO);
        Map<String, String> serviceInfoMap = new HashMap<String, String>();
        serviceInfoMap.put("status", status);


        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new ExtendedInternalServerErrorException(
                        "fetch from jedis pool failed,null object!");
            }
            jedis.hmset(serviceInfokey, serviceInfoMap);
            new_customRouteInfo.setStatus(status);

        } catch (Exception e) {

            LOGGER.error("update CustomRoute status throw exception", e);
            throw new ExtendedInternalServerErrorException(
                    "update CustomRoute status throw exception" + e.getMessage());

        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }

        return new_customRouteInfo;
    }

    public synchronized CustomRouteInfo saveCustomRouteInstance(CustomRouteInfo customRouteInfo,
            String serverPort) {

        if (StringUtils.isBlank(customRouteInfo.getServiceName())
                || customRouteInfo.getServers().length == 0) {
            throw new ExtendedNotSupportedException(
                    "save CustomRouteInfo FAIL: Some required fields are empty");
        }

       
            if (!RegExpTestUtil.urlRegExpTest(customRouteInfo.getServiceName())) {
                throw new ExtendedNotSupportedException(
                        "save CustomRouteInfo FAIL: ServiceName is not a valid format(ServiceName must be begin with /)");
    
            }
        
            if (StringUtils.isNotBlank(customRouteInfo.getUrl())){
                if (!RegExpTestUtil.urlRegExpTest(customRouteInfo.getUrl())) {
                    throw new ExtendedNotSupportedException(
                            "save CustomRouteInfo FAIL:url is not a valid format(url must be begin with /)");
        
                }
            }

        if (!RouteUtil.contain(RouteUtil.visualRangeRange, customRouteInfo.getVisualRange())) {
            throw new ExtendedNotSupportedException(
                    "save CustomRouteInfo FAIL:VisualRange is wrong,value range:("
                            + RouteUtil.show(RouteUtil.visualRangeMatches) + ")");
        }

        if (!RouteUtil.contain(RouteUtil.controlRangeMatches, customRouteInfo.getControl())) {
            throw new ExtendedNotSupportedException(
                    "save CustomRouteInfo FAIL:control is wrong,value range:("
                            + RouteUtil.show(RouteUtil.controlRangeMatches) + ")");
        }

        if (!RouteUtil.contain(RouteUtil.statusRangeMatches, customRouteInfo.getStatus())) {
            throw new ExtendedNotSupportedException(
                    "save CustomRouteInfo FAIL:status is wrong,value range:("
                            + RouteUtil.show(RouteUtil.statusRangeMatches) + ")");
        }

        if (!RouteUtil.contain(RouteUtil.useOwnUpstreamRangeMatches, customRouteInfo.getUseOwnUpstream())) {
            throw new ExtendedNotSupportedException(
                    "save apiRouteInfo FAIL:useOwnUpstream is wrong,value range:("
                            + RouteUtil.show(RouteUtil.useOwnUpstreamRangeMatches) + ")");
        }

        RouteServer[] serverList = customRouteInfo.getServers();
        for (int i = 0; i < serverList.length; i++) {
            RouteServer server = serverList[i];
            if (!RegExpTestUtil.ipRegExpTest(server.getIp())) {
                throw new ExtendedNotSupportedException("save CustomRouteInfo FAIL:IP("
                        + server.getIp() + ")is not a valid ip address");
            }

            if (!RegExpTestUtil.portRegExpTest(server.getPort())) {
                throw new ExtendedNotSupportedException("save CustomRouteInfo FAIL:Port("
                        + server.getPort() + ")is not a valid Port address");
            }
        }


        String serviceInfokey =
                RouteUtil.getPrefixedKey(serverPort, RouteUtil.CUSTOMROUTE,
                        customRouteInfo.getServiceName().trim(), RouteUtil.ROUTE_PATH_INFO);
        Map<String, String> serviceInfoMap = new HashMap<String, String>();
        serviceInfoMap.put("url", "/".equals(customRouteInfo.getUrl().trim())
                ? ""
                : customRouteInfo.getUrl().trim());
        serviceInfoMap.put("control", customRouteInfo.getControl());
        serviceInfoMap.put("status", customRouteInfo.getStatus());
        serviceInfoMap.put("visualRange", customRouteInfo.getVisualRange());
        serviceInfoMap.put("useOwnUpstream", customRouteInfo.getUseOwnUpstream());



        String serviceLBkey =
                RouteUtil.getPrefixedKey(serverPort, RouteUtil.CUSTOMROUTE,
                        customRouteInfo.getServiceName(), RouteUtil.ROUTE_PATH_LOADBALANCE);


        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new ExtendedInternalServerErrorException(
                        "fetch from jedis pool failed,null object!");
            }
            jedis.hmset(serviceInfokey, serviceInfoMap);


            for (int i = 0; i < serverList.length; i++) {
                Map<String, String> servermap = new HashMap<String, String>();
                RouteServer server = serverList[i];

                servermap.put("ip", server.getIp());
                servermap.put("port", server.getPort());
                servermap.put("weight", Integer.toString(server.getWeight()));

                jedis.hmset(serviceLBkey + ":server" + (i + 1), servermap);
            }


        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
            throw new ExtendedInternalServerErrorException("call redis throw exception:"
                    + e.getMessage());

        } finally {
            JedisUtil.returnJedisInstance(jedis);;
        }

        return customRouteInfo;
    }



    public synchronized void deleteCustomRoute(String serviceName, String delKey, String serverPort) {

        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException("serviceName  can't be empty");
        }

        Jedis jedis = null;

        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new ExtendedInternalServerErrorException(
                        "fetch from jedis pool failed,null object!");
            }

            String routekey =
                    RouteUtil
                            .getPrefixedKey(serverPort, RouteUtil.CUSTOMROUTE, serviceName, delKey);
            Set<String> infoSet = jedis.keys(routekey);

            if (infoSet.isEmpty()) {
                throw new ExtendedNotFoundException("delete CustomRoute FAIL:serviceName-"
                        + serviceName + " not fond ");
            }


            String[] paths = new String[infoSet.size()];

            infoSet.toArray(paths);

            jedis.del(paths);

        } catch (ExtendedNotFoundException e) {
            throw e;
        } catch (Exception e) {

            LOGGER.error("delete CustomRoute throw exception", e);
            throw new ExtendedInternalServerErrorException("delete CustomRoute throw exception:"
                    + e.getMessage());

        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }


    }
}
