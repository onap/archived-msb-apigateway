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

package org.onap.msb.apiroute.wrapper.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;


public class MicroServiceUtil {
    public static final String PREFIX_PATH = "discover:microservices";
    
    private static final Pattern SERVICE_KEY_REGEX_PATTERN =
            Pattern.compile("discover:microservices:(?<servicename>[^:]+)(:(?<version>[^:]*))");

   
    public static String getPrefixedKey(String... paths) {
        StringBuffer sb = new StringBuffer();
        
        sb.append(PREFIX_PATH);
       
        for (int i = 0; i < paths.length; i++) {
            sb.append(":");
            sb.append(paths[i]);
        }
        return sb.toString();
    }
    
   
    public static String getServiceKey(String serviceName, String version) {
        return getPrefixedKey(serviceName, version);
    }

    public static Pattern getServiceKeyRegexPattern(){
        return SERVICE_KEY_REGEX_PATTERN;
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
