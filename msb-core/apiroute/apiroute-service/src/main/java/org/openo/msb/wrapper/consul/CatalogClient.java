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
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.openo.msb.wrapper.consul.async.ConsulResponseCallback;
import org.openo.msb.wrapper.consul.model.ConsulResponse;
import org.openo.msb.wrapper.consul.model.catalog.CatalogNode;
import org.openo.msb.wrapper.consul.model.catalog.CatalogService;
import org.openo.msb.wrapper.consul.model.health.Node;
import org.openo.msb.wrapper.consul.option.CatalogOptions;
import org.openo.msb.wrapper.consul.option.QueryOptions;

/**
 * HTTP Client for /v1/catalog/ endpoints or api/catalog/v1  by openresty
 */
public class CatalogClient {

    private static final GenericType<List<String>> TYPE_STRING_LIST = new GenericType<List<String>>() {};
    private static final GenericType<List<Node>> TYPE_NODE_LIST = new GenericType<List<Node>>() {};
    private static final GenericType<Map<String, List<String>>> TYPE_SERVICES_MAP = new GenericType<Map<String, List<String>>>() {};
    private static final GenericType<List<CatalogService>> TYPE_CATALOG_SERVICE_LIST = new GenericType<List<CatalogService>>() {};
    private static final GenericType<CatalogNode> TYPE_CATALOG_NODE = new GenericType<CatalogNode>() {};
    
    private final WebTarget webTarget;
    
    /**
     * Constructs an instance of this class.
     *
     * @param webTarget The {@link javax.ws.rs.client.WebTarget} to base requests from.
     */
    CatalogClient(WebTarget webTarget) {
        this.webTarget = webTarget;        
    }

    /**
     * Retrieves all datacenters.
     *
     * GET /v1/catalog/datacenters
     *
     * @return A list of datacenter names.
     */
    public List<String> getDatacenters() {
        return webTarget.path("datacenters").request()
                .accept(MediaType.APPLICATION_JSON_TYPE).get(TYPE_STRING_LIST);
    }

    /**
     * Retrieves all nodes.
     *
     * GET /v1/catalog/nodes
     *
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a list of
     * {@link org.openo.msb.wrapper.consul.model.health.Node} objects.
     */
    public ConsulResponse<List<Node>> getNodes() {
        return getNodes(null, QueryOptions.BLANK);
    }

    /**
     * Retrieves all nodes for a given datacenter.
     *
     * GET /v1/catalog/nodes?dc={datacenter}
     *
     * @param catalogOptions Catalog specific options to use.      
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a list of
     * {@link org.openo.msb.wrapper.consul.model.health.Node} objects.
     */
    public ConsulResponse<List<Node>> getNodes(CatalogOptions catalogOptions) {
        return getNodes(catalogOptions, QueryOptions.BLANK);
    }

    /**
     * Retrieves all nodes with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     *
     * GET /v1/catalog/nodes
     *
     * @param queryOptions The Query Options to use.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a list of
     * {@link org.openo.msb.wrapper.consul.model.health.Node} objects.
     */
    public ConsulResponse<List<Node>> getNodes(QueryOptions queryOptions) {
        return getNodes(null, queryOptions);
    }

    /**
     * Retrieves all nodes for a given datacenter with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     *
     * GET /v1/catalog/nodes?dc={datacenter}
     *
     * @param catalogOptions Catalog specific options to use.      
     * @param queryOptions The Query Options to use.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a list of
     * {@link org.openo.msb.wrapper.consul.model.health.Node} objects.
     */
    public ConsulResponse<List<Node>> getNodes(CatalogOptions catalogOptions, QueryOptions queryOptions) {
        return response(webTarget.path("nodes"), catalogOptions, queryOptions, TYPE_NODE_LIST);
    }

    /**
     * Retrieves all services for a given datacenter.
     *
     * GET /v1/catalog/services?dc={datacenter}
     *
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a map of service name to list of tags.
     */
    public ConsulResponse<Map<String, List<String>>> getServices() {
        return getServices(null, QueryOptions.BLANK);
    }

    /**
     * Retrieves all services for a given datacenter.
     *
     * GET /v1/catalog/services?dc={datacenter}
     *
     * @param catalogOptions Catalog specific options to use.      
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a map of service name to list of tags.
     */
    public ConsulResponse<Map<String, List<String>>> getServices(CatalogOptions catalogOptions) {
        return getServices(catalogOptions, QueryOptions.BLANK);
    }

    /**
     * Retrieves all services for a given datacenter with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     *
     * GET /v1/catalog/services?dc={datacenter}
     *
     * @param queryOptions The Query Options to use.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a map of service name to list of tags.
     */
    public ConsulResponse<Map<String, List<String>>> getServices(QueryOptions queryOptions) {
        return getServices(null, queryOptions);
    }

    /**
     * Retrieves all services for a given datacenter with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     *
     * GET /v1/catalog/services?dc={datacenter}
     *
     * @param catalogOptions Catalog specific options to use.      
     * @param queryOptions The Query Options to use.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing a map of service name to list of tags.
     */
    public ConsulResponse<Map<String, List<String>>> getServices(CatalogOptions catalogOptions, QueryOptions queryOptions) {
        return response(webTarget.path("services"), catalogOptions, queryOptions, TYPE_SERVICES_MAP);
    }
    
    public void  getService(QueryOptions queryOptions, ConsulResponseCallback<Map<String, List<String>>> callback) {
        response(webTarget.path("services"), CatalogOptions.BLANK,
            queryOptions, TYPE_SERVICES_MAP, callback);
    }

    /**
     * Retrieves a single service.
     *
     * GET /v1/catalog/service/{service}
     *
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing
     * {@link org.openo.msb.wrapper.consul.model.catalog.CatalogService} objects.
     */
    public ConsulResponse<List<CatalogService>> getService(String service) {
        return getService(service, null, QueryOptions.BLANK);
    }

    /**
     * Retrieves a single service for a given datacenter.
     *
     * GET /v1/catalog/service/{service}?dc={datacenter}
     *
     * @param catalogOptions Catalog specific options to use.      
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing
     * {@link org.openo.msb.wrapper.consul.model.catalog.CatalogService} objects.
     */
    public ConsulResponse<List<CatalogService>> getService(String service, CatalogOptions catalogOptions) {
        return getService(service, catalogOptions, QueryOptions.BLANK);
    }

    /**
     * Retrieves a single service with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     *
     * GET /v1/catalog/service/{service}
     *
     * @param queryOptions The Query Options to use.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing
     * {@link org.openo.msb.wrapper.consul.model.catalog.CatalogService} objects.
     */
    public ConsulResponse<List<CatalogService>> getService(String service, QueryOptions queryOptions) {
        return getService(service, null, queryOptions);
    }
    
    public void getService(String service, QueryOptions queryOptions, ConsulResponseCallback<List<CatalogService>> callback) {
   
        response(webTarget.path("service").path(service), CatalogOptions.BLANK,
            queryOptions, TYPE_CATALOG_SERVICE_LIST, callback);
    }
    
   

    /**
     * Retrieves a single service for a given datacenter with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     *
     * GET /v1/catalog/service/{service}?dc={datacenter}
     *
     * @param catalogOptions Catalog specific options to use.      
     * @param queryOptions The Query Options to use.
     * @return A {@link org.openo.msb.wrapper.consul.model.ConsulResponse} containing
     * {@link org.openo.msb.wrapper.consul.model.catalog.CatalogService} objects.
     */
    public ConsulResponse<List<CatalogService>> getService(String service, CatalogOptions catalogOptions,
                                                           QueryOptions queryOptions) {
        return response(webTarget.path("service").path(service), catalogOptions, queryOptions,
                TYPE_CATALOG_SERVICE_LIST);
    }

    /**
     * Retrieves a single node.
     *
     * GET /v1/catalog/node/{node}
     *
     * @return A list of matching {@link org.openo.msb.wrapper.consul.model.catalog.CatalogService} objects.
     */
    public ConsulResponse<CatalogNode> getNode(String node) {
        return getNode(node, null, QueryOptions.BLANK);
    }

    /**
     * Retrieves a single node for a given datacenter.
     *
     * GET /v1/catalog/node/{node}?dc={datacenter}
     *
     * @param catalogOptions Catalog specific options to use.      
     * @return A list of matching {@link org.openo.msb.wrapper.consul.model.catalog.CatalogService} objects.
     */
    public ConsulResponse<CatalogNode> getNode(String node, CatalogOptions catalogOptions) {
        return getNode(node, catalogOptions, QueryOptions.BLANK);
    }

    /**
     * Retrieves a single node with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     *
     * GET /v1/catalog/node/{node}
     *
     * @param queryOptions The Query Options to use.
     * @return A list of matching {@link org.openo.msb.wrapper.consul.model.catalog.CatalogService} objects.
     */
    public ConsulResponse<CatalogNode> getNode(String node, QueryOptions queryOptions) {
        return getNode(node, null, queryOptions);
    }

    /**
     * Retrieves a single node for a given datacenter with {@link org.openo.msb.wrapper.consul.option.QueryOptions}.
     *
     * GET /v1/catalog/node/{node}?dc={datacenter}
     *
     * @param catalogOptions Catalog specific options to use.      
     * @param queryOptions The Query Options to use.
     * @return A list of matching {@link org.openo.msb.wrapper.consul.model.catalog.CatalogService} objects.
     */
    public ConsulResponse<CatalogNode> getNode(String node, CatalogOptions catalogOptions, QueryOptions queryOptions) {
        return response(webTarget.path("node").path(node), catalogOptions, queryOptions,
                TYPE_CATALOG_NODE);
    }
}
