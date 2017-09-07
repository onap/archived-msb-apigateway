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
 * limitations under the License.
 ******************************************************************************/
package org.onap.msb.apiroute.wrapper.consulextend.expose;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;




import org.junit.Assert;
import org.junit.Test;
import org.onap.msb.apiroute.wrapper.consulextend.Consul;
import org.onap.msb.apiroute.wrapper.consulextend.expose.CheckServiceDataEmptyAndAutoStopWatchFilter;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableService;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ImmutableServiceHealth;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.Service;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;

import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.ImmutableNode;

public class CheckServiceDataEmptyAndAutoStopWatchFilterTest {
	@Test
	public void testfilter()
	{
		
		
		CheckServiceDataEmptyAndAutoStopWatchFilter filter = new CheckServiceDataEmptyAndAutoStopWatchFilter("huangleibo");
		
		
		List<ServiceHealth> list = new ArrayList<ServiceHealth>();
		
		//id:huangleibo1,name:huangleibo,modifyIndex:1,createindex:1
		Service service0 = ImmutableService.builder().id("huangleibo1").port(0).address("")
				.service("huangleibo").addTags("").createIndex(1).modifyIndex(1).build();
		ServiceHealth serviceHealth0 = ImmutableServiceHealth.builder()
				.service(service0)
				.node(ImmutableNode.builder().node("").address("").build())
				.build();
		
		list.add(serviceHealth0);
		ConsulResponse<List<ServiceHealth>> object = new ConsulResponse<List<ServiceHealth>>(list,1,true, BigInteger.valueOf(1));
		
		//have service,return true
		Assert.assertTrue(filter.filter(object));
		
		//empty [],return false
		list.clear();
		Assert.assertFalse(filter.filter(object));
		
	}
}
