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
 * limitations under the License.
 ******************************************************************************/
package org.onap.msb.apiroute.wrapper;

import java.util.List;

import org.onap.msb.apiroute.api.IuiRouteInfo;
import org.onap.msb.apiroute.api.exception.ExtendedInternalServerErrorException;
import org.onap.msb.apiroute.api.exception.ExtendedNotFoundException;
import org.onap.msb.apiroute.wrapper.service.IuiRouteService;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IuiRouteServiceWrapper {


  private static final Logger LOGGER = LoggerFactory.getLogger(IuiRouteServiceWrapper.class);

  private static IuiRouteServiceWrapper instance = new IuiRouteServiceWrapper();

  private IuiRouteServiceWrapper() {}

  public static IuiRouteServiceWrapper getInstance() {
    return instance;
  }



  /**
   * @Title: getAllIuiRouteService
   * @Description: TODO(get All IuiRouteServices)
   * @param: @return
   * @return: IuiRouteInfo[]
   */
  public List<IuiRouteInfo> getAllIuiRouteInstances(String routeWay) {

    RouteUtil.checkRouteWay(routeWay);
    
    try {
      String iuiRedisKey = RouteUtil.getMutiRedisKey(RouteUtil.IUIROUTE, routeWay);

      return IuiRouteService.getInstance().getMultiIuiRouteInstances(iuiRedisKey);

    } catch (Exception e) {
      throw new ExtendedInternalServerErrorException(e.getMessage());
    }


  }



  /**
   * @Title: getIuiRouteInstance
   * @Description: TODO(get one IuiRouteInstance by serviceName)
   * @param: @param serviceName
   * @param: @return
   * @return: IuiRouteInfo
   */
  public IuiRouteInfo getIuiRouteInstance(String serviceName, String host, String publish_port,
      String routeWay) {

    RouteUtil.checkRouteWay(routeWay);

    String iuiRedisPrefixedKey =
        RouteUtil
            .getRedisPrefixedKey(RouteUtil.IUIROUTE, serviceName, host, publish_port, routeWay);

    IuiRouteInfo iuiRouteInfo;
    try {
      iuiRouteInfo = IuiRouteService.getInstance().getIuiRouteInstance(iuiRedisPrefixedKey);
    } catch (Exception e) {
      LOGGER.error("get IuiRouteInstance throw exception", e);
      throw new ExtendedInternalServerErrorException("get IuiRouteInstance throw exception"
          + e.getMessage());
    }



    if (null == iuiRouteInfo) {
      throw new ExtendedNotFoundException("iui RouteInfo not found");
    }

    return iuiRouteInfo;
  }



  /**
   * @Title updateIuiRouteStatus
   * @Description TODO(update one IuiRoute Status)
   * @param serviceName
   * @param status
   * @return
   * @return RouteResult
   */
  public synchronized IuiRouteInfo updateIuiRouteStatus(String serviceName, String host,
      String publish_port, String status, String routeWay) {

    RouteUtil.checkRouteWay(routeWay);

    RouteUtil.checkServiceStatus(status);

    try {
      String iuiRedisPrefixedKey =
          RouteUtil.getRedisPrefixedKey(RouteUtil.IUIROUTE, serviceName, host, publish_port,
              routeWay);

      IuiRouteService.getInstance().updateIuiRouteStatus2Redis(iuiRedisPrefixedKey, status);


    } catch (Exception e) {
      LOGGER.error("update IuiRoute status  throw exception", e);
      throw new ExtendedInternalServerErrorException(e.getMessage());
    }

    IuiRouteInfo new_iuiRouteInfo = getIuiRouteInstance(serviceName, host, publish_port, routeWay);

    return new_iuiRouteInfo;
  }

  /**
   * @Title: saveIuiRouteInstance
   * @Description: TODO(save one IuiRouteInstance)
   * @param: @param IuiRouteInfo
   * @param: @return
   * @return: IuiRouteInfo
   */
  public synchronized IuiRouteInfo saveIuiRouteInstance4Rest(IuiRouteInfo iuiRouteInfo,
      String routeWay) {

    RouteUtil.checkRouteWay(routeWay);

    RouteUtil.checkRouterInfoFormat(iuiRouteInfo);


    try {
      saveIuiRouteInstance(iuiRouteInfo, routeWay);
    } catch (Exception e) {
      throw new ExtendedInternalServerErrorException("save iuiRouteInfo  fail: [serviceName]"
          + iuiRouteInfo.getServiceName() + e.getMessage());
    }

    return iuiRouteInfo;
  }


  public synchronized void saveIuiRouteInstance(IuiRouteInfo iuiRouteInfo, String routeWay)
      throws Exception {
    try {
      String iuiRedisPrefixedKey =
          RouteUtil.getRedisPrefixedKey(RouteUtil.IUIROUTE, iuiRouteInfo.getServiceName(),
              iuiRouteInfo.getHost(), iuiRouteInfo.getPublish_port(), routeWay);

      IuiRouteService.getInstance().saveIuiRouteService2Redis(iuiRouteInfo, iuiRedisPrefixedKey);
      LOGGER.info("save iuiRouteInfo [serviceName]" + iuiRouteInfo.getServiceName() + " [host]"
          + iuiRouteInfo.getHost() + " [publish_port]" + iuiRouteInfo.getPublish_port()
          + " [routeWay]" + routeWay + " success");

    } catch (Exception e) {
      LOGGER.error("save iuiRouteInfo [serviceName]" + iuiRouteInfo.getServiceName() + " [host]"
          + iuiRouteInfo.getHost() + " [publish_port]" + iuiRouteInfo.getPublish_port()
          + " [routeWay]" + routeWay + " throw exception", e);
      throw e;
    }
  }



  /**
   * @Title: deleteIuiRoute
   * @Description: TODO(delete one IuiRoute)
   * @param: @param type
   * @param: @param serviceName
   * @param: @param delKey
   * @param: @return
   * @return: void
   */
  public synchronized void deleteIuiRoute(String serviceName, String host, String publish_port,
      String routeWay) {

    RouteUtil.checkRouteWay(routeWay);

    String iuiRedisPrefixedKey =
        RouteUtil
            .getRedisPrefixedKey(RouteUtil.IUIROUTE, serviceName, host, publish_port, routeWay);

    try {
      IuiRouteService.getInstance().deleteIuiRouteService2Redis(iuiRedisPrefixedKey);
      LOGGER.info("delete iuiRouteInfo [serviceName]" + serviceName + " [host]" + host
          + " [publish_port]" + publish_port + " [routeWay]" + routeWay + " success");

    } catch (ExtendedNotFoundException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.error("delete iuiRouteInfo [serviceName]" + serviceName + " [host]" + host
          + " [publish_port]" + publish_port + " [routeWay]" + routeWay + " throw exception", e);
      throw new ExtendedInternalServerErrorException("delete iuiRouteInfo [serviceName]" + serviceName +e.getMessage());
    }



  }



}
