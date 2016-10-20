/**
 * Copyright 2016 ZTE Corporation.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openo.msb.api.ApiRouteInfo;
import org.openo.msb.api.DiscoverInfo;
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


public class ApiRouteServiceWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRouteServiceWrapper.class);


    private static ApiRouteServiceWrapper instance = new ApiRouteServiceWrapper();

    private ApiRouteServiceWrapper() {}

    public static ApiRouteServiceWrapper getInstance() {
        return instance;
    }

  
    public ApiRouteInfo[] getAllApiRouteInstances() {


        Jedis jedis = null;
        ApiRouteInfo[] apiRouteList = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new ExtendedInternalServerErrorException(
                        "fetch from jedis pool failed,null object!");
            }

      
            String routekey =
                    RouteUtil
                            .getPrefixedKey("", RouteUtil.APIROUTE, "*", RouteUtil.ROUTE_PATH_INFO);
            Set<String> routeSet = jedis.keys(routekey);
            apiRouteList = new ApiRouteInfo[routeSet.size()];

            int i = 0;
            for (String routePath : routeSet) {
                String[] routePathArray = routePath.split(":");
                ApiRouteInfo apiRoute =
                        getApiRouteInstance(routePathArray[3], routePathArray[4], jedis);
                apiRouteList[i] = apiRoute;
                i++;
            }


        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
            throw new ExtendedInternalServerErrorException("call redis throw exception:"
                    + e.getMessage());

        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }

        return apiRouteList;
    }



    public static boolean checkRedisConnect() {

        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis != null) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }

        return false;
    }

 
    public ApiRouteInfo getApiRouteInstance(String serviceName, String version) {

        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException("serviceName  can't be empty");
        }

        if (StringUtils.isNotBlank(version)) {
            if (!RegExpTestUtil.versionRegExpTest(version)) {
                throw new ExtendedNotSupportedException("version (" + version
                        + ") is not a valid  format");
            }
        }


        ApiRouteInfo apiRouteInfo = null;

        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new ExtendedInternalServerErrorException(
                        "fetch from jedis pool failed,null object!");
            }

            apiRouteInfo = getApiRouteInstance(serviceName, version, jedis);


        } catch (Exception e) {
            LOGGER.error("call redis throw exception", e);
            throw new ExtendedInternalServerErrorException("call redis throw exception:"
                    + e.getMessage());

        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }

        if (null == apiRouteInfo) {
            String errInfo =
                    "ApiRouteInfo not found: serviceName-" + serviceName + " ,version-" + version;
            LOGGER.warn(errInfo);
            throw new ExtendedNotFoundException(errInfo);

        }

        return apiRouteInfo;

    }

    public ApiRouteInfo getApiRouteInstance(String serviceName, String version, Jedis jedis)
            throws Exception {
        if ("null".equals(version)) {
            version = "";
        }

        ApiRouteInfo apiRouteInfo = null;


   
        String routekey =
                RouteUtil.getPrefixedKey("", RouteUtil.APIROUTE, serviceName, version,
                        RouteUtil.ROUTE_PATH_INFO);
        Map<String, String> infomap = jedis.hgetAll(routekey);
        if (!infomap.isEmpty()) {
            apiRouteInfo = new ApiRouteInfo();
            apiRouteInfo.setServiceName(serviceName);
            apiRouteInfo.setVersion(version);
            apiRouteInfo.setUrl(infomap.get("url"));
            apiRouteInfo.setMetricsUrl(infomap.get("metricsUrl"));
            apiRouteInfo.setApiJson(infomap.get("apijson"));
            apiRouteInfo.setApiJsonType(infomap.get("apiJsonType"));
            apiRouteInfo.setControl(infomap.get("control"));
            apiRouteInfo.setStatus(infomap.get("status"));
            apiRouteInfo.setVisualRange(infomap.get("visualRange"));
            apiRouteInfo.setUseOwnUpstream(infomap.get("useOwnUpstream"));


        
            String serviceLBkey =
                    RouteUtil.getPrefixedKey("", RouteUtil.APIROUTE, serviceName, version,
                            RouteUtil.ROUTE_PATH_LOADBALANCE);
            Set<String> serviceLBset = jedis.keys(serviceLBkey + ":*");
            int serverNum = serviceLBset.size();
            RouteServer[] apiRouteServerList = new RouteServer[serverNum];
            int i = 0;
            for (String serviceInfo : serviceLBset) {
                Map<String, String> serviceLBmap = jedis.hgetAll(serviceInfo);
                RouteServer server = new RouteServer();
                server.setIp(serviceLBmap.get("ip"));
                server.setPort(serviceLBmap.get("port"));
                server.setWeight(Integer.parseInt(serviceLBmap.get("weight")));
                apiRouteServerList[i] = server;
                i++;
            }

            apiRouteInfo.setServers(apiRouteServerList);

         
        }


        return apiRouteInfo;
    }

  
    public synchronized ApiRouteInfo updateApiRouteInstance(String serviceName, String version,
            ApiRouteInfo apiRouteInfo, String serverPort) {

        if ("null".equals(version)) {
            version = "";
        }
        
        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException("serviceName  can't be empty");
        }

        if (StringUtils.isNotBlank(version)) {
            if (!RegExpTestUtil.versionRegExpTest(version)) {
                throw new ExtendedNotSupportedException("version (" + version
                        + ") is not a valid  format");
            }
        }

        


        try {


            if (serviceName.equals(apiRouteInfo.getServiceName())
                    && version.equals(apiRouteInfo.getVersion())) {
          
                deleteApiRoute(serviceName, version, RouteUtil.ROUTE_PATH_LOADBALANCE + "*",
                        serverPort);

            } else {
           
                deleteApiRoute(serviceName, version, "*", serverPort);
            }


            saveApiRouteInstance(apiRouteInfo, serverPort);


        } catch (ExtendedNotSupportedException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("update ApiRoute throw exception", e);
            throw new ExtendedInternalServerErrorException("update apiRouteInfo throw exception"
                    + e.getMessage());

        }

        return apiRouteInfo;

    }

   
    public synchronized ApiRouteInfo updateApiRouteStatus(String serviceName, String version,
            String status) {

        if ("null".equals(version)) {
            version = "";
        }
        
        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException("serviceName  can't be empty");
        }

        if (StringUtils.isNotBlank(version)) {
            if (!RegExpTestUtil.versionRegExpTest(version)) {
                throw new ExtendedNotSupportedException("version (" + version
                        + ") is not a valid  format");
            }
        }

        if (!RouteUtil.contain(RouteUtil.statusRangeMatches, status)) {
            throw new ExtendedNotSupportedException(
                    "save ApiRouteInfo  Status FAIL:status is wrong,value range:("
                            + RouteUtil.show(RouteUtil.statusRangeMatches) + ")");
        }

        ApiRouteInfo new_apiRouteInfo = getApiRouteInstance(serviceName, version);



        String serviceInfokey =
                RouteUtil.getPrefixedKey("", RouteUtil.APIROUTE, serviceName, version,
                        RouteUtil.ROUTE_PATH_INFO);
        Map<String, String> serviceInfoMap = new HashMap<String, String>();
        serviceInfoMap.put("status", status);


        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new Exception("fetch from jedis pool failed,null object!");
            }
   
            jedis.hmset(serviceInfokey, serviceInfoMap);
            new_apiRouteInfo.setStatus(status);


        } catch (Exception e) {
            LOGGER.error("update ApiRoute status throw exception", e);
            throw new ExtendedInternalServerErrorException("update ApiRoute status throw exception"
                    + e.getMessage());

        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }

        return new_apiRouteInfo;
    }


   
    public synchronized ApiRouteInfo saveApiRouteInstance(ApiRouteInfo apiRouteInfo,
            String serverPort) {

       
        
        if (StringUtils.isBlank(apiRouteInfo.getServiceName())
                || apiRouteInfo.getServers().length == 0) {
            throw new ExtendedNotSupportedException(
                    "save apiRouteInfo FAIL: Some required fields are empty");
        }

        if (StringUtils.isNotBlank(apiRouteInfo.getVersion())) {
            if (!RegExpTestUtil.versionRegExpTest(apiRouteInfo.getVersion())) {
                throw new ExtendedNotSupportedException("version (" + apiRouteInfo.getVersion()
                        + ") is not a valid  format");
            }
        }

        if (StringUtils.isNotBlank(apiRouteInfo.getUrl())) {
            if (!RegExpTestUtil.urlRegExpTest(apiRouteInfo.getUrl())) {
                throw new ExtendedNotSupportedException(
                        "save apiRouteInfo FAIL:url is not a valid format(url must be begin with /)");
    
            }
        }

        if (!RouteUtil.contain(RouteUtil.visualRangeRange, apiRouteInfo.getVisualRange())) {
            throw new ExtendedNotSupportedException(
                    "save apiRouteInfo FAIL:VisualRange is wrong,value range:("
                            + RouteUtil.show(RouteUtil.visualRangeMatches) + ")");
        }

        if (!RouteUtil.contain(RouteUtil.controlRangeMatches, apiRouteInfo.getControl())) {
            throw new ExtendedNotSupportedException(
                    "save apiRouteInfo FAIL:control is wrong,value range:("
                            + RouteUtil.show(RouteUtil.controlRangeMatches) + ")");
        }

        if (!RouteUtil.contain(RouteUtil.statusRangeMatches, apiRouteInfo.getStatus())) {
            throw new ExtendedNotSupportedException(
                    "save apiRouteInfo FAIL:status is wrong,value range:("
                            + RouteUtil.show(RouteUtil.statusRangeMatches) + ")");
        }

        if (!RouteUtil.contain(RouteUtil.useOwnUpstreamRangeMatches, apiRouteInfo.getUseOwnUpstream())) {
            throw new ExtendedNotSupportedException(
                    "save apiRouteInfo FAIL:useOwnUpstream is wrong,value range:("
                            + RouteUtil.show(RouteUtil.useOwnUpstreamRangeMatches) + ")");
        }

       
        RouteServer[] serverList = apiRouteInfo.getServers();
        for (int i = 0; i < serverList.length; i++) {
            RouteServer server = serverList[i];
            if (!RegExpTestUtil.ipRegExpTest(server.getIp())) {
                throw new ExtendedNotSupportedException("save apiRouteInfo FAIL:IP("
                        + server.getIp() + ")is not a valid ip address");
            }

            if (!RegExpTestUtil.portRegExpTest(server.getPort())) {
                throw new ExtendedNotSupportedException("save apiRouteInfo FAIL:Port("
                        + server.getPort() + ")is not a valid Port address");
            }
        }


        String serviceInfokey =
                RouteUtil.getPrefixedKey(serverPort, RouteUtil.APIROUTE,
                        apiRouteInfo.getServiceName().trim(), apiRouteInfo.getVersion().trim(),
                        RouteUtil.ROUTE_PATH_INFO);
        Map<String, String> serviceInfoMap = new HashMap<String, String>();
        serviceInfoMap.put("url", "/".equals(apiRouteInfo.getUrl().trim()) ? "" : apiRouteInfo
                .getUrl().trim());
        serviceInfoMap.put("apijson", apiRouteInfo.getApiJson());
        serviceInfoMap.put("apiJsonType", apiRouteInfo.getApiJsonType());
        serviceInfoMap.put("metricsUrl", apiRouteInfo.getMetricsUrl());
        serviceInfoMap.put("control", apiRouteInfo.getControl());
        serviceInfoMap.put("status", apiRouteInfo.getStatus());
        serviceInfoMap.put("visualRange", apiRouteInfo.getVisualRange());
        serviceInfoMap.put("useOwnUpstream", apiRouteInfo.getUseOwnUpstream());


        String serviceLBkey =
                RouteUtil.getPrefixedKey(serverPort, RouteUtil.APIROUTE,
                        apiRouteInfo.getServiceName(), apiRouteInfo.getVersion(),
                        RouteUtil.ROUTE_PATH_LOADBALANCE);


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
            JedisUtil.returnJedisInstance(jedis);
        }

        return apiRouteInfo;
    }




    public synchronized void deleteApiRoute(String serviceName, String version, String delKey,
            String serverPort) {

        if ("null".equals(version)) {
            version = "";
        }
        
        if (StringUtils.isBlank(serviceName)) {
            throw new ExtendedNotSupportedException("serviceName  can't be empty");
        }

        if (StringUtils.isNotBlank(version)) {
            if (!RegExpTestUtil.versionRegExpTest(version)) {
                throw new ExtendedNotSupportedException("version (" + version
                        + ") is not a valid  format");
            }
        }

        
        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            if (jedis == null) {
                throw new ExtendedInternalServerErrorException(
                        "fetch from jedis pool failed,null object!");
            }


            String routekey =
                    RouteUtil.getPrefixedKey(serverPort, RouteUtil.APIROUTE, serviceName, version,
                            delKey);
            Set<String> infoSet = jedis.keys(routekey);

            if (infoSet.isEmpty()) {
              LOGGER.warn("delete ApiRoute FAIL:serviceName-"
                  + serviceName + ",version:" + version + " not fond ");
            }
            else{

			  String[] paths = new String[infoSet.size()];
              infoSet.toArray(paths);
              jedis.del(paths);
            }


        } catch (ExtendedNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("delete ApiRoute throw exception", e);
            throw new ExtendedInternalServerErrorException("delete ApiRoute throw exception:"
                    + e.getMessage());
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }


    }

 
    public String[] getAllApiDocs() {
        URL apiDocsPath = ApiRouteServiceWrapper.class.getResource("/ext/initSwaggerJson");
        if (apiDocsPath != null) {
            String path = apiDocsPath.getPath();

            try {
                return readfile(path);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                LOGGER.error("read  ApiDocs Files throw FileNotFoundException", e);
                throw new ExtendedInternalServerErrorException("read  ApiDocs Files throw FileNotFoundException:"
                        + e.getMessage());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LOGGER.error("read  ApiDocs Files throw IOexception", e);
                throw new ExtendedInternalServerErrorException("read  ApiDocs Files throw IOexception:"
                        + e.getMessage());
            }

        }


        return null;
    }

    public String[] readfile(String filepath) throws FileNotFoundException, IOException {
        File file = new File(filepath);
        if (file.isDirectory()) {
            String[] filelist = file.list();
            return filelist;
        }
        return null;
    }

    public String getApiGatewayPort() {
        // return JedisUtil.serverIp+":"+JedisUtil.serverPort;
        return System.getenv("APIGATEWAY_EXPOSE_PORT") == null ? String
                .valueOf(JedisUtil.serverPort) : System.getenv("APIGATEWAY_EXPOSE_PORT");

    }

    public DiscoverInfo getServiceDiscoverInfo() {
        return RouteUtil.discoverInfo;

    }


}
