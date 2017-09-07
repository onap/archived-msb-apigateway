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
package org.onap.msb.apiroute.wrapper.consulextend.cache;

import org.junit.Assert;
import org.junit.Test;
import org.onap.msb.apiroute.wrapper.consulextend.Consul;
import org.onap.msb.apiroute.wrapper.consulextend.cache.ServiceHealthCache;

import com.orbitz.consul.option.CatalogOptions;

public class ServiceHealthCacheTest {

	@Test
	public void testnewCache() {
		Consul consul = Consul.newClient();
		ServiceHealthCache cache1 = ServiceHealthCache.newCache(
				consul.healthClient(), "huangleibo");
		Assert.assertNotNull(cache1);

		ServiceHealthCache cache2 = ServiceHealthCache.newCache(
				consul.healthClient(), "huangleibo", false,
				CatalogOptions.BLANK, 10);
		Assert.assertNotNull(cache2);
	}
}
