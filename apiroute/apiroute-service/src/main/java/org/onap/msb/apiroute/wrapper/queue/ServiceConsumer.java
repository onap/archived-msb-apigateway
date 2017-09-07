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
package org.onap.msb.apiroute.wrapper.queue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.onap.msb.apiroute.SyncDataManager;
import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.health.RedisHealthCheck;
import org.onap.msb.apiroute.wrapper.MicroServiceWrapper;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.onap.msb.apiroute.wrapper.util.CommonUtil;
import org.onap.msb.apiroute.wrapper.util.ServiceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceConsumer implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConsumer.class);

  private boolean isRunning = true;

  private int index;
  
  
  private static final int retryCount=3;

  //缓存服务信息：key：服务名 和对应的版本列表Set<String>
  private final Map<String, Set<String>> lastVersionResponse = new HashMap<String, Set<String>>();

  public ServiceConsumer(final int index) {
    this.index = index;
  }


  public void run() {

    LOGGER.info("run Service Consumer Thread [" + index + "]");

    while (isRunning) {
      try {
        ServiceData<List<ServiceHealth>> serviceData;

        serviceData = QueueManager.getInstance().takeFromServiceQueue(index);

        // LOGGER.info("Service Consumer Thread [" + index +
        // "]  take out serviceData from Queue successfully");

        if (serviceData.getOperate() == ServiceData.Operate.delete) {
          // 删除服务
          deleteMicroService(serviceData);
        } else {
          // 更新服务
          updateMicroService(serviceData);
        }
      } catch (InterruptedException e) {
        LOGGER.error("ServiceConsumer throw  InterruptedException: ", e);
        Thread.currentThread().interrupt();
      }

    }
  }



  private void deleteMicroService(ServiceData<List<ServiceHealth>> serviceData) {
    String serviceName = null;
    try {
      if (serviceData.getData() == null || serviceData.getData().size() == 0) {
        throw new Exception("sysn deleteMicroService is wrong:serviceData is empty");
      }

      serviceName = serviceData.getData().get(0).getService().getService();
//      LOGGER.info("Service Consumer [" + index + "] start to delete MicroService:[serviceName] "
//          + serviceName);

      //ServiceListCache.removeService(serviceName);
      MicroServiceWrapper.getInstance().deleteMicroService4AllVersion(serviceName);

    } catch (Exception e) {
      LOGGER.error("delete MicroServiceInfo 4AllVersion fail from consul:[serviceName]" + serviceName, e);
      //删除失败，重试三次
      for(int i=0;i<retryCount;i++){
       
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ex) {
          LOGGER.error("delete MicroServiceInfo 4AllVersion  Thread.sleep throw except:" + ex.getMessage());
        }
        if(reDeleteMicroService(serviceName)){
          LOGGER.info((i+1) + "/"+retryCount+" : retry to delete MicroServiceInfo success [serviceName]" + serviceName);
          break;  
        }
        else{
          LOGGER.error((i+1) + "/"+retryCount+" : retry to delete MicroServiceInfo  still fail [serviceName]" + serviceName); 
        }
      }
    }
  }
  
  private boolean reDeleteMicroService(String serviceName){
    try {
      MicroServiceWrapper.getInstance().deleteMicroService4AllVersion(serviceName);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private void updateMicroService(ServiceData<List<ServiceHealth>> serviceData) {

    if (serviceData.getData() == null || serviceData.getData().size() == 0) {
      LOGGER.warn("sysn updateMicroService is wrong:serviceData is empty ");
      return;
    }

    String serviceName = "";

    try {

      serviceName = serviceData.getData().get(0).getService().getService();
      List<ServiceHealth> serviceNodeList = serviceData.getData();


      Map<String, MicroServiceFullInfo> microServiceInfo4version =
          ServiceFilter.getInstance().transMicroServiceInfoFromConsul(serviceNodeList);

      // 删除数据库中已不存在的版本号服务信息
      Set<String> newAllVersion = microServiceInfo4version.keySet();

      if (lastVersionResponse.containsKey(serviceName)) {
        Set<String> dbAllVersionSet = lastVersionResponse.get(serviceName);
        // Set<String> dbAllVersionSet=MicroServiceWrapper.getInstance().getAllVersion(serviceName);
        Set<String> delVersionList = CommonUtil.getDiffrent(newAllVersion, dbAllVersionSet);

        if (delVersionList.size() > 0) {

          LOGGER.info("MicroService version is change from consul:[serviceName]" + serviceName
              + "[version]" + delVersionList);


          for (String version : delVersionList) {
            MicroServiceWrapper.getInstance().deleteMicroService(serviceName, version);
          }
         
        }
      }
   
        lastVersionResponse.put(serviceName, newAllVersion);

      for (Map.Entry<String, MicroServiceFullInfo> entry : microServiceInfo4version.entrySet()) {
        MicroServiceFullInfo new_microServiceFullInfo = entry.getValue();
        MicroServiceWrapper.getInstance().saveServiceAndnoticeRoute(new_microServiceFullInfo);
       
      }


    } catch (Exception e) {
      LOGGER.error("update MicroServiceInfo fail from consul:[serviceName]" + serviceName);
      //更新失败，重置任务服务的modifyIndex，等待重新更新
      RedisHealthCheck.writeCheckFlag = true;
      SyncDataManager.resetIndex(serviceName);
    }
  }
}
