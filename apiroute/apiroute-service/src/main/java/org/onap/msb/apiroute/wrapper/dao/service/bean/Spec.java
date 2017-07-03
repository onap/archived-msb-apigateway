package org.onap.msb.apiroute.wrapper.dao.service.bean;

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
    private String path = "";
    private String publish_port;
    private String host = "";
    private String protocol = "";
    private String lb_policy = "";
    private boolean enable_ssl = false;
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
                Objects.equals(path, spec.path) &&
                Objects.equals(publish_port, spec.publish_port) &&
                Objects.equals(host, spec.host) &&
                Objects.equals(protocol, spec.protocol) &&
                Objects.equals(lb_policy, spec.lb_policy) &&
                Arrays.equals(nodes, spec.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visualRange, url, path, publish_port, host, protocol, lb_policy, enable_ssl, nodes);
    }
    */
}
