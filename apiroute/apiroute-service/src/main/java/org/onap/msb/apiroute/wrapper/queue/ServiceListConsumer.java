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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.onap.msb.apiroute.SyncDataManager;
import org.onap.msb.apiroute.wrapper.MicroServiceWrapper;
import org.onap.msb.apiroute.wrapper.util.CommonUtil;
import org.onap.msb.apiroute.wrapper.util.ServiceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;


public class ServiceListConsumer implements Runnable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServiceListConsumer.class);

	private boolean isRunning = true;

	private int index;


	public ServiceListConsumer() {
		this.index = 0;
	}

	public void run() {
		LOGGER.info("run ServiceList Consumer Thread [" + index + "]");

		while (isRunning) {
			try {
				// 取最新一条记录
				ServiceData<HttpEntity> serviceData = QueueManager
						.getInstance().takeFromServiceListQueue(index);
				 LOGGER.debug("ServiceList Consumer Thread [" + index +
				 "]  take out serviceData from Queue successfully");

				HttpEntity newValues = serviceData.getData();

				Set<String> newServiceNameList = filterServiceList(newValues);

				if (ServiceListCache.getLatestServiceNamelist().size() == 0) {
					boolean initSuccess=initServiceList(newServiceNameList);
					if(initSuccess){
					  ServiceListCache.setLatestServiceNamelist(newServiceNameList);
					}
				} else {
					updateServiceList(newServiceNameList);
					ServiceListCache.setLatestServiceNamelist(newServiceNameList);
				}

				
			} catch (Exception e) {
				LOGGER.error(
						"ServiceListConsumer throw  Exception: ", e);
			}
		}
	}

	private void startWatchService(String serviceName) {
		// start to Watch service nodes

		SyncDataManager.startWatchService(serviceName);
	}

	private void updateServiceList(Set<String> newServiceNameList) {
		Set<String> registerServiceNameList = CommonUtil.getDiffrent(
		    ServiceListCache.getLatestServiceNamelist(), newServiceNameList);

		if (registerServiceNameList.size() > 0) {
			LOGGER.info("***need to start Watch Service num from consul :"
					+ registerServiceNameList.size());

			for (String serviceName : registerServiceNameList) {
				startWatchService(serviceName);
			}
		}
	}

	private boolean initServiceList(Set<String> newServiceNameList) {
		LOGGER.info("***start to initialize  service List when System startup ***");

		Set<String> dbServiceNameList = MicroServiceWrapper
				.getInstance().getAllMicroServiceKey();
		
		if(dbServiceNameList==null){		 
		  LOGGER.error("init ServiceList from redis fail ");
		  return false;
		}
		
		
    		// 对比删除redis脏数据
    		Set<String> delServiceNameList = CommonUtil.getDiffrent(
    				newServiceNameList, dbServiceNameList);
    
    		LOGGER.info("***need to delete Service num from redis :"
    				+ delServiceNameList.size());
    		for (String serviceName : delServiceNameList) {
    			try {
    				MicroServiceWrapper.getInstance()
    						.deleteMicroService4AllVersion(serviceName);
    				LOGGER.info("delete MicroService success from initialize:[serviceName]"
    						+ serviceName);
    
    			} catch (Exception e) {
    				LOGGER.error(
    						"initialize serviceList :Delete MicroServiceInfo serviceName:"
    								+ serviceName + " FAIL : ", e);
    			}
    		}
    
    		// 启动同步开启监听全部服务列表
    		LOGGER.info("***need to start Watch Service num from initialize :"
    				+ newServiceNameList.size());
    
    		for (String serviceName : newServiceNameList) {
    			startWatchService(serviceName);
    		}
		
    		return true;

	}

	/*private ImmutableSet<String> filterServiceList(
			final Map<String, List<String>> serviceList) {
		if (serviceList == null || serviceList.isEmpty()) {
			return ImmutableSet.of();
		}

		final ImmutableSet.Builder<String> builder = ImmutableSet.builder();

		for (Map.Entry<String, List<String>> entry : serviceList.entrySet()) {

			String key = entry.getKey();
			if (key != null && !"consul".equals(key)) {

				List<String> value = entry.getValue();
				if (ServiceFilter.getInstance().isFilterService(value)) {
					builder.add(key);
				}
			}
		}

		LOGGER.info("consul all service num:" + serviceList.size());
		LOGGER.info("consul filter service num:" + builder.build().size());

		return builder.build();
	}
*/
	private Set<String> filterServiceList(final HttpEntity serviceList) {

		if (serviceList == null || serviceList.getContentLength() == 0) {
			return new HashSet<String>();
		}

		final Set<String> builder = new HashSet<String>();

		JsonFactory f = new JsonFactory();
		JsonParser jp = null;
		List<String> tagList = null;
		int inputServiceNum = 0;
		try {
			jp = f.createParser(serviceList.getContent());
			jp.nextToken();
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				String serviceName = jp.getCurrentName();
				inputServiceNum++;
				jp.nextToken();
				tagList = new ArrayList<>();
				while (jp.nextToken() != JsonToken.END_ARRAY) {
					tagList.add(jp.getText());
				}
				
				if (serviceName != null && !"consul".equals(serviceName)) {
					if (ServiceFilter.getInstance().isFilterService(tagList)) {
						builder.add(serviceName);
					}
				}
			}
		} catch (IOException e) {
			LOGGER.warn("parse service list error",e);
			return new HashSet<String>();
		} finally {
			try {
				jp.close();
			} catch (IOException e) {
				LOGGER.warn("parse service list error",e);
				return new HashSet<String>();
			}
		}
		
		int latestServiceNum=ServiceListCache.getLatestServiceNamelist().size();
//		if(latestServiceNum!=builder.size()){
		  LOGGER.info("[consul] all service num:" + inputServiceNum+ ", filter service num: new——" + builder.size()+"  old——"+latestServiceNum);
//		}

		return builder;
	}

}
