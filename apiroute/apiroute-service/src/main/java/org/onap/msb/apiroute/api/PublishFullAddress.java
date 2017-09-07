/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
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
