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

package org.openo.msb.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.impl.client.CloseableHttpClient;
import org.openo.msb.api.MetricsInfo;
import org.openo.msb.wrapper.MetricsServiceWrapper;

import com.codahale.metrics.annotation.Timed;

@Path("/metrics")
@Api(tags = { "metrics" })
@Produces(MediaType.APPLICATION_JSON)
public class MetricsResource {
	

	@GET
	@Path("/")
	@ApiOperation(value = "get Metrics Info ", response = MetricsInfo.class)
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public MetricsInfo getMetricsInfo() {
		return MetricsServiceWrapper.getMetricsInfo();
	}
	
	
}
