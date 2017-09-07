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
package org.onap.msb.apiroute.wrapper.consulextend.expose;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.onap.msb.apiroute.wrapper.consulextend.Consul;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;
import org.onap.msb.apiroute.wrapper.consulextend.expose.WatchCatalogServicesTask;
import org.onap.msb.apiroute.wrapper.consulextend.expose.WatchTask;
import org.onap.msb.apiroute.wrapper.consulextend.expose.WatchTask.Filter;
import org.onap.msb.apiroute.wrapper.consulextend.util.Http;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.option.CatalogOptions;
import com.orbitz.consul.option.QueryOptions;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Http.class})
@PowerMockIgnore({"javax.net.ssl.*"})
public class WatchCatalogServicesTaskTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WatchCatalogServicesTaskTest.class);

    private Consul consul;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Before
    public void init() {

        Map<String, List<String>> catalogservices = new HashMap<String, List<String>>();
        String servicename = "huangleibo";
        List<String> tags = new ArrayList<String>();
        tags.add("1111");
        tags.add("2222");
        catalogservices.put(servicename, tags);

        long lastContact = 1;
        boolean knownLeader = true;
        BigInteger index = BigInteger.valueOf(1);
        final ConsulResponse<Map<String, List<String>>> response =
                        new ConsulResponse<Map<String, List<String>>>(catalogservices, lastContact, knownLeader, index);

        //
        Http http = PowerMockito.mock(Http.class);

        PowerMockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                ((ConsulResponseCallback) args[2]).onComplete(response);
                return null;
            }
        }).when(http).asyncGet(Mockito.anyString(), Mockito.any(TypeReference.class),
                        Mockito.any(ConsulResponseCallback.class));

        //
        PowerMockito.spy(Http.class);
        PowerMockito.when(Http.getInstance()).thenReturn(http);

        consul = Consul.newClient();

        WatchCatalogServicesTask task0 = new WatchCatalogServicesTask(consul.catalogClient(), CatalogOptions.BLANK,
                        QueryOptions.BLANK, 10);

        WatchCatalogServicesTask task1 = new WatchCatalogServicesTask(consul.catalogClient(), 10);

        WatchCatalogServicesTask task2 = new WatchCatalogServicesTask(consul.catalogClient());

    }

    public class StopHandler implements WatchTask.Handler<HttpEntity> {

        private WatchCatalogServicesTask task;

        StopHandler(WatchCatalogServicesTask task) {
            this.task = task;
        }

        @Override
        public void handle(ConsulResponse<HttpEntity> object) {
            // TODO Auto-generated method stub
            // Map<String, List<String>> map = (Map<String, List<String>>)object.getResponse();
            LOGGER.debug("handler is here");
            task.stopWatch();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void teststartWatch() {
        WatchCatalogServicesTask task0 = new WatchCatalogServicesTask(consul.catalogClient(), CatalogOptions.BLANK,
                        QueryOptions.BLANK, 10);

        task0.addFilter(new Filter() {
            @Override
            public boolean filter(ConsulResponse object) {
                // TODO Auto-generated method stub
                // Map<String, List<String>> map = (Map<String, List<String>>)object.getResponse();
                LOGGER.debug("filter is here");
                return true;
            }

        });

        task0.addHandler(new StopHandler(task0));

        task0.startWatch();
    }

}
