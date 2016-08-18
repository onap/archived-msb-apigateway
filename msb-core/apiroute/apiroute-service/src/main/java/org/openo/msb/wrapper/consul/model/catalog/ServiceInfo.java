/**
 * Copyright 2016 ZTE, Inc. and others.
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

package org.openo.msb.wrapper.consul.model.catalog;

import com.google.common.base.Objects;

public class ServiceInfo {

    private   String serviceName;
    
    private   String version="";

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(this == other)
            return true;
        if(other instanceof ServiceInfo)
        {
            ServiceInfo that = (ServiceInfo)other;
            return Objects.equal(serviceName, that.serviceName) && Objects.equal(version, that.version);
        } else
        {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(serviceName, version);
    }
    
}
