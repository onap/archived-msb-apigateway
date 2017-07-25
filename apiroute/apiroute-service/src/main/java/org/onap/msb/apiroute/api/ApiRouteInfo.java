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
package org.onap.msb.apiroute.api;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;


public class ApiRouteInfo extends RouteInfo {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(example = "v1", required = true)
	private String  version;  
	
	
	private String  apiJson="";  //swagger json Path
	
	 @ApiModelProperty(value = "[apiJson Type] 0：local file  1： remote file", allowableValues = "0,1", example = "1")
	private String apiJsonType="1";  
	private String 	metricsUrl="";     
  
   
 
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getApiJson() {
		return apiJson;
	}
	public void setApiJson(String apiJson) {
		this.apiJson = apiJson;
	}
	
	
	

	public String getApiJsonType() {
		return apiJsonType;
	}
	public void setApiJsonType(String apiJsonType) {
		this.apiJsonType = apiJsonType;
	}
	public String getMetricsUrl() {
		return metricsUrl;
	}
	public void setMetricsUrl(String metricsUrl) {
		this.metricsUrl = metricsUrl;
	}
	
   
   
   
    @Override  
    public Object clone() throws CloneNotSupportedException  
    {  
        return super.clone();  
    }
    
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ApiRouteInfo that = (ApiRouteInfo) o;
		return Objects.equals(isEnable_ssl(), that.isEnable_ssl()) &&
				Objects.equals(getServiceName(), that.getServiceName()) &&
				Objects.equals(version, that.version) &&
				Objects.equals(getUrl(), that.getUrl()) &&
				Objects.equals(apiJson, that.apiJson) &&
				Objects.equals(apiJsonType, that.apiJsonType) &&
				Objects.equals(metricsUrl, that.metricsUrl) &&
				Objects.equals(getControl(), that.getControl()) &&
				Objects.equals(getStatus(), that.getStatus()) &&
				Objects.equals(getVisualRange(), that.getVisualRange()) &&
				Objects.equals(getUseOwnUpstream(), that.getUseOwnUpstream()) &&
				Arrays.equals(getServers(), that.getServers()) &&
				Objects.equals(getHost(), that.getHost()) &&
				Objects.equals(getNamespace(), that.getNamespace()) &&
				Objects.equals(getPublish_port(), that.getPublish_port()) &&
				Objects.equals(getConsulServiceName(), that.getConsulServiceName()) &&
				Objects.equals(getPublishProtocol(), that.getPublishProtocol());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getServiceName(), version, getUrl(), apiJson, apiJsonType, metricsUrl, getControl(), getStatus(), getVisualRange(), getServers(), getHost(), getNamespace(), getPublish_port(), isEnable_ssl(), getConsulServiceName(), getPublishProtocol());
	}
}
