package org.onap.msb.apiroute.health;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.health.RedisHealthCheck;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.codahale.metrics.health.HealthCheck.Result;
import com.fiftyonred.mock_jedis.MockJedisPool;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JedisUtil.class,RedisAccessWrapper.class})
@PowerMockIgnore( {"javax.management.*"})
public class RedisHealthCheckTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RedisHealthCheckTest.class);
	
	@Before
    public void setUpBeforeTest() throws Exception {

    }
	
	@SuppressWarnings("static-access")
	@Test
	public void testchecksuccess()
	{

        try {
            final JedisPool mockJedisPool = new MockJedisPool(new JedisPoolConfig(), "localhost");
            PowerMockito.mockStatic(JedisUtil.class);
            JedisUtil jedisUtil=PowerMockito.mock(JedisUtil.class);
			PowerMockito.when(jedisUtil.borrowJedisInstance()).thenReturn(mockJedisPool.getResource());
			
			RedisHealthCheck check = new RedisHealthCheck();
			Result rst = check.execute();
			
			if (!rst.isHealthy()) {
				LOGGER.warn("testchecksuccess health check failed:"+rst.getMessage());
			}
			else
			{
				LOGGER.debug(" testchecksuccess health");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testcheckfailed()
	{
		RedisHealthCheck check = new RedisHealthCheck();
		Result rst = check.execute();
		
		if (!rst.isHealthy()) {
			LOGGER.warn("testcheckfailed health check failed:"+rst.getMessage());
		}
		else
		{
			LOGGER.debug("testcheckfailed health");
		}
		
	}
}
