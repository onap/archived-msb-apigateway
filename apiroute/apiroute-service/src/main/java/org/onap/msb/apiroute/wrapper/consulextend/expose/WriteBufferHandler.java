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
package org.onap.msb.apiroute.wrapper.consulextend.expose;

import org.onap.msb.apiroute.wrapper.queue.QueueManager;
import org.onap.msb.apiroute.wrapper.queue.ServiceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.model.ConsulResponse;

public class WriteBufferHandler<T> implements WatchTask.Handler<T> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WriteBufferHandler.class);
	private final ServiceData.DataType dataType;


	public WriteBufferHandler(final ServiceData.DataType dataType) {
      this.dataType =dataType;
  }

	@Override
	public void handle(ConsulResponse<T> object) {
		// TODO Auto-generated method stub
		ServiceData<T> data = new ServiceData<T>();
		data.setDataType(dataType);
		data.setData(object.getResponse());
		
		try {
			QueueManager.getInstance().putIn(data);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOGGER.warn("put data to buffer interrupted:", e);
		}
	}

}
