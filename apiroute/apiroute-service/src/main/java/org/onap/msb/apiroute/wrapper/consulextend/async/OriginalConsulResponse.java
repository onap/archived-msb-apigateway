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
package org.onap.msb.apiroute.wrapper.consulextend.async;

import org.apache.http.HttpResponse;

import com.fasterxml.jackson.core.type.TypeReference;

public class OriginalConsulResponse<T> {
    final HttpResponse response;
    final TypeReference<T> responseType;

    public OriginalConsulResponse(HttpResponse response, TypeReference<T> responseType) {
        this.response = response;
        this.responseType = responseType;

    }

    public HttpResponse getResponse() {
        return response;
    }

    public TypeReference<T> getResponseType() {
        return responseType;
    }



}
