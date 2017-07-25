package org.onap.msb.apiroute.wrapper.consulextend.expose;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.onap.msb.apiroute.wrapper.consulextend.Consul;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;
import org.onap.msb.apiroute.wrapper.consulextend.expose.WatchServiceHealthTask;
import org.onap.msb.apiroute.wrapper.consulextend.expose.WatchTask;
import org.onap.msb.apiroute.wrapper.consulextend.expose.WatchTask.Filter;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableService;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableServiceHealth;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.Service;
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
import com.orbitz.consul.model.health.ImmutableNode;
import com.orbitz.consul.option.CatalogOptions;
import com.orbitz.consul.option.QueryOptions;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Http.class })
@PowerMockIgnore({ "javax.net.ssl.*" })
public class WatchServiceHealthTaskTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(WatchServiceHealthTaskTest.class);

	private Consul consul;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void init() {
		
		List<ServiceHealth> list = new ArrayList<ServiceHealth>();
		
		Service service = ImmutableService.builder().id("").port(0).address("")
				.service("huangleibo").addTags("").createIndex(1).modifyIndex(1).build();
		ServiceHealth serviceHealth = ImmutableServiceHealth.builder()
				.service(service)
				.node(ImmutableNode.builder().node("").address("").build())
				.build();
		list.add(serviceHealth);
				
		long lastContact = 1;
		boolean knownLeader = true;
		BigInteger index = BigInteger.valueOf(1);
		final ConsulResponse<List<ServiceHealth>> response = new ConsulResponse<List<ServiceHealth>>(
				list, lastContact, knownLeader, index);

		//
		Http http = PowerMockito.mock(Http.class);
		
		PowerMockito
				.doAnswer(new Answer() {
					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						Object[] args = invocation.getArguments();
						((ConsulResponseCallback) args[2]).onComplete(response);						
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
		
	}

	@Test
	public void testgetServiceName() {
		consul = Consul.newClient();

		WatchServiceHealthTask task0 = new WatchServiceHealthTask(
				consul.healthClient(), "huangleibo_task0", true,
				CatalogOptions.BLANK, 10, QueryOptions.BLANK);

		LOGGER.info("service name:" + task0.getServiceName());

		WatchServiceHealthTask task1 = new WatchServiceHealthTask(
				consul.healthClient(), "huangleibo_task1", true, 10);

		LOGGER.debug("service name:" + task1.getServiceName());

		WatchServiceHealthTask task2 = new WatchServiceHealthTask(
				consul.healthClient(), "huangleibo_task2", 10);

		LOGGER.debug("service name:" + task2.getServiceName());

	}
	
	public class StopHandler implements WatchTask.Handler<List<ServiceHealth>>
	{
		
		private WatchServiceHealthTask task;
		
		StopHandler(WatchServiceHealthTask task)
		{
			this.task = task;
		}

		@Override
		public void handle(ConsulResponse<List<ServiceHealth>> object) {
			// TODO Auto-generated method stub
			List<ServiceHealth> list = (List<ServiceHealth>)object.getResponse();
			LOGGER.debug("handler:"+list.get(0).getService().getService());
			task.stopWatch();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void teststartWatch() {
		Consul consul = Consul.newClient();
		String serviceName = "huangleibo";

		WatchServiceHealthTask task0 = new WatchServiceHealthTask(
				consul.healthClient(), serviceName, true, CatalogOptions.BLANK,
				10, QueryOptions.BLANK);
		
		task0.addFilter(new Filter() {

			@Override
			public boolean filter(ConsulResponse object) {
				// TODO Auto-generated method stub
				List<ServiceHealth> list = (List<ServiceHealth>)object.getResponse();
				LOGGER.debug("filter:"+list.get(0).getService().getService());
				return true;
			}
			
		});
		
		task0.addHandler(new StopHandler(task0));
		
		task0.startWatch();
	}
}
