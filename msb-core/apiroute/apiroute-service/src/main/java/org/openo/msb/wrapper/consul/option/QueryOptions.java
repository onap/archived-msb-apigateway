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

package org.openo.msb.wrapper.consul.option;

import static com.google.common.base.Preconditions.checkArgument;
import static org.openo.msb.wrapper.consul.option.Options.optionallyAdd;

import java.math.BigInteger;

import javax.ws.rs.client.WebTarget;

import com.google.common.base.Optional;

/**
 * Container for common query options used by the Consul API.
 */

public abstract class QueryOptions implements ParamAdder {

    public static final QueryOptions BLANK = ImmutableQueryOptions.builder().build();

    public abstract Optional<String> getWait();
    public abstract Optional<String> getToken();
    public abstract Optional<BigInteger> getIndex();
    public abstract Optional<String> getNear();

  
    public ConsistencyMode getConsistencyMode() {
        return ConsistencyMode.DEFAULT;
    }


    public boolean isBlocking() {
        return getWait().isPresent();
    }


    public boolean hasToken() {
        return getToken().isPresent();
    }


    void validate() {
        if (isBlocking()) {
            checkArgument(getIndex().isPresent(), "If wait is specified, index must also be specified");
        }
    }

    public static ImmutableQueryOptions.Builder blockSeconds(int seconds, BigInteger index) {
        return blockBuilder("s", seconds, index);
    }

    public static ImmutableQueryOptions.Builder blockMinutes(int minutes, BigInteger index) {
        return blockBuilder("m", minutes, index);
    }

    private static ImmutableQueryOptions.Builder blockBuilder(String identifier, int qty, BigInteger index) {
        return ImmutableQueryOptions.builder()
                .wait(String.format("%s%s", qty, identifier))
                .index(index);
    }

    @Override
    public WebTarget apply(WebTarget input) {

        WebTarget added = input;
        switch (getConsistencyMode()) {
            case CONSISTENT:
                added = added.queryParam("consistent", "");
                break;
            case STALE:
                added = added.queryParam("stale", "");
                break;
        }

        if (isBlocking()) {
            added = added.queryParam("wait", getWait().get())
                    .queryParam("index", String.valueOf(getIndex().get()));
        }

        added = optionallyAdd(added, "token", getToken());
        added = optionallyAdd(added, "near", getToken());

        return added;
    }
}
