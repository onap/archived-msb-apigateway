/**
* Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.openo.msb.wrapper.consul.model.catalog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.openo.msb.wrapper.consul.model.health.Node;
import org.openo.msb.wrapper.consul.model.health.Service;

/**
 * Immutable implementation of {@link CatalogNode}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableCatalogNode.builder()}.
 */
@SuppressWarnings("all")
@Generated({"Immutables.generator", "CatalogNode"})
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ImmutableCatalogNode extends CatalogNode {
  private final Node node;
  private final ImmutableMap<String, Service> services;

  private ImmutableCatalogNode(
      Node node,
      ImmutableMap<String, Service> services) {
    this.node = node;
    this.services = services;
  }

  /**
   * @return The value of the {@code node} attribute
   */
  @JsonProperty(value = "Node")
  @Override
  public Node getNode() {
    return node;
  }

  /**
   * @return The value of the {@code services} attribute
   */
  @JsonProperty(value = "Services")
  @Override
  public ImmutableMap<String, Service> getServices() {
    return services;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link CatalogNode#getNode() node} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for node
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableCatalogNode withNode(Node value) {
    if (this.node == value) return this;
    return new ImmutableCatalogNode(Preconditions.checkNotNull(value, "node"), this.services);
  }

  /**
   * Copy the current immutable object by replacing the {@link CatalogNode#getServices() services} map with the specified map.
   * Nulls are not permitted as keys or values.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param entries The entries to be added to the services map
   * @return A modified copy of {@code this} object
   */
  public final ImmutableCatalogNode withServices(Map<String, ? extends Service> entries) {
    if (this.services == entries) return this;
    ImmutableMap<String, Service> value = ImmutableMap.copyOf(entries);
    return new ImmutableCatalogNode(this.node, value);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableCatalogNode} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableCatalogNode
        && equalTo((ImmutableCatalogNode) another);
  }

  private boolean equalTo(ImmutableCatalogNode another) {
    return node.equals(another.node)
        && services.equals(another.services);
  }

  /**
   * Computes a hash code from attributes: {@code node}, {@code services}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + node.hashCode();
    h = h * 17 + services.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code CatalogNode...} with all non-generated
   * and non-auxiliary attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("CatalogNode")
        .add("node", node)
        .add("services", services)
        .toString();
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonDeserialize
  static final class Json extends CatalogNode {
    Node node;
    Map<String, Service> services;
    @JsonProperty(value = "Node")
    public void setNode(Node node) {
      this.node = node;
    }
    @JsonProperty(value = "Services")
    public void setServices(Map<String, Service> services) {
      this.services = services;
    }
    @Override
    public Node getNode() { throw new UnsupportedOperationException(); }
    @Override
    public Map<String, Service> getServices() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator
  static ImmutableCatalogNode fromJson(Json json) {
    ImmutableCatalogNode.Builder builder = ImmutableCatalogNode.builder();
    if (json.node != null) {
      builder.node(json.node);
    }
    if (json.services != null) {
      builder.putAllServices(json.services);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link CatalogNode} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable CatalogNode instance
   */
  public static ImmutableCatalogNode copyOf(CatalogNode instance) {
    if (instance instanceof ImmutableCatalogNode) {
      return (ImmutableCatalogNode) instance;
    }
    return ImmutableCatalogNode.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableCatalogNode ImmutableCatalogNode}.
   * @return A new ImmutableCatalogNode builder
   */
  public static ImmutableCatalogNode.Builder builder() {
    return new ImmutableCatalogNode.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableCatalogNode ImmutableCatalogNode}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  public static final class Builder {
    private static final long INIT_BIT_NODE = 0x1L;
    private long initBits = 0x1;

    private Node node;
    private ImmutableMap.Builder<String, Service> servicesBuilder = ImmutableMap.builder();

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code CatalogNode} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(CatalogNode instance) {
      Preconditions.checkNotNull(instance, "instance");
      node(instance.getNode());
      putAllServices(instance.getServices());
      return this;
    }

    /**
     * Initializes the value for the {@link CatalogNode#getNode() node} attribute.
     * @param node The value for node 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder node(Node node) {
      this.node = Preconditions.checkNotNull(node, "node");
      initBits &= ~INIT_BIT_NODE;
      return this;
    }

    /**
     * Put one entry to the {@link CatalogNode#getServices() services} map.
     * @param key The key in the services map
     * @param value The associated value in the services map
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder putServices(String key, Service value) {
      servicesBuilder.put(key, value);
      return this;
    }

    /**
     * Put one entry to the {@link CatalogNode#getServices() services} map. Nulls are not permitted
     * @param entry The key and value entry
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder putServices(Map.Entry<String, ? extends Service> entry) {
      servicesBuilder.put(entry);
      return this;
    }

    /**
     * Sets or replaces all mappings from the specified map as entries for the {@link CatalogNode#getServices() services} map. Nulls are not permitted
     * @param entries The entries that will be added to the services map
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder services(Map<String, ? extends Service> entries) {
      servicesBuilder = ImmutableMap.builder();
      return putAllServices(entries);
    }

    /**
     * Put all mappings from the specified map as entries to {@link CatalogNode#getServices() services} map. Nulls are not permitted
     * @param entries The entries that will be added to the services map
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder putAllServices(Map<String, ? extends Service> entries) {
      servicesBuilder.putAll(entries);
      return this;
    }

    /**
     * Builds a new {@link ImmutableCatalogNode ImmutableCatalogNode}.
     * @return An immutable instance of CatalogNode
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableCatalogNode build() throws IllegalStateException {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableCatalogNode(node, servicesBuilder.build());
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_NODE) != 0) attributes.add("node");
      return "Cannot build CatalogNode, some of required attributes are not set " + attributes;
    }
  }
}
