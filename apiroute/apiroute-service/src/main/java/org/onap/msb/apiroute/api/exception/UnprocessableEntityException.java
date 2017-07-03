package org.onap.msb.apiroute.api.exception;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;

public class UnprocessableEntityException extends ClientErrorException{
  private static final long serialVersionUID = -8266622745725405656L;
  
  public UnprocessableEntityException(final String message) {
    super(Response.status(HttpStatus.SC_UNPROCESSABLE_ENTITY).entity(message).type(MediaType.TEXT_PLAIN).build());
  }
  
  

}
