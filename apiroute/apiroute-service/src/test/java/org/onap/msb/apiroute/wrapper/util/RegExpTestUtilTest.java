/**
 * Copyright 2016-2017 ZTE, Inc. and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.msb.apiroute.wrapper.util;

import org.junit.Assert;
import org.junit.Test;
import org.onap.msb.apiroute.wrapper.util.RegExpTestUtil;

public class RegExpTestUtilTest {

    @Test
    public void test_HostRegExpTest(){
        Assert.assertTrue(RegExpTestUtil.hostRegExpTest("127.0.0.1:8080"));
        Assert.assertFalse(RegExpTestUtil.hostRegExpTest("0.0.0.1:89"));
    }
    
  
    
    @Test
    public void test_IpRegExpTest(){
        Assert.assertTrue(RegExpTestUtil.ipRegExpTest("127.0.0.1"));
        Assert.assertFalse(RegExpTestUtil.ipRegExpTest("127.0.0.1.5"));
    }
    
 
    
    @Test
    public void test_PortRegExpTest(){
        Assert.assertTrue(RegExpTestUtil.portRegExpTest("80"));        
        Assert.assertFalse(RegExpTestUtil.portRegExpTest("898989"));
    }
    
    
    @Test
    public void test_VersionRegExpTest(){
        Assert.assertTrue(RegExpTestUtil.versionRegExpTest("v1"));
        Assert.assertFalse(RegExpTestUtil.versionRegExpTest("23"));
    }
    
 
    
    @Test
    public void test_urlRegExpTest(){
        Assert.assertTrue(RegExpTestUtil.urlRegExpTest("/test"));
        Assert.assertTrue(RegExpTestUtil.urlRegExpTest("/"));
        Assert.assertFalse(RegExpTestUtil.urlRegExpTest("test"));
    }
    
 
    @Test
    public void test_apiRouteUrlRegExpTest(){
        Assert.assertTrue(RegExpTestUtil.apiRouteUrlRegExpTest("/api/test/v1"));
        Assert.assertFalse(RegExpTestUtil.apiRouteUrlRegExpTest("/test"));
    }
    
 
    
    @Test
    public void test_iuiRouteUrlRegExpTest(){
        Assert.assertTrue(RegExpTestUtil.iuiRouteUrlRegExpTest("/iui/test"));
        Assert.assertFalse(RegExpTestUtil.iuiRouteUrlRegExpTest("/test"));
    }   
    
    @Test
    public void test_apiServiceNameMatch4URL(){
      String[] apiServiceNameArray={"testApiName","v1"};
      Assert.assertArrayEquals(apiServiceNameArray, RegExpTestUtil.apiServiceNameMatch4URL("/api/testApiName/v1"));
      
      String[] apiServiceNameArray_noversion={"testApiName",""};
      Assert.assertArrayEquals(apiServiceNameArray_noversion, RegExpTestUtil.apiServiceNameMatch4URL("/api/testApiName"));

      Assert.assertNull(RegExpTestUtil.apiServiceNameMatch4URL("/apiw/name/v1"));
    }
    
    @Test
    public void test_iuiServiceNameMatch4URL(){
      String iuiServiceName="testIuiName";
      Assert.assertEquals(iuiServiceName, RegExpTestUtil.iuiServiceNameMatch4URL("/iui/testIuiName"));
      
      Assert.assertNull(RegExpTestUtil.iuiServiceNameMatch4URL("/api/name/v1"));
    }
  
}
