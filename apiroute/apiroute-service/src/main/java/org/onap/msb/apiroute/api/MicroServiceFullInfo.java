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
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MicroServiceFullInfo  implements Serializable {
    private static final long serialVersionUID = 1L;
 

    @ApiModelProperty(required = true)
    private String serviceName;
   
    @ApiModelProperty(example = "v1")
    private String version="";
   
    @ApiModelProperty(value = "Target Service URL,start with /",example = "/api/serviceName/v1", required = true)
    private String url="";
   
    @ApiModelProperty(value = "Service Protocol", allowableValues = "REST,UI, MQ, FTP,SNMP,TCP,UDP", example = "REST",required = true)
    private String protocol = "";
    
    @ApiModelProperty(value = "[visual Range]interSystem:0,inSystem:1", allowableValues = "0,1", example = "1")
    private String visualRange = "1";
   
    @ApiModelProperty(value = "lb policy", allowableValues = "round-robin,hash,least_conn", example = "hash")
    private String lb_policy="";
    
    private String namespace="";
    
    private String host="";
    
    private String path="";
    
    private String publish_port="";
    
    @ApiModelProperty(value = "enable ssl", allowableValues = "true,false", example = "false")
    private boolean enable_ssl=false; //true:https:开启SSL加密,  false:http
  
    private String custom; //PORTAL协议标志
 
    private Set<Node> nodes;
    
    @ApiModelProperty(value = "Service Status", allowableValues = "0,1", example = "1")
    private String status = "1";  //0:disable 1:enable
    

   
    
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

    public String getNamespace() {
      return namespace;
    }

    public void setNamespace(String namespace) {
      this.namespace = namespace;
    }

    
    public String getHost() {
      return host;
    }
    public void setHost(String host) {
      this.host = host;
    }
    public String getPath() {
      return path;
    }
    public void setPath(String path) {
      this.path = path;
    }
    
    

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }
    
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getPublish_port() {
      return publish_port;
    }
    public void setPublish_port(String publish_port) {
      this.publish_port = publish_port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MicroServiceFullInfo that = (MicroServiceFullInfo) o;
        return Objects.equals(serviceName, that.serviceName) &&
                Objects.equals(version, that.version) &&
                Objects.equals(url, that.url) &&
                Objects.equals(protocol, that.protocol) &&
                Objects.equals(visualRange, that.visualRange) &&
                Objects.equals(lb_policy, that.lb_policy) &&
                Objects.equals(namespace, that.namespace) &&
                Objects.equals(host, that.host) &&
                Objects.equals(path, that.path) &&
                Objects.equals(publish_port, that.publish_port) &&
                Objects.equals(enable_ssl, that.enable_ssl) &&
                Objects.equals(nodes, that.nodes) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, version, url, protocol, visualRange, lb_policy, namespace, host, path, publish_port, enable_ssl, nodes, status);
    }
    public boolean isEnable_ssl() {
      return enable_ssl;
    }
    public void setEnable_ssl(boolean enable_ssl) {
      this.enable_ssl = enable_ssl;
    }
    public String getCustom() {
      return custom;
    }
    public void setCustom(String custom) {
      this.custom = custom;
    }
  
 
}