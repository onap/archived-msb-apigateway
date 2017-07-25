package org.onap.msb.apiroute.wrapper.dao.route.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Spec {
    private String visualRange = "";
    private String url = "";
    private String publish_port;
    private String host = "";
    private String apijson = "";
    private String apijsontype = "";
    private String metricsUrl = "";
    private String consulServiceName = "";
    private String useOwnUpstream = "";
    private String publish_protocol = "";
    private boolean enable_ssl = false;
    private String control = "";
    private Node[] nodes;

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spec spec = (Spec) o;
        return Objects.equals(enable_ssl, spec.enable_ssl) &&
                Objects.equals(visualRange, spec.visualRange) &&
                Objects.equals(url, spec.url) &&
                Objects.equals(publish_port, spec.publish_port) &&
                Objects.equals(host, spec.host) &&
                Objects.equals(apijson, spec.apijson) &&
                Objects.equals(apijsontype, spec.apijsontype) &&
                Objects.equals(metricsUrl, spec.metricsUrl) &&
                Objects.equals(consulServiceName, spec.consulServiceName) &&
                Objects.equals(useOwnUpstream, spec.useOwnUpstream) &&
                Objects.equals(publish_protocol, spec.publish_protocol) &&
                Objects.equals(control, spec.control) &&
                Arrays.equals(nodes, spec.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visualRange, url, publish_port, host, apijson, apijsontype, metricsUrl, consulServiceName, useOwnUpstream, publish_protocol, enable_ssl, control, nodes);
    }
    */
}
