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
package org.onap.msb.apiroute.wrapper.consulextend.model.health;

import org.junit.Assert;
import org.junit.Test;

public class ServiceTest {

	@Test
	public void testImmutableService() {
		ImmutableService service0 = ImmutableService.builder()
				.id("huangleibo_id").port(0).address("").service("huangleibo")
				.addTags("111", "222").createIndex(1).modifyIndex(1).build();
		Assert.assertEquals("huangleibo_id", service0.getId());
		Assert.assertEquals(1, service0.getCreateIndex());
		Assert.assertEquals(1, service0.getModifyIndex());

		ImmutableService service1 = service0.withId("huangleibo_id")
				.withId("new_id").withService("huangleibo")
				.withService("new_service").withTags("new_tags")
				.withAddress("").withAddress("new_address").withPort(0)
				.withPort(1).withCreateIndex(1).withCreateIndex(2)
				.withModifyIndex(1).withModifyIndex(2);

		Assert.assertFalse(service0.equals(service1));

		System.out.println(service1.hashCode());

		ImmutableService service2 = ImmutableService.builder().from(service1)
				.build();
		Assert.assertEquals("new_id", service2.getId());
	}
}
