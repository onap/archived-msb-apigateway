/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.onap.msb.apiroute.api.exception;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ExtendedNotFoundException extends NotFoundException {

    public ExtendedNotFoundException(final String message) {
        super(Response.status(Response.Status.NOT_FOUND).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}

