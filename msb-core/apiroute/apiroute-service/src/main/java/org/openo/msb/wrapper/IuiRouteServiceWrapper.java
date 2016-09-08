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
import org.openo.msb.api.IuiRouteInfo;
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

public class IuiRouteServiceWrapper {


    private static final Logger LOGGER = LoggerFactory.getLogger(IuiRouteServiceWrapper.class);

    private static IuiRouteServiceWrapper instance = new IuiRouteServiceWrapper();

    private IuiRouteServiceWrapper() {}

    public static IuiRouteServiceWrapper getInstance() {
        return instance;
    }


    /**
     * @Title: getAllIuiRouteService
     * @Description: TODO(获取全部内容服务列表)
     * @param: @return
     * @return: IuiRouteInfo[]
     */
    public IuiRouteInfo[] getAllIuiRouteInstances() {


        Jedis jedis = null;
        IuiRouteInfo[] iuiRouteList = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new ExtendedInternalServerErrorException(
                        "fetch from jedis pool failed,null object!");
            }

            // 获取全部服务列表
            String routekey =
                    RouteUtil
                            .getPrefixedKey("", RouteUtil.IUIROUTE, "*", RouteUtil.ROUTE_PATH_INFO);
            Set<String> routeSet = jedis.keys(routekey);
            iuiRouteList = new IuiRouteInfo[routeSet.size()];

            int i = 0;
            for (String routePath : routeSet) {
                String[] routePathArray = routePath.split(":");
                IuiRouteInfo iuiRoute = getIuiRouteInstance(routePathArray[3], jedis);
                iuiRouteList[i] = iuiRoute;
                i++;
            }


        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
            throw new ExtendedInternalServerErrorException("call redis throw exception:"
                    + e.getMessage());
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }

        return iuiRouteList;
    }



    /**
     * @Title: getIuiRouteInstance
     * @Description: TODO(通过服务名获取单个内容服务对象信息)
     * @param: @param serviceName
     * @param: @return
     * @return: IuiRouteInfo
     */
    public IuiRouteInfo getIuiRouteInstance(String serviceName) {

        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException("serviceName  can't be empty");
        }

        IuiRouteInfo iuiRouteInfo = null;

        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new ExtendedInternalServerErrorException(
                        "fetch from jedis pool failed,null object!");
            }

            iuiRouteInfo = getIuiRouteInstance(serviceName, jedis);


        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
            throw new ExtendedInternalServerErrorException("call redis throw exception:"
                    + e.getMessage());
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }

        if (null == iuiRouteInfo) {
            String errInfo = "iuiRouteInfo not found: serviceName-" + serviceName;
            LOGGER.warn(errInfo);
            throw new ExtendedNotFoundException(errInfo);

        }

        return iuiRouteInfo;

    }

    public IuiRouteInfo getIuiRouteInstance(String serviceName, Jedis jedis) throws Exception {


        IuiRouteInfo iuiRouteInfo = null;


        // 获取info信息
        String routekey =
                RouteUtil.getPrefixedKey("", RouteUtil.IUIROUTE, serviceName,
                        RouteUtil.ROUTE_PATH_INFO);
        Map<String, String> infomap = jedis.hgetAll(routekey);
        if (!infomap.isEmpty()) {
            iuiRouteInfo = new IuiRouteInfo();
            iuiRouteInfo.setServiceName(serviceName);
            iuiRouteInfo.setUrl(infomap.get("url"));
            iuiRouteInfo.setControl(infomap.get("control"));
            iuiRouteInfo.setStatus(infomap.get("status"));
            iuiRouteInfo.setVisualRange(infomap.get("visualRange"));
            iuiRouteInfo.setUseOwnUpstream(infomap.get("useOwnUpstream"));


            // 获取负载均衡信息
            String serviceLBkey =
                    RouteUtil.getPrefixedKey("", RouteUtil.IUIROUTE, serviceName,
                            RouteUtil.ROUTE_PATH_LOADBALANCE);
            Set<String> serviceLBset = jedis.keys(serviceLBkey + ":*");
            int serverNum = serviceLBset.size();
            RouteServer[] iuiRouteServerList = new RouteServer[serverNum];
            int i = 0;
            for (String serviceInfo : serviceLBset) {
                Map<String, String> serviceLBmap = jedis.hgetAll(serviceInfo);
                RouteServer server = new RouteServer();
                server.setIp(serviceLBmap.get("ip"));
                server.setPort(serviceLBmap.get("port"));
                server.setWeight(Integer.parseInt(serviceLBmap.get("weight")));
                iuiRouteServerList[i] = server;
                i++;
            }

            iuiRouteInfo.setServers(iuiRouteServerList);
        }


        return iuiRouteInfo;
    }

    /**
     * @Title: updateIuiRouteInstance
     * @Description: TODO(更新单个服务信息)
     * @param: @param serviceName
     * @param: @param IuiRouteInfo
     * @param: @return
     * @return: IuiRouteInfo
     */
    public synchronized IuiRouteInfo updateIuiRouteInstance(String serviceName,
            IuiRouteInfo iuiRouteInfo) {

        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException("serviceName  can't be empty");
        }

        try {
            if (serviceName.equals(iuiRouteInfo.getServiceName())) {
                // 删除已存在负载均衡服务器信息
                deleteIuiRoute(serviceName, RouteUtil.ROUTE_PATH_LOADBALANCE + "*");

            } else {
                // 如果已修改服务名，先删除此服务全部已有信息
                deleteIuiRoute(serviceName, "*");
            }
            saveIuiRouteInstance(iuiRouteInfo);

        } catch (ExtendedNotSupportedException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("updateIuiRoute throw exception", e);
            throw new ExtendedInternalServerErrorException("update IuiRouteInfo throw exception"
                    + e.getMessage());
        }

        return iuiRouteInfo;

    }

    /**
     * @Title updateIuiRouteStatus
     * @Description TODO(更新单个服务状态)
     * @param serviceName
     * @param status
     * @return
     * @return RouteResult
     */
    public synchronized IuiRouteInfo updateIuiRouteStatus(String serviceName, String status) {


        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException("serviceName  can't be empty");
        }

        if (!RouteUtil.contain(RouteUtil.statusRangeMatches, status)) {
            throw new ExtendedNotSupportedException(
                    "save IuiRouteInfo Status FAIL:status is wrong,value range:("
                            + RouteUtil.show(RouteUtil.statusRangeMatches) + ")");
        }

        IuiRouteInfo new_iuiRouteInfo = getIuiRouteInstance(serviceName);

        // 准备info信息
        String serviceInfokey =
                RouteUtil.getPrefixedKey("", RouteUtil.IUIROUTE, serviceName,
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
            // 保存info信息
            jedis.hmset(serviceInfokey, serviceInfoMap);
            new_iuiRouteInfo.setStatus(status);

        } catch (Exception e) {
            LOGGER.error("update IuiRoute status throw exception", e);
            throw new ExtendedInternalServerErrorException(
                    "update IuiRouteInfo status throw exception" + e.getMessage());

        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }

        return new_iuiRouteInfo;
    }

    /**
     * @Title: saveIuiRouteInstance
     * @Description: TODO(存储单个服务信息)
     * @param: @param IuiRouteInfo
     * @param: @return
     * @return: IuiRouteInfo
     */
    public synchronized IuiRouteInfo saveIuiRouteInstance(IuiRouteInfo iuiRouteInfo) {

        if (StringUtils.isBlank(iuiRouteInfo.getServiceName())
                || iuiRouteInfo.getServers().length == 0) {
            throw new ExtendedNotSupportedException(
                    "save iuiRouteInfo FAIL: Some required fields are empty");
        }

        if (StringUtils.isNotBlank(iuiRouteInfo.getUrl())){
            if (!RegExpTestUtil.urlRegExpTest(iuiRouteInfo.getUrl())) {
                throw new ExtendedNotSupportedException(
                        "save iuiRouteInfo FAIL:url is not a valid format(url must be begin with /)");
    
            }
        }

        if (!RouteUtil.contain(RouteUtil.visualRangeRange, iuiRouteInfo.getVisualRange())) {
            throw new ExtendedNotSupportedException(
                    "save iuiRouteInfo FAIL:VisualRange is wrong,value range:("
                            + RouteUtil.show(RouteUtil.visualRangeMatches) + ")");
        }

        if (!RouteUtil.contain(RouteUtil.controlRangeMatches, iuiRouteInfo.getControl())) {
            throw new ExtendedNotSupportedException(
                    "save iuiRouteInfo FAIL:control is wrong,value range:("
                            + RouteUtil.show(RouteUtil.controlRangeMatches) + ")");
        }

        if (!RouteUtil.contain(RouteUtil.statusRangeMatches, iuiRouteInfo.getStatus())) {
            throw new ExtendedNotSupportedException(
                    "save iuiRouteInfo FAIL:status is wrong,value range:("
                            + RouteUtil.show(RouteUtil.statusRangeMatches) + ")");
        }

        if (!RouteUtil.contain(RouteUtil.useOwnUpstreamRangeMatches, iuiRouteInfo.getUseOwnUpstream())) {
            throw new ExtendedNotSupportedException(
                    "save apiRouteInfo FAIL:useOwnUpstream is wrong,value range:("
                            + RouteUtil.show(RouteUtil.useOwnUpstreamRangeMatches) + ")");
        }

        // 检查服务实例格式
        RouteServer[] serverList = iuiRouteInfo.getServers();
        for (int i = 0; i < serverList.length; i++) {
            RouteServer server = serverList[i];
            if (!RegExpTestUtil.ipRegExpTest(server.getIp())) {
                throw new ExtendedNotSupportedException("save iuiRouteInfo FAIL:IP("
                        + server.getIp() + ")is not a valid ip address");
            }

            if (!RegExpTestUtil.portRegExpTest(server.getPort())) {
                throw new ExtendedNotSupportedException("save iuiRouteInfo FAIL:Port("
                        + server.getPort() + ")is not a valid Port address");
            }
        }


        // 准备info信息
        String serviceInfokey =
                RouteUtil.getPrefixedKey("", RouteUtil.IUIROUTE, iuiRouteInfo.getServiceName().trim(),
                        RouteUtil.ROUTE_PATH_INFO);
        Map<String, String> serviceInfoMap = new HashMap<String, String>();
        serviceInfoMap.put("url", "/".equals(iuiRouteInfo.getUrl().trim()) ? "" : iuiRouteInfo
                .getUrl().trim());
        serviceInfoMap.put("control", iuiRouteInfo.getControl());
        serviceInfoMap.put("status", iuiRouteInfo.getStatus());
        serviceInfoMap.put("visualRange", iuiRouteInfo.getVisualRange());
        serviceInfoMap.put("useOwnUpstream", iuiRouteInfo.getUseOwnUpstream());


        // 准备负载均衡信息
        String serviceLBkey =
                RouteUtil.getPrefixedKey("", RouteUtil.IUIROUTE, iuiRouteInfo.getServiceName(),
                        RouteUtil.ROUTE_PATH_LOADBALANCE);


        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new ExtendedInternalServerErrorException(
                        "fetch from jedis pool failed,null object!");
            }
            // 保存info信息
            jedis.hmset(serviceInfokey, serviceInfoMap);

            // 保存负载均衡信息
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

        return iuiRouteInfo;
    }



    /**
     * @Title: deleteIuiRoute
     * @Description: TODO(删除单个服务信息)
     * @param: @param type
     * @param: @param serviceName
     * @param: @param delKey
     * @param: @return
     * @return: void
     */
    public synchronized void deleteIuiRoute(String serviceName, String delKey) {

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

            // 获取info信息
            String routekey = RouteUtil.getPrefixedKey("", RouteUtil.IUIROUTE, serviceName, delKey);
            Set<String> infoSet = jedis.keys(routekey);

            if (infoSet.isEmpty()) {
                throw new ExtendedNotFoundException("delete IuiRoute FAIL:serviceName-"
                        + serviceName + " not fond ");
            }

            String[] paths = new String[infoSet.size()];

            // Set-->数组
            infoSet.toArray(paths);

            jedis.del(paths);


        } catch (ExtendedNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("delete IuiRoute throw exception", e);
            throw new ExtendedInternalServerErrorException("delete IuiRoute throw exception:"
                    + e.getMessage());
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }


    }



}
