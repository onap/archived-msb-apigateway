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
package org.onap.msb.apiroute.wrapper.consulextend.expose;

import org.apache.http.HttpEntity;
import org.onap.msb.apiroute.wrapper.consulextend.CatalogClient;
import org.onap.msb.apiroute.wrapper.consulextend.cache.ServicesCatalogCache;
import org.onap.msb.apiroute.wrapper.consulextend.cache.ConsulCache.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.option.CatalogOptions;
import com.orbitz.consul.option.QueryOptions;

public class WatchCatalogServicesTask  extends WatchTask<HttpEntity> {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(WatchCatalogServicesTask.class);
	
	private ServicesCatalogCache servicesCache = null;
	
	public WatchCatalogServicesTask(
			final CatalogClient catalogClient,
            final CatalogOptions catalogOptions,
            final QueryOptions queryOptions,
            final int watchSeconds)
	{
		initCache(catalogClient,catalogOptions,queryOptions,watchSeconds);
	}
	
	public WatchCatalogServicesTask(
			final CatalogClient catalogClient,
            final int watchSeconds)
	{
		initCache(catalogClient,CatalogOptions.BLANK,QueryOptions.BLANK,watchSeconds);
	}
	
	public WatchCatalogServicesTask(
			final CatalogClient catalogClient)
	{
		initCache(catalogClient,CatalogOptions.BLANK,QueryOptions.BLANK,10);
	}
	
	private ServicesCatalogCache initCache(final CatalogClient catalogClient,
            final CatalogOptions catalogOptions,
            final QueryOptions queryOptions,
            final int watchSeconds) {
		LOGGER.info("************create all services watch task*****************");
		servicesCache = ServicesCatalogCache.newCache(catalogClient,
				catalogOptions, queryOptions, watchSeconds);

		servicesCache
		.addListener((Listener<HttpEntity>) new InternalListener());

		return servicesCache;
	}
	
	@Override
	public boolean startWatch() {
		// TODO Auto-generated method stub
		if(servicesCache!=null)
		{
			try {
				servicesCache.start();
				LOGGER.info("************start all services watch task*****************");
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.warn("start service list watch failed:", e);
			}
		}
		
		return false;
	}

	@Override
	public boolean stopWatch() {
		// TODO Auto-generated method stub
		if (servicesCache != null) {
			try {
				servicesCache.stop();
				LOGGER.info("************stop all services watch task*****************");
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.warn("stop service list watch failed:", e);
			}
		}
		return false;
	}

}
