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

package org.openo.msb.wrapper.consul.model.health;

import org.openo.msb.wrapper.consul.model.catalog.ServiceInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public  class ServiceHealth {

    @JsonProperty("Node")
    public  Node node;

    @JsonProperty("Service")
    public  Service service;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(this == other)
            return true;
        if(other instanceof ServiceHealth)
        {
            ServiceHealth that = (ServiceHealth)other;
            return Objects.equal(node, that.node) && Objects.equal(service, that.service);
        } else
        {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(node, service);
    }

   
    
}
