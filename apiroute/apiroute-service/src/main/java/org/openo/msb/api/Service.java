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
package org.openo.msb.api;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Service<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    // 服务名
    @ApiModelProperty(required = true)
    private String serviceName;
    // 版本号
    @ApiModelProperty(example = "v1")
    private String version="";
    // 服务url
    @ApiModelProperty(value = "Target Service URL,start with /",example = "/api/serviceName/v1", required = true)
    private String url="";
    // 服务对应协议，比如REST、UI、MQ、FTP、SNMP、TCP、UDP
    @ApiModelProperty(value = "Service Protocol", allowableValues = "REST,UI, MQ, FTP,SNMP,TCP,UDP", example = "REST",required = true)
    private String protocol = "";
    
    //服务的可见范围   系统间:0   系统内:1 
    @ApiModelProperty(value = "[visual Range]interSystem:0,inSystem:1", allowableValues = "0,1", example = "1")
    private String visualRange = "1";
   
    //负载均衡策略类型
    @ApiModelProperty(value = "lb policy", allowableValues = "round-robin,hash,least_conn", example = "hash")
    private String lb_policy="";
   
    @ApiModelProperty(required = true)
    private Set<T> nodes;

    public Set<T> getNodes() {
        return nodes;
    }

    public void setNodes(Set<T> nodes) {
        this.nodes = nodes;
    }
    
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
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getProtocol() {
        return protocol;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public String getVisualRange() {
        return visualRange;
    }

    public void setVisualRange(String visualRange) {
        this.visualRange = visualRange;
    }
    

    public String getLb_policy() {
        return lb_policy;
    }

    public void setLb_policy(String lb_policy) {
        this.lb_policy = lb_policy;
    }

    
    
}
