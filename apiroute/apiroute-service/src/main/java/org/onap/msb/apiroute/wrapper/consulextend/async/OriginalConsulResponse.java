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
