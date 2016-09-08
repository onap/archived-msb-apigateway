/**
 * Copyright 2016 2015-2016 ZTE, Inc. and others. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openo.msb.wrapper.consul.model.health;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Generated;

/**
 * Immutable implementation of {@link Node}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableNode.builder()}.
 */
@SuppressWarnings("all")
@Generated({"Immutables.generator", "Node"})
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ImmutableNode extends Node {
  private final String node;
  private final String address;

  private ImmutableNode(String node, String address) {
    this.node = node;
    this.address = address;
  }

  /**
   * @return The value of the {@code node} attribute
   */
  @JsonProperty(value = "Node")
  @Override
  public String getNode() {
    return node;
  }

  /**
   * @return The value of the {@code address} attribute
   */
  @JsonProperty(value = "Address")
  @Override
  public String getAddress() {
    return address;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link Node#getNode() node} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for node
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableNode withNode(String value) {
    if (this.node.equals(value)) return this;
    return new ImmutableNode(Preconditions.checkNotNull(value, "node"), this.address);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link Node#getAddress() address} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for address
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableNode withAddress(String value) {
    if (this.address.equals(value)) return this;
    return new ImmutableNode(this.node, Preconditions.checkNotNull(value, "address"));
  }

  /**
   * This instance is equal to all instances of {@code ImmutableNode} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableNode
        && equalTo((ImmutableNode) another);
  }

  private boolean equalTo(ImmutableNode another) {
    return node.equals(another.node)
        && address.equals(another.address);
  }

  /**
   * Computes a hash code from attributes: {@code node}, {@code address}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + node.hashCode();
    h = h * 17 + address.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code Node...} with all non-generated
   * and non-auxiliary attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("Node")
        .add("node", node)
        .add("address", address)
        .toString();
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonDeserialize
  static final class Json extends Node {
    String node;
    String address;
    @JsonProperty(value = "Node")
    public void setNode(String node) {
      this.node = node;
    }
    @JsonProperty(value = "Address")
    public void setAddress(String address) {
      this.address = address;
    }
    @Override
    public String getNode() { throw new UnsupportedOperationException(); }
    @Override
    public String getAddress() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator
  static ImmutableNode fromJson(Json json) {
    ImmutableNode.Builder builder = ImmutableNode.builder();
    if (json.node != null) {
      builder.node(json.node);
    }
    if (json.address != null) {
      builder.address(json.address);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link Node} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable Node instance
   */
  public static ImmutableNode copyOf(Node instance) {
    if (instance instanceof ImmutableNode) {
      return (ImmutableNode) instance;
    }
    return ImmutableNode.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableNode ImmutableNode}.
   * @return A new ImmutableNode builder
   */
  public static ImmutableNode.Builder builder() {
    return new ImmutableNode.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableNode ImmutableNode}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  public static final class Builder {
    private static final long INIT_BIT_NODE = 0x1L;
    private static final long INIT_BIT_ADDRESS = 0x2L;
    private long initBits = 0x3;

    private String node;
    private String address;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code Node} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(Node instance) {
      Preconditions.checkNotNull(instance, "instance");
      node(instance.getNode());
      address(instance.getAddress());
      return this;
    }

    /**
     * Initializes the value for the {@link Node#getNode() node} attribute.
     * @param node The value for node 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder node(String node) {
      this.node = Preconditions.checkNotNull(node, "node");
      initBits &= ~INIT_BIT_NODE;
      return this;
    }

    /**
     * Initializes the value for the {@link Node#getAddress() address} attribute.
     * @param address The value for address 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder address(String address) {
      this.address = Preconditions.checkNotNull(address, "address");
      initBits &= ~INIT_BIT_ADDRESS;
      return this;
    }

    /**
     * Builds a new {@link ImmutableNode ImmutableNode}.
     * @return An immutable instance of Node
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableNode build() throws IllegalStateException {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableNode(node, address);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_NODE) != 0) attributes.add("node");
      if ((initBits & INIT_BIT_ADDRESS) != 0) attributes.add("address");
      return "Cannot build Node, some of required attributes are not set " + attributes;
    }
  }
}
