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
package org.onap.msb.apiroute.wrapper.dao.route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.dao.route.bean.RouteInfo;
import org.onap.msb.apiroute.wrapper.util.Jackson;

import com.fasterxml.jackson.core.JsonProcessingException;

public class RouteDAOImpl implements IRouteDAO {
    public void saveRoute(String key, RouteInfo routeInfo) throws Exception {
        String routeInfoStr = null;
        // change orginal url from “/” to empty string accord to the rewrite rule while forwarding
        if ("/".equals(routeInfo.getSpec().getUrl())) {
            routeInfo.getSpec().setUrl("");
        }
        try {
            routeInfoStr = Jackson.MAPPER.writeValueAsString(routeInfo);
        } catch (JsonProcessingException e) {
            throw new Exception("error occurred while parsing RouteInfo to json data", e);
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
            throw new Exception("error occurred while parsing the redis json data to RouteInfo", e);
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
                throw new Exception("error occurred while parsing the redis json data to RouteInfo", e);
            }
        }
        return routeInfoList;
    }

    public long deleteRoute(String key) throws Exception {
        return RedisAccessWrapper.delete(key);
    }

    public long deleteMultiRoute(String keyPattern) throws Exception {
        return RedisAccessWrapper.deleteMultiKeys(keyPattern);
    }
}
