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
package org.onap.msb.apiroute.health;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.msb.apiroute.health.ConsulLinkHealthCheck;
import org.onap.msb.apiroute.wrapper.util.HttpClientUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck.Result;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClientUtil.class,ConsulLinkHealthCheck.class })
public class ConsulLinkHealthCheckTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConsulLinkHealthCheckTest.class);
	
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
		
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.getenv("CONSUL_IP")).thenReturn("192.168.0.1");
		
		ConsulLinkHealthCheck check = new ConsulLinkHealthCheck();
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
		
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.getenv("CONSUL_IP")).thenReturn("192.168.0.1");
		
		ConsulLinkHealthCheck check = new ConsulLinkHealthCheck();
		Result rst = check.execute();
		
		if (!rst.isHealthy()) {
			LOGGER.warn("testcheckfailed health check failed:"+rst.getMessage());
		}
		else
		{
			LOGGER.debug("testcheckfailed health");
		}
	}
	
	
	@Test
	public void testcheckNoENV()
	{
		PowerMockito.mockStatic(HttpClientUtil.class);
		try {
			
			PowerMockito.when(HttpClientUtil.httpGetStatus(Mockito.anyString())).thenReturn(400);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.getenv("CONSUL_IP")).thenReturn("");
		
		ConsulLinkHealthCheck check = new ConsulLinkHealthCheck();
		Result rst = check.execute();
		
		if (!rst.isHealthy()) {
			LOGGER.warn("testcheckNoENV health check failed:"+rst.getMessage());
		}
		else
		{
			LOGGER.debug("testcheckNoENV health");
		}
	}
}
