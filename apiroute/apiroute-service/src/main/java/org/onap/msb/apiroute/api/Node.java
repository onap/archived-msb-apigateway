/*******************************************************************************
 * Copyright 2016-2018 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.onap.msb.apiroute.api;

import java.io.Serializable;
import java.util.Objects;

import io.swagger.annotations.ApiModelProperty;

public class Node implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(required = true)
    private String ip;

    @ApiModelProperty(required = true)
    private String port;

    private String status = "passing"; 

    private int ttl = -1;

    // health check type, allowableValues = "HTTP,TCP", example = "HTTP")
    private String checkType = "";
    // health check url, example for http "http://192.168.0.2:80/heallth", example for tcp
    // "192.168.1.100:80"
    private String checkUrl = "";

    // TCP or HTTP health check Interval,Unit: second", example = "10s"
    private String checkInterval = "10s";

    // TCP or HTTP health check TimeOut,Unit: second", example = "10s"
    private String checkTimeOut = "10s";

    private Boolean tls_skip_verify = true;
    /**
     * @return the checkType
     */
    public String getCheckType() {
        return checkType;
    }

    /**
     * @param checkType the checkType to set
     */
    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    /**
     * @return the checkUrl
     */
    public String getCheckUrl() {
        return checkUrl;
    }

    /**
     * @param checkUrl the checkUrl to set
     */
    public void setCheckUrl(String checkUrl) {
        this.checkUrl = checkUrl;
    }

    /**
     * @return the checkInterval
     */
    public String getCheckInterval() {
        return checkInterval;
    }

    /**
     * @param checkInterval the checkInterval to set
     */
    public void setCheckInterval(String checkInterval) {
        this.checkInterval = checkInterval;
    }

    /**
     * @return the checkTimeOut
     */
    public String getCheckTimeOut() {
        return checkTimeOut;
    }

    /**
     * @param checkTimeOut the checkTimeOut to set
     */
    public void setCheckTimeOut(String checkTimeOut) {
        this.checkTimeOut = checkTimeOut;
    }

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

    public Node() {

    }

    public Node(String ip, String port, int ttl) {
        this.ip = ip;
        this.port = port;
        this.ttl = ttl;
    }

    public Node(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Node node = (Node) o;
        return Objects.equals(ttl, node.ttl) && Objects.equals(ip, node.ip) && Objects.equals(port, node.port)
                        && Objects.equals(status, node.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, status, ttl);
    }
}
