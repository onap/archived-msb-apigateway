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
package org.onap.msb.apiroute.wrapper;

import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.SimpleServerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.onap.msb.apiroute.ApiRouteAppConfig;
import org.onap.msb.apiroute.api.ApiRouteInfo;
import org.onap.msb.apiroute.api.CustomRouteInfo;
import org.onap.msb.apiroute.api.DiscoverInfo;
import org.onap.msb.apiroute.api.IuiRouteInfo;
import org.onap.msb.apiroute.api.RouteServer;
import org.onap.msb.apiroute.wrapper.ApiRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.CustomRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.InitRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.IuiRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.consulextend.Consul;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;
import org.onap.msb.apiroute.wrapper.consulextend.util.Http;
import org.onap.msb.apiroute.wrapper.dao.RedisAccessWrapper;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
import org.onap.msb.apiroute.wrapper.util.JedisUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fiftyonred.mock_jedis.MockJedisPool;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JedisUtil.class, RedisAccessWrapper.class, ConfigUtil.class,
		Http.class, InitRouteServiceWrapper.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class InitRouteServiceWrapperTest {
	private static InitRouteServiceWrapper initRouteServiceWrapper;
	private static Consul consul;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		initRouteServiceWrapper = InitRouteServiceWrapper.getInstance();

		Http http = PowerMockito.mock(Http.class);

		PowerMockito
				.doNothing()
				.when(http)
				.asyncGet(Mockito.anyString(),
						Mockito.any(TypeReference.class),
						Mockito.any(ConsulResponseCallback.class));

		PowerMockito
				.doNothing()
				.when(http)
				.asyncGetDelayHandle(Mockito.anyString(),
						Mockito.any(TypeReference.class),
						Mockito.any(ConsulResponseCallback.class));

		PowerMockito.spy(Http.class);
		PowerMockito.when(Http.getInstance()).thenReturn(http);

		consul = Consul.builder().withHostAndPort("127.0.0.1", 8500).build();
	}

	@Before
	public void initReidsMock() throws Exception {
		final JedisPool mockJedisPool = new MockJedisPool(
				new JedisPoolConfig(), "localhost");
		PowerMockito.mockStatic(JedisUtil.class);
		JedisUtil jedisUtil = PowerMockito.mock(JedisUtil.class);
		PowerMockito.when(jedisUtil.borrowJedisInstance()).thenReturn(
				mockJedisPool.getResource());

		PowerMockito.replace(
				PowerMockito.method(RedisAccessWrapper.class, "filterKeys"))
				.with(new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						return mockJedisPool.getResource().keys(
								(String) args[0]);
					}
				});
	}

	@Test
	public void test_startCheckRedisConnect() {
		try {
			boolean ifRedisConnect = initRouteServiceWrapper
					.startCheckRedisConnect();
			Assert.assertEquals(true, ifRedisConnect);

		} catch (Exception e) {
			Assert.fail("throw exception means error occured!" + e.getMessage());

		}
	}

	@Test
	public void test_runConsulClientApp() {

		DiscoverInfo discoverInfo = new DiscoverInfo();
		discoverInfo.setEnabled(true);
		discoverInfo.setIp("127.0.0.1");
		discoverInfo.setPort(10081);

		ApiRouteAppConfig configuration = new ApiRouteAppConfig();
		configuration.setDiscoverInfo(discoverInfo);

		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.getenv("SDCLIENT_IP")).thenReturn("127.0.0.1");
		ConfigUtil.getInstance().initDiscoverInfo(configuration);

		try {
			initRouteServiceWrapper.runConsulClientApp();

			ApiRouteInfo discoverApiService = new ApiRouteInfo();
			discoverApiService.setServiceName("msdiscover");
			discoverApiService.setUrl("/api/microservices/v1");
			discoverApiService.setVersion("v1");
			discoverApiService.setMetricsUrl("/admin/metrics");
			discoverApiService.setApiJson("/api/microservices/v1/swagger.json");
			discoverApiService.setHost("msb");

			RouteServer[] servers = new RouteServer[1];
			servers[0] = new RouteServer(discoverInfo.getIp(),
					String.valueOf(discoverInfo.getPort()));
			discoverApiService.setServers(servers);

			ApiRouteInfo db_discoverApiService = ApiRouteServiceWrapper
					.getInstance().getApiRouteInstance("msdiscover", "v1",
							"msb", "", "ip");
			Assert.assertEquals(discoverApiService, db_discoverApiService);

			IuiRouteInfo discoverIUIService = new IuiRouteInfo();
			discoverIUIService.setServiceName("msdiscover");
			discoverIUIService.setUrl("/iui/microservices");
			discoverIUIService.setHost("msb");
			discoverIUIService.setServers(servers);

			Assert.assertEquals(
					discoverIUIService,
					IuiRouteServiceWrapper.getInstance().getIuiRouteInstance(
							"msdiscover", "msb", "", "ip"));

		} catch (Exception e) {
			assert false : "throw exception means error occured!"
					+ e.getMessage();
		}
	}

	@Test
	public void test_initRouteInfoFromJson() {
		try {

			PowerMockito.mockStatic(System.class);
			PowerMockito.when(System.getenv("dwApp_server_connector_port"))
					.thenReturn("8068");
			initRouteServiceWrapper.initRouteInfoFromJson();

			ApiRouteInfo apiroute = new ApiRouteInfo();
			apiroute.setServiceName("microservices");
			apiroute.setUrl("/api/microservices/v1");
			apiroute.setVersion("v1");
			apiroute.setMetricsUrl("/admin/metrics");
			apiroute.setApiJson("/api/microservices/v1/swagger.json");
			apiroute.setHost("msb");
			apiroute.setControl("1");
			apiroute.setStatus("1");

			RouteServer[] servers = new RouteServer[1];
			servers[0] = new RouteServer("127.0.0.1", "8068");
			apiroute.setServers(servers);

			ApiRouteInfo db_apiService = ApiRouteServiceWrapper
					.getInstance()
					.getApiRouteInstance("microservices", "v1", "msb", "", "ip");
			Assert.assertEquals(apiroute, db_apiService);

			IuiRouteInfo iuiRoute = new IuiRouteInfo();
			iuiRoute.setServiceName("microservices");
			iuiRoute.setUrl("/iui/microservices");
			iuiRoute.setHost("msb");
			iuiRoute.setControl("1");
			iuiRoute.setStatus("1");
			iuiRoute.setServers(servers);

			Assert.assertEquals(iuiRoute, IuiRouteServiceWrapper.getInstance()
					.getIuiRouteInstance("microservices", "msb", "", "ip"));

			CustomRouteInfo customRoute = new CustomRouteInfo();
			customRoute.setServiceName("/custom");
			customRoute.setUrl("/custom");
			customRoute.setHost("msb");
			customRoute.setControl("1");
			customRoute.setStatus("1");
			RouteServer[] servers2 = new RouteServer[1];
			servers2[0] = new RouteServer("127.0.0.1", "8066");
			customRoute.setServers(servers2);

			Assert.assertEquals(customRoute,
					CustomRouteServiceWrapper.getInstance()
							.getCustomRouteInstance("/custom", "msb", "", "ip"));

		} catch (Exception e) {
			Assert.fail("throw exception means error occured!" + e.getMessage());
		}
	}

	@Test
	public void test_initMetricsConfig() {
		ApiRouteAppConfig configuration = new ApiRouteAppConfig();
		SimpleServerFactory simpleServerFactory = new SimpleServerFactory();
		HttpConnectorFactory httpConnectorFactory = new HttpConnectorFactory();
		httpConnectorFactory.setPort(8888);
		simpleServerFactory.setConnector(httpConnectorFactory);
		simpleServerFactory.setAdminContextPath("/admin");

		configuration.setServerFactory(simpleServerFactory);

		initRouteServiceWrapper.initMetricsConfig(configuration);

		Assert.assertEquals("http://127.0.0.1:8888/admin/metrics", ConfigUtil
				.getInstance().getMetricsUrl());
	}

}
