/**
* Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.openo.msb.wrapper.util;

import java.util.List;

import org.openo.msb.api.ApiRouteInfo;
import org.openo.msb.api.RouteServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class JacksonJsonUtil {
	
	 private static final Logger logger = LoggerFactory.getLogger(JacksonJsonUtil.class);
	 
	private static ObjectMapper mapper;
	
	
	public static synchronized ObjectMapper getMapperInstance() {   
       if (mapper == null) {   
            mapper = new ObjectMapper();   
        }   
        return mapper;   
    } 
	
	/**
	 * from java object to json 
	 * @param obj 
	 * @return json
	 * @throws Exception 
	 */
	public static String beanToJson(Object obj) throws Exception {
		String json=null;
		try {
			ObjectMapper objectMapper = getMapperInstance();
			 objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);  
			json =objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
		    logger.error("Class beanToJson faild");
			 throw new Exception("Class beanToJson faild");
		}		
		return json;
	}
	
	
	
	/**
	 * from json to java object
	 * @param json 
	 * @param cls  
	 * @return 
	 * @throws Exception 
	 */
	public static Object jsonToBean(String json, Class<?> cls) throws Exception {
		Object vo =null;
		try {
		ObjectMapper objectMapper = getMapperInstance();
		  objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);  
		 vo = objectMapper.readValue(json, cls);
		
		} catch (Exception e) {
		    logger.error(cls+" JsonTobean faild");
			throw new Exception(cls+" JsonTobean faild");
		}	
		return vo;
	}
	
	/**
     * from json to java List
     * @param json 
     * @return 
     * @throws Exception 
     */
    public static List<ApiRouteInfo> jsonToListBean(String json) throws Exception {
        List<ApiRouteInfo> vo =null;
        try {
      
        ObjectMapper objectMapper = getMapperInstance();
    

         vo = objectMapper.readValue(json, new TypeReference<List<ApiRouteInfo>>() {});
        
        } catch (Exception e) {
            throw new Exception( "JSON_TO_BEAN_FAILD");
        }   
        return vo;
    }
    
    public static void main(String[] args) {
        RouteServer server=new RouteServer("127.0.0.1","80");
        try {
            String json=beanToJson(server);
            System.out.println(json);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	

}
