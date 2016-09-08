/**
 * Copyright 2016 2015-2016 ZTE, Inc. and others. All rights reserved.
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;



public final class JedisUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(JedisUtil.class);
	private static String host = "127.0.0.1";
	private static int port = 6379;
	private static int connectionTimeout = 2000;
	private static int DEFAULT_DB_INDEX = 0;

	private static JedisPool jedisPool = null;
	
	public static String serverIp="127.0.0.1";
	
	public static int serverPort=10080;
	
	public static String propertiesName="redis.properties";
	    
	public static String propertiesPath="";

	
	
public static void main(String[] args) {
	
}

	private JedisUtil() {
		// private constructor

	}
	
	private static void initialPool() {
		try {
		    JedisPoolConfig config = new JedisPoolConfig();
			
//			String pathtest=JedisUtil.class.getResource("").getPath();
//			String path ="/"+ pathtest.substring(0, pathtest.indexOf("assembly")).replace("file:/", "") +"assembly/"+defaultWorkspace;
		
			File propertiesFile = new File(propertiesPath);
			
			   if (propertiesFile.exists()) {
				  
				 
				 BufferedInputStream inputStream =new BufferedInputStream(new FileInputStream(propertiesPath));  
				 ResourceBundle bundle =new PropertyResourceBundle(inputStream);
				 
				 if (bundle == null) {
						throw new IllegalArgumentException(
								"[redis.properties] is not found!");
					}
				 

					// 设置连接池基本信息
					String strHost = bundle.getString("redis.host");
					if(StringUtils.isNotEmpty(strHost)){
						host = strHost;
					}
					String strPort = bundle.getString("redis.port");
					if(StringUtils.isNotEmpty(strPort)){
						port = Integer.valueOf(strPort);
					}
				
					
					String strTimeout = bundle.getString("redis.connectionTimeout");
					if (StringUtils.isNotEmpty(strTimeout) ){
						connectionTimeout = Integer.valueOf(strTimeout);
					}
					
//					serverIp=bundle.getString("server.ip");
					serverPort=Integer.valueOf(bundle.getString("server.port"));
					
					String strDbIndex = bundle.getString("redis.db_index");
					if (StringUtils.isNotEmpty(strDbIndex)) {
						DEFAULT_DB_INDEX = Integer.valueOf(strDbIndex);
					}

					String strMaxTotal = bundle.getString("redis.pool.maxTotal");
					if (StringUtils.isNotEmpty(strMaxTotal)) {
						config.setMaxTotal(Integer.valueOf(strMaxTotal));
					}

					String strMaxIdle = bundle.getString("redis.pool.maxIdle");
					if (StringUtils.isNotEmpty(strMaxIdle)) {
						config.setMaxIdle(Integer.valueOf(strMaxIdle));
					}

					String strMaxWaitMillis = bundle.getString("redis.pool.maxWaitMillis");
					if (StringUtils.isNotEmpty(strMaxWaitMillis)) {
						config.setMaxWaitMillis(Long.valueOf(strMaxWaitMillis));
					}

					String strTestOnBorrow = bundle
							.getString("redis.pool.testOnBorrow");
					if (StringUtils.isNotEmpty(strTestOnBorrow)) {
						config.setTestOnBorrow(Boolean.valueOf(strTestOnBorrow));
					}

					String strTestOnReturn = bundle
							.getString("redis.pool.testOnReturn");
					if (StringUtils.isNotEmpty(strTestOnReturn)) {
						config.setTestOnReturn(Boolean.valueOf(strTestOnReturn));
					}

			   }
			
				LOGGER.info("Redis server info: " + host + ":" + port);
				LOGGER.info("nginx server info: " + serverIp + ":" + serverPort);
	
			
//			ResourceBundle bundle = ResourceBundle.getBundle("conf.redis");
	
			jedisPool = new JedisPool(config, host, port, connectionTimeout);
		} catch (Exception e) {
			LOGGER.error("Initiate Jedis pool failed!", e);
		}
	}
	/**
	 * From the connection pool to obtain jedis instance, use the default database index number 0
	 * @return
	 */
	public synchronized static Jedis borrowJedisInstance() {
		if (jedisPool == null) {
			initialPool();
		}
		try {
			if (jedisPool != null) {
				Jedis resource = jedisPool.getResource();
				resource.select(DEFAULT_DB_INDEX);
				return resource;
			} else {
				return null;
			}
		} catch (Exception e) {
			LOGGER.error("Get Jedis from pool failed!", e);
			return null;
		}
	}
	/**
	 * From the connection pool to obtain jedis instance, using the specified database index number
	 * @return
	 */
	public synchronized static Jedis borrowJedisInstance(final int dbIndex) {
		if (jedisPool == null) {
			initialPool();
		}
		try {
			if (jedisPool != null) {
				Jedis resource = jedisPool.getResource();
				resource.select(dbIndex);
				return resource;
			} else {
				return null;
			}
		} catch (Exception e) {
			LOGGER.error("Get Jedis from pool failed!", e);
			return null;
		}
	}
	
	/**
	 *  returned to the pool jedis instance
	 * @param jedis
	 */
	public static void returnJedisInstance(final Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}
	
	
	/** 
	* @Title getJedis 
	* @Description TODO(From the connection pool to obtain jedis instance) 
	* @throws Exception      
	* @return Jedis    
	*/
	public static Jedis getJedis() throws Exception{
	     
  
	        Jedis jedis = borrowJedisInstance();
            if (jedis == null) {
                throw new Exception("fetch from jedis pool failed,null object!");
                
            }
            
            return jedis;
      
	}

}