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
 * limitations under the License.
 ******************************************************************************/
package org.onap.msb.apiroute.wrapper.util;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.onap.msb.apiroute.wrapper.util.HttpClientUtil;

public class HttpClientUtilTest {
 
  private String testIp="http://10.74.151.26:8500";
  
  @Test
  public void test_httpGet() {
    /*try {
      int result = HttpClientUtil.httpGetStatus(testIp);
      if(result==200){
        Assert.assertEquals("Consul Agent", HttpClientUtil.httpGet(testIp));
      }
      else{
        Assert.assertEquals(500, result); 
      }
      

    } catch (Exception e) {
      Assert.assertTrue(e instanceof IOException);
    }*/
  }
}
