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
package org.onap.msb.apiroute.wrapper.consulextend.util;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.eclipse.jetty.http.HttpStatus;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseHeader;
import org.onap.msb.apiroute.wrapper.consulextend.async.OriginalConsulResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.orbitz.consul.ConsulException;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.option.CatalogOptions;
import com.orbitz.consul.option.QueryOptions;
import com.orbitz.consul.util.Jackson;

public class Http {
	private static final Logger LOGGER = LoggerFactory.getLogger(Http.class);

	private final static CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients
			.custom().setMaxConnTotal(Integer.MAX_VALUE)
			.setMaxConnPerRoute(Integer.MAX_VALUE).build();

	private static Http instance = null;

	private Http() {
	}

	public static Http getInstance() {
		if (instance == null) {
			instance = new Http();
			httpAsyncClient.start();
		}

		return instance;
	}

	// async get data from consul,and handle response immediately
	public <T> void asyncGet(String requestURI,
			final TypeReference<T> responseType,
			final ConsulResponseCallback<T> callback, final Integer... okCodes) {
		// LOGGER.info("Async request:"+requestURI);

		httpAsyncClient.execute(new HttpGet(requestURI),
				new FutureCallback<HttpResponse>() {

					public void completed(final HttpResponse response) {
						callback.onComplete(consulResponse(responseType,
								response));
					}

					public void failed(final Exception ex) {
						callback.onFailure(ex);
					}

					public void cancelled() {
						LOGGER.warn("cancelled async request");
					}
				});
	}

	// async get data from consul,and handle response delay
	public <T> void asyncGetDelayHandle(String requestURI,
			final TypeReference<T> responseType,
			final ConsulResponseCallback<T> callback, final Integer... okCodes) {

		httpAsyncClient.execute(new HttpGet(requestURI),
				new FutureCallback<HttpResponse>() {

					public void completed(final HttpResponse response) {
						OriginalConsulResponse<T> originalConsulResponse = new OriginalConsulResponse<T>(
								response, responseType);
						
						//handle not 2xx code
						if (!isSuccessful(response)) {
							
							LOGGER.warn("response statuscode:"
									+ response.getStatusLine().getStatusCode());
							
							callback.onFailure(new ConsulException(
									"response statuscode:"
											+ response.getStatusLine()
													.getStatusCode()));
						} else {
							callback.onDelayComplete(originalConsulResponse);
						}

					}

					public void failed(final Exception ex) {
						callback.onFailure(ex);
					}

					public void cancelled() {
						LOGGER.warn("cancelled async request");
					}
				});
	}

	public static ConsulResponseHeader consulResponseHeader(
			HttpResponse response) {
		String indexHeaderValue = response.getFirstHeader("X-Consul-Index")
				.getValue();
		String lastContactHeaderValue = response.getFirstHeader(
				"X-Consul-Lastcontact").getValue();
		String knownLeaderHeaderValue = response.getFirstHeader(
				"X-Consul-Knownleader").getValue();

		BigInteger index = indexHeaderValue == null ? new BigInteger("0")
				: new BigInteger(indexHeaderValue);
		long lastContact = lastContactHeaderValue == null ? 0 : Long
				.parseLong(lastContactHeaderValue);
		boolean knownLeader = knownLeaderHeaderValue == null ? false : Boolean
				.parseBoolean(knownLeaderHeaderValue);

		return new ConsulResponseHeader(lastContact, knownLeader, index);
	}

	public static <T> ConsulResponse<T> consulResponse(
			TypeReference<T> responseType, HttpResponse response) {

		String indexHeaderValue = response.getFirstHeader("X-Consul-Index")
				.getValue();
		String lastContactHeaderValue = response.getFirstHeader(
				"X-Consul-Lastcontact").getValue();
		String knownLeaderHeaderValue = response.getFirstHeader(
				"X-Consul-Knownleader").getValue();

		BigInteger index = indexHeaderValue == null ? new BigInteger("0")
				: new BigInteger(indexHeaderValue);
		long lastContact = lastContactHeaderValue == null ? 0 : Long
				.parseLong(lastContactHeaderValue);
		boolean knownLeader = knownLeaderHeaderValue == null ? false : Boolean
				.parseBoolean(knownLeaderHeaderValue);

		ConsulResponse<T> consulResponse = new ConsulResponse<T>(readResponse(
				response, responseType), lastContact, knownLeader, index);
		return consulResponse;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T readResponse(HttpResponse response,
			TypeReference<T> responseType) {

		// read streamed entity
		T object;

		// HttpEntity,read original data.
		Type _type = responseType.getType();
		if (_type instanceof Class
				&& (((Class) _type).isAssignableFrom(HttpEntity.class))) {
			object = (T) response.getEntity();
			return object;
		}

		// String,read original data.
		if (_type instanceof Class
				&& (((Class) _type).isAssignableFrom(String.class))) {

			try {

				object = (T) IOUtils
						.toString(response.getEntity().getContent());
				response.getEntity().getContent().close();

			} catch (UnsupportedOperationException e) {
				object = (T) "";
				LOGGER.warn("covert streamed entity to String exception:", e);
			} catch (IOException e) {
				object = (T) "";
				LOGGER.warn("covert streamed entity to String exception:", e);
			}

			return object;
		}

		// change data type
		try {
			object = Jackson.MAPPER.readValue(
					response.getEntity().getContent(), responseType);
		} catch (IOException e) {
			LOGGER.warn("covert streamed entity to object exception:", e);
			object = readDefaultResponse(responseType);
		}

		return object;
	}

	@SuppressWarnings("unchecked")
	public static <T> T readDefaultResponse(TypeReference<T> responseType) {
		Type _type = responseType.getType();
		if (_type instanceof ParameterizedType
				&& ((ParameterizedType) _type).getRawType() == List.class) {
			return (T) ImmutableList.of();
		} else if (_type instanceof ParameterizedType
				&& ((ParameterizedType) _type).getRawType() == Map.class) {
			return (T) ImmutableMap.of();
		} else {
			// Not sure if this case will be reached, but if it is it'll be nice
			// to know
			throw new IllegalStateException(
					"Cannot determine empty representation for " + _type);
		}
	}

	public static boolean isSuccessful(HttpResponse response,
			Integer... okCodes) {
		return HttpStatus.isSuccess(response.getStatusLine().getStatusCode())
				|| Sets.newHashSet(okCodes).contains(
						response.getStatusLine().getStatusCode());
	}

	public static String optionsFrom(CatalogOptions catalogOptions,
			QueryOptions queryOptions) {
		String params = "";

		if (catalogOptions != null) {
			Map<String, Object> options = catalogOptions.toQuery();

			if (options.containsKey("dc")) {
				params += "dc=" + options.get("dc");
			}
			if (options.containsKey("tag")) {
				params += params.isEmpty() ? "" : "&";
				params += "tag=" + options.get("tag");
			}
		}

		if (queryOptions != null) {
			Map<String, Object> options = queryOptions.toQuery();

			if (options.containsKey("consistent")) {
				params += params.isEmpty() ? "" : "&";
				params += "consistent=" + options.get("consistent");
			}
			if (options.containsKey("stale")) {
				params += params.isEmpty() ? "" : "&";
				params += "stale=" + options.get("stale");
			}
			if (options.containsKey("wait")) {
				params += params.isEmpty() ? "" : "&";
				params += "wait=" + options.get("wait");
			}

			if (options.containsKey("index")) {
				params += params.isEmpty() ? "" : "&";
				params += "index=" + options.get("index");
			}
			if (options.containsKey("token")) {
				params += params.isEmpty() ? "" : "&";
				params += "token=" + options.get("token");
			}
			if (options.containsKey("near")) {
				params += params.isEmpty() ? "" : "&";
				params += "near=" + options.get("near");
			}
			if (options.containsKey("dc")) {
				params += params.isEmpty() ? "" : "&";
				params += "dc=" + options.get("dc");
			}
		}
		return params;
	}
}
