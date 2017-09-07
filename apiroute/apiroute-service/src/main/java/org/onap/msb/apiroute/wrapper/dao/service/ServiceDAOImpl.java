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
package org.onap.msb.apiroute.wrapper.dao.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.dao.service.bean.ServiceInfo;
import org.onap.msb.apiroute.wrapper.util.Jackson;

public class ServiceDAOImpl implements  IServiceDAO{
    public void saveService(String key, ServiceInfo serviceInfo) throws Exception {
        String serviceInfoStr = null;
        try {
            serviceInfoStr = Jackson.MAPPER.writeValueAsString(serviceInfo);
        } catch (JsonProcessingException e) {
            throw new Exception("error occurred while parsing ServiceInfo to json data",e);
        }
        RedisAccessWrapper.save(key, serviceInfoStr);
    }

    public ServiceInfo queryService(String key) throws Exception {
        ServiceInfo serviceInfo = null;
        String serviceInfoStr = RedisAccessWrapper.query(key);
        if (null == serviceInfoStr || "".equals(serviceInfoStr))
            return null;
        try {
            serviceInfo = Jackson.MAPPER.readValue(serviceInfoStr, ServiceInfo.class);
        } catch (IOException e) {
            throw new Exception("error occurred while parsing the redis json data to ServiceInfo",e);
        }
        return serviceInfo;
    }

    public List<ServiceInfo> queryMultiService(String keyPattern) throws Exception {
        List<String> serviceInfoStrList = RedisAccessWrapper.queryMultiKeys(keyPattern);
        List<ServiceInfo> routeInfoList = new ArrayList<>();
        for (String serviceInfoStr : serviceInfoStrList) {
            ServiceInfo serviceInfo = null;
            try {
                serviceInfo = Jackson.MAPPER.readValue(serviceInfoStr, ServiceInfo.class);
                routeInfoList.add(serviceInfo);
            } catch (IOException e) {
                throw new Exception("error occurred while parsing the redis json data to ServiceInfo",e);
            }
        }
        return routeInfoList;
    }

    public long deleteService(String key) throws Exception {
        return RedisAccessWrapper.delete(key);
    }

    public long deleteMultiService(String keyPattern) throws Exception{
        return RedisAccessWrapper.deleteMultiKeys(keyPattern);
    }
}
