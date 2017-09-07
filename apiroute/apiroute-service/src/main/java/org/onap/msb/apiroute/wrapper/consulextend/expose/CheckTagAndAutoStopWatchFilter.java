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

import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.onap.msb.apiroute.wrapper.queue.QueueManager;
import org.onap.msb.apiroute.wrapper.queue.ServiceData;
import org.onap.msb.apiroute.wrapper.queue.ServiceData.Operate;
import org.onap.msb.apiroute.wrapper.util.ServiceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.model.ConsulResponse;

public class CheckTagAndAutoStopWatchFilter implements WatchTask.Filter<List<ServiceHealth>> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CheckTagAndAutoStopWatchFilter.class);

    private final String serviceName;

    public CheckTagAndAutoStopWatchFilter(final String serviceName) {
        this.serviceName = serviceName;
    }

    // from consul,the response data:List<ServiceHealth>
    // filter ServiceHealth list and find the ServiceHealths which satisfy the
    // tags conditions
    // 1)if all ServiceHealth don't satisfy,create delete event and stop watch
    // 2)if have some ServiceHealths satisfy the tags conditions,create update
    // event and send these ServiceHealths
    @Override
    public boolean filter(ConsulResponse<List<ServiceHealth>> object) {
        // TODO Auto-generated method stub

        // find #ServiceHealth# which satisfy the tag conditions
        List<ServiceHealth> satisfyList = getSatisfyList(object);

        // no satisfied ServiceHealth
        if (satisfyList.isEmpty()) {

            LOGGER.info("put delete service[" + serviceName
                            + "] to service queue :because of NO tag meet the conditions");

            // create delete
            writeServiceToQueue(object.getResponse(), ServiceData.Operate.delete);
            // stop watch
            // SyncDataManager.stopWatchService(serviceName);
            return false;
        }

        LOGGER.info("put update service[" + serviceName + "] to service queue :which tags meet the conditions");

        // put the satisfy list to queue
        writeServiceToQueue(satisfyList, ServiceData.Operate.update);

        return true;
    }

    private List<ServiceHealth> getSatisfyList(ConsulResponse<List<ServiceHealth>> object) {
        List<ServiceHealth> satisfyList = new ArrayList<ServiceHealth>();
        for (ServiceHealth health : object.getResponse()) {

            if (ServiceFilter.getInstance().isFilterCheck(health)) {
                satisfyList.add(health);
            }
        }

        return satisfyList;
    }

    private void writeServiceToQueue(List<ServiceHealth> serviceData, Operate operate) {
        ServiceData<List<ServiceHealth>> data = new ServiceData<List<ServiceHealth>>();
        data.setOperate(operate);
        data.setDataType(ServiceData.DataType.service);
        data.setData(serviceData);


        try {
            QueueManager.getInstance().putIn(data);
        } catch (InterruptedException e) {
            LOGGER.warn("put " + operate + " service[" + serviceName + "]  to  service queue interrupted ", e);
        }

    }
}
