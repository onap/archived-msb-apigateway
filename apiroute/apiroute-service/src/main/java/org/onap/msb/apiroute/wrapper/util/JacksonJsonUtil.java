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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class JacksonJsonUtil {

  private static final Logger logger = LoggerFactory.getLogger(JacksonJsonUtil.class);

  private volatile static ObjectMapper mapper = null;

  private static ObjectMapper getMapperInstance() {
    if (mapper == null) {
      synchronized (JacksonJsonUtil.class) {
        if (mapper == null) {
          mapper = new ObjectMapper();
        }
      }
    }
    return mapper;
  }

  /**
   * from java object to json
   * 
   * @param obj
   * @return json
   * @throws Exception
   */
  public static String beanToJson(Object obj) throws Exception {
      String json = null;

      ObjectMapper objectMapper = getMapperInstance();
      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      json = objectMapper.writeValueAsString(obj);
    
      return json;
  }



  /**
   * from json to java object
   * 
   * @param json
   * @param cls
   * @return
   * @throws Exception
   */
  public static Object jsonToBean(String json, Class<?> cls) throws Exception {
    Object vo = null;
    try {
      ObjectMapper objectMapper = getMapperInstance();
      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      vo = objectMapper.readValue(json, cls);

    } catch (Exception e) {
      logger.error(cls + " JsonTobean faild");
      throw new Exception(cls + " JsonTobean faild");
    }
    return vo;
  }

  /**
   * from json to java List
   * 
   * @param json
   * @return
   * @throws Exception
   */

  public static <T> T jsonToListBean(String json, TypeReference<T> valueTypeRef) {
    try {

      ObjectMapper objectMapper = getMapperInstance();


      return objectMapper.readValue(json, valueTypeRef);

    } catch (Exception e) {
      String errorMsg = " JsonTobean faild:" + e.getMessage();
      logger.error(errorMsg);
    }
    return null;
  }



}
