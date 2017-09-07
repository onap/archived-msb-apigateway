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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.onap.msb.apiroute.wrapper.consulextend.Consul;
import org.onap.msb.apiroute.wrapper.consulextend.expose.CheckServiceDataEmptyAndAutoStopWatchFilter;
import org.onap.msb.apiroute.wrapper.consulextend.expose.CheckTagAndAutoStopWatchFilter;
import org.onap.msb.apiroute.wrapper.consulextend.expose.ServiceModifyIndexFilter;
import org.onap.msb.apiroute.wrapper.consulextend.expose.WatchCatalogServicesTask;
import org.onap.msb.apiroute.wrapper.consulextend.expose.WatchServiceHealthTask;
import org.onap.msb.apiroute.wrapper.consulextend.expose.WriteBufferHandler;
import org.onap.msb.apiroute.wrapper.queue.ServiceConsumer;
import org.onap.msb.apiroute.wrapper.queue.ServiceData;
import org.onap.msb.apiroute.wrapper.queue.ServiceListConsumer;
import org.onap.msb.apiroute.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncDataManager {
    private static Consul consul;
    private static WatchCatalogServicesTask serviceListWatchTask;
    private final static Map<String, WatchServiceHealthTask> serviceWatchTaskMap =
                    new ConcurrentHashMap<String, WatchServiceHealthTask>();

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncDataManager.class);

    private SyncDataManager() {}

    public static void initSyncTask(final String ip, final int port) {
        consul = Consul.builder().withHostAndPort(ip, port).build();
        startWatchServiceList();
        startQueueConsumer();
    }

    public static void startWatchServiceList() {

        LOGGER.info("===========start to WatchServiceList============");

        // create service list watch task
        serviceListWatchTask = new WatchCatalogServicesTask(consul.catalogClient(), RouteUtil.WATCH_SECOND);

        // first,write data to serviceListQueue buffer.
        // second,async thread will read data from serviceListQueue buffer.
        serviceListWatchTask.addHandler(new WriteBufferHandler<HttpEntity>(ServiceData.DataType.service_list));

        // start watch
        serviceListWatchTask.startWatch();
    }

    public static void startQueueConsumer() {
        LOGGER.info("===========start to QueueConsumer Thread============");

        // start ServiceListConsumer
        new Thread(new ServiceListConsumer(), "ServiceListConsumerThread").start();

        // start Service Consumer
        int serviceQueneNum = RouteUtil.SERVICE_DATA_QUEUE_NUM;
        for (int i = 0; i < serviceQueneNum; i++) {
            new Thread(new ServiceConsumer(i), "ServiceConsumerThread" + i).start();
        }

    }

    public static void startWatchService(final String serviceName) {

        LOGGER.info("===========start to Watch Service[" + serviceName + "]============");
        // create service watch task
        WatchServiceHealthTask serviceWatchTask =
                        new WatchServiceHealthTask(consul.healthClient(), serviceName, RouteUtil.WATCH_SECOND);

        // 1.service Data Empty filter
        serviceWatchTask.addFilter(new CheckServiceDataEmptyAndAutoStopWatchFilter(serviceName));

        // 2.service change filter
        serviceWatchTask.addFilter(new ServiceModifyIndexFilter());

        // 3.apigateway tag filter:check tag and auto stop watch
        serviceWatchTask.addFilter(new CheckTagAndAutoStopWatchFilter(serviceName));

        // start watch
        serviceWatchTask.startWatch();

        // save
        serviceWatchTaskMap.put(serviceName, serviceWatchTask);
    }

    public static void stopWatchServiceList() {
        if (serviceListWatchTask != null) {
            serviceListWatchTask.removeAllFilter();
            serviceListWatchTask.removeAllHandler();
            serviceListWatchTask.stopWatch();
        }
    }

    public static void stopWatchService(String serviceName) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stop " + serviceName + " service watch!");
        }

        WatchServiceHealthTask watchTask = serviceWatchTaskMap.get(serviceName);
        if (watchTask != null) {
            watchTask.removeAllFilter();
            watchTask.removeAllHandler();
            watchTask.stopWatch();
        }
        serviceWatchTaskMap.remove(serviceName);
    }

    public static boolean resetIndex(String serviceName) {

        WatchServiceHealthTask watchTask = serviceWatchTaskMap.get(serviceName);

        if (watchTask != null) {
            return watchTask.resetIndex();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("reset modify index.did not find:" + serviceName);
        }

        return false;
    }

}
