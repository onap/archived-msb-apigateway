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

public class Node implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(required = true)
    private String ip;
    
    @ApiModelProperty(required = true)
    private String port;
    
    private String status="passing"; //实例健康检查状态
    
    private int ttl=-1;
  
    public String getStatus() {
      return status;
  }

  public void setStatus(String status) {
      this.status = status;
  }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public Node(){
        
    }
    
    public Node(String ip,String port,int ttl){
        this.ip = ip;
        this.port = port;
        this.ttl = ttl;
    }
    
    public Node(String ip,String port){
      this.ip = ip;
      this.port = port;
  }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(ttl, node.ttl) &&
                Objects.equals(ip, node.ip) &&
                Objects.equals(port, node.port) &&
                Objects.equals(status, node.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, status, ttl);
    }
}
