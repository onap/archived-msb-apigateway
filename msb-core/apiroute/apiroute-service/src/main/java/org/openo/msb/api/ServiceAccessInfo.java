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
package org.openo.msb.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ServiceAccessInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    // (api|iui|custom|p2p)
    private String serviceType;

    private String serviceName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String version;

    private String accessAddr;

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the accessAddr
     */
    public String getAccessAddr() {
        return accessAddr;
    }

    /**
     * @param accessAddr the accessAddr to set
     */
    public void setAccessAddr(String accessAddr) {
        this.accessAddr = accessAddr;
    }
}
