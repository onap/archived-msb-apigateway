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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Generated;

/**
 * Immutable implementation of {@link CatalogService}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableCatalogService.builder()}.
 */
@SuppressWarnings("all")
@Generated({"Immutables.generator", "CatalogService"})
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ImmutableCatalogService extends CatalogService {
  private final String node;
  private final String address;
  private final String serviceName;
  private final String serviceId;
  private final String serviceAddress;
  private final int servicePort;
  private final ImmutableList<String> serviceTags;

  private ImmutableCatalogService(
      String node,
      String address,
      String serviceName,
      String serviceId,
      String serviceAddress,
      int servicePort,
      ImmutableList<String> serviceTags) {
    this.node = node;
    this.address = address;
    this.serviceName = serviceName;
    this.serviceId = serviceId;
    this.serviceAddress = serviceAddress;
    this.servicePort = servicePort;
    this.serviceTags = serviceTags;
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
   * @return The value of the {@code serviceName} attribute
   */
  @JsonProperty(value = "ServiceName")
  @Override
  public String getServiceName() {
    return serviceName;
  }

  /**
   * @return The value of the {@code serviceId} attribute
   */
  @JsonProperty(value = "ServiceID")
  @Override
  public String getServiceId() {
    return serviceId;
  }

  /**
   * @return The value of the {@code serviceAddress} attribute
   */
  @JsonProperty(value = "ServiceAddress")
  @Override
  public String getServiceAddress() {
    return serviceAddress;
  }

  /**
   * @return The value of the {@code servicePort} attribute
   */
  @JsonProperty(value = "ServicePort")
  @Override
  public int getServicePort() {
    return servicePort;
  }

  /**
   * @return The value of the {@code serviceTags} attribute
   */
  @JsonProperty(value = "ServiceTags")
  @Override
  public ImmutableList<String> getServiceTags() {
    return serviceTags;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link CatalogService#getNode() node} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for node
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableCatalogService withNode(String value) {
    if (this.node.equals(value)) return this;
    return new ImmutableCatalogService(
        Preconditions.checkNotNull(value, "node"),
        this.address,
        this.serviceName,
        this.serviceId,
        this.serviceAddress,
        this.servicePort,
        this.serviceTags);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link CatalogService#getAddress() address} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for address
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableCatalogService withAddress(String value) {
    if (this.address.equals(value)) return this;
    return new ImmutableCatalogService(
        this.node,
        Preconditions.checkNotNull(value, "address"),
        this.serviceName,
        this.serviceId,
        this.serviceAddress,
        this.servicePort,
        this.serviceTags);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link CatalogService#getServiceName() serviceName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for serviceName
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableCatalogService withServiceName(String value) {
    if (this.serviceName.equals(value)) return this;
    return new ImmutableCatalogService(
        this.node,
        this.address,
        Preconditions.checkNotNull(value, "serviceName"),
        this.serviceId,
        this.serviceAddress,
        this.servicePort,
        this.serviceTags);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link CatalogService#getServiceId() serviceId} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for serviceId
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableCatalogService withServiceId(String value) {
    if (this.serviceId.equals(value)) return this;
    return new ImmutableCatalogService(
        this.node,
        this.address,
        this.serviceName,
        Preconditions.checkNotNull(value, "serviceId"),
        this.serviceAddress,
        this.servicePort,
        this.serviceTags);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link CatalogService#getServiceAddress() serviceAddress} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for serviceAddress
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableCatalogService withServiceAddress(String value) {
    if (this.serviceAddress.equals(value)) return this;
    return new ImmutableCatalogService(
        this.node,
        this.address,
        this.serviceName,
        this.serviceId,
        Preconditions.checkNotNull(value, "serviceAddress"),
        this.servicePort,
        this.serviceTags);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link CatalogService#getServicePort() servicePort} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for servicePort
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableCatalogService withServicePort(int value) {
    if (this.servicePort == value) return this;
    return new ImmutableCatalogService(
        this.node,
        this.address,
        this.serviceName,
        this.serviceId,
        this.serviceAddress,
        value,
        this.serviceTags);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link CatalogService#getServiceTags() serviceTags}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableCatalogService withServiceTags(String... elements) {
    ImmutableList<String> newValue = ImmutableList.copyOf(elements);
    return new ImmutableCatalogService(
        this.node,
        this.address,
        this.serviceName,
        this.serviceId,
        this.serviceAddress,
        this.servicePort,
        newValue);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link CatalogService#getServiceTags() serviceTags}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of serviceTags elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableCatalogService withServiceTags(Iterable<String> elements) {
    if (this.serviceTags == elements) return this;
    ImmutableList<String> newValue = ImmutableList.copyOf(elements);
    return new ImmutableCatalogService(
        this.node,
        this.address,
        this.serviceName,
        this.serviceId,
        this.serviceAddress,
        this.servicePort,
        newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableCatalogService} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableCatalogService
        && equalTo((ImmutableCatalogService) another);
  }

  private boolean equalTo(ImmutableCatalogService another) {
    return node.equals(another.node)
        && address.equals(another.address)
        && serviceName.equals(another.serviceName)
        && serviceId.equals(another.serviceId)
        && serviceAddress.equals(another.serviceAddress)
        && servicePort == another.servicePort
        && serviceTags.equals(another.serviceTags);
  }

  /**
   * Computes a hash code from attributes: {@code node}, {@code address}, {@code serviceName}, {@code serviceId}, {@code serviceAddress}, {@code servicePort}, {@code serviceTags}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + node.hashCode();
    h = h * 17 + address.hashCode();
    h = h * 17 + serviceName.hashCode();
    h = h * 17 + serviceId.hashCode();
    h = h * 17 + serviceAddress.hashCode();
    h = h * 17 + servicePort;
    h = h * 17 + serviceTags.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code CatalogService...} with all non-generated
   * and non-auxiliary attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("CatalogService")
        .add("node", node)
        .add("address", address)
        .add("serviceName", serviceName)
        .add("serviceId", serviceId)
        .add("serviceAddress", serviceAddress)
        .add("servicePort", servicePort)
        .add("serviceTags", serviceTags)
        .toString();
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonDeserialize
  static final class Json extends CatalogService {
    String node;
    String address;
    String serviceName;
    String serviceId;
    String serviceAddress;
    Integer servicePort;
    List<String> serviceTags = ImmutableList.of();
    @JsonProperty(value = "Node")
    public void setNode(String node) {
      this.node = node;
    }
    @JsonProperty(value = "Address")
    public void setAddress(String address) {
      this.address = address;
    }
    @JsonProperty(value = "ServiceName")
    public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
    }
    @JsonProperty(value = "ServiceID")
    public void setServiceId(String serviceId) {
      this.serviceId = serviceId;
    }
    @JsonProperty(value = "ServiceAddress")
    public void setServiceAddress(String serviceAddress) {
      this.serviceAddress = serviceAddress;
    }
    @JsonProperty(value = "ServicePort")
    public void setServicePort(int servicePort) {
      this.servicePort = servicePort;
    }
    @JsonProperty(value = "ServiceTags")
    public void setServiceTags(List<String> serviceTags) {
      this.serviceTags = serviceTags;
    }
    @Override
    public String getNode() { throw new UnsupportedOperationException(); }
    @Override
    public String getAddress() { throw new UnsupportedOperationException(); }
    @Override
    public String getServiceName() { throw new UnsupportedOperationException(); }
    @Override
    public String getServiceId() { throw new UnsupportedOperationException(); }
    @Override
    public String getServiceAddress() { throw new UnsupportedOperationException(); }
    @Override
    public int getServicePort() { throw new UnsupportedOperationException(); }
    @Override
    public List<String> getServiceTags() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator
  static ImmutableCatalogService fromJson(Json json) {
    ImmutableCatalogService.Builder builder = ImmutableCatalogService.builder();
    if (json.node != null) {
      builder.node(json.node);
    }
    if (json.address != null) {
      builder.address(json.address);
    }
    if (json.serviceName != null) {
      builder.serviceName(json.serviceName);
    }
    if (json.serviceId != null) {
      builder.serviceId(json.serviceId);
    }
    if (json.serviceAddress != null) {
      builder.serviceAddress(json.serviceAddress);
    }
    if (json.servicePort != null) {
      builder.servicePort(json.servicePort);
    }
    if (json.serviceTags != null) {
      builder.addAllServiceTags(json.serviceTags);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link CatalogService} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable CatalogService instance
   */
  public static ImmutableCatalogService copyOf(CatalogService instance) {
    if (instance instanceof ImmutableCatalogService) {
      return (ImmutableCatalogService) instance;
    }
    return ImmutableCatalogService.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableCatalogService ImmutableCatalogService}.
   * @return A new ImmutableCatalogService builder
   */
  public static ImmutableCatalogService.Builder builder() {
    return new ImmutableCatalogService.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableCatalogService ImmutableCatalogService}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  public static final class Builder {
    private static final long INIT_BIT_NODE = 0x1L;
    private static final long INIT_BIT_ADDRESS = 0x2L;
    private static final long INIT_BIT_SERVICE_NAME = 0x4L;
    private static final long INIT_BIT_SERVICE_ID = 0x8L;
    private static final long INIT_BIT_SERVICE_ADDRESS = 0x10L;
    private static final long INIT_BIT_SERVICE_PORT = 0x20L;
    private long initBits = 0x3f;

    private String node;
    private String address;
    private String serviceName;
    private String serviceId;
    private String serviceAddress;
    private int servicePort;
    private ImmutableList.Builder<String> serviceTagsBuilder = ImmutableList.builder();

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code CatalogService} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(CatalogService instance) {
      Preconditions.checkNotNull(instance, "instance");
      node(instance.getNode());
      address(instance.getAddress());
      serviceName(instance.getServiceName());
      serviceId(instance.getServiceId());
      serviceAddress(instance.getServiceAddress());
      servicePort(instance.getServicePort());
      addAllServiceTags(instance.getServiceTags());
      return this;
    }

    /**
     * Initializes the value for the {@link CatalogService#getNode() node} attribute.
     * @param node The value for node 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder node(String node) {
      this.node = Preconditions.checkNotNull(node, "node");
      initBits &= ~INIT_BIT_NODE;
      return this;
    }

    /**
     * Initializes the value for the {@link CatalogService#getAddress() address} attribute.
     * @param address The value for address 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder address(String address) {
      this.address = Preconditions.checkNotNull(address, "address");
      initBits &= ~INIT_BIT_ADDRESS;
      return this;
    }

    /**
     * Initializes the value for the {@link CatalogService#getServiceName() serviceName} attribute.
     * @param serviceName The value for serviceName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder serviceName(String serviceName) {
      this.serviceName = Preconditions.checkNotNull(serviceName, "serviceName");
      initBits &= ~INIT_BIT_SERVICE_NAME;
      return this;
    }

    /**
     * Initializes the value for the {@link CatalogService#getServiceId() serviceId} attribute.
     * @param serviceId The value for serviceId 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder serviceId(String serviceId) {
      this.serviceId = Preconditions.checkNotNull(serviceId, "serviceId");
      initBits &= ~INIT_BIT_SERVICE_ID;
      return this;
    }

    /**
     * Initializes the value for the {@link CatalogService#getServiceAddress() serviceAddress} attribute.
     * @param serviceAddress The value for serviceAddress 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder serviceAddress(String serviceAddress) {
      this.serviceAddress = Preconditions.checkNotNull(serviceAddress, "serviceAddress");
      initBits &= ~INIT_BIT_SERVICE_ADDRESS;
      return this;
    }

    /**
     * Initializes the value for the {@link CatalogService#getServicePort() servicePort} attribute.
     * @param servicePort The value for servicePort 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder servicePort(int servicePort) {
      this.servicePort = servicePort;
      initBits &= ~INIT_BIT_SERVICE_PORT;
      return this;
    }

    /**
     * Adds one element to {@link CatalogService#getServiceTags() serviceTags} list.
     * @param element A serviceTags element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addServiceTags(String element) {
      serviceTagsBuilder.add(element);
      return this;
    }

    /**
     * Adds elements to {@link CatalogService#getServiceTags() serviceTags} list.
     * @param elements An array of serviceTags elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addServiceTags(String... elements) {
      serviceTagsBuilder.add(elements);
      return this;
    }

    /**
     * Sets or replaces all elements for {@link CatalogService#getServiceTags() serviceTags} list.
     * @param elements An iterable of serviceTags elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder serviceTags(Iterable<String> elements) {
      serviceTagsBuilder = ImmutableList.builder();
      return addAllServiceTags(elements);
    }

    /**
     * Adds elements to {@link CatalogService#getServiceTags() serviceTags} list.
     * @param elements An iterable of serviceTags elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllServiceTags(Iterable<String> elements) {
      serviceTagsBuilder.addAll(elements);
      return this;
    }

    /**
     * Builds a new {@link ImmutableCatalogService ImmutableCatalogService}.
     * @return An immutable instance of CatalogService
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableCatalogService build() throws IllegalStateException {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableCatalogService(
          node,
          address,
          serviceName,
          serviceId,
          serviceAddress,
          servicePort,
          serviceTagsBuilder.build());
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_NODE) != 0) attributes.add("node");
      if ((initBits & INIT_BIT_ADDRESS) != 0) attributes.add("address");
      if ((initBits & INIT_BIT_SERVICE_NAME) != 0) attributes.add("serviceName");
      if ((initBits & INIT_BIT_SERVICE_ID) != 0) attributes.add("serviceId");
      if ((initBits & INIT_BIT_SERVICE_ADDRESS) != 0) attributes.add("serviceAddress");
      if ((initBits & INIT_BIT_SERVICE_PORT) != 0) attributes.add("servicePort");
      return "Cannot build CatalogService, some of required attributes are not set " + attributes;
    }
  }
}
