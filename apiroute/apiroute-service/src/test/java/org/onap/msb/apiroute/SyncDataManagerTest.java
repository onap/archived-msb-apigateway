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
package org.onap.msb.apiroute;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;
import org.onap.msb.apiroute.wrapper.consulextend.util.Http;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.core.type.TypeReference;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Http.class})
@PowerMockIgnore({"javax.net.ssl.*"})
public class SyncDataManagerTest {

    @SuppressWarnings("unchecked")
    @Before
    public void setUpBeforeTest() {
        Http http = PowerMockito.mock(Http.class);

        PowerMockito.doNothing().when(http).asyncGet(Mockito.anyString(), Mockito.any(TypeReference.class),
                        Mockito.any(ConsulResponseCallback.class));

        PowerMockito.doNothing().when(http).asyncGetDelayHandle(Mockito.anyString(), Mockito.any(TypeReference.class),
                        Mockito.any(ConsulResponseCallback.class));

        //
        PowerMockito.spy(Http.class);
        PowerMockito.when(Http.getInstance()).thenReturn(http);

    }

    @Test
    public void testSyncDataManager() {
        SyncDataManager.initSyncTask("127.0.0.1", 8500);
        SyncDataManager.startWatchService("huangleibo");
        SyncDataManager.resetIndex("huangleibo");
        SyncDataManager.stopWatchService("huangleibo");
        SyncDataManager.stopWatchServiceList();
    }
}
