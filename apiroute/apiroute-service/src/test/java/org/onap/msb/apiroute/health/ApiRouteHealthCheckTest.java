package org.onap.msb.apiroute.health;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.msb.apiroute.health.ApiRouteHealthCheck;
import org.onap.msb.apiroute.wrapper.util.HttpClientUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck.Result;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClientUtil.class })
public class ApiRouteHealthCheckTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ApiRouteHealthCheckTest.class);
	
	@Test
	public void testchecksuccess()
	{
		PowerMockito.mockStatic(HttpClientUtil.class);
		try {
			
			PowerMockito.when(HttpClientUtil.httpGetStatus(Mockito.anyString())).thenReturn(200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ApiRouteHealthCheck check = new ApiRouteHealthCheck();
		Result rst = check.execute();
		
		if (!rst.isHealthy()) {
			LOGGER.warn("testchecksuccess health check failed:"+rst.getMessage());
		}
		else
		{
			LOGGER.debug(" testchecksuccess health");
		}
	}
	
	@Test
	public void testcheckfailed()
	{
		PowerMockito.mockStatic(HttpClientUtil.class);
		try {
			
			PowerMockito.when(HttpClientUtil.httpGetStatus(Mockito.anyString())).thenReturn(400);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ApiRouteHealthCheck check = new ApiRouteHealthCheck();
		Result rst = check.execute();
		
		if (!rst.isHealthy()) {
			LOGGER.warn("testcheckfailed health check failed:"+rst.getMessage());
		}
		else
		{
			LOGGER.debug(" testcheckfailed health");
		}
	}
}
