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
