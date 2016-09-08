/**
 * Copyright 2016 2015-2016 ZTE, Inc. and others. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.openo.msb.api.ServiceAccessInfo;
import org.openo.msb.wrapper.ServiceAccessWrapper;

import com.codahale.metrics.annotation.Timed;


@Path("/serviceaccess")
@Api(tags = {"ServiceAccess"})
@Produces(MediaType.APPLICATION_JSON)
public class ServiceAccessResource {

    @GET
    @Path("/{serviceName}")
    @ApiOperation(value = "get the msb access address of the service ", response = ServiceAccessInfo.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "get access address error ")})
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public List<ServiceAccessInfo> getApiRoute(
            @ApiParam(value = "serviceName") @PathParam("serviceName") String serviceName,
            @ApiParam(value = "service type", allowableValues = "api,iui,custom,p2p") @QueryParam("type") String serviceType,
            @ApiParam(value = "version") @QueryParam("version") @DefaultValue("") String version,
            @ApiParam(hidden = true) @HeaderParam("Host") String host) {
        
        host=host.split(":")[0];

        return ServiceAccessWrapper.getInstance().getApiRouteAccessAddr(serviceType, serviceName,
                version, host);

    }
}
