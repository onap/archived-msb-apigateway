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
package org.onap.msb.apiroute.wrapper.consulextend;

import java.util.List;

import org.apache.http.HttpHost;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.onap.msb.apiroute.wrapper.consulextend.util.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orbitz.consul.option.CatalogOptions;
import com.orbitz.consul.option.QueryOptions;

/**
 * HTTP Client for /v1/health/ endpoints.
 */
public class HealthClient {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(HealthClient.class);

	private static final TypeReference<List<ServiceHealth>> TYPE_SERVICE_HEALTH_LIST = new TypeReference<List<ServiceHealth>>() {
	};

	private static final String HEALTH_URI_10081 = "/api/health/v1";
	private static final String HEALTH_URI_8500 = "/v1/health";
	private static final String GET_HEALTH_SERVICE_URI = "/service";
	
//	private static final String GET_HEALTH_SERVICE_URI = "/v1/health/service";
	
//	private static final String GET_HEALTH_SERVICE_URI = "/api/health/v1/service";

	private final static Http httpClient = Http.getInstance();

	private HttpHost targetHost = null;
	private String healthUri = HEALTH_URI_10081;

	HealthClient(final HttpHost targetHost) {
		this.targetHost = targetHost;
		
		if(targetHost.getPort() == 8500)
		{
			healthUri = HEALTH_URI_8500;
		}
	}

	/**
	 * Asynchronously retrieves the healthchecks for all healthy service
	 * instances in a given datacenter with
	 * {@link com.orbitz.consul.option.QueryOptions}.
	 * 
	 * GET /v1/health/service/{service}?dc={datacenter}&amp;passing
	 * 
	 * Experimental.
	 * 
	 * @param service
	 *            The service to query.
	 * @param catalogOptions
	 *            The catalog specific options to use.
	 * @param queryOptions
	 *            The Query Options to use.
	 * @param callback
	 *            Callback implemented by callee to handle results.
	 */
	public void getHealthyServiceInstances(String service,
			CatalogOptions catalogOptions, QueryOptions queryOptions,
			ConsulResponseCallback<List<ServiceHealth>> callback) {
		// prepare access path
		String path = targetHost.toString() + healthUri + GET_HEALTH_SERVICE_URI + "/"+ service;
		
		String params = Http.optionsFrom(catalogOptions, queryOptions);
		path = (params != null && !params.isEmpty()) ? path += "?"
				+ params : path;  //query all nodes without filter for health

		// async watch
//		LOGGER.info("get health paasing service:" + path);
		httpClient.asyncGetDelayHandle(path, TYPE_SERVICE_HEALTH_LIST, callback);
	}

	/**
	 * Asynchronously retrieves the healthchecks for all nodes in a given
	 * datacenter with {@link com.orbitz.consul.option.QueryOptions}.
	 * 
	 * GET /v1/health/service/{service}?dc={datacenter}
	 * 
	 * Experimental.
	 * 
	 * @param service
	 *            The service to query.
	 * @param catalogOptions
	 *            The catalog specific options to use.
	 * @param queryOptions
	 *            The Query Options to use.
	 * @param callback
	 *            Callback implemented by callee to handle results.
	 */
	public void getAllServiceInstances(String service,
			CatalogOptions catalogOptions, QueryOptions queryOptions,
			ConsulResponseCallback<List<ServiceHealth>> callback) {

		// prepare access path
		String path = targetHost.toString() + healthUri + GET_HEALTH_SERVICE_URI + "/"+ service;
		String params = Http.optionsFrom(catalogOptions, queryOptions);
		path = (params != null && !params.isEmpty()) ? path += "?" + params
				: path;

		// async watch
//		LOGGER.debug("get service:" + path);
		httpClient.asyncGetDelayHandle(path, TYPE_SERVICE_HEALTH_LIST, callback);
	}
}
