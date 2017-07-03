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