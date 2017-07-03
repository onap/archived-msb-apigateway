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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.api.Node;
import org.onap.msb.apiroute.api.exception.ExtendedInternalServerErrorException;
import org.onap.msb.apiroute.api.exception.ExtendedNotFoundException;
import org.onap.msb.apiroute.api.exception.UnprocessableEntityException;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.service.MicroServiceFullService;
import org.onap.msb.apiroute.wrapper.serviceListener.RouteNotify;
import org.onap.msb.apiroute.wrapper.util.MicroServiceUtil;
import org.onap.msb.apiroute.wrapper.util.RegExpTestUtil;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MicroServiceWrapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(MicroServiceWrapper.class);

  private static MicroServiceWrapper instance = new MicroServiceWrapper();


  private MicroServiceWrapper() {}

  public static MicroServiceWrapper getInstance() {
    return instance;
  }
  



  /**
   * @Title: getAllMicroServiceInstances
   * @Description: getAllMicroServiceInstances
   * @param: @return
   * @return: Response
   * @throws Exception
   */
  public List<MicroServiceFullInfo> getAllMicroServiceInstances() {

    try {
      return MicroServiceFullService.getInstance().getAllMicroServiceInstances();

    } catch (Exception e) {
      throw new ExtendedInternalServerErrorException(e.getMessage());
    }

  }

  public Set<String> getAllMicroServiceKey() {
     int failedNum = 0;
     int retryCount=3;
     int failedTimer = 5 * 1000;
    
     Set<String> serviceKeys=null;
     
     do {
       
       try {
         serviceKeys= MicroServiceFullService.getInstance().getAllMicroServiceKey();
         break;

       } catch (Exception e) {
         
         LOGGER.error(failedNum + "/"+retryCount+" :  get AllMicroServiceKey  fail"+e); 
         failedNum++;
       
           try {
             Thread.sleep(failedTimer);
           } catch (InterruptedException ex) {
             LOGGER.warn("get AllMicroServiceKey  Thread.sleep throw except:" + ex.getMessage());
           }
       }
       
     }while (failedNum <= retryCount);
    
      
      return serviceKeys;
   
  }
  
  


  /**
   * @Title: getMicroServiceInstance
   * @Description: (getMicroServiceInstance)
   * @param: @param serviceName
   * @param: @param version
   * @param: @return
   * @return: ApiRouteInfo
   */
  public MicroServiceFullInfo getMicroServiceInstance(String serviceName, String version) {
    if ("null".equals(version)) {
      version = "";
    }
    serviceName = serviceName.replace("*", "/");

    RouteUtil.checkServiceNameAndVersion(serviceName, version);

    MicroServiceFullInfo microServiceInfo;
    try {
      microServiceInfo =
          MicroServiceFullService.getInstance().getMicroServiceInstance(serviceName, version);

    } catch (Exception e) {
      throw new ExtendedInternalServerErrorException(e.getMessage());
    }

    if (null == microServiceInfo) {
      String errInfo = "microservice not found: serviceName-" + serviceName + ",version-" + version;
      throw new ExtendedNotFoundException(errInfo);

    }

    return microServiceInfo;
  }



  /**
   * @Title updateMicroServiceStatus
   * @Description updateMicroServiceStatus
   * @param serviceName
   * @param version
   * @param status
   * @return
   * @return RouteResult
   */

  public synchronized MicroServiceFullInfo updateMicroServiceStatus(String serviceName,
      String version, String status) {

    if ("null".equals(version)) {
      version = "";
    }
    serviceName = serviceName.replace("*", "/");

    RouteUtil.checkServiceNameAndVersion(serviceName, version);

    RouteUtil.checkServiceStatus(status);

    try {

      MicroServiceFullService.getInstance().updateMicroServiceStatus(serviceName, version, status);

      MicroServiceFullInfo newMicroServiceInfo =
          MicroServiceFullService.getInstance().getMicroServiceInstance(serviceName, version);

      // Notify the listeners
      RouteNotify.getInstance().noticeUpdateStatusListener(newMicroServiceInfo, status);


      return newMicroServiceInfo;
    } catch (NullPointerException e) {
      throw new ExtendedNotFoundException(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("update MicroServiceNode throw exception", e);
      throw new ExtendedInternalServerErrorException(e.getMessage());
    }


  }


  public synchronized MicroServiceFullInfo saveMicroServiceInstance(
      MicroServiceFullInfo microServiceInfo, boolean createOrUpdate, String requestIP,
      String serverPort) {

    RouteUtil.checkMicroServiceInfoFormat(microServiceInfo, requestIP);

    try {

      if (createOrUpdate == false) {
        deleteServiceAndnoticeRoute(microServiceInfo);
      }

      saveServiceAndnoticeRoute(microServiceInfo);


      MicroServiceFullInfo newMicroServiceInfo =
          MicroServiceFullService.getInstance().getMicroServiceInstance(
              microServiceInfo.getServiceName(), microServiceInfo.getVersion());


      return newMicroServiceInfo;

    } catch (UnprocessableEntityException e) {
      throw e;
    } catch (Exception e) {
      throw new ExtendedInternalServerErrorException("save MicroServiceInfo  fail :[serviceName]" + microServiceInfo.getServiceName()
          + "[version]" + microServiceInfo.getVersion()+ e.getMessage());
    }

  }


  public synchronized void deleteMicroService4AllVersion(String serviceName) {
    try {

      List<MicroServiceFullInfo> microServiceList4AllVersion =
          MicroServiceFullService.getInstance().getAllVersionsOfTheService(serviceName);

      if (microServiceList4AllVersion.size() == 0) {
        LOGGER.info("delete MicroServiceInfo for All Version Fail:serviceName-" + serviceName
            + " not fond");
      } else {
        for (MicroServiceFullInfo microServiceInfo : microServiceList4AllVersion) {
          deleteServiceAndnoticeRoute(microServiceInfo);
        }
      }

    } catch (Exception e) {
      LOGGER.error("delete MicroServiceInfo for all version :serviceName-" + serviceName +" throw exception", e);

    }
  }


  public synchronized void deleteMicroService(String serviceName, String version) {
    if ("null".equals(version)) {
      version = "";
    }
    serviceName = serviceName.replace("*", "/");

    RouteUtil.checkServiceNameAndVersion(serviceName, version);

    try {

      MicroServiceFullInfo microServiceInfo =
          MicroServiceFullService.getInstance().getMicroServiceInstance(serviceName, version);

      if (microServiceInfo == null) {
        LOGGER.error("delete MicroServiceInfo FAIL:serviceName-" + serviceName + ",version-"+ version + " not fond ");
      } else {

        deleteServiceAndnoticeRoute(microServiceInfo);       
      }


    } catch (ExtendedNotFoundException e) {
      throw e;
    } catch (Exception e) {

      throw new ExtendedInternalServerErrorException("delete MicroServiceInfo serviceName-" + serviceName + ",version-" + version+e.getMessage());

    }


  }

  public synchronized void deleteMicroServiceInstance(String serviceName, String version,
      String ip, String port) {
    if ("null".equals(version)) {
      version = "";
    }
    serviceName = serviceName.replace("*", "/");

    RouteUtil.checkServiceNameAndVersion(serviceName, version);

    if (!RegExpTestUtil.ipRegExpTest(ip)) {
      throw new UnprocessableEntityException("delete MicroServiceInfo FAIL:IP(" + ip+ ")is not a valid IP address");
    }

    if (!RegExpTestUtil.portRegExpTest(port)) {
      throw new UnprocessableEntityException("delete MicroServiceInfo FAIL:Port(" + port + ")is not a valid Port address");
    }


    try {
      MicroServiceFullInfo microServiceInfo =
          MicroServiceFullService.getInstance().getMicroServiceInstance(serviceName, version);

      if (microServiceInfo == null) {
        throw new UnprocessableEntityException("delete MicroServiceInfo FAIL:serviceName-"+ serviceName + ",version-" + version + " not fond ");
      }

      Set<Node> nodes = microServiceInfo.getNodes();

      boolean ifFindBNode = false;

      for (Node node : nodes) {
        if (node.getIp().equals(ip) && node.getPort().equals(port)) {
          ifFindBNode = true;
          nodes.remove(node);

          if (nodes.isEmpty()) {
            // delete MicroService
            deleteServiceAndnoticeRoute(microServiceInfo);
          } else {
            // delete Node
            MicroServiceFullService.getInstance().saveMicroServiceInfo2Redis(microServiceInfo);
            RouteNotify.getInstance().noticeRouteListener4Update(serviceName, version,
                microServiceInfo);
          }

          break;
        }
      }

      if (!ifFindBNode) {
        throw new ExtendedNotFoundException("delete MicroServiceInfo FAIL:serviceName-"+ serviceName + ",version-" + version +",node-" + ip + ":" + port + " not fond ");
      }


    } catch (ExtendedNotFoundException e) {
      throw e;
    } catch (Exception e) {
      throw new ExtendedInternalServerErrorException("delete MicroServiceInfo :serviceName-"+ serviceName + ",version-" + version+",node-" + ip + ":" + port +"throw exception"+e.getMessage());

    }

  }


  public void deleteServiceAndnoticeRoute(MicroServiceFullInfo service) throws Exception {

    try {
      // Delete the redis record
      MicroServiceFullService.getInstance().deleteMicroService(service.getServiceName(),
          service.getVersion());
      LOGGER.info("delete MicroServiceInfo  And notice to Route success:[serviceName]"+ service.getServiceName() + "[version]" + service.getVersion());

      // Notify the listeners
      RouteNotify.getInstance().noticeRouteListener4Delete(service);
      
    } catch (Exception e) {
      LOGGER.error("delete MicroService And synchro to Route:[serviceName]"+ service.getServiceName() + "[version]" + service.getVersion()+" throw exception", e);
      throw e;
    }
  }

  public void saveServiceAndnoticeRoute(MicroServiceFullInfo service) throws Exception {

    try {
      // save the redis record
      MicroServiceFullService.getInstance().saveMicroServiceInfo2Redis(service);
     
      LOGGER.info("save MicroServiceInfo  And notice to Route success:[serviceName]"+ service.getServiceName() + "[version]" + service.getVersion());

      // Notify the listeners
      RouteNotify.getInstance().noticeRouteListener4Add(service);


    } catch (Exception e) {
      LOGGER.error("save MicroServiceInfo And synchro to  Route fail :[serviceName]" + service.getServiceName()+ "[version]" + service.getVersion() + " throw exception", e);
      throw e;
    }
  }

  public Set<String> getAllVersion(String serviceName) {
    Set<String> serviceVersionSet = new HashSet<String>();
    try {
      String pattern = MicroServiceUtil.getServiceKey(serviceName, "*");
      Set<String> serviceKeySet = RedisAccessWrapper.filterKeys(pattern);


      Pattern serviceKeyRegexPattern = MicroServiceUtil.getServiceKeyRegexPattern();
      for (String serviceKey : serviceKeySet) {
        Matcher matcher = serviceKeyRegexPattern.matcher(serviceKey);
        if (matcher.matches()) {
          serviceVersionSet.add(matcher.group("version"));
        }
      }
    } catch (Exception e) {
      LOGGER.error("getAllVersion [serviceName]:" + serviceName + "  throw exception", e);
    }

    return serviceVersionSet;

  }



}
