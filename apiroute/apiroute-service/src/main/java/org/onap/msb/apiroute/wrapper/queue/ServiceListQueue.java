package org.onap.msb.apiroute.wrapper.queue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceListQueue extends BaseQueue<HttpEntity> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceListQueue.class);

  
  private static final int SERVICE_LIST_DATA_QUEUE_NUM = 1;
  private static final int SERVICE_LIST_QUEUE_INDEX = 0;

  public ServiceListQueue(final int queueCapacity) {
    super(SERVICE_LIST_DATA_QUEUE_NUM,queueCapacity);
  }

  @Override
  public void put(ServiceData<HttpEntity> data) throws InterruptedException {
    BlockingQueue<ServiceData<HttpEntity>> queue=getQueue(SERVICE_LIST_QUEUE_INDEX);
    
    int size=queue.size();
//    LOGGER.info("before put ServiceListQueue[size:"+size+"] success :[service num]"+data.getData().size());
    //先清空队列
    if(size>0){
      queue.clear();
    }
    //插入记录
    queue.put(data);
    
  }

  @Override
  public ServiceData<HttpEntity> take(int queueIndex) throws InterruptedException {
    BlockingQueue<ServiceData<HttpEntity>> queue = getQueue(queueIndex);
    ServiceData<HttpEntity> serviceData = queue.take();
    return serviceData;
    
    /*//取队列最新一条数据
    if (queue.isEmpty()) {
      LOGGER.info("take a single serviceData from ServiceListQueue ");
      return serviceData;
    } else {
      List<ServiceData<Map<String, List<String>>>> serviceDataList =
          new ArrayList<ServiceData<Map<String, List<String>>>>();
      //一次性从BlockingQueue获取所有数据
      queue.drainTo(serviceDataList);
      LOGGER.info("take multiple serviceDatas from ServiceListQueue :[num]"+serviceDataList.size());
      return serviceDataList.get(serviceDataList.size() - 1);
    }*/
  }


}
