package org.onap.msb.apiroute.wrapper.queue;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceQueue extends BaseQueue<List<ServiceHealth>> {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceQueue.class);

  private  int queneNum;
  
  public ServiceQueue(final int queneNum,final int queueCapacity) {
    super(queneNum,queueCapacity);
    this.queneNum=queneNum;
  }
  

  @Override
  public void put(final ServiceData<List<ServiceHealth>> data) throws InterruptedException {
    if(data.getData()==null || data.getData().size()==0) return;
    
    String serviceName = data.getData().get(0).getService().getService();
    long serviceNameHashCode=serviceName.hashCode() & 0x7FFFFFFF;
    int queneIndex=(int) (serviceNameHashCode % queneNum);
    
//    LOGGER.info("put ServiceQueue [serviceName.hashCode():"+serviceNameHashCode+",queneIndex:"+queneIndex+",queneNum:"+queneNum+"] :[serviceName]"+serviceName);
   
    BlockingQueue<ServiceData<List<ServiceHealth>>>  queue=getQueue(queneIndex);
    queue.put(data);
    
    LOGGER.info("put ServiceQueue[index:"+queneIndex+",size:"+queue.size()+"] success :[serviceName]"+serviceName);
  }

  @Override
  public ServiceData<List<ServiceHealth>> take(final int queueIndex) throws InterruptedException {
      return getQueue(queueIndex).take();
  }
  

}
