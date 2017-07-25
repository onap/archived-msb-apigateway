package org.onap.msb.apiroute.wrapper.queue;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceListCache {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceListCache.class);

  private final static AtomicReference<Set<String>> serviceNameList4Cache = new AtomicReference<Set<String>>(new HashSet<String>());

  public static Set<String> getLatestServiceNamelist() {
    return serviceNameList4Cache.get();
  }
  
  public static void setLatestServiceNamelist(Set<String> newServicenamelist){
    serviceNameList4Cache.set(newServicenamelist);
    LOGGER.info("------current total Watch Service Num :"+ newServicenamelist.size());
  }
  
  public synchronized static void removeService(String serviceName){
    
      Set<String> servicenamelist=serviceNameList4Cache.get();  
      servicenamelist.remove(serviceName);      
      serviceNameList4Cache.set(servicenamelist);
      LOGGER.info("------current total Watch Service Num :"+ servicenamelist.size());
    }


}
