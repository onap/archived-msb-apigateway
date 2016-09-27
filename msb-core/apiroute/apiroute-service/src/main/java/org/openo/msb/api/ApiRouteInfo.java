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

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


public class ApiRouteInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(required = true)
	private String  serviceName;  	
	
	@ApiModelProperty(example = "v1", required = true)
	private String  version;  
	
	@ApiModelProperty(value = "Target Service URL,start with /",example = "/test", required = true)
	private String 	url;       
	
	private String  apiJson="";  //swagger json Path
	
	 @ApiModelProperty(value = "[apiJson Type] 0：local file  1： remote file", allowableValues = "0,1", example = "1")
	private String apiJsonType="1";  
	private String 	metricsUrl="";     
    
	@ApiModelProperty(value = "[control Range] 0：default   1：readonly  2：hidden ", allowableValues = "0,1,2", example = "0")
	private String 	control="0";     
    
	@ApiModelProperty(value = "[status] 1：abled    0：disabled ", allowableValues = "0,1", example = "1")
	private String  status="1"; 
   
	@ApiModelProperty(value = "[visual Range]interSystem:0,inSystem:1", allowableValues = "0,1", example = "1")
	private String visualRange = "1";   
   
	@ApiModelProperty(value = "[LB Policy]non_ip_hash:0,ip_hash:1", allowableValues = "0,1", example = "0")
    private String useOwnUpstream="0"; 
	
    @ApiModelProperty(required = true)
	private RouteServer servers[]; 
	
	
	
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
	
	public String getApiJson() {
		return apiJson;
	}
	public void setApiJson(String apiJson) {
		this.apiJson = apiJson;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public RouteServer[] getServers() {
		return servers;
	}
	public void setServers(RouteServer[] servers) {
		this.servers = servers;
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
	public String getControl() {
		return control;
	}
	public void setControl(String control) {
		this.control = control;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
    public String getVisualRange() {
        return visualRange;
    }
    public void setVisualRange(String visualRange) {
        this.visualRange = visualRange;
    }
    public String getUseOwnUpstream() {
        return useOwnUpstream;
    }

    public void setUseOwnUpstream(String useOwnUpstream) {
        this.useOwnUpstream = useOwnUpstream;
    }


	
	

}
