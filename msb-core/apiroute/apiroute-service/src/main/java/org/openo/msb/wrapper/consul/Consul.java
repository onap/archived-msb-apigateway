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

import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.openo.msb.wrapper.consul.util.Jackson;
import org.openo.msb.wrapper.consul.util.ObjectMapperContextResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.net.HostAndPort;

/**
 * Client for interacting with the Consul HTTP API.
 *
 * @author rfast
 */
public class Consul {

    /**
     * Default Consul HTTP API host.
     */
    public static final String DEFAULT_HTTP_HOST = "localhost";

    /**
     * Default Consul HTTP API port.
     */
    public static final int DEFAULT_HTTP_PORT = 8500;




    private final CatalogClient catalogClient;
    
    private final HealthClient healthClient;


    /**
     * Private constructor.
     *
     * @param url     The full URL of a running Consul instance.
     * @param builder JAX-RS client builder instance.
     */
    private Consul(String url, ClientBuilder builder, ObjectMapper mapper) {

        if (!FluentIterable.from(builder.getConfiguration().getClasses())
                      .filter(new Predicate<Class<?>>() {
                        @Override
                        public boolean apply(final Class<?> clazz) {
                            return JacksonJaxbJsonProvider.class.isAssignableFrom(clazz);
                        }
                    }).first().isPresent()) {
            builder.register(JacksonJaxbJsonProvider.class);
        }
        final Client client = builder
                .register(new ObjectMapperContextResolver(mapper))
                .build();

     
        if(url.endsWith("8500")){
            this.catalogClient = new CatalogClient(client.target(url).path("v1").path("catalog"));
            this.healthClient = new HealthClient(client.target(url).path("v1").path("health"));
            }
            else{
                this.catalogClient = new CatalogClient(client.target(url).path("api").path("catalog").path("v1"));
                this.healthClient = new HealthClient(client.target(url).path("api").path("health").path("v1"));   
            }
       

//        agentClient.ping();
    }

    /**
     * Creates a new client given a complete URL.
     *
     * @deprecated Use {@link Consul.Builder}
     *
     * @param url     The Consul API URL.
     * @param builder The JAX-RS client builder instance.
     * @return A new client.
     */
    @Deprecated
    public static Consul newClient(String url, ClientBuilder builder, ObjectMapper mapper) {
        return new Consul(url, builder, mapper);
    }

    /**
     * Creates a new client given a host and a port.
     *
     * @deprecated Use {@link Consul.Builder}
     *
     * @param host    The Consul API hostname or IP.
     * @param port    The Consul port.
     * @param builder The JAX-RS client builder instance.
     * @return A new client.
     */
    @Deprecated
    public static Consul newClient(String host, int port, ClientBuilder builder, ObjectMapper mapper) {
        try {
            return new Consul(new URL("http", host, port, "").toString(), builder, mapper);
        } catch (MalformedURLException e) {
            throw new ConsulException("Bad Consul URL", e);
        }
    }

    /**
     * Creates a new client given a host and a port.
     *
     * @deprecated Use {@link Consul.Builder}
     *
     * @param host The Consul API hostname or IP.
     * @param port The Consul port.
     * @return A new client.
     */
    @Deprecated
    public static Consul newClient(String host, int port) {
        return newClient(host, port, ClientBuilder.newBuilder(), Jackson.MAPPER);
    }

    /**
     * Creates a new client given a host and a port.
     *
     * @deprecated Use {@link Consul.Builder}
     *
     * @return A new client.
     */
    @Deprecated
    public static Consul newClient() {
        return newClient(DEFAULT_HTTP_HOST, DEFAULT_HTTP_PORT);
    }

   
    /**
     * Get the Catalog HTTP client.
     * <p>
     * /v1/catalog
     *
     * @return The Catalog HTTP client.
     */
    public CatalogClient catalogClient() {
        return catalogClient;
    }
    
    public HealthClient healthClient() {
        return healthClient;
    }
    /**
     * Creates a new {@link Builder} object.
     *
     * @return A new Consul builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link Consul} client objects.
     */
    public static class Builder {
        private URL url;
        private Optional<SSLContext> sslContext = Optional.absent();
        private ObjectMapper objectMapper = Jackson.MAPPER;
        private ClientBuilder clientBuilder = ClientBuilder.newBuilder();

        {
            try {
                url = new URL("http", "localhost", 8500, "");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Constructs a new builder.
         */
        Builder() {

        }

        /**
         * Sets the URL from a {@link URL} object.
         *
         * @param url The Consul agent URL.
         * @return The builder.
         */
        public Builder withUrl(URL url) {
            this.url = url;

            return this;
        }

        /**
         * Sets the URL from a {@link HostAndPort} object.
         *
         * @param hostAndPort The Consul agent host and port.
         * @return The builder.
         */
        public Builder withHostAndPort(HostAndPort hostAndPort) {
            try {
                this.url = new URL("http", hostAndPort.getHostText(), hostAndPort.getPort(), "");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        /**
         * Sets the URL from a string.
         *
         * @param url The Consul agent URL.
         * @return The builder.
         */
        public Builder withUrl(String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        /**
         * Sets the {@link SSLContext} for the client.
         *
         * @param sslContext The SSL context for HTTPS agents.
         * @return The builder.
         */
        public Builder withSslContext(SSLContext sslContext) {
            this.sslContext = Optional.of(sslContext);

            return this;
        }

        /**
         * Sets the {@link ObjectMapper} for the client.
         *
         * @param objectMapper The {@link ObjectMapper} to use.
         * @return The builder.
         */
        public Builder withObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;

            objectMapper.registerModule(new GuavaModule());

            return this;
        }

        /**
         * Sets the JAX-RS {@link ClientBuilder} to use.
         *
         * @param clientBuilder The JAX-RS builder.
         * @return This builder.
         */
        public Builder withClientBuilder(ClientBuilder clientBuilder) {
            this.clientBuilder = clientBuilder;

            return this;
        }

        /**
         * Constructs a new {@link Consul} client.
         *
         * @return A new Consul client.
         */
        public Consul build() {
            if (this.sslContext.isPresent()) {
                this.clientBuilder.sslContext(this.sslContext.get());
            }

            return new Consul(this.url.toExternalForm(), this.clientBuilder, this.objectMapper);
        }
    }
}
