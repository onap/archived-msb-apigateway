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
package org.onap.msb.apiroute.health;

import java.text.SimpleDateFormat;

import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;

import redis.clients.jedis.Jedis;

public class RedisHealthCheck extends HealthCheck implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisHealthCheck.class);

    public static boolean writeCheckFlag = true;
    private static Result result = Result.healthy();

    private static int failedLoopCheckNum = 12;
    private static int failedTimer = 5 * 1000;

    private static int normalTimer = 20 * 1000;

    public static Result getResult() {
        return result;
    }

    @Override
    protected Result check() {

        // check write
        if (writeCheckFlag) {
            Result writeCheckResult = checkWrite();
            if (writeCheckResult.isHealthy()) {
                writeCheckFlag = false;
            }

            // write failed
            if (!writeCheckResult.isHealthy()) {
                return writeCheckResult;
            }
        }

        // check read
        Result readCheckResult = checkRead();

        // read failed
        if (!readCheckResult.isHealthy()) {
            return readCheckResult;
        }

        return Result.healthy();
    }

    private Result checkRead() {
        Jedis jedisHandle = null;

        Result healthRst = Result.healthy();
        try {

            jedisHandle = JedisUtil.borrowJedisInstance();
            jedisHandle.get("healthchek:checktime");

        } catch (Exception e) {
            LOGGER.warn("RedisHealthCheck exception", e);
            healthRst = Result.unhealthy(e);
        } finally {
            JedisUtil.returnJedisInstance(jedisHandle);
        }

        return healthRst;
    }

    private Result checkWrite() {
        Jedis jedisHandle = null;

        Result healthRst = Result.healthy();
        try {

            long currentTime = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(currentTime);

            jedisHandle = JedisUtil.borrowJedisInstance();
            String statusCode = jedisHandle.set("healthchek:checktime", date);

            if (statusCode != null && statusCode.equals("OK")) {
                healthRst = Result.healthy("check redis:" + statusCode);
            } else {
                healthRst = Result.unhealthy("check redis:" + statusCode);
            }

        } catch (Exception e) {
            LOGGER.warn("RedisHealthCheck exception", e);
            healthRst = Result.unhealthy(e);
        } finally {
            JedisUtil.returnJedisInstance(jedisHandle);
        }

        return healthRst;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("redis check starttime:" + System.currentTimeMillis());
            }

            result = checkWithPolicy();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("redis check result:" + result.isHealthy() + " message:" + result.getMessage());

                LOGGER.debug("redis check endtime:" + System.currentTimeMillis());
            }

            try {
                Thread.sleep(normalTimer);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                LOGGER.warn("loop check redis,thread sleep excepiton", e);
            }
        }
    }

    private Result checkWithPolicy() {
        int failedNum = 0;
        Result temp = Result.healthy();

        do {
            // check again
            temp = check();

            // healthy break;
            if (temp.isHealthy()) {
                break;
            }

            // unhealthy go on
            failedNum++;

            try {
                Thread.sleep(failedTimer);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                LOGGER.warn("loop check redis,thread sleep excepiton", e);
            }

        } while (failedNum <= failedLoopCheckNum);

        return temp;
    }
}
