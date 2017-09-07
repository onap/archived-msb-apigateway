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
package org.onap.msb.apiroute.wrapper.consulextend.util;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.onap.msb.apiroute.wrapper.consulextend.util.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orbitz.consul.option.ConsistencyMode;
import com.orbitz.consul.option.ImmutableCatalogOptions;
import com.orbitz.consul.option.ImmutableQueryOptions;
import com.orbitz.consul.option.QueryOptions;

public class HttpTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(HttpTest.class);

	@Before
	public void init() {

	}

	@Test
	public void testoptionsFrom() {
		ImmutableCatalogOptions catalogs = ImmutableCatalogOptions.builder()
				.build();
		catalogs = catalogs.withDatacenter("datacenter").withTag("tag");

		BigInteger index = new BigInteger("1");
		ImmutableQueryOptions querys = QueryOptions.blockSeconds(10, index)
				.build();
		querys = querys.withConsistencyMode(ConsistencyMode.STALE)
				.withDatacenter("datacenter").withNear("near")
				.withToken("taoken");
		String url = Http.optionsFrom(catalogs, querys);
		LOGGER.info(url);
	}

	@Test
	public void testconsulResponse() {
		
		TypeReference<Map<String, List<String>>> TYPE_SERVICES_MAP = new TypeReference<Map<String, List<String>>>() {};
		
		ProtocolVersion version = new ProtocolVersion("HTTP",1,1);
		StatusLine status= new BasicStatusLine(version,200,"ok");
		BasicHttpResponse response = new BasicHttpResponse(status);
		
		response.setHeader("X-Consul-Index", "1");
		response.setHeader("X-Consul-Lastcontact", "1");
		response.setHeader("X-Consul-Knownleader", "true");
		
		BasicHttpEntity entity = new BasicHttpEntity();
		InputStream content = HttpTest.class.getResourceAsStream("serviceslist.json");
		entity.setContent(content);
		response.setEntity(entity);
		
		Http.consulResponse(TYPE_SERVICES_MAP, response);
		
		TypeReference<String> TYPE_SERVICES_MAP_STRING = new TypeReference<String>() {};
		InputStream content1 = HttpTest.class.getResourceAsStream("serviceslist.json");
		entity.setContent(content1);
		
		Http.consulResponse(TYPE_SERVICES_MAP_STRING, response);

		TypeReference<HttpEntity> TYPE_SERVICES_MAP_ENTITY = new TypeReference<HttpEntity>() {};
		Http.consulResponse(TYPE_SERVICES_MAP_ENTITY, response);
	}
}
