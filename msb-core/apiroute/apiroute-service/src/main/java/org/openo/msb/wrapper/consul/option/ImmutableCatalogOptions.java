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
package org.openo.msb.wrapper.consul.option;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import javax.annotation.Generated;

/**
 * Immutable implementation of {@link CatalogOptions}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableCatalogOptions.builder()}.
 */
@SuppressWarnings("all")
@Generated({"Immutables.generator", "CatalogOptions"})
public final class ImmutableCatalogOptions extends CatalogOptions {
  private final Optional<String> datacenter;
  private final Optional<String> tag;

  private ImmutableCatalogOptions(
      Optional<String> datacenter,
      Optional<String> tag) {
    this.datacenter = datacenter;
    this.tag = tag;
  }

  /**
   * @return The value of the {@code datacenter} attribute
   */
  @Override
  public Optional<String> getDatacenter() {
    return datacenter;
  }

  /**
   * @return The value of the {@code tag} attribute
   */
  @Override
  public Optional<String> getTag() {
    return tag;
  }

  /**
   * Copy the current immutable object by setting a <i>present</i> value for the optional {@link CatalogOptions#getDatacenter() datacenter} attribute.
   * @param value The value for datacenter
   * @return A modified copy of {@code this} object
   */
  public final ImmutableCatalogOptions withDatacenter(String value) {
    Optional<String> newValue = Optional.of(value);
    return new ImmutableCatalogOptions(newValue, this.tag);
  }

  /**
   * Copy the current immutable object by setting an optional value for the {@link CatalogOptions#getDatacenter() datacenter} attribute.
   * A shallow reference equality check on the optional value is used to prevent copying of the same value by returning {@code this}.
   * @param optional A value for datacenter
   * @return A modified copy of {@code this} object
   */
  public final ImmutableCatalogOptions withDatacenter(Optional<String> optional) {
    Optional<String> value = Preconditions.checkNotNull(optional, "datacenter");
    if (this.datacenter == value) return this;
    return new ImmutableCatalogOptions(value, this.tag);
  }

  /**
   * Copy the current immutable object by setting a <i>present</i> value for the optional {@link CatalogOptions#getTag() tag} attribute.
   * @param value The value for tag
   * @return A modified copy of {@code this} object
   */
  public final ImmutableCatalogOptions withTag(String value) {
    Optional<String> newValue = Optional.of(value);
    return new ImmutableCatalogOptions(this.datacenter, newValue);
  }

  /**
   * Copy the current immutable object by setting an optional value for the {@link CatalogOptions#getTag() tag} attribute.
   * A shallow reference equality check on the optional value is used to prevent copying of the same value by returning {@code this}.
   * @param optional A value for tag
   * @return A modified copy of {@code this} object
   */
  public final ImmutableCatalogOptions withTag(Optional<String> optional) {
    Optional<String> value = Preconditions.checkNotNull(optional, "tag");
    if (this.tag == value) return this;
    return new ImmutableCatalogOptions(this.datacenter, value);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableCatalogOptions} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableCatalogOptions
        && equalTo((ImmutableCatalogOptions) another);
  }

  private boolean equalTo(ImmutableCatalogOptions another) {
    return datacenter.equals(another.datacenter)
        && tag.equals(another.tag);
  }

  /**
   * Computes a hash code from attributes: {@code datacenter}, {@code tag}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + datacenter.hashCode();
    h = h * 17 + tag.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code CatalogOptions...} with all non-generated
   * and non-auxiliary attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("CatalogOptions")
        .add("datacenter", datacenter)
        .add("tag", tag)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link CatalogOptions} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable CatalogOptions instance
   */
  public static ImmutableCatalogOptions copyOf(CatalogOptions instance) {
    if (instance instanceof ImmutableCatalogOptions) {
      return (ImmutableCatalogOptions) instance;
    }
    return ImmutableCatalogOptions.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableCatalogOptions ImmutableCatalogOptions}.
   * @return A new ImmutableCatalogOptions builder
   */
  public static ImmutableCatalogOptions.Builder builder() {
    return new ImmutableCatalogOptions.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableCatalogOptions ImmutableCatalogOptions}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  public static final class Builder {
    private Optional<String> datacenter = Optional.absent();
    private Optional<String> tag = Optional.absent();

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code CatalogOptions} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(CatalogOptions instance) {
      Preconditions.checkNotNull(instance, "instance");
      Optional<String> datacenterOptional = instance.getDatacenter();
      if (datacenterOptional.isPresent()) {
        datacenter(datacenterOptional);
      }
      Optional<String> tagOptional = instance.getTag();
      if (tagOptional.isPresent()) {
        tag(tagOptional);
      }
      return this;
    }

    /**
     * Initializes the optional value {@link CatalogOptions#getDatacenter() datacenter} to datacenter.
     * @param datacenter The value for datacenter
     * @return {@code this} builder for chained invocation
     */
    public final Builder datacenter(String datacenter) {
      this.datacenter = Optional.of(datacenter);
      return this;
    }

    /**
     * Initializes the optional value {@link CatalogOptions#getDatacenter() datacenter} to datacenter.
     * @param datacenter The value for datacenter
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder datacenter(Optional<String> datacenter) {
      this.datacenter = Preconditions.checkNotNull(datacenter, "datacenter");
      return this;
    }

    /**
     * Initializes the optional value {@link CatalogOptions#getTag() tag} to tag.
     * @param tag The value for tag
     * @return {@code this} builder for chained invocation
     */
    public final Builder tag(String tag) {
      this.tag = Optional.of(tag);
      return this;
    }

    /**
     * Initializes the optional value {@link CatalogOptions#getTag() tag} to tag.
     * @param tag The value for tag
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder tag(Optional<String> tag) {
      this.tag = Preconditions.checkNotNull(tag, "tag");
      return this;
    }

    /**
     * Builds a new {@link ImmutableCatalogOptions ImmutableCatalogOptions}.
     * @return An immutable instance of CatalogOptions
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableCatalogOptions build() throws IllegalStateException {
      return new ImmutableCatalogOptions(datacenter, tag);
    }
  }
}
