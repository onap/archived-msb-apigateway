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

package org.openo.msb.wrapper.consul.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * For use with JSON fields that Consul Base 64 encodes.
 */
public class Base64EncodingDeserializer extends JsonDeserializer<Optional<String>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();

        if (StringUtils.isNotEmpty(value)) {
            return Optional.of(new String(BaseEncoding.base64().decode(value)));
        }
        return Optional.absent();
    }
}
