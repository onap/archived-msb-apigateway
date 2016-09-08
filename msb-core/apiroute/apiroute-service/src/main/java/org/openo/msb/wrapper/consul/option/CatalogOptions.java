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

import static org.openo.msb.wrapper.consul.option.Options.optionallyAdd;

import javax.ws.rs.client.WebTarget;

import com.google.common.base.Optional;


public abstract class CatalogOptions implements ParamAdder {

    public abstract Optional<String> getDatacenter();
    public abstract Optional<String> getTag();

    public static final CatalogOptions BLANK = ImmutableCatalogOptions.builder().build();

    @Override
    public final WebTarget apply(final WebTarget input) {
        WebTarget added = optionallyAdd(input, "dc", getDatacenter());
        added = optionallyAdd(added, "tag", getTag());
        return added;
    }
}
