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

package org.openo.msb.wrapper.consul.model;

import com.google.common.base.Objects;

import java.math.BigInteger;

public class ConsulResponse<T> {

    private final T response;
    private final long lastContact;
    private final boolean knownLeader;
    private final BigInteger index;

    public ConsulResponse(T response, long lastContact, boolean knownLeader, BigInteger index) {
        this.response = response;
        this.lastContact = lastContact;
        this.knownLeader = knownLeader;
        this.index = index;
    }

    public T getResponse() {
        return response;
    }

    public long getLastContact() {
        return lastContact;
    }

    public boolean isKnownLeader() {
        return knownLeader;
    }

    public BigInteger getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "ConsulResponse{" +
                "response=" + response +
                ", lastContact=" + lastContact +
                ", knownLeader=" + knownLeader +
                ", index=" + index +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConsulResponse that = (ConsulResponse) o;

        return Objects.equal(this.response, that.response) &&
                Objects.equal(this.lastContact, that.lastContact) &&
                Objects.equal(this.knownLeader, that.knownLeader) &&
                Objects.equal(this.index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(response, lastContact, knownLeader, index);
    }
}
