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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Node {
    private String ip;
    private String port;
    private int ttl = -1;

    /*
     * @Override public boolean equals(Object o) { if (this == o) return true; if (o == null ||
     * getClass() != o.getClass()) return false; Node node = (Node) o; return Objects.equals(port,
     * node.port) && Objects.equals(ttl, node.ttl) && Objects.equals(ip, node.ip); }
     * 
     * @Override public int hashCode() { return Objects.hash(ip, port, ttl); }
     */
}
