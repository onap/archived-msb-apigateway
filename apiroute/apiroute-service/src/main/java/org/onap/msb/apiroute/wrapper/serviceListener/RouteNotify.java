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
package org.onap.msb.apiroute.wrapper.serviceListener;

import java.util.ArrayList;
import java.util.List;

import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.wrapper.util.ServiceFilter;

public class RouteNotify {
  
  private static RouteNotify instance = new RouteNotify();

  private List<IMicroServiceChangeListener> serviceListenerlist =
      new ArrayList<IMicroServiceChangeListener>();

  private RouteNotify() {}

  public static RouteNotify getInstance() {
    return instance;
  }

  
  public void addServiceChangeListener(IMicroServiceChangeListener listener) {
    synchronized (serviceListenerlist) {
      serviceListenerlist.add(listener);
    }
  }


 /* public void removeServiceChangeListener(IMicroServiceChangeListener listener) {
    synchronized (serviceListenerlist) {
      serviceListenerlist.remove(listener);
    }
  }*/


  public void noticeRouteListener4Update(String serviceName, String version, MicroServiceFullInfo microServiceInfo) throws Exception {
    if (ServiceFilter.getInstance().isNeedNotifyByProtocol(microServiceInfo.getProtocol())) {
      for (IMicroServiceChangeListener serviceListener : serviceListenerlist) {
        serviceListener.onChange(serviceName, version, microServiceInfo);
      }
    }

  }

  public void noticeUpdateStatusListener(MicroServiceFullInfo microServiceInfo, String status) {

    for (IMicroServiceChangeListener serviceListener : serviceListenerlist) {
      serviceListener.onStatusChange(microServiceInfo.getServiceName(),
          microServiceInfo.getVersion(), microServiceInfo.getHost(),microServiceInfo.getProtocol(), microServiceInfo.getPublish_port(),status);
    }
  }



  
  public void noticeRouteListener4Add(MicroServiceFullInfo microServiceInfo) throws Exception {
    if (ServiceFilter.getInstance().isNeedNotifyByProtocol(microServiceInfo.getProtocol())) {
        for (IMicroServiceChangeListener serviceListener : serviceListenerlist) {
          serviceListener.onSave(microServiceInfo);
        }
    }
  }
  
  public void noticeRouteListener4Delete(MicroServiceFullInfo microServiceInfo) throws Exception {
    if (ServiceFilter.getInstance().isNeedNotifyByProtocol(microServiceInfo.getProtocol())) {
        for (IMicroServiceChangeListener serviceListener : serviceListenerlist) {
          serviceListener.onDelete(microServiceInfo);
        }
    }
  }

  
  


}
