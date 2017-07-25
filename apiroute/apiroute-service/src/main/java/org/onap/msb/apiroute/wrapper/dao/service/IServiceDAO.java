package org.onap.msb.apiroute.wrapper.dao.service;

import java.util.List;

import org.onap.msb.apiroute.wrapper.dao.service.bean.ServiceInfo;

public interface IServiceDAO {
    public void saveService(String key, ServiceInfo serviceInfo) throws Exception;

    public ServiceInfo queryService(String key) throws Exception;

    public List<ServiceInfo> queryMultiService(String keyPattern) throws Exception;

    public long deleteService(String key) throws Exception;

    public long deleteMultiService(String keyPattern) throws Exception;
}
