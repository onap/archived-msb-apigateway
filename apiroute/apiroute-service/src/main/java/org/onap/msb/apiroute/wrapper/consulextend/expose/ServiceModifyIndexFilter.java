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

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.onap.msb.apiroute.wrapper.consulextend.model.health.Service;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.onap.msb.apiroute.wrapper.util.ServiceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.HealthCheck;

public class ServiceModifyIndexFilter implements WatchTask.Filter<List<ServiceHealth>> {

    private final AtomicReference<ImmutableList<ServiceHealth>> lastResponse =
                    new AtomicReference<ImmutableList<ServiceHealth>>(ImmutableList.<ServiceHealth>of());

    private final static Logger LOGGER = LoggerFactory.getLogger(ServiceModifyIndexFilter.class);


    @Override
    public boolean filter(ConsulResponse<List<ServiceHealth>> object) {
        // TODO Auto-generated method stub

        List<ServiceHealth> newList = object.getResponse();
        if (realFilter(newList)) {
            lastResponse.set(ImmutableList.copyOf(newList));
            return true;
        }

        return false;
    }

    private boolean realFilter(List<ServiceHealth> newList) {
        // 1)判断list的size，不等则改变
        if (newList.size() != lastResponse.get().size()) {
            // 第一次不打印
            if (lastResponse.get().size() != 0) {
                LOGGER.info(newList.get(0).getService().getService() + " instance count is different.new_count:"
                                + newList.size() + " old_count:" + lastResponse.get().size());
            }

            return true;
        }


        // 2）循环服务实例判断服务内容和健康检查是否改变
        for (ServiceHealth newData : newList) {
            ServiceHealth sameIdOldData = findSameIdInOldList(newData);
            // 若在oldlist中不存在，则改变
            if (sameIdOldData == null) {

                LOGGER.info(newData.getService().getId() + " is a new service instance.the createindex:"
                                + newData.getService().getCreateIndex() + " the modifyIndex:"
                                + newData.getService().getModifyIndex());

                return true;
            }

            // 若在oldlist中存在，则比较ModifyIndex的值和健康检查状态.不等则改变
            if (!compareService(newData, sameIdOldData)) {
                LOGGER.info(newData.getService().getId()
                                + " instance  is change because of modifyIndex  or health check");
                return true;
            }
        }

        return false;


    }


    private boolean compareService(ServiceHealth oldData, ServiceHealth newData) {

        return compareServiceInfo(oldData.getService(), newData.getService())
                        && compareServiceHealthStatus(oldData.getChecks(), newData.getChecks());

    }



    private boolean compareServiceInfo(Service oldServiceInfo, Service newServiceInfo) {
        if (oldServiceInfo.getModifyIndex() != newServiceInfo.getModifyIndex()) {
            LOGGER.info(newServiceInfo.getId() + " new_modifyIndex:" + newServiceInfo.getModifyIndex()
                            + " old_modifyIndex:" + oldServiceInfo.getModifyIndex());
            return false;
        }
        return true;
    }

    private boolean compareServiceHealthStatus(List<HealthCheck> oldData, List<HealthCheck> newData) {
        boolean oldHealthCheck = ServiceFilter.getInstance().isFilterHealthCheck(oldData);
        boolean newHealthCheck = ServiceFilter.getInstance().isFilterHealthCheck(newData);
        return oldHealthCheck == newHealthCheck;

    }


    private ServiceHealth findSameIdInOldList(ServiceHealth newData) {
        for (ServiceHealth oldData : lastResponse.get()) {
            if (oldData.getService().getId().equals(newData.getService().getId())) {
                return oldData;
            }
        }

        return null;
    }

    public boolean resetModifyIndex() {
        // clear last response
        lastResponse.set(ImmutableList.<ServiceHealth>of());
        return true;
    }
}
