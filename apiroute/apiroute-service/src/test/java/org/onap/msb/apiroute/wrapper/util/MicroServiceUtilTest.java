package org.onap.msb.apiroute.wrapper.util;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.msb.apiroute.wrapper.util.MicroServiceUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import redis.clients.jedis.Jedis;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpServletRequest.class})
public class MicroServiceUtilTest {

  @Test
  public void test_getPrefixedKey(){
    Assert.assertEquals("discover:microservices:test:v1",MicroServiceUtil.getPrefixedKey("test","v1"));
  }
  
  @Test
  public void test_getServiceKey(){
    Assert.assertEquals("discover:microservices:test:v1",MicroServiceUtil.getServiceKey("test","v1"));
  }
  
  @Test
  public void test_getRealIp(){
    HttpServletRequest request=PowerMockito.mock(HttpServletRequest.class);  
    PowerMockito.when(request.getHeader("X-Forwarded-For")).thenReturn("127.0.0.1");
    Assert.assertEquals("127.0.0.1",MicroServiceUtil.getRealIp(request));
  
    PowerMockito.when(request.getHeader("X-Forwarded-For")).thenReturn("");
    PowerMockito.when(request.getHeader("X-Real-IP")).thenReturn("127.0.0.2");
    Assert.assertEquals("127.0.0.2",MicroServiceUtil.getRealIp(request));
    
    PowerMockito.when(request.getHeader("X-Forwarded-For")).thenReturn("");
    PowerMockito.when(request.getHeader("X-Real-IP")).thenReturn("");
    PowerMockito.when(request.getRemoteAddr()).thenReturn("127.0.0.3");
    Assert.assertEquals("127.0.0.3",MicroServiceUtil.getRealIp(request));
    
  }
}
