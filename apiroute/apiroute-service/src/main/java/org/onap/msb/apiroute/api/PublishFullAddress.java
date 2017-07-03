package org.onap.msb.apiroute.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PublishFullAddress implements Serializable {
  private static final long serialVersionUID = 1L;


  private String ip;  

  private String port;
  

  private String publish_protocol;

  public String getPublish_protocol() {
    return publish_protocol;
  }

  public void setPublish_protocol(String publish_protocol) {
    this.publish_protocol = publish_protocol;
  }


  public String getIp() {
      return ip;
  }

  public void setIp(String ip) {
      this.ip = ip;
  }

  public String getPort() {
      return port;
  }

  public void setPort(String port) {
      this.port = port;
  }
  
  public PublishFullAddress(){
    
  }
 
}
