package org.onap.msb.apiroute.wrapper.dao.route.bean;

import org.onap.msb.apiroute.wrapper.dao.DAOConstants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RouteInfo {
    private String kind = DAOConstants.ROUTE_KIND;
    private String apiVersion = "";
    private String status = "";
    private Metadata metadata;
    private Spec spec;

    /**
 Example route:
 {
	"kind" : "route",
	"apiVersion" : "v1",
     "status" : "1"
	"metadata" : {
		"name" : "kubernetes",
		"namespace" : "default",
		"uid" : "0b6f198e-c6ab-11e6-86aa-fa163ee2118b",
		"creationTimestamp" : "2016-12-20T11:54:21Z",
		"updateTimestamp" : "",
		"labels" : {
			"component" : "apiserver",
			"provider" : "kubernetes"
		},
		"annotations" : {}
	},
	"spec" : {
		"visualRange" : 0,
		"url" : "",
		"publish_port" : "",
		"host" : "",
		"apijson" : "",
		"apijsontype" : ""
		"metricsUrl" : ""
		"consulServiceName" : ""
		"useOwnUpstream" : "" //是否使用该服务独立的upstream转发
		"publishProtocol" : "", //发布地址使用http还是http协议
		"enable_ssl" : "0|1", //转发时，使用http还是http转发。http:0/https:1
		"controll" : "", //是否可以修改
		"nodes" : [{
				"ip" : 10.10.10.2,
				"port" : 8080,
				"weight" : ""
			}
		],
	}
}
     */
	/*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteInfo that = (RouteInfo) o;
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
