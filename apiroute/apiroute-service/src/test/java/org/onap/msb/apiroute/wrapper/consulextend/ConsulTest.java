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
package org.onap.msb.apiroute.wrapper.consulextend;

import java.util.List;

import org.apache.http.HttpEntity;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.onap.msb.apiroute.wrapper.consulextend.CatalogClient;
import org.onap.msb.apiroute.wrapper.consulextend.Consul;
import org.onap.msb.apiroute.wrapper.consulextend.HealthClient;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;
import org.onap.msb.apiroute.wrapper.consulextend.async.OriginalConsulResponse;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.onap.msb.apiroute.wrapper.consulextend.util.Http;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.option.CatalogOptions;
import com.orbitz.consul.option.QueryOptions;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Http.class })
@PowerMockIgnore({ "javax.net.ssl.*" })
public class ConsulTest {
	private static Consul consul10081;
	private static Consul consul8500;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConsulTest.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		Http http = PowerMockito.mock(Http.class);
		
		PowerMockito
				.doAnswer(new Answer() {
					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						Object[] args = invocation.getArguments();
						((ConsulResponseCallback) args[2]).onComplete(null);						
						return null;
					}
				})
				.when(http)
				.asyncGet(Mockito.anyString(),
						Mockito.any(TypeReference.class),
						Mockito.any(ConsulResponseCallback.class));
		
		//
		PowerMockito.spy(Http.class);
		PowerMockito.when(Http.getInstance()).thenReturn(http);
				
		//
		consul10081 = Consul.builder().withHostAndPort("10.74.148.111", 10081)
				.build();
		consul8500 = Consul.builder().withHostAndPort("10.74.148.111", 8500)
				.build();
	}

	@Test
	public void testcatalogClient() {
		
		ConsulResponseCallback<HttpEntity> callback = new ConsulResponseCallback<HttpEntity>() {

			@Override
			public void onComplete(
					ConsulResponse<HttpEntity> consulResponse) {
				LOGGER.info("service list complete!");
			}

			@Override
			public void onFailure(Throwable throwable) {
				LOGGER.info("service list failure!");
			}

			@Override
			public void onDelayComplete(
					OriginalConsulResponse<HttpEntity> originalConsulResponse) {
				// TODO Auto-generated method stub
				LOGGER.info("service list complete!");
			}

		};

		// 10081
		CatalogClient catalogclient10081 = consul10081.catalogClient();
		catalogclient10081.getServices(CatalogOptions.BLANK, QueryOptions.BLANK, callback);

		// 8500
		CatalogClient catalogclient8500 = consul8500.catalogClient();
		catalogclient8500.getServices(CatalogOptions.BLANK, QueryOptions.BLANK, callback);
	}

	@Test
	public void testhealthClient() {

		ConsulResponseCallback<List<ServiceHealth>> callback = new ConsulResponseCallback<List<ServiceHealth>>() {

			@Override
			public void onComplete(
					ConsulResponse<List<ServiceHealth>> consulResponse) {
				LOGGER.info("health service complete!");

			}

			@Override
			public void onFailure(Throwable throwable) {
				LOGGER.info("health service failure!");
			}

			@Override
			public void onDelayComplete(
					OriginalConsulResponse<List<ServiceHealth>> originalConsulResponse) {
				// TODO Auto-generated method stub
				LOGGER.info("health service complete!");
			}

		};

		// 10081
		HealthClient healthClient10081 = consul10081.healthClient();
		healthClient10081.getAllServiceInstances("apigateway-default", CatalogOptions.BLANK,
				QueryOptions.BLANK, callback);

		healthClient10081.getHealthyServiceInstances("apigateway-default",
				CatalogOptions.BLANK, QueryOptions.BLANK, callback);

		// 8500
		HealthClient healthClient8500 = consul8500.healthClient();
		healthClient8500.getAllServiceInstances("apigateway-default", CatalogOptions.BLANK,
				QueryOptions.BLANK, callback);
		healthClient8500.getHealthyServiceInstances("apigateway-default", CatalogOptions.BLANK,
				QueryOptions.BLANK, callback);

	}
}
