/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
    public void test_getPrefixedKey() {
        Assert.assertEquals("discover:microservices:test:v1", MicroServiceUtil.getPrefixedKey("test", "v1"));
    }

    @Test
    public void test_getServiceKey() {
        Assert.assertEquals("discover:microservices:test:v1", MicroServiceUtil.getServiceKey("test", "v1"));
    }

    @Test
    public void test_getRealIp() {
        HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        PowerMockito.when(request.getHeader("X-Forwarded-For")).thenReturn("127.0.0.1");
        Assert.assertEquals("127.0.0.1", MicroServiceUtil.getRealIp(request));

        PowerMockito.when(request.getHeader("X-Forwarded-For")).thenReturn("");
        PowerMockito.when(request.getHeader("X-Real-IP")).thenReturn("127.0.0.2");
        Assert.assertEquals("127.0.0.2", MicroServiceUtil.getRealIp(request));

        PowerMockito.when(request.getHeader("X-Forwarded-For")).thenReturn("");
        PowerMockito.when(request.getHeader("X-Real-IP")).thenReturn("");
        PowerMockito.when(request.getRemoteAddr()).thenReturn("127.0.0.3");
        Assert.assertEquals("127.0.0.3", MicroServiceUtil.getRealIp(request));

    }
}
