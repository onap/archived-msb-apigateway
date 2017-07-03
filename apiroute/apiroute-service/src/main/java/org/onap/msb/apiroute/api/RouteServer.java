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


public class RouteServer implements Serializable{
	private static final long serialVersionUID = 1L;
	 @ApiModelProperty(required = true)
	private String ip;
	 
	 @ApiModelProperty(required = true) 
	private String port;
	private int weight=0;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public RouteServer(){
		
	}
	
	public RouteServer(String ip,String port){
		this.ip=ip;
		this.port=port;
		this.weight=0;
	}

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RouteServer that = (RouteServer) o;
		return Objects.equals(weight, that.weight) &&
				Objects.equals(ip, that.ip) &&
				Objects.equals(port, that.port);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ip, port, weight);
	}
}
