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
package org.onap.msb.apiroute.health;

import org.apache.commons.lang3.StringUtils;
import org.onap.msb.apiroute.ApiRouteApp;
import org.onap.msb.apiroute.wrapper.InitRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
import org.onap.msb.apiroute.wrapper.util.HttpClientUtil;
import org.onap.msb.apiroute.wrapper.util.HttpGetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;

public class ConsulLinkHealthCheck extends HealthCheck implements Runnable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConsulLinkHealthCheck.class);

	private final static String CONSUL_IP_ENV = ConfigUtil.getInstance().getConsul_ip();

	private static int failedLoopCheckNum = 12;
	private static int failedTimer = 5 * 1000;

	private static int normalTimer = 20 * 1000;
	private static Result result = Result.healthy();

	private String CHECK_IP = "127.0.0.1";
	private String CHECK_PORT = "8500";
	private String CHECK_URL = "http://" + CHECK_IP + ":" + CHECK_PORT
			+ "/v1/status/leader";

	public static Result getResult() {
		return result;
	}

	@Override
	protected Result check() {
		// TODO Auto-generated method stub

		if (!StringUtils.isBlank(CONSUL_IP_ENV)) {
			CHECK_IP = CONSUL_IP_ENV;
			CHECK_URL = "http://" + CHECK_IP + ":" + CHECK_PORT
					+ "/v1/status/leader";

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("check consul URL:" + CHECK_URL);
			}

			try {

				HttpGetResult result = HttpClientUtil
						.httpGetStatusAndBody(CHECK_URL);

				//response format:"127.0.0.1:8300"
				if (result.getStatusCode() == 200 && result.getBody() != null
						&& result.getBody().contains(":8300")) {
					return Result.healthy();
				} else {
					return Result.unhealthy("check consul link " + CHECK_URL
							+ " fail:" + result.getStatusCode()+":"+result.getBody());
				}

			} catch (Exception e) {
				LOGGER.warn(
						"ConsulLinkHealthCheck:" + CHECK_URL + " execption", e);
				return Result.unhealthy("check consul link " + CHECK_URL
						+ " exception:{}");
			}

		}

		return Result.healthy();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("consul link check starttime:"
						+ System.currentTimeMillis());
			}

			result = checkWithPolicy();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("consul link check result:" + result.isHealthy()
						+ " message:" + result.getMessage());

				LOGGER.debug("consul link check endtime:"
						+ System.currentTimeMillis());
			}

			try {
				Thread.sleep(normalTimer);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LOGGER.warn("loop check consul,thread sleep excepiton", e);
			}
		}
	}

	private Result checkWithPolicy() {
		int failedNum = 0;
		Result temp = Result.healthy();

		do {
			// check again
			temp = check();

			// healthy break;
			if (temp.isHealthy()) {
				break;
			}

			// unhealthy go on
			failedNum++;
			
			try {
				Thread.sleep(failedTimer);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LOGGER.warn("loop check consul,thread sleep excepiton", e);
			}
			
		} while (failedNum <= failedLoopCheckNum);

		return temp;
	}
	
}
