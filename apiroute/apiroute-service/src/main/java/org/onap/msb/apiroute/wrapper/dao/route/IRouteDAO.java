package org.onap.msb.apiroute.wrapper.dao.route;

import java.util.List;

import org.onap.msb.apiroute.wrapper.dao.route.bean.RouteInfo;

public interface IRouteDAO {
    public void saveRoute(String key, RouteInfo routeInfo) throws Exception;

    public RouteInfo queryRoute(String key) throws Exception;

    public List<RouteInfo> queryMultiRoute(String keyPattern) throws Exception;

    public long deleteRoute(String key) throws Exception;

    public long deleteMultiRoute(String keyPattern) throws Exception;

}
