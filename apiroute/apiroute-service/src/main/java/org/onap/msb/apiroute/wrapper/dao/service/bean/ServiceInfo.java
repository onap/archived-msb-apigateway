/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
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
package org.onap.msb.apiroute.wrapper.dao.service.bean;

import org.onap.msb.apiroute.wrapper.dao.DAOConstants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServiceInfo {
    private String kind = DAOConstants.SERVICE_KIND;
    private String apiVersion = "";
    private String status = "";
    private Metadata metadata;
    private Spec spec;

    /**
     * Example Service: { "kind" : "service", "apiVersion" : "v1", "metadata" : { "name" :
     * "kubernetes", "namespace" : "default", "uid" : "0b6f198e-c6ab-11e6-86aa-fa163ee2118b",
     * "creationTimestamp" : "2016-12-20T11:54:21Z", "labels" : { "component" : "apiserver",
     * "provider" : "kubernetes" }, "annotations" : {} }, "spec" : { "visualRange" : 0, "url" : "",
     * "path" : "", "publish_port" : "", "host" : "", "protocol" : "", "lb_policy" : "",
     * "enable_ssl" : "0|1", //转发时，使用http还是http转发。http:0/https:1 "nodes" : [{ "ip" : 10.10.10.2,
     * "port" : 8080, "ttl" : } ], } "status" : "" }
     * 
     */

    /*
     * @Override public boolean equals(Object o) { if (this == o) return true; if (o == null ||
     * getClass() != o.getClass()) return false; ServiceInfo that = (ServiceInfo) o; return
     * Objects.equals(kind, that.kind) && Objects.equals(apiVersion, that.apiVersion) &&
     * Objects.equals(status, that.status) && Objects.equals(metadata, that.metadata) &&
     * Objects.equals(spec, that.spec); }
     * 
     * @Override public int hashCode() { return Objects.hash(kind, apiVersion, status, metadata,
     * spec); }
     */
}
