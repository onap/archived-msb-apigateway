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

import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HttpStatus;
import org.openo.msb.api.IuiRouteInfo;
import org.openo.msb.wrapper.IuiRouteServiceWrapper;

import com.codahale.metrics.annotation.Timed;

@Path("/iuiRoute")
@Api(tags = { "iuiRoute" })
@Produces(MediaType.APPLICATION_JSON)
public class IuiRouteResource {

    @Context
    UriInfo uriInfo; // actual uri info

	@GET
	@Path("/")
	@ApiOperation(value = "get all iuiRoute ", code = HttpStatus.SC_OK,response = IuiRouteInfo.class, responseContainer = "List")
	@ApiResponses(value = {@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "get iuiRouteInfo List  fail", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
	@Timed
	public IuiRouteInfo[] getIuiRoutes() {
		return IuiRouteServiceWrapper.getInstance().getAllIuiRouteInstances();
	}

	@POST
	@Path("/")
	@ApiOperation(value = "add one iuiRoute ", code = HttpStatus.SC_CREATED,response = IuiRouteInfo.class)
	@ApiResponses(value = {
                           @ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "Unprocessable iuiRouteInfo Entity ", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "add iuiRouteInfo fail", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = "Unprocessable iuiRouteInfo JSON REQUEST", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response addIuiRoute(
			@ApiParam(value = "iuiRoute Instance Info", required = true) IuiRouteInfo iuiRouteInfo) {
	    IuiRouteInfo new_iuiRouteInfo = IuiRouteServiceWrapper.getInstance().saveIuiRouteInstance(iuiRouteInfo);
	    URI returnURI =
                uriInfo.getAbsolutePathBuilder()
                        .path("/" + new_iuiRouteInfo.getServiceName()).build();
        return Response.created(returnURI).entity(new_iuiRouteInfo).build();

	}
	
	@GET
	@Path("/{serviceName}")
	@ApiOperation(value = "get one iuiRoute ",code = HttpStatus.SC_OK, response = IuiRouteInfo.class)
	@ApiResponses(value = {
                           @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "IuiRouteInfo not found", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "Unprocessable IuiRouteInfo Entity ", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "get IuiRouteInfo fail", response = String.class)})
   @Produces(MediaType.APPLICATION_JSON)
	@Timed
	public IuiRouteInfo getIuiRoute(
			@ApiParam(value = "iuiRoute serviceName", required = true) @PathParam("serviceName") String serviceName) {

		return IuiRouteServiceWrapper.getInstance().getIuiRouteInstance(serviceName);

	}

	@PUT
	@Path("/{serviceName}")
	@ApiOperation(value = "update one iuiRoute by serviceName", code = HttpStatus.SC_CREATED,response = IuiRouteInfo.class)
	@ApiResponses(value = {
                           @ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "Unprocessable IuiRouteInfo Entity ", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "update IuiRouteInfo fail", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = "Unprocessable IuiRouteInfo JSON REQUEST", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response updateIuiRoute(
			@ApiParam(value = "iuiRoute serviceName", required = true) @PathParam("serviceName") String serviceName,
			@ApiParam(value = "iuiRoute Instance Info", required = true) IuiRouteInfo iuiRouteInfo) {

	    IuiRouteInfo new_iuiRouteInfo =  IuiRouteServiceWrapper.getInstance().updateIuiRouteInstance(serviceName,iuiRouteInfo);
	    URI returnURI =
                uriInfo.getAbsolutePathBuilder()
                        .path("/" + serviceName).build();
        return Response.created(returnURI).entity(new_iuiRouteInfo).build();
	}

	@DELETE
	@Path("/{serviceName}")
	@ApiOperation(value = "delete one iuiRoute by serviceName", code = HttpStatus.SC_NO_CONTENT)
	@ApiResponses(value = {
                           @ApiResponse(code = HttpStatus.SC_NO_CONTENT, message = "delete IuiRouteInfo succeed "),
                           @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "IuiRouteInfo not found", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "delete IuiRouteInfo fail", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
	@Timed
	public void deleteIuiRoute(
			@ApiParam(value = "iuiRoute serviceName", required = true) @PathParam("serviceName") String serviceName) {

		IuiRouteServiceWrapper.getInstance().deleteIuiRoute(serviceName,"*");

	}
	
	@PUT
	@Path("/{serviceName}/status/{status}")
	@ApiOperation(value = "update one iuiRoute  status by serviceName ",code = HttpStatus.SC_CREATED, response = IuiRouteInfo.class)
	@ApiResponses(value = {
                           @ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "Unprocessable IuiRouteInfo Entity ", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "IuiRouteInfo not found", response = String.class),
                           @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "update IuiRouteInfo status fail", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
	@Timed
	public Response updateIuiRouteStatus(
			@ApiParam(value = "iuiRoute serviceName", required = true) @PathParam("serviceName") String serviceName,
			@ApiParam(value = "iuiRoute status,1：abled  0：disabled", required = true)  @PathParam("status") String status) {

	    IuiRouteInfo new_iuiRouteInfo =  IuiRouteServiceWrapper.getInstance().updateIuiRouteStatus(serviceName,status);
        URI returnURI =
                uriInfo.getAbsolutePathBuilder()
                        .path("/" + serviceName).build();
        return Response.created(returnURI).entity(new_iuiRouteInfo).build();

	}
	
}
