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
 Example Service:
	 {
	 "kind" : "service",
	 "apiVersion" : "v1",
	 "metadata" : {
	 "name" : "kubernetes",
	 "namespace" : "default",
	 "uid" : "0b6f198e-c6ab-11e6-86aa-fa163ee2118b",
	 "creationTimestamp" : "2016-12-20T11:54:21Z",
	 "labels" : {
	 "component" : "apiserver",
	 "provider" : "kubernetes"
	 },
	 "annotations" : {}
	 },
	 "spec" : {
	 "visualRange" : 0,
	 "url" : "",
	 "path" : "",
	 "publish_port" : "",
	 "host" : "",
	 "protocol" : "",
	 "lb_policy" : "",
	 "enable_ssl" : "0|1", //转发时，使用http还是http转发。http:0/https:1
	 "nodes" : [{
	 "ip" : 10.10.10.2,
	 "port" : 8080,
	 "ttl" :
	 }
	 ],
	 }
	 "status" : ""
	 }

     */

	/*
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServiceInfo that = (ServiceInfo) o;
		return Objects.equals(kind, that.kind) &&
				Objects.equals(apiVersion, that.apiVersion) &&
				Objects.equals(status, that.status) &&
				Objects.equals(metadata, that.metadata) &&
				Objects.equals(spec, that.spec);
	}

	@Override
	public int hashCode() {
		return Objects.hash(kind, apiVersion, status, metadata, spec);
	}
	*/
}
