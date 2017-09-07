/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.onap.msb.apiroute.wrapper.consulextend.expose;

import java.util.ArrayList;
import java.util.List;

import org.onap.msb.apiroute.SyncDataManager;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableService;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableServiceHealth;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.Service;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.onap.msb.apiroute.wrapper.queue.QueueManager;
import org.onap.msb.apiroute.wrapper.queue.ServiceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.ImmutableNode;



public class CheckServiceDataEmptyAndAutoStopWatchFilter implements WatchTask.Filter<List<ServiceHealth>> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CheckServiceDataEmptyAndAutoStopWatchFilter.class);
    private final String serviceName;

    public CheckServiceDataEmptyAndAutoStopWatchFilter(final String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public boolean filter(ConsulResponse<List<ServiceHealth>> object) {
        // TODO Auto-generated method stub
        boolean result = check(object);

        if (!result) {
            // create delete
            writeServiceToQueue4Del();
            // stop watch
            SyncDataManager.stopWatchService(serviceName);
        }

        return result;
    }

    // when:
    // 1)service had been deleted
    // 2)service Health check was not passing
    // single service return [],size==0
    // stop this service watching task and create delete event
    private boolean check(ConsulResponse<List<ServiceHealth>> object) {
        boolean result = true;

        if (object == null || object.getResponse() == null || object.getResponse().size() == 0) {
            LOGGER.info("check service-{},its data is empty", serviceName);
            return false;
        }

        return result;
    }

    private void writeServiceToQueue4Del() {
        ServiceData<List<ServiceHealth>> data = new ServiceData<List<ServiceHealth>>();
        data.setDataType(ServiceData.DataType.service);
        data.setOperate(ServiceData.Operate.delete);

        // tell the subsequent operation the service name which will be deleted
        Service service = ImmutableService.builder().id("").port(0).address("").service(serviceName).addTags("")
                        .createIndex(0).modifyIndex(0).build();
        ServiceHealth serviceHealth = ImmutableServiceHealth.builder().service(service)
                        .node(ImmutableNode.builder().node("").address("").build()).build();
        List<ServiceHealth> serviceHealthList = new ArrayList<ServiceHealth>();
        serviceHealthList.add(serviceHealth);

        data.setData(serviceHealthList);

        LOGGER.info("put delete service[" + serviceName + "] to service queue :because of deleted ");

        try {
            QueueManager.getInstance().putIn(data);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            LOGGER.warn("put delete service[" + serviceName + "]  to  service queue interrupted because of deleted:",
                            e);
        }
    }
}
