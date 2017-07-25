package org.onap.msb.apiroute.wrapper.consulextend.expose;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.onap.msb.apiroute.wrapper.consulextend.expose.WriteBufferHandler;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableService;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableServiceHealth;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.Service;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.onap.msb.apiroute.wrapper.queue.ServiceData;

import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.ImmutableNode;

public class WriteBufferHandlerTest {
	@Test
	public void testhandle() {
		List<ServiceHealth> list = new ArrayList<ServiceHealth>();

		// modifyIndex 1
		Service service0 = ImmutableService.builder().id("huangleibo1").port(0)
				.address("").service("huangleibo").addTags("").createIndex(1)
				.modifyIndex(1).build();
		ServiceHealth serviceHealth0 = ImmutableServiceHealth.builder()
				.service(service0)
				.node(ImmutableNode.builder().node("").address("").build())
				.build();

		list.add(serviceHealth0);

		long lastContact = 1;
		boolean knownLeader = true;
		BigInteger index = BigInteger.valueOf(1);
		ConsulResponse<List<ServiceHealth>> object = new ConsulResponse<List<ServiceHealth>>(
				list, lastContact, knownLeader, index);

		WriteBufferHandler<List<ServiceHealth>> handler = new WriteBufferHandler<List<ServiceHealth>>(
				ServiceData.DataType.service);

		handler.handle(object);

	}
}
