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
/**
 * Copyright 2016-2017 ZTE, Inc. and others.
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
package org.onap.msb.apiroute.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.net.URI;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HttpStatus;
import org.onap.msb.apiroute.api.CustomRouteInfo;
import org.onap.msb.apiroute.wrapper.CustomRouteServiceWrapper;

import com.codahale.metrics.annotation.Timed;

@Path("/customRoute")
@Api(tags = { "CustomRoute" })
@Produces(MediaType.APPLICATION_JSON)
public class CustomRouteResource {
	
    @Context
    UriInfo uriInfo; // actual uri info
    
	@GET
	@Path("/all")
	@ApiOperation(value = "get all CustomRoute ",  code = HttpStatus.SC_OK,response = CustomRouteInfo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "get CustomRouteInfo List  fail", response = String.class)})
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<CustomRouteInfo> getCustomRoutes(@ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay) {
		return CustomRouteServiceWrapper.getInstance().getAllCustomRouteInstances(routeWay);
	}
	
	@POST
	@Path("/instance")
	@ApiOperation(value = "add one CustomRoute ", code = HttpStatus.SC_CREATED,response = CustomRouteInfo.class)
	@ApiResponses(value = {
	                       @ApiResponse(code = HttpStatus.SC_UNPROCESSABLE_ENTITY, message = "Unprocessable CustomRouteInfo Entity ", response = String.class),
	                       @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "add CustomRouteInfo fail", response = String.class),
	                       @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = "Unprocessable CustomRouteInfo JSON REQUEST", response = String.class)})
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response addCustomRoute(
			@ApiParam(value = "CustomRoute Instance Info", required = true) CustomRouteInfo customRouteInfo,
			@ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay) {
	    CustomRouteInfo new_customRouteInfo = CustomRouteServiceWrapper.getInstance().saveCustomRouteInstance4Rest(customRouteInfo,routeWay);
	    URI returnURI =uriInfo.getAbsolutePathBuilder().path("/instance?serviceName=" + new_customRouteInfo.getServiceName()).build();
        return Response.created(returnURI).entity(new_customRouteInfo).build();

	}
	
	@GET
	@Path("/instance")
	@ApiOperation(value = "get one CustomRoute ",code = HttpStatus.SC_OK,  response = CustomRouteInfo.class)
	@ApiResponses(value = {
	                        @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "CustomRoute not found", response = String.class),
	                        @ApiResponse(code = HttpStatus.SC_UNPROCESSABLE_ENTITY, message = "Unprocessable CustomRoute Entity ", response = String.class),
	                        @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "get CustomRoute fail", response = String.class)})
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public CustomRouteInfo getCustomRoute(
			@ApiParam(value = "CustomRoute serviceName", required = false) @QueryParam("serviceName") String serviceName,
			@ApiParam(value = "CustomRoute host", required = false) @QueryParam("host") String host,
			@ApiParam(value = "CustomRoute Publish port", required = false) @QueryParam("publish_port") @DefaultValue("")String publish_port,
			@ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay) {
	    
	 
		return CustomRouteServiceWrapper.getInstance().getCustomRouteInstance(serviceName,host,publish_port,routeWay);

	}
	
	@PUT
	@Path("/instance")
	@ApiOperation(value = "update one CustomRoute by serviceName",  code = HttpStatus.SC_CREATED,response = CustomRouteInfo.class)
	@ApiResponses(value = {
	                       @ApiResponse(code = HttpStatus.SC_UNPROCESSABLE_ENTITY, message = "Unprocessable CustomRoute Entity ", response = String.class),
	                       @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "update CustomRoute fail", response = String.class),
	                       @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = "Unprocessable CustomRoute JSON REQUEST", response = String.class)})
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response updateCustomRoute(
			@ApiParam(value = "CustomRoute serviceName", required = true) @QueryParam("serviceName") String serviceName,
			@ApiParam(value = "CustomRoute Instance Info", required = true) CustomRouteInfo customRoute,
			@ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay
			) {

	    CustomRouteInfo new_customRouteInfo= CustomRouteServiceWrapper.getInstance().saveCustomRouteInstance4Rest(customRoute,routeWay);

        return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(new_customRouteInfo).build();

	}

	@DELETE
	@Path("/instance")
	@ApiOperation(value = "delete one CustomRoute by serviceName",  code = HttpStatus.SC_NO_CONTENT)
	@ApiResponses(value = {
	                        @ApiResponse(code = HttpStatus.SC_NO_CONTENT, message = "delete customRoute succeed "),
	                        @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "customRoute not found", response = String.class),
	                        @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "delete customRoute fail", response = String.class)})
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public void deleteCustomRoute(
			@ApiParam(value = "CustomRoute serviceName", required = true) @QueryParam("serviceName") String serviceName,
			@ApiParam(value = "CustomRoute host", required = false) @QueryParam("host") String host,
			@ApiParam(value = "CustomRoute Publish port", required = false) @QueryParam("publish_port") @DefaultValue("")String publish_port,
			@ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay) {

		 CustomRouteServiceWrapper.getInstance().deleteCustomRoute(serviceName,host,publish_port,routeWay);

	}
	
	@PUT
	@Path("/status")
	@ApiOperation(value = "update one CustomRoute  status by serviceName ",code = HttpStatus.SC_CREATED, response = CustomRouteInfo.class)
    @ApiResponses(value = {
	                        @ApiResponse(code = HttpStatus.SC_UNPROCESSABLE_ENTITY, message = "Unprocessable customRoute Entity ", response = String.class),
	                        @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "customRoute not found", response = String.class),
	                        @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "update status fail", response = String.class)})
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response updateCustomRouteStatus(
			@ApiParam(value = "CustomRoute serviceName", required = true) @QueryParam("serviceName") String serviceName,
			@ApiParam(value = "CustomRoute host", required = false) @QueryParam("host") String host,
			@ApiParam(value = "CustomRoute status,1：abled  0：disabled", required = true)  @QueryParam("status") String status,
			@ApiParam(value = "CustomRoute Publish port", required = false) @QueryParam("publish_port") @DefaultValue("")String publish_port,
			@ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay) {

	    CustomRouteInfo new_customRouteInfo = CustomRouteServiceWrapper.getInstance().updateCustomRouteStatus(serviceName,host,publish_port,status,routeWay);
        return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(new_customRouteInfo).build();


	}

}
