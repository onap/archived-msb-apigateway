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
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
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
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.openo.msb.api.MicroServiceFullInfo;
import org.openo.msb.api.MicroServiceInfo;
import org.openo.msb.wrapper.MicroServiceWrapper;
import org.openo.msb.wrapper.util.MicroServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

@Path("/services")
@Api(tags = {"MSB-Service Resource"})
@Produces(MediaType.APPLICATION_JSON)
public class MicroServiceResource {

    @Context
    UriInfo uriInfo; // actual uri info


    private static final Logger LOGGER = LoggerFactory.getLogger(MicroServiceResource.class);

    @GET
    @Path("/")
    @ApiOperation(value = "get all microservices ", code = HttpStatus.SC_OK, response = MicroServiceFullInfo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "get microservice List  fail", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public MicroServiceFullInfo[] getMicroService() {
        return MicroServiceWrapper.getInstance().getAllMicroServiceInstances();
    }

    @POST
    @Path("/")
    @ApiOperation(value = "add one microservice ", code = HttpStatus.SC_CREATED, response = MicroServiceFullInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "add microservice fail", response = String.class),
            @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = "Unprocessable MicroServiceInfo JSON REQUEST", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response addMicroService(
            @ApiParam(value = "MicroServiceInfo Instance Info", required = true) MicroServiceInfo microServiceInfo,
            @Context HttpServletRequest request,
            @ApiParam(value = "createOrUpdate", required = false) @QueryParam("createOrUpdate") @DefaultValue("true") boolean createOrUpdate,
            @ApiParam(value = "port", required = false) @QueryParam("port") @DefaultValue("") String port) {
       
       String ip=MicroServiceUtil.getRealIp(request);
        
        MicroServiceFullInfo microServiceFullInfo =
                MicroServiceWrapper.getInstance().saveMicroServiceInstance(microServiceInfo,
                    createOrUpdate,ip,port);
        URI returnURI =
                uriInfo.getAbsolutePathBuilder()
                        .path("/" + microServiceInfo.getServiceName() + "/version/"
                                + microServiceInfo.getVersion()).build();
        return Response.created(returnURI).entity(microServiceFullInfo).build();
    }



    @GET
    @Path("/{serviceName}/version/{version}")
    @ApiOperation(value = "get one microservice ", code = HttpStatus.SC_OK, response = MicroServiceFullInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "microservice not found", response = String.class),
            @ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "get microservice fail", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public MicroServiceFullInfo getMicroService(
            @ApiParam(value = "microservice serviceName") @PathParam("serviceName") String serviceName,
            @ApiParam(value = "microservice version,if the version is empty, please enter \"null\"") @PathParam("version") @DefaultValue("") String version,
            @ApiParam(value = "port", required = false) @QueryParam("port") @DefaultValue("") String port) {


        return MicroServiceWrapper.getInstance().getMicroServiceInstance(serviceName, version,port);


    }

    @PUT
    @Path("/{serviceName}/version/{version}")
    @ApiOperation(value = "update one microservice by serviceName and version", code = HttpStatus.SC_CREATED, response = MicroServiceFullInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "update microservice fail", response = String.class),
            @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = "Unprocessable MicroServiceInfo JSON REQUEST", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response updateMicroService(
            @ApiParam(value = "microservice serviceName") @PathParam("serviceName") String serviceName,
            @ApiParam(value = "microservice version,if the version is empty, please enter \"null\"") @PathParam("version") @DefaultValue("") String version,
            @ApiParam(value = "microservice Instance Info", required = true) MicroServiceInfo microServiceInfo) {

        MicroServiceFullInfo microServiceFullInfo = MicroServiceWrapper.getInstance().updateMicroServiceInstance(serviceName, version,
                microServiceInfo);
        return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(microServiceFullInfo).build();

    }

    @PUT
    @Path("/{serviceName}/version/{version}/nodes/{ip}/{port}")
    @ApiOperation(value = "update  single node by serviceName and version and node", code = HttpStatus.SC_CREATED, response = MicroServiceFullInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "microservice not found", response = String.class),
            @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "update node fail", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response updateNode(
            @ApiParam(value = "microservice serviceName", required = true) @PathParam("serviceName") String serviceName,
            @ApiParam(value = "microservice version,if the version is empty, please enter \"null\"", required = false) @PathParam("version") @DefaultValue("") String version,
            @ApiParam(value = "ip") @PathParam("ip") String ip,
            @ApiParam(value = "port") @PathParam("port") String port,
            @ApiParam(value = "ttl") @QueryParam("ttl") int ttl) {

        MicroServiceFullInfo microServiceFullInfo = MicroServiceWrapper.getInstance().updateMicroServiceNode(serviceName, version, ip,port, ttl);

        return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(microServiceFullInfo).build();

    }



    @DELETE
    @Path("/{serviceName}/version/{version}/nodes/{ip}/{port}")
    @ApiOperation(value = "delete single node by serviceName and version and node", code = HttpStatus.SC_NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.SC_NO_CONTENT, message = "delete node succeed "),
            @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "node not found", response = String.class),
            @ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "delete node fail", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public void deleteNode(
            @ApiParam(value = "microservice serviceName", required = true) @PathParam("serviceName") String serviceName,
            @ApiParam(value = "microservice version,if the version is empty, please enter \"null\"", required = false) @PathParam("version") @DefaultValue("") String version,
            @ApiParam(value = "ip") @PathParam("ip") String ip,
            @ApiParam(value = "port") @PathParam("port") String port) {

        MicroServiceWrapper.getInstance().deleteMicroServiceInstance(serviceName, version, ip,port);

    }


    @DELETE
    @Path("/{serviceName}/version/{version}")
    @ApiOperation(value = "delete one full microservice by serviceName and version", code = HttpStatus.SC_NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.SC_NO_CONTENT, message = "delete microservice succeed "),
            @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "microservice not found", response = String.class),
            @ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "delete microservice fail", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public void deleteMicroService(
            @ApiParam(value = "microservice serviceName", required = true) @PathParam("serviceName") String serviceName,
            @ApiParam(value = "microservice version,if the version is empty, please enter \"null\"", required = false) @PathParam("version") @DefaultValue("") String version,
            @ApiParam(value = "port", required = false) @QueryParam("port") @DefaultValue("") String port) {


         MicroServiceWrapper.getInstance().deleteMicroService(serviceName, version,port);

    }

    @PUT
    @Path("/{serviceName}/version/{version}/status/{status}")
    @ApiOperation(value = "update  microservice status by serviceName and version", code = HttpStatus.SC_CREATED, response = MicroServiceFullInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "microservice not found", response = String.class),
            @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "update status fail", response = String.class)})
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response updateServiceStatus(
            @ApiParam(value = "microservice serviceName", required = true) @PathParam("serviceName") String serviceName,
            @ApiParam(value = "microservice version,if the version is empty, please enter \"null\"", required = false) @PathParam("version") @DefaultValue("") String version,
            @ApiParam(value = "status,1：abled  0：disabled") @PathParam("status") String status) {

        MicroServiceFullInfo microServiceFullInfo = MicroServiceWrapper.getInstance().updateMicroServiceStatus(serviceName, version,status);

        return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(microServiceFullInfo).build();

    }

//    @PUT
//    @Path("/ttl")
//    @ApiOperation(value = "test json date iso8601 ", code = 200, response = String.class)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Timed
//    public String testIso8601(
//              @ApiParam(value = "microservice Instance Info", required = true) MicroServiceFullInfo microServiceInfo) {
//        Set<NodeInfo> nodes=microServiceInfo.getNodes();
//        String rtn="rtn:";
//        for(NodeInfo node:nodes){
//            Date date=node.getExpiration();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//            String datettl=sdf.format(date);
//            rtn+=datettl;
//        }
//        
//    
//        return rtn;
//    }
}
