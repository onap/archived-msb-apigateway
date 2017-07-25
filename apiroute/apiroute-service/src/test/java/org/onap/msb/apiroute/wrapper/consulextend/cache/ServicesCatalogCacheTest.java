package org.onap.msb.apiroute.wrapper.consulextend.cache;

import org.junit.Test;
import org.onap.msb.apiroute.wrapper.consulextend.Consul;
import org.onap.msb.apiroute.wrapper.consulextend.cache.ServicesCatalogCache;

public class ServicesCatalogCacheTest {

	@Test
	public void testnewCache()
	{
		Consul consul = Consul.newClient();
		ServicesCatalogCache cache = ServicesCatalogCache.newCache(consul.catalogClient());
	}	
}
