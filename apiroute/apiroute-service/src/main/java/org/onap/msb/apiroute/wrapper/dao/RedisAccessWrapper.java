/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.onap.msb.apiroute.wrapper.dao;

import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RedisAccessWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisAccessWrapper.class);
    //An iteration starts when the cursor is set to 0
    private static final String REDIS_SCAN_POINTER_NEW_ITERATION = "0";
    //An iteration terminated when the cursor returned by the server is 0
    private static final String REDIS_SCAN_POINTER_ITERATION_END = "0";
    private static final int REDIS_SCAN_COUNT = 50;


    public static void save(String key,String value) throws Exception {
        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            jedis.set(key,value);
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
    }

    public static String query(String key) throws Exception {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            value = jedis.get(key);
        }finally {
            JedisUtil.returnJedisInstance(jedis);
        }
        return value;
    }

    public static long delete(String key) throws Exception {
        Jedis jedis = null;
        long reply = 0L;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            reply = jedis.del(key);
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
        return reply;
    }

    public static boolean isExist(String key) throws Exception {
        boolean isExist = false;
        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            isExist = jedis.exists(key);
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
        return isExist;
    }

    public static List<String> queryMultiKeys(String keyPattern) throws Exception {
        Set<String> keySet = filterKeys(keyPattern);
        List<String> valueList = new ArrayList<>();
        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            for(String key : keySet){
                String value = jedis.get(key);
                if(value !=null && !"".equals(value)){
                    valueList.add(value);
                }
            }
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
        return valueList;
    }

    public static long deleteMultiKeys(String keyPattern) throws Exception {
        Set<String> keySet = filterKeys(keyPattern);
        long replySum = 0L;
        Jedis jedis = null;
        try {
            jedis = JedisUtil.borrowJedisInstance();
            for(String key : keySet){
                long reply = jedis.del(key);
                replySum = replySum + reply;
            }
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
        return replySum;
    }

    /**
     * filter the keys according to the given pattern
     * using "scan" instead of using "keys", incrementally iterate the keys space
     * @param pattern the input filter pattern
     * @return the matched keys set
     */
    public static Set<String> filterKeys(String pattern) throws Exception{
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        Set<String> filteredKeys = new HashSet<>();
        ScanParams scanParams = new ScanParams();
        scanParams.match(pattern);
        scanParams.count(REDIS_SCAN_COUNT);
        try {
            jedis = JedisUtil.borrowJedisInstance();
            ScanResult<String> scanResult = jedis.scan(REDIS_SCAN_POINTER_NEW_ITERATION,scanParams);
            filteredKeys.addAll(scanResult.getResult());
            while(!scanResult.getStringCursor().equals(REDIS_SCAN_POINTER_ITERATION_END)){
                scanResult = jedis.scan(scanResult.getStringCursor(),scanParams);
                filteredKeys.addAll(scanResult.getResult());
            }
        } finally {
            JedisUtil.returnJedisInstance(jedis);
        }
        long end = System.currentTimeMillis();
        long costTime = end-start;
        LOGGER.info("filterKeys " + pattern + " count:" + filteredKeys.size() + " cost: " + costTime);
        return filteredKeys;
    }
}
