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

package org.openo.msb.wrapper.consul.model.catalog;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize(as = ImmutableCatalogService.class)
@JsonDeserialize(as = ImmutableCatalogService.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class CatalogService {

    @JsonProperty("Node")
    public abstract String getNode();

    @JsonProperty("Address")
    public abstract String getAddress();

    @JsonProperty("ServiceName")
    public abstract String getServiceName();

    @JsonProperty("ServiceID")
    public abstract String getServiceId();

	@JsonProperty("ServiceAddress")
    public abstract String getServiceAddress();

    @JsonProperty("ServicePort")
    public abstract int getServicePort();

    @JsonProperty("ServiceTags")
    public abstract List<String> getServiceTags();
}
