package org.onap.msb.apiroute.wrapper.dao.route;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.dao.route.bean.RouteInfo;
import org.onap.msb.apiroute.wrapper.util.Jackson;

public class RouteDAOImpl implements IRouteDAO{
    public void saveRoute(String key, RouteInfo routeInfo) throws Exception {
        String routeInfoStr = null;
        // change orginal url from “/” to empty string accord to the rewrite rule while forwarding
        if("/".equals(routeInfo.getSpec().getUrl())){
            routeInfo.getSpec().setUrl("");
        }
        try {
            routeInfoStr = Jackson.MAPPER.writeValueAsString(routeInfo);
        } catch (JsonProcessingException e) {
            throw new Exception("error occurred while parsing RouteInfo to json data",e);
        }
        RedisAccessWrapper.save(key, routeInfoStr);
    }

    public RouteInfo queryRoute(String key) throws Exception {
        RouteInfo routeInfo = null;
        String routeInfoStr = RedisAccessWrapper.query(key);
        if (null == routeInfoStr || "".equals(routeInfoStr))
            return null;
        try {
            routeInfo = Jackson.MAPPER.readValue(routeInfoStr, RouteInfo.class);
        } catch (IOException e) {
            throw new Exception("error occurred while parsing the redis json data to RouteInfo",e);
        }
        return routeInfo;
    }

    public List<RouteInfo> queryMultiRoute(String keyPattern) throws Exception {
        List<String> routeInfoStrList = RedisAccessWrapper.queryMultiKeys(keyPattern);
        List<RouteInfo> routeInfoList = new ArrayList<>();
        for (String routeInfoStr : routeInfoStrList) {
            RouteInfo routeInfo = null;
            try {
                routeInfo = Jackson.MAPPER.readValue(routeInfoStr, RouteInfo.class);
                routeInfoList.add(routeInfo);
            } catch (IOException e) {
                throw new Exception("error occurred while parsing the redis json data to RouteInfo",e);
            }
        }
        return routeInfoList;
    }

    public long deleteRoute(String key) throws Exception {
        return RedisAccessWrapper.delete(key);
    }

    public long deleteMultiRoute(String keyPattern) throws Exception{
        return RedisAccessWrapper.deleteMultiKeys(keyPattern);
    }
}