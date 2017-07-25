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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.onap.msb.apiroute.wrapper.InitRouteServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;



public class JedisUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(JedisUtil.class);
  private static String host = "127.0.0.1";
  private static int port = 6379;
  private static int connectionTimeout = 2000;
  private static int DEFAULT_DB_INDEX = 0;

  private volatile static JedisPool jedisPool = null;


  public static String propertiesName = "redis.properties";
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");


  private JedisUtil() {
    // private constructor

  }

  private synchronized static JedisPool initialPool() throws IOException {

    JedisPoolConfig config = new JedisPoolConfig();
    config.setMaxTotal(50);
    config.setMaxIdle(30);
    config.setMaxWaitMillis(5000);
    config.setTestOnBorrow(false);
    config.setTestOnReturn(true);

    URL urlPath = JedisUtil.class.getResource("/ext/redisConf/redis.properties");
    if (urlPath != null) {
      String propertiesPath = urlPath.getPath();


      File propertiesFile = new File(propertiesPath);

      if (propertiesFile.exists()) {


        BufferedInputStream inputStream =
            new BufferedInputStream(new FileInputStream(propertiesPath));
        ResourceBundle bundle = new PropertyResourceBundle(inputStream);

        if (bundle == null) {
          throw new IllegalArgumentException("[redis.properties] is not found!");
        }


        // Set up the connection pool basic information
        String strHost = bundle.getString("redis.host");
        if (StringUtils.isNotEmpty(strHost)) {
          host = strHost;
        }

        // redis port: first read from env
        if (StringUtils.isNotBlank(System.getenv("APIGATEWAY_REDIS_PORT"))) {
          port = Integer.parseInt(System.getenv("APIGATEWAY_REDIS_PORT"));
        } else {
          String strPort = bundle.getString("redis.port");
          if (StringUtils.isNotEmpty(strPort)) {
            port = Integer.parseInt(strPort);
          }
        }


        String strTimeout = bundle.getString("redis.connectionTimeout");
        if (StringUtils.isNotEmpty(strTimeout)) {
          connectionTimeout = Integer.parseInt(strTimeout);
        }


        String strDbIndex = bundle.getString("redis.db_index");
        if (StringUtils.isNotEmpty(strDbIndex)) {
          DEFAULT_DB_INDEX = Integer.parseInt(strDbIndex);
        }

        String strMaxTotal = bundle.getString("redis.pool.maxTotal");
        if (StringUtils.isNotEmpty(strMaxTotal)) {
          config.setMaxTotal(Integer.parseInt(strMaxTotal));
        }

        String strMaxIdle = bundle.getString("redis.pool.maxIdle");
        if (StringUtils.isNotEmpty(strMaxIdle)) {
          config.setMaxIdle(Integer.parseInt(strMaxIdle));
        }

        String strMaxWaitMillis = bundle.getString("redis.pool.maxWaitMillis");
        if (StringUtils.isNotEmpty(strMaxWaitMillis)) {
          config.setMaxWaitMillis(Long.parseLong(strMaxWaitMillis));
        }

        String strTestOnBorrow = bundle.getString("redis.pool.testOnBorrow");
        if (StringUtils.isNotEmpty(strTestOnBorrow)) {
          config.setTestOnBorrow(Boolean.valueOf(strTestOnBorrow));
        }

        String strTestOnReturn = bundle.getString("redis.pool.testOnReturn");
        if (StringUtils.isNotEmpty(strTestOnReturn)) {
          config.setTestOnReturn(Boolean.valueOf(strTestOnReturn));
        }

      }
    }

    StringBuffer redisinfo = new StringBuffer();
    redisinfo.append("------redis.properties------").append(LINE_SEPARATOR);
    redisinfo.append("redis.host: ").append(host).append(":").append(port).append(LINE_SEPARATOR);
    redisinfo.append("redis.connectionTimeout: ").append(connectionTimeout).append(LINE_SEPARATOR);
    redisinfo.append("redis.pool.maxTotal: ").append(config.getMaxTotal()).append(LINE_SEPARATOR);
    redisinfo.append("redis.pool.maxIdle: ").append(config.getMaxIdle()).append(LINE_SEPARATOR);
    redisinfo.append("redis.pool.maxWaitMillis: ").append(config.getMaxWaitMillis())
        .append(LINE_SEPARATOR);
    redisinfo.append("redis.pool.testOnBorrow: ").append(config.getTestOnBorrow())
        .append(LINE_SEPARATOR);
    redisinfo.append("redis.pool.testOnReturn: ").append(config.getTestOnReturn())
        .append(LINE_SEPARATOR);


    LOGGER.info(redisinfo.toString());
    return new JedisPool(config, host, port, connectionTimeout);

  }

  /**
   * From the connection pool to obtain jedis instance, use the default database index number 0
   * 
   * @return
   * @throws Exception
   */
  public static Jedis borrowJedisInstance() throws Exception {
    return borrowJedisInstance(DEFAULT_DB_INDEX);
  }

  /**
   * From the connection pool to obtain jedis instance, using the specified database index number
   * 
   * @return
   * @throws Exception
   */

  public static Jedis borrowJedisInstance(final int dbIndex) throws Exception {
    if (jedisPool == null) {
      synchronized (JedisUtil.class) {
        if (jedisPool == null) {         
            jedisPool = initialPool();         
        }
      }
    }
    Jedis resource = jedisPool.getResource();

    if (resource == null) {
      throw new Exception("fetch from jedis pool failed,null object!");
    }

    resource.select(dbIndex);
    return resource;

  }

  /**
   * returned to the pool jedis instance
   * 
   * @param jedis
   */
  public static void returnJedisInstance(final Jedis jedis) {
    if (jedis != null) {
      jedis.close();
    }
  }


}
