/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.onap.msb.apiroute.api;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteInfo implements Serializable,Cloneable {
  private static final long serialVersionUID = 1L; 
  @ApiModelProperty(required = true)
  private String  serviceName; 
  
  @ApiModelProperty(value = "Target Service URL,start with /",example = "/test", required = true)
  private String  url;      
  
  @ApiModelProperty(value = "[control Range] 0：default   1：readonly  2：hidden ", allowableValues = "0,1,2", example = "0")
  private String  control="0";    
  
  @ApiModelProperty(value = "[status] 1：abled    0：disabled ", allowableValues = "0,1", example = "1")
  private String  status="1"; 
  
  @ApiModelProperty(value = "[visual Range]interSystem:0,inSystem:1", allowableValues = "0,1", example = "1")
  private String visualRange = "1"; 
 
  @ApiModelProperty(value = "[LB Policy]non_ip_hash:0,ip_hash:1", allowableValues = "0,1", example = "0")
  private String useOwnUpstream="0"; //lb policy   

  @ApiModelProperty(required = true)
  private RouteServer servers[]; 
  
  private String host="";
  
  private String namespace="";
  
  private String publish_port="";
  
  private boolean enable_ssl=false; //true:https:开启SSL加密,  false:http
  
  private String consulServiceName=""; 
  
  private String publishProtocol="http"; 
  
  
  
  public String getPublish_port() {
    return publish_port;
  }
  public void setPublish_port(String publish_port) {
    this.publish_port = publish_port;
  }

  

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }


public String getServiceName() {
      return serviceName;
  }

  public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
  }

  public String getUrl() {
      return url;
  }

  public void setUrl(String url) {
      this.url = url;
  }

  public RouteServer[] getServers() {
      return servers.clone();
  }

  public void setServers(RouteServer[] servers) {
      this.servers = servers.clone();
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

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }
  public String getConsulServiceName() {
    return consulServiceName;
  }
  public void setConsulServiceName(String consulServiceName) {
    this.consulServiceName = consulServiceName;
  }
  
  @Override  
  public Object clone() throws CloneNotSupportedException  
  {  
      return super.clone();  
  }
  public String getPublishProtocol() {
    return publishProtocol;
  }
  public void setPublishProtocol(String publishProtocol) {
    this.publishProtocol = publishProtocol;
  }
  public boolean isEnable_ssl() {
    return enable_ssl;
  }
  public void setEnable_ssl(boolean enable_ssl) {
    this.enable_ssl = enable_ssl;
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      RouteInfo that = (RouteInfo) o;
      return Objects.equals(enable_ssl, that.enable_ssl) &&
              Objects.equals(serviceName, that.serviceName) &&
              Objects.equals(url, that.url) &&
              Objects.equals(control, that.control) &&
              Objects.equals(status, that.status) &&
              Objects.equals(visualRange, that.visualRange) &&
              Objects.equals(useOwnUpstream, that.useOwnUpstream) &&
              Arrays.equals(servers, that.servers) &&
              Objects.equals(host, that.host) &&
              Objects.equals(namespace, that.namespace) &&
              Objects.equals(publish_port, that.publish_port) &&
              Objects.equals(consulServiceName, that.consulServiceName) &&
              Objects.equals(publishProtocol, that.publishProtocol);
  }

  @Override
  public int hashCode() {
      return Objects.hash(serviceName, url, control, status, visualRange, useOwnUpstream, servers, host, namespace, publish_port, enable_ssl, consulServiceName, publishProtocol);
  }
}
