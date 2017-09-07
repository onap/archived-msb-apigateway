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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck.Result;
import com.fiftyonred.mock_jedis.MockJedisPool;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JedisUtil.class, RedisAccessWrapper.class})
@PowerMockIgnore({"javax.management.*"})
public class RedisHealthCheckTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisHealthCheckTest.class);

    @Before
    public void setUpBeforeTest() throws Exception {

    }

    @SuppressWarnings("static-access")
    @Test
    public void testchecksuccess() {

        try {
            final JedisPool mockJedisPool = new MockJedisPool(new JedisPoolConfig(), "localhost");
            PowerMockito.mockStatic(JedisUtil.class);
            JedisUtil jedisUtil = PowerMockito.mock(JedisUtil.class);
            PowerMockito.when(jedisUtil.borrowJedisInstance()).thenReturn(mockJedisPool.getResource());

            RedisHealthCheck check = new RedisHealthCheck();
            Result rst = check.execute();

            if (!rst.isHealthy()) {
                LOGGER.warn("testchecksuccess health check failed:" + rst.getMessage());
            } else {
                LOGGER.debug(" testchecksuccess health");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testcheckfailed() {
        RedisHealthCheck check = new RedisHealthCheck();
        Result rst = check.execute();

        if (!rst.isHealthy()) {
            LOGGER.warn("testcheckfailed health check failed:" + rst.getMessage());
        } else {
            LOGGER.debug("testcheckfailed health");
        }

    }
}
