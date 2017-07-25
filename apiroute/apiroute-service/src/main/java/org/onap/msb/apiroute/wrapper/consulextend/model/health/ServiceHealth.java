package org.onap.msb.apiroute.wrapper.consulextend.model.health;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.model.health.Node;

import org.immutables.value.Value;

import java.util.List;
@Value.Immutable
@JsonSerialize(as = ImmutableServiceHealth.class)
@JsonDeserialize(as = ImmutableServiceHealth.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ServiceHealth {

    @JsonProperty("Node")
    public abstract Node getNode();

    @JsonProperty("Service")
    public abstract Service getService();

    @JsonProperty("Checks")
    @JsonDeserialize(as = ImmutableList.class, contentAs = HealthCheck.class)
    public abstract List<HealthCheck> getChecks();
    
}
