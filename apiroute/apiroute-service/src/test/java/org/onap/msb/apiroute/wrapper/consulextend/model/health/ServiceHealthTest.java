package org.onap.msb.apiroute.wrapper.consulextend.model.health;

import org.junit.Assert;
import org.junit.Test;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.Service;

import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.model.health.ImmutableHealthCheck;
import com.orbitz.consul.model.health.ImmutableNode;
import com.orbitz.consul.model.health.Node;

public class ServiceHealthTest {

	@Test
	public void TestImmutableServiceHealth() {
		Service service = ImmutableService.builder().id("").port(0).address("")
				.service("huangleibo").addTags("").createIndex(1)
				.modifyIndex(1).build();
		Node node = ImmutableNode.builder().node("").address("").build();

		HealthCheck healthCheck0 = ImmutableHealthCheck.builder().checkId("")
				.name("").node("").notes("").output("").serviceId("")
				.serviceName("").status("").build();
		HealthCheck healthCheck1 = ImmutableHealthCheck.builder().checkId("")
				.name("").node("").notes("").output("").serviceId("")
				.serviceName("").status("").build();
		HealthCheck healthCheck2 = ImmutableHealthCheck.builder().checkId("")
				.name("").node("").notes("").output("").serviceId("")
				.serviceName("").status("").build();

		ImmutableServiceHealth serviceHealth0 = ImmutableServiceHealth
				.builder().service(service).node(node).addChecks(healthCheck0)
				.addChecks(healthCheck1, healthCheck2).build();

		Assert.assertNotNull(serviceHealth0.getNode());
		Assert.assertNotNull(serviceHealth0.getChecks());
		/* ############################################################### */

		ImmutableServiceHealth serviceHealth1 = serviceHealth0.withNode(node)
				.withNode(ImmutableNode.builder().node("").address("").build())
				.withService(service).withService(ImmutableService.builder().id("").port(0).address("")
						.service("huangleibo1111").addTags("").createIndex(1)
						.modifyIndex(1).build()).withChecks(healthCheck0);
		
		Assert.assertFalse(serviceHealth1.equals(serviceHealth0));
		System.out.println(serviceHealth1.hashCode());
		
		/* ############################################################### */
		
		ImmutableServiceHealth serviceHealth2 = ImmutableServiceHealth.builder().from(serviceHealth1).build();
		Assert.assertTrue(serviceHealth1.equals(serviceHealth2));
	}

}
