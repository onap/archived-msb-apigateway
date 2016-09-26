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

package org.openo.msb;

import io.dropwizard.Configuration;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;
import org.openo.msb.api.ConsulInfo;
import org.openo.msb.api.DiscoverInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiRouteAppConfig  extends Configuration {
    @NotEmpty
    private String defaultWorkspace = "apiroute-works"; 

    @NotEmpty
    private String defaultName = "Stranger";
    
    @NotEmpty
    private String propertiesName="redis.properties";
    
    @NotEmpty
    private String propertiesDir="conf";
    
    
    @Valid
    private DiscoverInfo discoverInfo;
    
    @Valid
    private ConsulInfo  consulInfo;

    @JsonProperty
    public ConsulInfo getConsulInfo() {
        return consulInfo;
    }

    @JsonProperty
    public void setConsulInfo(ConsulInfo consulInfo) {
        this.consulInfo = consulInfo;
    }
  

    public String getPropertiesDir() {
		return propertiesDir;
	}

	public void setPropertiesDir(String propertiesDir) {
		this.propertiesDir = propertiesDir;
	}

	public String getPropertiesName() {
		return propertiesName;
	}

	public void setPropertiesName(String propertiesName) {
		this.propertiesName = propertiesName;
	}

	@JsonProperty
    public String getDefaultWorkspace() {
        return defaultWorkspace;
    }

    @JsonProperty
    public void setDefaultWorkspace(String defaultWorkspace) {
        this.defaultWorkspace = defaultWorkspace;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }

    

    @JsonProperty
    public DiscoverInfo getDiscoverInfo() {
        return discoverInfo;
    }

    @JsonProperty
    public void setDiscoverInfo(DiscoverInfo discoverInfo) {
        this.discoverInfo = discoverInfo;
    }
    
    
  

}