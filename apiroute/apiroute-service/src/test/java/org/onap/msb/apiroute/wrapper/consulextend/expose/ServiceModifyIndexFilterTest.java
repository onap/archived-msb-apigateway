package org.onap.msb.apiroute.wrapper.consulextend.expose;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.onap.msb.apiroute.wrapper.consulextend.expose.ServiceModifyIndexFilter;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableService;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableServiceHealth;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.Service;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;

import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.ImmutableNode;

public class ServiceModifyIndexFilterTest {
	
	@Test
	public void testfilter()
	{
		ServiceModifyIndexFilter filter = new ServiceModifyIndexFilter();
		
		
		List<ServiceHealth> list0 = new ArrayList<ServiceHealth>();
		
		//id:huangleibo1,name:huangleibo,modifyIndex:1,createIndex:1
		Service service0 = ImmutableService.builder().id("huangleibo1").port(0).address("")
				.service("huangleibo").addTags("").createIndex(1).modifyIndex(1).build();
		ServiceHealth serviceHealth0 = ImmutableServiceHealth.builder()
				.service(service0)
				.node(ImmutableNode.builder().node("").address("").build())
				.build();	
		list0.add(serviceHealth0);
		
		ConsulResponse<List<ServiceHealth>> object0 = new ConsulResponse<List<ServiceHealth>>(list0,1,true,BigInteger.valueOf(1));

		//list-size:1,id:huangleibo1,name:huangleibo,modifyIndex:1;the first time:return true
		Assert.assertTrue(filter.filter(object0));
		
		//list-size:1,id:huangleibo1,name:huangleibo,modifyIndex:1;same index:return false
		Assert.assertFalse(filter.filter(object0));
		
		/////////////////////////////////////////////////////////////////////////////////
		
		List<ServiceHealth> list1 = new ArrayList<ServiceHealth>();
		
		//id:huangleibo2,name:huangleibo,modifyIndex:1,createIndex:1
		Service service1 = ImmutableService.builder().id("huangleibo2").port(0).address("")
				.service("huangleibo").addTags("").createIndex(1).modifyIndex(1).build();
		ServiceHealth serviceHealth1 = ImmutableServiceHealth.builder()
				.service(service1)
				.node(ImmutableNode.builder().node("").address("").build())
				.build();
		
		list1.add(serviceHealth0);
		list1.add(serviceHealth1);
		
		ConsulResponse<List<ServiceHealth>> object1 = new ConsulResponse<List<ServiceHealth>>(list1,1,true,BigInteger.valueOf(1));

		//list-size:2,
		//id:huangleibo1,name:huangleibo,modifyIndex:1,createIndex:1
		//id:huangleibo2,name:huangleibo,modifyIndex:1,createIndex:1
		//size different,return true
		Assert.assertTrue(filter.filter(object1));
		
		//////////////////////////////////////////////////////////////////////////
		List<ServiceHealth> list2 = new ArrayList<ServiceHealth>();
		
		//id:huangleibo3,name:huangleibo,modifyIndex:1,createIndex:1
		ImmutableService service2 = ImmutableService.builder().id("huangleibo3").port(0).address("")
				.service("huangleibo").addTags("").createIndex(1).modifyIndex(1).build();
		ServiceHealth serviceHealth2 = ImmutableServiceHealth.builder()
				.service(service2)
				.node(ImmutableNode.builder().node("").address("").build())
				.build();
		list2.add(serviceHealth0);
		list2.add(serviceHealth2);
		
		ConsulResponse<List<ServiceHealth>> object2 = new ConsulResponse<List<ServiceHealth>>(list2,1,true,BigInteger.valueOf(1));
		
		//list-size:2,
		//id:huangleibo1,name:huangleibo,modifyIndex:1,createIndex:1
		//id:huangleibo3,name:huangleibo,modifyIndex:1,createIndex:1
		//instance id different,return true
		Assert.assertTrue(filter.filter(object2));
		
		
		//////////////////////////////////////////////////////////////////////////
		List<ServiceHealth> list3 = new ArrayList<ServiceHealth>();
		
		//edit modifyindex 1 to 2
		Service service3 = service2.withModifyIndex(2);
		ServiceHealth serviceHealth3 = ImmutableServiceHealth.builder()
				.service(service3)
				.node(ImmutableNode.builder().node("").address("").build())
				.build();
		list3.add(serviceHealth0);
		list3.add(serviceHealth3);
		
		ConsulResponse<List<ServiceHealth>> object3 = new ConsulResponse<List<ServiceHealth>>(list3,1,true,BigInteger.valueOf(1));
		
		//list-size:2,
		//id:huangleibo1,name:huangleibo,modifyIndex:1,createIndex:1
		//id:huangleibo3,name:huangleibo,modifyIndex:2,createIndex:1
		//modifyIndex different,return true
		Assert.assertTrue(filter.filter(object3));
		
		//the same content,return false
		Assert.assertFalse(filter.filter(object3));
		
	}
}
