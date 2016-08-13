/**
* Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.openo.msb.wrapper.util;

import org.junit.Assert;

import org.junit.Test;

public class RegExpTestUtilTest {

    @Test
    public void testHostRegExpTest(){
        boolean result=RegExpTestUtil.hostRegExpTest("127.0.0.1:8080");
        Assert.assertTrue(result);
        
        boolean err_result=RegExpTestUtil.hostRegExpTest("0.0.0.1:89");
        Assert.assertFalse(err_result);
    }
    
    @Test
    public void testIpRegExpTest(){
        boolean result=RegExpTestUtil.ipRegExpTest("127.0.0.1");
        Assert.assertTrue(result);
        
        boolean err_result=RegExpTestUtil.ipRegExpTest("127.0.0.1.5");
        Assert.assertFalse(err_result);
    }
    
    @Test
    public void testPortRegExpTest(){
        boolean result=RegExpTestUtil.portRegExpTest("80");
        Assert.assertTrue(result);
        
        boolean err_result=RegExpTestUtil.portRegExpTest("898989");
        Assert.assertFalse(err_result);
    }
    
    @Test
    public void testVersionRegExpTest(){
        boolean result=RegExpTestUtil.versionRegExpTest("v1");
        Assert.assertTrue(result);
        
        boolean err_result=RegExpTestUtil.versionRegExpTest("23");
        Assert.assertFalse(err_result);
    }
    
    @Test
    public void testurlRegExpTest(){
        boolean result=RegExpTestUtil.urlRegExpTest("/test");
        Assert.assertTrue(result);
        
        boolean err_result=RegExpTestUtil.urlRegExpTest("test");
        Assert.assertFalse(err_result);
    }
    
    @Test
    public void testapiRouteUrlRegExpTest(){
        boolean result=RegExpTestUtil.apiRouteUrlRegExpTest("/api/test/v1");
        Assert.assertTrue(result);
        
        boolean err_result=RegExpTestUtil.apiRouteUrlRegExpTest("/test");
        Assert.assertFalse(err_result);
    }
    
    @Test
    public void testiuiRouteUrlRegExpTest(){
        boolean result=RegExpTestUtil.iuiRouteUrlRegExpTest("/iui/test");
        Assert.assertTrue(result);
        
        boolean err_result=RegExpTestUtil.iuiRouteUrlRegExpTest("/test");
        Assert.assertFalse(err_result);
    }
}
