/**
 * Copyright 2016 2015-2016 ZTE, Inc. and others. All rights reserved.
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
package org.openo.msb.wrapper.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class RouteUtilTest {
    
    @Test
    public void testGetPrefixedKey(){
        String path=RouteUtil.getPrefixedKey("","test","v1");
        Assert.assertEquals("msb:routing:test:v1",path);
    }
    
    @Test
    public void testConcat(){
        Object[] str1=new String[]{"test1","test2"}; 
        Object[] str2=new String[]{"test3"}; 
        Object[] str3=RouteUtil.concat(str1, str2);
        
        Assert.assertEquals(3,str3.length);
    }
    
    @Test
    public void testContainStr(){
        String value="1";
        String array[]={"1","2"};
        boolean result=RouteUtil.contain(array, value);
        Assert.assertTrue(result);
    }
    
    @Test
    public void testContainArray(){
        String value[]={"0"};
        String array[]={"1","2"};
        boolean result=RouteUtil.contain(array, value);
        Assert.assertFalse(result);
        
    }
    
    @Test
    public void testShow(){
        String array[]={"1","2"};
        String result=RouteUtil.show(array);
        Assert.assertEquals("1|2",result);
        
    }

}
