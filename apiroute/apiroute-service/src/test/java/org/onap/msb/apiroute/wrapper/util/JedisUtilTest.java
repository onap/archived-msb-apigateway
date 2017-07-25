package org.onap.msb.apiroute.wrapper.util;

import org.junit.Assert;
import org.junit.Test;
import org.onap.msb.apiroute.wrapper.util.JedisUtil;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisUtilTest {
  @Test
  public void test_initialPool() {
    try {
      JedisUtil.borrowJedisInstance();
    
    } catch (Exception e) {
      Assert.assertTrue(e instanceof JedisConnectionException);
      
    }
  }
}
