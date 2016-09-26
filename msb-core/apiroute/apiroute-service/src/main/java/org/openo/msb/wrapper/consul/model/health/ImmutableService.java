/**
 * Copyright 2016 ZTE Corporation.
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Generated;

/**
 * Immutable implementation of {@link Service}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableService.builder()}.
 */
@SuppressWarnings("all")
@Generated({"Immutables.generator", "Service"})
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ImmutableService extends Service {
  private final String id;
  private final String service;
  private final ImmutableList<String> tags;
  private final String address;
  private final int port;

  private ImmutableService(
      String id,
      String service,
      ImmutableList<String> tags,
      String address,
      int port) {
    this.id = id;
    this.service = service;
    this.tags = tags;
    this.address = address;
    this.port = port;
  }

  /**
   * @return The value of the {@code id} attribute
   */
  @JsonProperty(value = "ID")
  @Override
  public String getId() {
    return id;
  }

  /**
   * @return The value of the {@code service} attribute
   */
  @JsonProperty(value = "Service")
  @Override
  public String getService() {
    return service;
  }

  /**
   * @return The value of the {@code tags} attribute
   */
  @JsonProperty(value = "Tags")
  @JsonDeserialize(as = ImmutableList.class, contentAs = String.class)
  @Override
  public ImmutableList<String> getTags() {
    return tags;
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
   * @return The value of the {@code port} attribute
   */
  @JsonProperty(value = "Port")
  @Override
  public int getPort() {
    return port;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link Service#getId() id} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for id
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableService withId(String value) {
    if (this.id.equals(value)) return this;
    return new ImmutableService(
        Preconditions.checkNotNull(value, "id"),
        this.service,
        this.tags,
        this.address,
        this.port);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link Service#getService() service} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for service
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableService withService(String value) {
    if (this.service.equals(value)) return this;
    return new ImmutableService(
        this.id,
        Preconditions.checkNotNull(value, "service"),
        this.tags,
        this.address,
        this.port);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link Service#getTags() tags}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableService withTags(String... elements) {
    ImmutableList<String> newValue = ImmutableList.copyOf(elements);
    return new ImmutableService(this.id, this.service, newValue, this.address, this.port);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link Service#getTags() tags}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of tags elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableService withTags(Iterable<String> elements) {
    if (this.tags == elements) return this;
    ImmutableList<String> newValue = ImmutableList.copyOf(elements);
    return new ImmutableService(this.id, this.service, newValue, this.address, this.port);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link Service#getAddress() address} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for address
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableService withAddress(String value) {
    if (this.address.equals(value)) return this;
    return new ImmutableService(
        this.id,
        this.service,
        this.tags,
        Preconditions.checkNotNull(value, "address"),
        this.port);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link Service#getPort() port} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for port
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableService withPort(int value) {
    if (this.port == value) return this;
    return new ImmutableService(this.id, this.service, this.tags, this.address, value);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableService} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableService
        && equalTo((ImmutableService) another);
  }

  private boolean equalTo(ImmutableService another) {
    return id.equals(another.id)
        && service.equals(another.service)
        && tags.equals(another.tags)
        && address.equals(another.address)
        && port == another.port;
  }

  /**
   * Computes a hash code from attributes: {@code id}, {@code service}, {@code tags}, {@code address}, {@code port}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + id.hashCode();
    h = h * 17 + service.hashCode();
    h = h * 17 + tags.hashCode();
    h = h * 17 + address.hashCode();
    h = h * 17 + port;
    return h;
  }

  /**
   * Prints the immutable value {@code Service...} with all non-generated
   * and non-auxiliary attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("Service")
        .add("id", id)
        .add("service", service)
        .add("tags", tags)
        .add("address", address)
        .add("port", port)
        .toString();
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonDeserialize
  static final class Json extends Service {
    String id;
    String service;
    List<String> tags = ImmutableList.of();
    String address;
    Integer port;
    @JsonProperty(value = "ID")
    public void setId(String id) {
      this.id = id;
    }
    @JsonProperty(value = "Service")
    public void setService(String service) {
      this.service = service;
    }
    @JsonProperty(value = "Tags")
    @JsonDeserialize(as = ImmutableList.class, contentAs = String.class)
    public void setTags(List<String> tags) {
      this.tags = tags;
    }
    @JsonProperty(value = "Address")
    public void setAddress(String address) {
      this.address = address;
    }
    @JsonProperty(value = "Port")
    public void setPort(int port) {
      this.port = port;
    }
    @Override
    public String getId() { throw new UnsupportedOperationException(); }
    @Override
    public String getService() { throw new UnsupportedOperationException(); }
    @Override
    public List<String> getTags() { throw new UnsupportedOperationException(); }
    @Override
    public String getAddress() { throw new UnsupportedOperationException(); }
    @Override
    public int getPort() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator
  static ImmutableService fromJson(Json json) {
    ImmutableService.Builder builder = ImmutableService.builder();
    if (json.id != null) {
      builder.id(json.id);
    }
    if (json.service != null) {
      builder.service(json.service);
    }
    if (json.tags != null) {
      builder.addAllTags(json.tags);
    }
    if (json.address != null) {
      builder.address(json.address);
    }
    if (json.port != null) {
      builder.port(json.port);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link Service} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable Service instance
   */
  public static ImmutableService copyOf(Service instance) {
    if (instance instanceof ImmutableService) {
      return (ImmutableService) instance;
    }
    return ImmutableService.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableService ImmutableService}.
   * @return A new ImmutableService builder
   */
  public static ImmutableService.Builder builder() {
    return new ImmutableService.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableService ImmutableService}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  public static final class Builder {
    private static final long INIT_BIT_ID = 0x1L;
    private static final long INIT_BIT_SERVICE = 0x2L;
    private static final long INIT_BIT_ADDRESS = 0x4L;
    private static final long INIT_BIT_PORT = 0x8L;
    private long initBits = 0xf;

    private String id;
    private String service;
    private ImmutableList.Builder<String> tagsBuilder = ImmutableList.builder();
    private String address;
    private int port;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code Service} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(Service instance) {
      Preconditions.checkNotNull(instance, "instance");
      id(instance.getId());
      service(instance.getService());
      addAllTags(instance.getTags());
      address(instance.getAddress());
      port(instance.getPort());
      return this;
    }

    /**
     * Initializes the value for the {@link Service#getId() id} attribute.
     * @param id The value for id 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder id(String id) {
      this.id = Preconditions.checkNotNull(id, "id");
      initBits &= ~INIT_BIT_ID;
      return this;
    }

    /**
     * Initializes the value for the {@link Service#getService() service} attribute.
     * @param service The value for service 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder service(String service) {
      this.service = Preconditions.checkNotNull(service, "service");
      initBits &= ~INIT_BIT_SERVICE;
      return this;
    }

    /**
     * Adds one element to {@link Service#getTags() tags} list.
     * @param element A tags element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addTags(String element) {
      tagsBuilder.add(element);
      return this;
    }

    /**
     * Adds elements to {@link Service#getTags() tags} list.
     * @param elements An array of tags elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addTags(String... elements) {
      tagsBuilder.add(elements);
      return this;
    }

    /**
     * Sets or replaces all elements for {@link Service#getTags() tags} list.
     * @param elements An iterable of tags elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder tags(Iterable<String> elements) {
      tagsBuilder = ImmutableList.builder();
      return addAllTags(elements);
    }

    /**
     * Adds elements to {@link Service#getTags() tags} list.
     * @param elements An iterable of tags elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllTags(Iterable<String> elements) {
      tagsBuilder.addAll(elements);
      return this;
    }

    /**
     * Initializes the value for the {@link Service#getAddress() address} attribute.
     * @param address The value for address 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder address(String address) {
      this.address = Preconditions.checkNotNull(address, "address");
      initBits &= ~INIT_BIT_ADDRESS;
      return this;
    }

    /**
     * Initializes the value for the {@link Service#getPort() port} attribute.
     * @param port The value for port 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder port(int port) {
      this.port = port;
      initBits &= ~INIT_BIT_PORT;
      return this;
    }

    /**
     * Builds a new {@link ImmutableService ImmutableService}.
     * @return An immutable instance of Service
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableService build() throws IllegalStateException {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableService(id, service, tagsBuilder.build(), address, port);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_ID) != 0) attributes.add("id");
      if ((initBits & INIT_BIT_SERVICE) != 0) attributes.add("service");
      if ((initBits & INIT_BIT_ADDRESS) != 0) attributes.add("address");
      if ((initBits & INIT_BIT_PORT) != 0) attributes.add("port");
      return "Cannot build Service, some of required attributes are not set " + attributes;
    }
  }
}
