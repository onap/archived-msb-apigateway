/**
 * Copyright 2016-2017 ZTE, Inc. and others.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpTestUtil {
  
 
  private final static String API_KEY_PATTERN ="/api/(?<servicename>[^/]+)(/(?<version>[^/]*)).*";
  
  private final static String IUI_KEY_PATTERN ="/iui/(?<servicename>[^/]+)/.*";
    
  public static boolean  hostRegExpTest(String host){
      
      String hostReg = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."  
              +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."  
              +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."  
              +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)"
              +":(\\d{1,5})$";   
      return Pattern.matches(hostReg, host); 
      
  }
  
  public static boolean  ipRegExpTest(String ip){
      
      String hostReg = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."  
              +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."  
              +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."  
              +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";  
      return Pattern.matches(hostReg, ip); 
      
  }
  
 public static boolean  portRegExpTest(String port){
      
      String hostReg = "^\\d{1,5}$";  
      return Pattern.matches(hostReg, port); 
      
  }
  
public static boolean versionRegExpTest(String version){
      
      String versionReg = "^v\\d+(\\.\\d+)?$";  
      return Pattern.matches(versionReg, version); 
      
  }

public static boolean urlRegExpTest(String url){
    if(url.equals("/")) return true;
    
    String urlReg = "^\\/.*((?!\\/).)$";  
    return Pattern.matches(urlReg, url); 
    
}

public static boolean apiRouteUrlRegExpTest(String url){
    
    String urlReg = "^\\/"+ConfigUtil.getInstance().getAPI_ROOT_PATH()+"\\/.*$";  
    return Pattern.matches(urlReg, url); 
    
}

public static boolean iuiRouteUrlRegExpTest(String url){
    
    String urlReg = "^\\/"+ConfigUtil.getInstance().getIUI_ROOT_PATH()+"\\/.*$";  
    return Pattern.matches(urlReg, url); 
    
}

public static String[] apiServiceNameMatch4URL(String url){
  Pattern redisKeyPattern =Pattern.compile(API_KEY_PATTERN);
  Matcher matcher = redisKeyPattern.matcher(url+"/");
  if (matcher.matches()) {
    String version;
    if(versionRegExpTest(matcher.group("version"))){
      version=matcher.group("version");
    }
    else{
      version="";
    }
     return new String[]{matcher.group("servicename"),version}; 
    }
    else{
      return null;
    }
}


public static String iuiServiceNameMatch4URL(String url){
  Pattern redisKeyPattern =Pattern.compile(IUI_KEY_PATTERN);
  Matcher matcher = redisKeyPattern.matcher(url+"/");
  if (matcher.matches()) {
     return matcher.group("servicename"); 
    }
    else{
      return null;
    }
}

}
