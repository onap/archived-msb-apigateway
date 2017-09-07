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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HttpStatus;
import org.onap.msb.apiroute.api.ApiRouteInfo;
import org.onap.msb.apiroute.api.DiscoverInfo;
import org.onap.msb.apiroute.wrapper.ApiRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.CustomRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.IuiRouteServiceWrapper;
import org.onap.msb.apiroute.wrapper.util.CommonUtil;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
import org.onap.msb.apiroute.wrapper.util.JacksonJsonUtil;

import com.codahale.metrics.annotation.Timed;

@Path("/apiRoute")
@Api(tags = { "ApiRoute" })
@Produces(MediaType.APPLICATION_JSON)
public class ApiRouteResource {

    @Context
    UriInfo uriInfo; // actual uri info

	@GET
	@Path("/")
	@ApiOperation(value = "get all ApiRoute ", code = HttpStatus.SC_OK,response = ApiRouteInfo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "get ApiRouteInfo List  fail", response = String.class)})
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public List<ApiRouteInfo> getApiRoutes(@ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay) {
		return ApiRouteServiceWrapper.getInstance().getAllApiRouteInstances(routeWay);
	}

	@POST
	@Path("/")
	@ApiOperation(value = "add one ApiRoute ", code = HttpStatus.SC_CREATED,response = ApiRouteInfo.class)
	@ApiResponses(value = {
                           @ApiResponse(code = HttpStatus.SC_UNPROCESSABLE_ENTITY, message = "Unprocessable ApiRouteInfo Entity ", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "add ApiRouteInfo fail", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = "Unprocessable ApiRouteInfo JSON REQUEST", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response addApiRoute(
			@ApiParam(value = "ApiRoute Instance Info", required = true) ApiRouteInfo apiRouteInfo,
			@ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay) {
	    ApiRouteInfo new_apiRouteInfo = ApiRouteServiceWrapper.getInstance().saveApiRouteInstance4Rest(apiRouteInfo,routeWay);
	    URI returnURI = uriInfo.getAbsolutePathBuilder().path("/" + new_apiRouteInfo.getServiceName()+"/version/"+new_apiRouteInfo.getVersion()).build();
        return Response.created(returnURI).entity(new_apiRouteInfo).build();

	}
	
	@GET
	@Path("/{serviceName}/version/{version}")
	@ApiOperation(value = "get one ApiRoute ",code = HttpStatus.SC_OK, response = ApiRouteInfo.class)
	@ApiResponses(value = {
                           @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "ApiRouteInfo not found", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_UNPROCESSABLE_ENTITY, message = "Unprocessable ApiRouteInfo Entity ", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "get ApiRouteInfo fail", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
	@Timed
	public ApiRouteInfo getApiRoute(
			@ApiParam(value = "ApiRoute serviceName", required = true) @PathParam("serviceName") String serviceName,
			@ApiParam(value = "ApiRoute version,if the version is empty, please enter \"null\"", required = false) @PathParam("version") @DefaultValue("") String version,
	        @ApiParam(value = "ApiRoute host", required = false) @QueryParam("host") String host,
	        @ApiParam(value = "ApiRoute Publish port", required = false) @QueryParam("publish_port") @DefaultValue("")String publish_port,
	        @ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay) {

		return ApiRouteServiceWrapper.getInstance().getApiRouteInstance(serviceName,version,host,publish_port,routeWay);

	}

	@PUT
	@Path("/{serviceName}/version/{version}")
	@ApiOperation(value = "update one ApiRoute by serviceName and version",  code = HttpStatus.SC_CREATED,response = ApiRouteInfo.class)
	@ApiResponses(value = {
                           @ApiResponse(code = HttpStatus.SC_UNPROCESSABLE_ENTITY, message = "Unprocessable ApiRouteInfo Entity ", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "update ApiRouteInfo fail", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = "Unprocessable ApiRouteInfo JSON REQUEST", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response updateApiRoute(
			@ApiParam(value = "ApiRoute serviceName", required = true) @PathParam("serviceName") String serviceName,
			@ApiParam(value = "ApiRoute version,if the version is empty, please enter \"null\"", required = false) @PathParam("version") @DefaultValue("") String version,
			@ApiParam(value = "ApiRoute Instance Info", required = true) ApiRouteInfo apiRouteInfo,
			@ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay) {

		ApiRouteInfo new_apiRouteInfo = ApiRouteServiceWrapper.getInstance().saveApiRouteInstance4Rest(apiRouteInfo,routeWay);
        URI returnURI =uriInfo.getAbsolutePathBuilder().path("/" + new_apiRouteInfo.getServiceName()+"/version/"+new_apiRouteInfo.getVersion()).build();
        return Response.created(returnURI).entity(new_apiRouteInfo).build();

	}
	
	

	@DELETE
	@Path("/{serviceName}/version/{version}")
	@ApiOperation(value = "delete one ApiRoute by serviceName and version", code = HttpStatus.SC_NO_CONTENT)
	@ApiResponses(value = {
                           @ApiResponse(code = HttpStatus.SC_NO_CONTENT, message = "delete ApiRouteInfo succeed "),
                           @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "ApiRouteInfo not found", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "delete ApiRouteInfo fail", response = String.class)})
   @Produces(MediaType.APPLICATION_JSON)
	@Timed
	public void deleteApiRoute(
			@ApiParam(value = "ApiRoute serviceName", required = true) @PathParam("serviceName") String serviceName,
			@ApiParam(value = "ApiRoute version,if the version is empty, please enter \"null\"", required = false) @PathParam("version") @DefaultValue("")  String version,
			@ApiParam(value = "ApiRoute host", required = false) @QueryParam("host") String host,
			@ApiParam(value = "ApiRoute Publish port", required = false) @QueryParam("publish_port") @DefaultValue("")String publish_port,
			@ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay) {

		 ApiRouteServiceWrapper.getInstance().deleteApiRoute(serviceName, version,host,publish_port,routeWay);
	}
	

	@GET
	@Path("/apiDocs")
	@ApiOperation(value = "get all Local apiDoc ", code = HttpStatus.SC_OK, response = String.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "get apiDoc List  fail", response = String.class)})
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public String[] getApiDocs() {

	  return ApiRouteServiceWrapper.getInstance().getAllApiDocs();
	}

	
	@GET
    @Path("/discoverInfo")
    @ApiOperation(value = "get discover Info ", code = HttpStatus.SC_OK,response = DiscoverInfo.class)
    @ApiResponses(value = {@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "get discover Info fail", response = String.class)})
	@Produces(MediaType.APPLICATION_JSON)
    @Timed
    public DiscoverInfo getServiceDiscoverInfo() {

	    return ConfigUtil.getInstance().getDiscoverInfo();
    }
	
	@PUT
	@Path("/{serviceName}/version/{version}/status/{status}")
	@ApiOperation(value = "update one ApiRoute  status by serviceName and version", code = HttpStatus.SC_CREATED,response = ApiRouteInfo.class)
	@ApiResponses(value = {
                            @ApiResponse(code = HttpStatus.SC_UNPROCESSABLE_ENTITY, message = "Unprocessable ApiRouteInfo Entity ", response = String.class),
                            @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "ApiRouteInfo not found", response = String.class),
                            @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "update status fail", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response updateApiRouteStatus(
			@ApiParam(value = "ApiRoute serviceName", required = true) @PathParam("serviceName") String serviceName,
			@ApiParam(value = "ApiRoute version,if the version is empty, please enter \"null\"", required = false) @PathParam("version") @DefaultValue("") String version,
			@ApiParam(value = "ApiRoute status,1：abled  0：disabled", required = true)  @PathParam("status") String status,
			@ApiParam(value = "ApiRoute host", required = false) @QueryParam("host") String host,
			@ApiParam(value = "ApiRoute Publish port", required = false) @QueryParam("publish_port") @DefaultValue("")String publish_port,
			@ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay) {

	    ApiRouteInfo new_apiRouteInfo =  ApiRouteServiceWrapper.getInstance().updateApiRouteStatus(serviceName,version,host,publish_port,status,routeWay);
        return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(new_apiRouteInfo).build();

	}
	
	@GET
    @Path("/export")
	@ApiOperation(value = "export all route service Info by json-file", code = HttpStatus.SC_OK,response = String.class)
    @ApiResponses(value = {
                           @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "export fail", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_NOT_ACCEPTABLE, message = " not Acceptable client-side", response = String.class)})
	@Produces(MediaType.TEXT_PLAIN)  
    public Response  exportService(@ApiParam(value = "Route Way", required = false) @QueryParam("routeWay") @DefaultValue("ip")String routeWay) throws Exception {

	    ResponseBuilder response = Response.ok( ApiRouteServiceWrapper.getInstance().getAllrouteByJson(routeWay));   
        return response.header("Content-Disposition", "attachment; filename=\"RouteService.json\"").build();   

    }

	

}
