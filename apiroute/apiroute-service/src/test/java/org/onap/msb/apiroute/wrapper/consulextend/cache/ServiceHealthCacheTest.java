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
