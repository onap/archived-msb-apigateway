package org.onap.msb.apiroute.wrapper.util;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.onap.msb.apiroute.wrapper.util.HttpClientUtil;

public class HttpClientUtilTest {
 
  private String testIp="http://10.74.151.26:8500";
  
  @Test
  public void test_httpGet() {
    try {
      int result = HttpClientUtil.httpGetStatus(testIp);
      if(result==200){
        Assert.assertEquals("Consul Agent", HttpClientUtil.httpGet(testIp));
      }
      else{
        Assert.assertEquals(500, result); 
      }
      

    } catch (Exception e) {
      Assert.assertTrue(e instanceof IOException);
    }
  }
}
