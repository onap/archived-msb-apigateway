/**
 * Copyright 2016 ZTE, Inc. and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openo.msb.wrapper.util;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;


public class MicroServiceUtil {
    public static final String PREFIX_PATH = "discover:microservices";
    
    public static final String PREFIX_PATH_PORT = "discover:";

    public static final String SUFFIX_PATH_INFO = "info";

    public static final String REDIS_KEY_PATTERN =
            "discover:microservices:(?<servicename>[^:]+)(:(?<version>[^:]*))?:info";

    public static final String REQUEST_SUCCESS = "SUCCESS";

    public static final String REQUEST_FAIL = "FAIL";

    public static final String ROUTE_PATH_LOADBALANCE = "lb"; // 负载均衡路径名

   
    public static String getPrefixedKey(String... paths) {
        StringBuffer sb = new StringBuffer();
        
        if(paths[0].trim().equals("") || paths[0].equals(String.valueOf(JedisUtil.serverPort))){
            sb.append(PREFIX_PATH);
        }
        else{
           sb.append(PREFIX_PATH_PORT).append(paths[0]); 
        }
        
        for (int i = 1; i < paths.length; i++) {
            sb.append(":");
            sb.append(paths[i]);
        }
        return sb.toString();
    }

    
    public static String getServiceInfoKey(String serverPort,String serviceName, String version) {
        return getPrefixedKey(serverPort,serviceName, version, SUFFIX_PATH_INFO);
    }

 
    
    
    
    public static Pattern getRedisKeyPattern() {
        return Pattern.compile(REDIS_KEY_PATTERN);
    }

    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // After the reverse proxy can have multiple IP value for many times, the first IP is the real IP
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");

        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        

        return request.getRemoteAddr();

    }


}
