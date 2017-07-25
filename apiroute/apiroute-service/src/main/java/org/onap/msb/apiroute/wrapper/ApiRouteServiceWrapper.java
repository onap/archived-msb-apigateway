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
package org.onap.msb.apiroute.wrapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.onap.msb.apiroute.api.ApiRouteInfo;
import org.onap.msb.apiroute.api.exception.ExtendedInternalServerErrorException;
import org.onap.msb.apiroute.api.exception.ExtendedNotFoundException;
import org.onap.msb.apiroute.wrapper.service.ApiRouteService;
import org.onap.msb.apiroute.wrapper.util.CommonUtil;
import org.onap.msb.apiroute.wrapper.util.FileUtil;
import org.onap.msb.apiroute.wrapper.util.JacksonJsonUtil;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ApiRouteServiceWrapper  {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiRouteServiceWrapper.class);


  private static ApiRouteServiceWrapper instance = new ApiRouteServiceWrapper();

  private ApiRouteServiceWrapper() {}

  public static ApiRouteServiceWrapper getInstance() {
    return instance;
  }


  public List<ApiRouteInfo> getAllApiRouteInstances(String routeWay) {
   
    RouteUtil.checkRouteWay(routeWay);
    
    try {
      String apiRedisKey=RouteUtil.getMutiRedisKey(RouteUtil.APIROUTE, routeWay);

     return ApiRouteService.getInstance().getMultiApiRouteInstances(apiRedisKey);
      
  } catch (Exception e) {
      throw new ExtendedInternalServerErrorException(e.getMessage());
  }
  }



  /**
   * @Title: getApiRouteInstance
   * @Description: TODO(Through the name + version for a single service object information)
   * @param: @param serviceName
   * @param: @param version
   * @param: @return
   * @return: ApiRouteInfo
   */
  public ApiRouteInfo getApiRouteInstance(String serviceName, String version, String host,String publish_port,String routeWay) {

    RouteUtil.checkRouteWay(routeWay);
    
    if ("null".equals(version)) {
      version = "";
    }

    RouteUtil.checkServiceNameAndVersion(serviceName,version);
    
    String apiRedisPrefixedKey=RouteUtil.getAPIRedisPrefixedKey(serviceName, version, host, publish_port, routeWay);
      
    ApiRouteInfo apiRouteInfo;
    try {
      apiRouteInfo = ApiRouteService.getInstance().getApiRouteInstance(apiRedisPrefixedKey);
    } catch (Exception e) {
      LOGGER.error("get ApiRouteInstance throw exception", e);
      throw new ExtendedInternalServerErrorException("get ApiRouteInstance throw exception" + e.getMessage());
    }

    

    if (null == apiRouteInfo) {
      throw new ExtendedNotFoundException("Api RouteInfo not found");
    }

    return apiRouteInfo;

  }

 

  /**
   * @Title updateApiRouteStatus
   * @Description TODO(update ApiRoute Status)
   * @param serviceName
   * @param version
   * @param status
   * @return
   * @return RouteResult
   */
  public synchronized ApiRouteInfo updateApiRouteStatus(String serviceName, String version,String host,String publish_port,
      String status,String routeWay) {

    RouteUtil.checkRouteWay(routeWay);
    
    if ("null".equals(version)) {
      version = "";
    }
    
    RouteUtil.checkServiceNameAndVersion(serviceName,version);
   
    RouteUtil.checkServiceStatus(status);

    
    String apiRedisPrefixedKey=RouteUtil.getAPIRedisPrefixedKey(serviceName, version, host, publish_port, routeWay);
  
    try {
      ApiRouteService.getInstance().updateApiRouteStatus2Redis(apiRedisPrefixedKey, status);
    } catch (Exception e) {
      LOGGER.error("update ApiRoute status  throw exception", e);
      throw new ExtendedInternalServerErrorException(e.getMessage());
    }

    ApiRouteInfo new_apiRouteInfo = getApiRouteInstance(serviceName, version,host,publish_port,routeWay);
    return new_apiRouteInfo;
  }


  /**
   * @Title: saveApiRouteInstance
   * @Description: TODO(save ApiRouteInstance)
   * @param: @param apiRouteInfo
   * @param: @return
   * @return: ApiRouteInfo
   */
  public synchronized ApiRouteInfo saveApiRouteInstance4Rest(ApiRouteInfo apiRouteInfo,String routeWay) {

    RouteUtil.checkRouteWay(routeWay);
    
    RouteUtil.checkRouterInfoFormat(apiRouteInfo);
   
    try {
      saveApiRouteInstance(apiRouteInfo,routeWay);
    } catch (Exception e) {     
      throw new ExtendedInternalServerErrorException("save apiRouteInfo  fail:  [serviceName]"+apiRouteInfo.getServiceName()+"[version]"+apiRouteInfo.getVersion()+" [routeWay]"+routeWay+e.getMessage());
    }
   
    return apiRouteInfo;
  }
  
  

  public synchronized void saveApiRouteInstance(ApiRouteInfo apiRouteInfo,String routeWay) throws Exception {
    try {
     String apiRedisPrefixedKey=RouteUtil.getAPIRedisPrefixedKey(apiRouteInfo.getServiceName(), apiRouteInfo.getVersion(), apiRouteInfo.getHost(), apiRouteInfo.getPublish_port(), routeWay);

     ApiRouteService.getInstance().saveApiRouteService2Redis(apiRouteInfo, apiRedisPrefixedKey);
     LOGGER.info("save apiRouteInfo [serviceName]"+apiRouteInfo.getServiceName()+"[version]"+apiRouteInfo.getVersion()+" [routeWay]"+routeWay+" success");
    } catch (Exception e) {   
      LOGGER.error("save apiRouteInfo [serviceName]"+apiRouteInfo.getServiceName()+"[version]"+apiRouteInfo.getVersion()+" [routeWay]"+routeWay+" throw exception", e);
      throw e;
    }
    
    
  }



  /**
   * @Title: deleteApiRoute
   * @Description: TODO(delete one ApiRoute)
   * @param: @param type
   * @param: @param serviceName
   * @param: @param version
   * @param: @param delKey
   * @param: @return
   * @return: void
   */
  public synchronized void deleteApiRoute(String serviceName, String version, String host,String publish_port,String routeWay) {

    RouteUtil.checkRouteWay(routeWay);
    
    if ("null".equals(version)) {
      version = "";
    }

    RouteUtil.checkServiceNameAndVersion(serviceName,version);

    String apiRedisPrefixedKey=RouteUtil.getAPIRedisPrefixedKey(serviceName, version, host, publish_port, routeWay);

    
    try {
      ApiRouteService.getInstance()
          .deleteApiRouteService2Redis(apiRedisPrefixedKey);
      LOGGER.info("delete apiRouteInfo [serviceName]"+serviceName+"[version]"+version+" [host]"+host +" [publish_port]"+publish_port+" [routeWay]"+routeWay+" success");

    }
    catch (ExtendedNotFoundException e) {
      throw e;
    }catch (Exception e) {
      LOGGER.error("delete apiRouteInfo [serviceName]"+serviceName+"[version]"+version+" [host]"+host +" [publish_port]"+publish_port+" [routeWay]"+routeWay+" throw exception", e);

      throw new ExtendedInternalServerErrorException("delete apiRouteInfo [serviceName]"+serviceName+"[version]"+version+e.getMessage());
    }
   

  }
  
  
  /**
   * @Title: getAllApiDocs
   * @Description: TODO(For local ext\initSwaggerJson directory of all the json file directory)
   * @param: @return
   * @return: String[]
   */
  public String[] getAllApiDocs() {
    URL apiDocsPath = ApiRouteServiceWrapper.class.getResource("/ext/initSwaggerJson");
    if (apiDocsPath != null) {
      String path = apiDocsPath.getPath();

      try {
        return FileUtil.readfile(path);
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        LOGGER.error("read  ApiDocs Files throw FileNotFoundException", e);
        throw new ExtendedInternalServerErrorException("read  ApiDocs Files throw FileNotFoundException:" + e.getMessage());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        LOGGER.error("read  ApiDocs Files throw IOexception", e);
        throw new ExtendedInternalServerErrorException("read  ApiDocs Files throw IOexception:" + e.getMessage());
      }

    }

    return null;
  }
  
  public String getAllrouteByJson(String routeWay){

    Object[] apirouteArray= ApiRouteServiceWrapper.getInstance().getAllApiRouteInstances(routeWay).toArray();
    Object[] iuirouteArray= IuiRouteServiceWrapper.getInstance().getAllIuiRouteInstances(routeWay).toArray();
    Object[] customrouteArray= CustomRouteServiceWrapper.getInstance().getAllCustomRouteInstances(routeWay).toArray();
    
    Object[] temprouteArray =CommonUtil.concat(apirouteArray, iuirouteArray);
    Object[] allrouteArray=CommonUtil.concat(temprouteArray, customrouteArray);
    
   
    String allrouteJson;
    try {
      allrouteJson = JacksonJsonUtil.beanToJson(allrouteArray);
    } catch (Exception e) {
      LOGGER.error("exportService beanToJson throw Exception", e);
      throw new ExtendedInternalServerErrorException("exportService beanToJson throw Exception:"+ e.getMessage());
    }
    return allrouteJson;
  }


}
