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
package org.onap.msb.apiroute.wrapper.consulextend.model.health;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableService.class)
@JsonDeserialize(as = ImmutableService.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Service {

    @JsonProperty("ID")
    public abstract String getId();

    @JsonProperty("Service")
    public abstract String getService();

    @JsonProperty("Tags")
    @JsonDeserialize(as = ImmutableList.class, contentAs = String.class)
    public abstract List<String> getTags();

    @JsonProperty("Address")
    public abstract String getAddress();

    @JsonProperty("Port")
    public abstract int getPort();

    @JsonProperty("CreateIndex")
    public abstract int getCreateIndex();

    @JsonProperty("ModifyIndex")
    public abstract int getModifyIndex();
}
