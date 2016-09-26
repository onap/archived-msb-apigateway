/**
 * Copyright 2016 ZTE Corporation.
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

package org.openo.msb.wrapper.consul;

import static org.openo.msb.wrapper.consul.util.ClientUtil.response;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.openo.msb.wrapper.consul.async.ConsulResponseCallback;
import org.openo.msb.wrapper.consul.model.ConsulResponse;
import org.openo.msb.wrapper.consul.model.health.ServiceHealth;
import org.openo.msb.wrapper.consul.option.CatalogOptions;
import org.openo.msb.wrapper.consul.option.QueryOptions;

/**
 * HTTP Client for /v1/health/ endpoints.
 */
public class HealthClient {


    private static final GenericType<List<ServiceHealth>> TYPE_SERVICE_HEALTH_LIST =
            new GenericType<List<ServiceHealth>>() {};
            
    private final WebTarget webTarget;

    /**
     * Constructs an instance of this class.
     *
     * @param webTarget The {@link javax.ws.rs.client.WebTarget} to base requests from.
     */
    HealthClient(WebTarget webTarget) {
        this.webTarget = webTarget;
    }

   

    /**
     * Retrieves the healthchecks for all healthy service instances.
     * 
     * GET /v1/health/service/{service}?passing
     *
     * @param service The service to query.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a list of
     * {@link com.orbitz.consul.model.health.HealthCheck} objects.
     */
    public ConsulResponse<List<ServiceHealth>> getHealthyServiceInstances(String service) {
        return getHealthyServiceInstances(service, null, QueryOptions.BLANK);
    }

    /**
     * Retrieves the healthchecks for all healthy service instances in a given datacenter.
     * 
     * GET /v1/health/service/{service}?dc={datacenter}&amp;passing
     *
     * @param service        The service to query.
     * @param catalogOptions The catalog specific options to use.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a list of
     * {@link com.orbitz.consul.model.health.HealthCheck} objects.
     */
    public ConsulResponse<List<ServiceHealth>> getHealthyServiceInstances(String service, CatalogOptions catalogOptions) {
        return getHealthyServiceInstances(service, catalogOptions, QueryOptions.BLANK);
    }

    /**
     * Retrieves the healthchecks for all healthy service instances with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     * 
     * GET /v1/health/service/{service}?passing
     *
     * @param service      The service to query.
     * @param queryOptions The Query Options to use.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a list of
     * {@link com.orbitz.consul.model.health.HealthCheck} objects.
     */
    public ConsulResponse<List<ServiceHealth>> getHealthyServiceInstances(String service, QueryOptions queryOptions) {
        return getHealthyServiceInstances(service, null, queryOptions);
    }

    /**
     * Retrieves the healthchecks for all healthy service instances in a given datacenter with
     * {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     * 
     * GET /v1/health/service/{service}?dc={datacenter}&amp;passing
     *
     * @param service        The service to query.
     * @param catalogOptions The catalog specific options to use.
     * @param queryOptions   The Query Options to use.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a list of
     * {@link com.orbitz.consul.model.health.HealthCheck} objects.
     */
    public ConsulResponse<List<ServiceHealth>> getHealthyServiceInstances(String service, CatalogOptions catalogOptions,
                                                                          QueryOptions queryOptions) {
        return response(webTarget.path("service").path(service).queryParam("passing", "true"),
                catalogOptions, queryOptions, TYPE_SERVICE_HEALTH_LIST);
    }

    /**
     * Asynchronously retrieves the healthchecks for all healthy service instances in a given
     * datacenter with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     * 
     * GET /v1/health/service/{service}?dc={datacenter}&amp;passing
     * 
     * Experimental.
     *
     * @param service        The service to query.
     * @param catalogOptions The catalog specific options to use.
     * @param queryOptions   The Query Options to use.
     * @param callback       Callback implemented by callee to handle results.
     */
    public void getHealthyServiceInstances(String service, CatalogOptions catalogOptions,
                                           QueryOptions queryOptions,
                                           ConsulResponseCallback<List<ServiceHealth>> callback) {
        response(webTarget.path("service").path(service).queryParam("passing", "true"),
                catalogOptions, queryOptions, TYPE_SERVICE_HEALTH_LIST, callback);
    }

    /**
     * Asynchronously retrieves the healthchecks for all healthy service instances in a given
     * datacenter with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     * 
     * GET /v1/health/service/{service}?dc={datacenter}&amp;passing
     * 
     * Experimental.
     *
     * @param service      The service to query.
     * @param queryOptions The Query Options to use.
     * @param callback     Callback implemented by callee to handle results.
     */
    public void getHealthyServiceInstances(String service, QueryOptions queryOptions,
                                           ConsulResponseCallback<List<ServiceHealth>> callback) {
        response(webTarget.path("service").path(service).queryParam("passing", "true"),
                CatalogOptions.BLANK, queryOptions, TYPE_SERVICE_HEALTH_LIST, callback);
    }

    /**
     * Retrieves the healthchecks for all nodes.
     * 
     * GET /v1/health/service/{service}
     *
     * @param service The service to query.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a list of
     * {@link com.orbitz.consul.model.health.HealthCheck} objects.
     */
    public ConsulResponse<List<ServiceHealth>> getAllServiceInstances(String service) {
        return getAllServiceInstances(service, null, QueryOptions.BLANK);
    }

    /**
     * Retrieves the healthchecks for all nodes in a given datacenter.
     * 
     * GET /v1/health/service/{service}?dc={datacenter}
     *
     * @param service        The service to query.
     * @param catalogOptions The catalog specific options to use.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a list of
     * {@link com.orbitz.consul.model.health.HealthCheck} objects.
     */
    public ConsulResponse<List<ServiceHealth>> getAllServiceInstances(String service, CatalogOptions catalogOptions) {
        return getAllServiceInstances(service, catalogOptions, QueryOptions.BLANK);
    }

    /**
     * Retrieves the healthchecks for all nodes with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     * 
     * GET /v1/health/service/{service}
     *
     * @param service      The service to query.
     * @param queryOptions The Query Options to use.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a list of
     * {@link com.orbitz.consul.model.health.HealthCheck} objects.
     */
    public ConsulResponse<List<ServiceHealth>> getAllServiceInstances(String service, QueryOptions queryOptions) {
        return getAllServiceInstances(service, null, queryOptions);
    }

    /**
     * Retrieves the healthchecks for all nodes in a given datacenter with
     * {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     * 
     * GET /v1/health/service/{service}?dc={datacenter}
     *
     * @param service        The service to query.
     * @param catalogOptions The catalog specific options to use.
     * @param queryOptions   The Query Options to use.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a list of
     * {@link com.orbitz.consul.model.health.HealthCheck} objects.
     */
    public ConsulResponse<List<ServiceHealth>> getAllServiceInstances(String service, CatalogOptions catalogOptions,
                                                                      QueryOptions queryOptions) {
        return response(webTarget.path("service").path(service), catalogOptions, queryOptions,
                TYPE_SERVICE_HEALTH_LIST);
    }

    /**
     * Asynchronously retrieves the healthchecks for all nodes in a given
     * datacenter with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     * 
     * GET /v1/health/service/{service}?dc={datacenter}
     * 
     * Experimental.
     *
     * @param service        The service to query.
     * @param catalogOptions The catalog specific options to use.
     * @param queryOptions   The Query Options to use.
     * @param callback       Callback implemented by callee to handle results.
     */
    public void getAllServiceInstances(String service, CatalogOptions catalogOptions,
                                       QueryOptions queryOptions,
                                       ConsulResponseCallback<List<ServiceHealth>> callback) {
        response(webTarget.path("service").path(service), catalogOptions, queryOptions,
                TYPE_SERVICE_HEALTH_LIST, callback);
    }

    /**
     * Asynchronously retrieves the healthchecks for all nodes in a given
     * datacenter with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     * 
     * GET /v1/health/service/{service}?dc={datacenter}
     * 
     * Experimental.
     *
     * @param service      The service to query.
     * @param queryOptions The Query Options to use.
     * @param callback     Callback implemented by callee to handle results.
     */
    public void getAllServiceInstances(String service, QueryOptions queryOptions,
                                       ConsulResponseCallback<List<ServiceHealth>> callback) {
        response(webTarget.path("service").path(service), CatalogOptions.BLANK,
                queryOptions, TYPE_SERVICE_HEALTH_LIST, callback);
    }
}
