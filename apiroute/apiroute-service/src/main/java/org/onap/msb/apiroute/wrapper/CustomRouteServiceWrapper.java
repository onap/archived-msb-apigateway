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
/**
 * Copyright 2016-2017 ZTE, Inc. and others.
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

import java.util.List;

import org.onap.msb.apiroute.api.CustomRouteInfo;
import org.onap.msb.apiroute.api.exception.ExtendedInternalServerErrorException;
import org.onap.msb.apiroute.api.exception.ExtendedNotFoundException;
import org.onap.msb.apiroute.wrapper.service.CustomRouteService;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRouteServiceWrapper {


  private static final Logger LOGGER = LoggerFactory.getLogger(CustomRouteServiceWrapper.class);

  private static CustomRouteServiceWrapper instance = new CustomRouteServiceWrapper();

  private CustomRouteServiceWrapper() {}

  public static CustomRouteServiceWrapper getInstance() {
    return instance;
  }


  /**
   * @Title: getAllCustomRouteService
   * @Description: TODO(get AllCustomRoute Service)
   * @param: @return
   * @return: CustomRouteInfo[]
   */
  public List<CustomRouteInfo> getAllCustomRouteInstances(String routeWay) {

    RouteUtil.checkRouteWay(routeWay);
    
    try {
      String customRedisKey = RouteUtil.getMutiRedisKey(RouteUtil.CUSTOMROUTE, routeWay);
      return CustomRouteService.getInstance().getMultiCustomRouteInstances(customRedisKey);

    } catch (Exception e) {
      throw new ExtendedInternalServerErrorException(e.getMessage());
    }


  }



  /**
   * @Title: getCustomRouteInstance
   * @Description: TODO(get CustomRouteInstance by serviceName)
   * @param: @param serviceName
   * @param: @return
   * @return: CustomRouteInfo
   */
  public CustomRouteInfo getCustomRouteInstance(String serviceName, String host,
      String publish_port, String routeWay) {

    RouteUtil.checkRouteWay(routeWay);

    String customRedisPrefixedKey =
        RouteUtil.getRedisPrefixedKey(RouteUtil.CUSTOMROUTE, serviceName, host, publish_port,
            routeWay);

    CustomRouteInfo customRouteInfo;
    try {
      customRouteInfo =
          CustomRouteService.getInstance().getCustomRouteInstance(customRedisPrefixedKey);
    } catch (Exception e) {
      LOGGER.error("get customRouteInstance throw exception", e);
      throw new ExtendedInternalServerErrorException("get customRouteInstance throw exception"+ e.getMessage());
    }


    if (null == customRouteInfo) {
      throw new ExtendedNotFoundException("customRoute Info not found");

    }

    return customRouteInfo;

  }


  /**
   * @Title updateCustomRouteStatus
   * @Description TODO(update one CustomRoute Status)
   * @param serviceName
   * @param status
   * @return
   * @return RouteResult
   */
  public synchronized CustomRouteInfo updateCustomRouteStatus(String serviceName, String host,
      String publish_port, String status, String routeWay) {

    RouteUtil.checkRouteWay(routeWay);

    RouteUtil.checkServiceStatus(status);

    String customRedisPrefixedKey =
        RouteUtil.getRedisPrefixedKey(RouteUtil.CUSTOMROUTE, serviceName, host, publish_port,
            routeWay);


    try {
      CustomRouteService.getInstance()
          .updateCustomRouteStatus2Redis(customRedisPrefixedKey, status);
    } catch (Exception e) {
      LOGGER.error("update CustomRoute status  throw exception", e);
      throw new ExtendedInternalServerErrorException(e.getMessage());
    }

    CustomRouteInfo new_customRouteInfo =
        getCustomRouteInstance(serviceName, host, publish_port, routeWay);

    return new_customRouteInfo;
  }

  /**
   * @Title: saveCustomRouteInstance
   * @Description: TODO(save one CustomRouteInstance)
   * @param: @param CustomRouteInfo
   * @param: @return
   * @return: CustomRouteInfo
   */
  public synchronized CustomRouteInfo saveCustomRouteInstance4Rest(CustomRouteInfo customRouteInfo,
      String routeWay) {

    RouteUtil.checkRouteWay(routeWay);

    RouteUtil.checkRouterInfoFormat(customRouteInfo);

    try {
      saveCustomRouteInstance(customRouteInfo, routeWay);

    } catch (Exception e) {

      throw new ExtendedInternalServerErrorException("save CustomRouteInfo  fail: [serviceName]"+customRouteInfo.getServiceName()+e.getMessage());
    }

    return customRouteInfo;

  }


  public synchronized CustomRouteInfo saveCustomRouteInstance(CustomRouteInfo customRouteInfo,
      String routeWay) throws Exception {
    try {
    String customRedisPrefixedKey =
        RouteUtil.getRedisPrefixedKey(RouteUtil.CUSTOMROUTE, customRouteInfo.getServiceName(),
            customRouteInfo.getHost(), customRouteInfo.getPublish_port(), routeWay);;

  
      CustomRouteService.getInstance().saveCustomRouteService2Redis(customRouteInfo,
          customRedisPrefixedKey);
      LOGGER.info("save CustomRouteInfo [serviceName]"+customRouteInfo.getServiceName()+" [host]"+customRouteInfo.getHost() +" [publish_port]"+customRouteInfo.getPublish_port()+" [routeWay]"+routeWay+" success");

    } catch (Exception e) {
      LOGGER.error("save CustomRouteInfo [serviceName]"+customRouteInfo.getServiceName()+" [host]"+customRouteInfo.getHost() +" [publish_port]"+customRouteInfo.getPublish_port()+" [routeWay]"+routeWay+" throw exception", e);

      throw e;
    }
   
    return customRouteInfo;

  }


  /**
   * @Title: deleteCustomRoute
   * @Description: TODO(delete one CustomRoute)
   * @param: @param type
   * @param: @param serviceName
   * @param: @param delKey
   * @param: @return
   * @return: void
   */
  public synchronized void deleteCustomRoute(String serviceName, String host, String publish_port,
      String routeWay) {

    RouteUtil.checkRouteWay(routeWay);

    String customRedisPrefixedKey =
        RouteUtil.getRedisPrefixedKey(RouteUtil.CUSTOMROUTE, serviceName, host, publish_port,
            routeWay);

    try {
      CustomRouteService.getInstance().deleteCustomRouteService2Redis(customRedisPrefixedKey);
      LOGGER.info("delete CustomRouteInfo [serviceName]"+serviceName+" [host]"+host +" [publish_port]"+publish_port+" [routeWay]"+routeWay+" success");

    } catch (ExtendedNotFoundException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.error("delete CustomRouteInfo [serviceName]"+serviceName+" [host]"+host +" [publish_port]"+publish_port+" [routeWay]"+routeWay+" throw exception", e);
      throw new ExtendedInternalServerErrorException("delete CustomRouteInfo [serviceName]"+serviceName+e.getMessage());
    }



  }
}
