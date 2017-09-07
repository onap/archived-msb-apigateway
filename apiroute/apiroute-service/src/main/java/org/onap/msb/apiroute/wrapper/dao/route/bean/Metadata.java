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
package org.onap.msb.apiroute.wrapper.dao.route.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Metadata {
    private String name;
    private String namespace;
    private String uid = "";
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    //private Date creationTimestamp;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private Date updateTimestamp;
    private Map labels = new HashMap();
    private String[] annotations = null;

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(name, metadata.name) &&
                Objects.equals(namespace, metadata.namespace) &&
                Objects.equals(uid, metadata.uid) &&
                //Objects.equals(creationTimestamp, metadata.creationTimestamp) &&
                Objects.equals(updateTimestamp, metadata.updateTimestamp) &&
                Objects.equals(labels, metadata.labels) &&
                Objects.equals(annotations, metadata.annotations);
    }

    @Override
    public int hashCode() {
        //return Objects.hash(name, namespace, uid, creationTimestamp, updateTimestamp, labels, annotations);
        return Objects.hash(name, namespace, uid, updateTimestamp, labels, annotations);
    }
    */
}
