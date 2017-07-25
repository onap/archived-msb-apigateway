package org.onap.msb.apiroute.wrapper.util;

public class HttpGetResult {
	private int statusCode;
	private String body;
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
