/**
 * Copyright 2016 ZTE, Inc. and others.
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
import com.google.common.collect.Lists;
import com.google.common.primitives.Booleans;
import java.math.BigInteger;
import java.util.ArrayList;
import javax.annotation.Generated;

/**
 * Immutable implementation of {@link QueryOptions}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableQueryOptions.builder()}.
 */
@SuppressWarnings("all")
@Generated({"Immutables.generator", "QueryOptions"})
public final class ImmutableQueryOptions extends QueryOptions {
  private final Optional<String> wait;
  private final Optional<String> token;
  private final Optional<BigInteger> index;
  private final Optional<String> near;
  private final ConsistencyMode consistencyMode;
  private final boolean isBlocking;
  private final boolean hasToken;

  private ImmutableQueryOptions(ImmutableQueryOptions.Builder builder) {
    this.wait = builder.wait;
    this.token = builder.token;
    this.index = builder.index;
    this.near = builder.near;
    if (builder.consistencyMode != null) {
      initShim.consistencyMode(builder.consistencyMode);
    }
    this.consistencyMode = initShim.getConsistencyMode();
    this.isBlocking = initShim.isBlocking();
    this.hasToken = initShim.hasToken();
    this.initShim = null;
  }

  private ImmutableQueryOptions(
      Optional<String> wait,
      Optional<String> token,
      Optional<BigInteger> index,
      Optional<String> near,
      ConsistencyMode consistencyMode) {
    this.wait = wait;
    this.token = token;
    this.index = index;
    this.near = near;
    this.consistencyMode = consistencyMode;
    initShim.consistencyMode(consistencyMode);
    this.isBlocking = initShim.isBlocking();
    this.hasToken = initShim.hasToken();
    this.initShim = null;
  }

  private static final int STAGE_INITIALIZING = -1;
  private static final int STAGE_UNINITIALIZED = 0;
  private static final int STAGE_INITIALIZED = 1;
  private volatile InitShim initShim = new InitShim();

  private final class InitShim {
    private ConsistencyMode consistencyMode;
    private byte consistencyModeStage;

    ConsistencyMode getConsistencyMode() {
      if (consistencyModeStage == STAGE_INITIALIZING) throw new IllegalStateException(formatInitCycleMessage());
      if (consistencyModeStage == STAGE_UNINITIALIZED) {
        consistencyModeStage = STAGE_INITIALIZING;
        this.consistencyMode = Preconditions.checkNotNull(ImmutableQueryOptions.super.getConsistencyMode(), "consistencyMode");
        consistencyModeStage = STAGE_INITIALIZED;
      }
      return consistencyMode;
    }

    ConsistencyMode consistencyMode(ConsistencyMode value) {
      this.consistencyMode = value;
      consistencyModeStage = STAGE_INITIALIZED;
      return value;
    }
    private boolean isBlocking;
    private byte isBlockingStage;

    boolean isBlocking() {
      if (isBlockingStage == STAGE_INITIALIZING) throw new IllegalStateException(formatInitCycleMessage());
      if (isBlockingStage == STAGE_UNINITIALIZED) {
        isBlockingStage = STAGE_INITIALIZING;
        this.isBlocking = ImmutableQueryOptions.super.isBlocking();
        isBlockingStage = STAGE_INITIALIZED;
      }
      return isBlocking;
    }
    private boolean hasToken;
    private byte hasTokenStage;

    boolean hasToken() {
      if (hasTokenStage == STAGE_INITIALIZING) throw new IllegalStateException(formatInitCycleMessage());
      if (hasTokenStage == STAGE_UNINITIALIZED) {
        hasTokenStage = STAGE_INITIALIZING;
        this.hasToken = ImmutableQueryOptions.super.hasToken();
        hasTokenStage = STAGE_INITIALIZED;
      }
      return hasToken;
    }

    private String formatInitCycleMessage() {
      ArrayList<String> attributes = Lists.newArrayList();
      if (consistencyModeStage == STAGE_INITIALIZING) attributes.add("consistencyMode");
      if (isBlockingStage == STAGE_INITIALIZING) attributes.add("isBlocking");
      if (hasTokenStage == STAGE_INITIALIZING) attributes.add("hasToken");
      return "Cannot build QueryOptions, attribute initializers form cycle" + attributes;
    }
  }

  /**
   * @return The value of the {@code wait} attribute
   */
  @Override
  public Optional<String> getWait() {
    return wait;
  }

  /**
   * @return The value of the {@code token} attribute
   */
  @Override
  public Optional<String> getToken() {
    return token;
  }

  /**
   * @return The value of the {@code index} attribute
   */
  @Override
  public Optional<BigInteger> getIndex() {
    return index;
  }

  /**
   * @return The value of the {@code near} attribute
   */
  @Override
  public Optional<String> getNear() {
    return near;
  }

  /**
   * @return The value of the {@code consistencyMode} attribute
   */
  @Override
  public ConsistencyMode getConsistencyMode() {
    return initShim != null
        ? initShim.getConsistencyMode()
        : consistencyMode;
  }

  /**
   * @return The computed-at-construction value of the {@code isBlocking} attribute
   */
  @Override
  public boolean isBlocking() {
    return initShim != null
        ? initShim.isBlocking()
        : isBlocking;
  }

  /**
   * @return The computed-at-construction value of the {@code hasToken} attribute
   */
  @Override
  public boolean hasToken() {
    return initShim != null
        ? initShim.hasToken()
        : hasToken;
  }

  /**
   * Copy the current immutable object by setting a <i>present</i> value for the optional {@link QueryOptions#getWait() wait} attribute.
   * @param value The value for wait
   * @return A modified copy of {@code this} object
   */
  public final ImmutableQueryOptions withWait(String value) {
    Optional<String> newValue = Optional.of(value);
    return validate(new ImmutableQueryOptions(newValue, this.token, this.index, this.near, this.consistencyMode));
  }

  /**
   * Copy the current immutable object by setting an optional value for the {@link QueryOptions#getWait() wait} attribute.
   * A shallow reference equality check on the optional value is used to prevent copying of the same value by returning {@code this}.
   * @param optional A value for wait
   * @return A modified copy of {@code this} object
   */
  public final ImmutableQueryOptions withWait(Optional<String> optional) {
    Optional<String> value = Preconditions.checkNotNull(optional, "wait");
    if (this.wait == value) return this;
    return validate(new ImmutableQueryOptions(value, this.token, this.index, this.near, this.consistencyMode));
  }

  /**
   * Copy the current immutable object by setting a <i>present</i> value for the optional {@link QueryOptions#getToken() token} attribute.
   * @param value The value for token
   * @return A modified copy of {@code this} object
   */
  public final ImmutableQueryOptions withToken(String value) {
    Optional<String> newValue = Optional.of(value);
    return validate(new ImmutableQueryOptions(this.wait, newValue, this.index, this.near, this.consistencyMode));
  }

  /**
   * Copy the current immutable object by setting an optional value for the {@link QueryOptions#getToken() token} attribute.
   * A shallow reference equality check on the optional value is used to prevent copying of the same value by returning {@code this}.
   * @param optional A value for token
   * @return A modified copy of {@code this} object
   */
  public final ImmutableQueryOptions withToken(Optional<String> optional) {
    Optional<String> value = Preconditions.checkNotNull(optional, "token");
    if (this.token == value) return this;
    return validate(new ImmutableQueryOptions(this.wait, value, this.index, this.near, this.consistencyMode));
  }

  /**
   * Copy the current immutable object by setting a <i>present</i> value for the optional {@link QueryOptions#getIndex() index} attribute.
   * @param value The value for index
   * @return A modified copy of {@code this} object
   */
  public final ImmutableQueryOptions withIndex(BigInteger value) {
    Optional<BigInteger> newValue = Optional.of(value);
    return validate(new ImmutableQueryOptions(this.wait, this.token, newValue, this.near, this.consistencyMode));
  }

  /**
   * Copy the current immutable object by setting an optional value for the {@link QueryOptions#getIndex() index} attribute.
   * A shallow reference equality check on the optional value is used to prevent copying of the same value by returning {@code this}.
   * @param optional A value for index
   * @return A modified copy of {@code this} object
   */
  public final ImmutableQueryOptions withIndex(Optional<BigInteger> optional) {
    Optional<BigInteger> value = Preconditions.checkNotNull(optional, "index");
    if (this.index == value) return this;
    return validate(new ImmutableQueryOptions(this.wait, this.token, value, this.near, this.consistencyMode));
  }

  /**
   * Copy the current immutable object by setting a <i>present</i> value for the optional {@link QueryOptions#getNear() near} attribute.
   * @param value The value for near
   * @return A modified copy of {@code this} object
   */
  public final ImmutableQueryOptions withNear(String value) {
    Optional<String> newValue = Optional.of(value);
    return validate(new ImmutableQueryOptions(this.wait, this.token, this.index, newValue, this.consistencyMode));
  }

  /**
   * Copy the current immutable object by setting an optional value for the {@link QueryOptions#getNear() near} attribute.
   * A shallow reference equality check on the optional value is used to prevent copying of the same value by returning {@code this}.
   * @param optional A value for near
   * @return A modified copy of {@code this} object
   */
  public final ImmutableQueryOptions withNear(Optional<String> optional) {
    Optional<String> value = Preconditions.checkNotNull(optional, "near");
    if (this.near == value) return this;
    return validate(new ImmutableQueryOptions(this.wait, this.token, this.index, value, this.consistencyMode));
  }

  /**
   * Copy the current immutable object by setting a value for the {@link QueryOptions#getConsistencyMode() consistencyMode} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for consistencyMode
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableQueryOptions withConsistencyMode(ConsistencyMode value) {
    if (this.consistencyMode == value) return this;
    return validate(new ImmutableQueryOptions(
        this.wait,
        this.token,
        this.index,
        this.near,
        Preconditions.checkNotNull(value, "consistencyMode")));
  }

  /**
   * This instance is equal to all instances of {@code ImmutableQueryOptions} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableQueryOptions
        && equalTo((ImmutableQueryOptions) another);
  }

  private boolean equalTo(ImmutableQueryOptions another) {
    return wait.equals(another.wait)
        && token.equals(another.token)
        && index.equals(another.index)
        && near.equals(another.near)
        && consistencyMode.equals(another.consistencyMode)
        && isBlocking == another.isBlocking
        && hasToken == another.hasToken;
  }

  /**
   * Computes a hash code from attributes: {@code wait}, {@code token}, {@code index}, {@code near}, {@code consistencyMode}, {@code isBlocking}, {@code hasToken}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + wait.hashCode();
    h = h * 17 + token.hashCode();
    h = h * 17 + index.hashCode();
    h = h * 17 + near.hashCode();
    h = h * 17 + consistencyMode.hashCode();
    h = h * 17 + Booleans.hashCode(isBlocking);
    h = h * 17 + Booleans.hashCode(hasToken);
    return h;
  }

  /**
   * Prints the immutable value {@code QueryOptions...} with all non-generated
   * and non-auxiliary attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("QueryOptions")
        .add("wait", wait)
        .add("token", token)
        .add("index", index)
        .add("near", near)
        .add("consistencyMode", consistencyMode)
        .add("isBlocking", isBlocking)
        .add("hasToken", hasToken)
        .toString();
  }

  private static ImmutableQueryOptions validate(ImmutableQueryOptions instance) {
    instance.validate();
    return instance;
  }

  /**
   * Creates an immutable copy of a {@link QueryOptions} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable QueryOptions instance
   */
  public static ImmutableQueryOptions copyOf(QueryOptions instance) {
    if (instance instanceof ImmutableQueryOptions) {
      return (ImmutableQueryOptions) instance;
    }
    return ImmutableQueryOptions.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableQueryOptions ImmutableQueryOptions}.
   * @return A new ImmutableQueryOptions builder
   */
  public static ImmutableQueryOptions.Builder builder() {
    return new ImmutableQueryOptions.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableQueryOptions ImmutableQueryOptions}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  public static final class Builder {
    private Optional<String> wait = Optional.absent();
    private Optional<String> token = Optional.absent();
    private Optional<BigInteger> index = Optional.absent();
    private Optional<String> near = Optional.absent();
    private ConsistencyMode consistencyMode;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code QueryOptions} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(QueryOptions instance) {
      Preconditions.checkNotNull(instance, "instance");
      Optional<String> waitOptional = instance.getWait();
      if (waitOptional.isPresent()) {
        wait(waitOptional);
      }
      Optional<String> tokenOptional = instance.getToken();
      if (tokenOptional.isPresent()) {
        token(tokenOptional);
      }
      Optional<BigInteger> indexOptional = instance.getIndex();
      if (indexOptional.isPresent()) {
        index(indexOptional);
      }
      Optional<String> nearOptional = instance.getNear();
      if (nearOptional.isPresent()) {
        near(nearOptional);
      }
      consistencyMode(instance.getConsistencyMode());
      return this;
    }

    /**
     * Initializes the optional value {@link QueryOptions#getWait() wait} to wait.
     * @param wait The value for wait
     * @return {@code this} builder for chained invocation
     */
    public final Builder wait(String wait) {
      this.wait = Optional.of(wait);
      return this;
    }

    /**
     * Initializes the optional value {@link QueryOptions#getWait() wait} to wait.
     * @param wait The value for wait
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder wait(Optional<String> wait) {
      this.wait = Preconditions.checkNotNull(wait, "wait");
      return this;
    }

    /**
     * Initializes the optional value {@link QueryOptions#getToken() token} to token.
     * @param token The value for token
     * @return {@code this} builder for chained invocation
     */
    public final Builder token(String token) {
      this.token = Optional.of(token);
      return this;
    }

    /**
     * Initializes the optional value {@link QueryOptions#getToken() token} to token.
     * @param token The value for token
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder token(Optional<String> token) {
      this.token = Preconditions.checkNotNull(token, "token");
      return this;
    }

    /**
     * Initializes the optional value {@link QueryOptions#getIndex() index} to index.
     * @param index The value for index
     * @return {@code this} builder for chained invocation
     */
    public final Builder index(BigInteger index) {
      this.index = Optional.of(index);
      return this;
    }

    /**
     * Initializes the optional value {@link QueryOptions#getIndex() index} to index.
     * @param index The value for index
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder index(Optional<BigInteger> index) {
      this.index = Preconditions.checkNotNull(index, "index");
      return this;
    }

    /**
     * Initializes the optional value {@link QueryOptions#getNear() near} to near.
     * @param near The value for near
     * @return {@code this} builder for chained invocation
     */
    public final Builder near(String near) {
      this.near = Optional.of(near);
      return this;
    }

    /**
     * Initializes the optional value {@link QueryOptions#getNear() near} to near.
     * @param near The value for near
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder near(Optional<String> near) {
      this.near = Preconditions.checkNotNull(near, "near");
      return this;
    }

    /**
     * Initializes the value for the {@link QueryOptions#getConsistencyMode() consistencyMode} attribute.
     * <p><em>If not set, this attribute will have a default value as returned by the initializer of {@link QueryOptions#getConsistencyMode() consistencyMode}.</em>
     * @param consistencyMode The value for consistencyMode 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder consistencyMode(ConsistencyMode consistencyMode) {
      this.consistencyMode = Preconditions.checkNotNull(consistencyMode, "consistencyMode");
      return this;
    }

    /**
     * Builds a new {@link ImmutableQueryOptions ImmutableQueryOptions}.
     * @return An immutable instance of QueryOptions
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableQueryOptions build() throws IllegalStateException {
      return ImmutableQueryOptions.validate(new ImmutableQueryOptions(this));
    }
  }
}
