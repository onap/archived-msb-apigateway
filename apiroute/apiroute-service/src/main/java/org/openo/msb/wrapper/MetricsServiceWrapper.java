/**
* Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.openo.msb.wrapper;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openo.msb.api.MetricsInfo;
import org.openo.msb.wrapper.util.MetricsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MetricsServiceWrapper {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MetricsServiceWrapper.class);

	public static MetricsInfo getMetricsInfo() {

		String metricsUrl = MetricsUtil.adminContextPath;
		String metricsJson = sendGetRequest(metricsUrl);

		metricsJson = metricsJson.replace("E_", "");//.replaceAll("(?![0-9])(\\.)(?![0-9])", "_").replace("-", "_")
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		MetricsInfo metricsInfo = new MetricsInfo();
		try {
			metricsInfo = mapper.readValue(metricsJson, MetricsInfo.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Jackson readValue to metricsInfo throw exception", e);
		}

		return metricsInfo;
	}

	public static String sendGetRequest(String url) {
		CloseableHttpClient httpClient = HttpClients.createDefault();  
		HttpGet httpGet = new HttpGet(url);

		try {
			CloseableHttpResponse res = httpClient.execute(httpGet);
			try {
				if (res.getStatusLine().getStatusCode() == MetricsUtil.SC_OK) {
					return EntityUtils.toString(res.getEntity());
				}
			} finally {
				res.close();
			}
		} catch (ParseException e) {
			LOGGER.error("HttpClient throw ParseException:" + url, e);
		} catch (IOException e) {
			LOGGER.error("HttpClient throw IOException:" + url, e);
		}
		finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.error("HttpClient Close throw IOException", e);
			}
		}

		return null;
	}
}
