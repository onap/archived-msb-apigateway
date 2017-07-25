package org.onap.msb.apiroute.wrapper.queue;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class QueueManager {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(QueueManager.class);

	private final BaseQueue<HttpEntity> serviceListQueue;
	private final BaseQueue<List<ServiceHealth>> serviceQueue;

	private volatile  static QueueManager instance = null;

	  public static QueueManager getInstance() {
	    if (instance == null) {    
	      synchronized (QueueManager.class) {    
	         if (instance == null) {    
	            instance = new QueueManager();   
	         }    
	      }    
	    }   
	    return instance;
	  }

	private QueueManager() {
		serviceListQueue = new ServiceListQueue(
				RouteUtil.SERVICE_LIST_QUEUE_CAPACITY);
		serviceQueue = new ServiceQueue(RouteUtil.SERVICE_DATA_QUEUE_NUM,
				RouteUtil.SERVICE_QUEUE_CAPACITY);
	}

	public ServiceData<HttpEntity> takeFromServiceListQueue(
			int queueIndex) throws InterruptedException {
		return serviceListQueue.take(queueIndex);
	}

	public ServiceData<List<ServiceHealth>> takeFromServiceQueue(int queueIndex)
			throws InterruptedException {
		return serviceQueue.take(queueIndex);
	}


	@SuppressWarnings("unchecked")
	public <T> void putIn(ServiceData<T> data) throws InterruptedException {

		if (data.getDataType() == ServiceData.DataType.service_list) {
		    LOGGER.debug("putIn service_list queue success");
			serviceListQueue.put((ServiceData<HttpEntity>) data);
		} else if (data.getDataType() == ServiceData.DataType.service) {
			serviceQueue.put((ServiceData<List<ServiceHealth>>) data);
		} else {
			LOGGER.warn("DATA TYPE NOT SUPPORT:"+data.getDataType());
		}
	}
}
