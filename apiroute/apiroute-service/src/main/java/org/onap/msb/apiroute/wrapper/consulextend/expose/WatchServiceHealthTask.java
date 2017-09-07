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
import java.util.List;

import org.onap.msb.apiroute.wrapper.consulextend.HealthClient;
import org.onap.msb.apiroute.wrapper.consulextend.cache.ConsulCache.Listener;
import org.onap.msb.apiroute.wrapper.consulextend.cache.ServiceHealthCache;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.option.CatalogOptions;
import com.orbitz.consul.option.QueryOptions;

public class WatchServiceHealthTask extends WatchTask<List<ServiceHealth>> {
    private final static Logger LOGGER = LoggerFactory.getLogger(WatchServiceHealthTask.class);

    private ServiceHealthCache serviceHealthCache = null;
    private String serviceName = "";

    public String getServiceName() {
        return serviceName;
    }

    public WatchServiceHealthTask(final HealthClient healthClient, final String serviceName, final boolean passing,
                    final CatalogOptions catalogOptions, final int watchSeconds, final QueryOptions queryOptions) {
        initCache(healthClient, serviceName, passing, catalogOptions, watchSeconds, queryOptions);
    }

    public WatchServiceHealthTask(final HealthClient healthClient, final String serviceName, final boolean passing,
                    final int watchSeconds)

    {
        initCache(healthClient, serviceName, passing, CatalogOptions.BLANK, watchSeconds, QueryOptions.BLANK);
    }

    public WatchServiceHealthTask(final HealthClient healthClient, final String serviceName, final int watchSeconds)

    {
        initCache(healthClient, serviceName, true, CatalogOptions.BLANK, watchSeconds, QueryOptions.BLANK);
    }

    private ServiceHealthCache initCache(final HealthClient healthClient, final String serviceName,
                    final boolean passing, final CatalogOptions catalogOptions, final int watchSeconds,
                    final QueryOptions queryOptions) {
        // LOGGER.info("************create {} watch task*****************",serviceName);
        this.serviceName = serviceName;
        serviceHealthCache = ServiceHealthCache.newCache(healthClient, serviceName, passing, catalogOptions,
                        watchSeconds, queryOptions);

        serviceHealthCache.addListener((Listener<List<ServiceHealth>>) new InternalListener());

        return serviceHealthCache;
    }

    public boolean startWatch() {

        if (serviceHealthCache != null) {
            try {
                serviceHealthCache.start();
                LOGGER.info("************start {} watch task*****************", serviceName);
                return true;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                LOGGER.warn("start service watch failed:", e);
            }
        }

        return false;

    }

    public boolean stopWatch() {
        if (serviceHealthCache != null) {
            try {
                serviceHealthCache.stop();
                LOGGER.info("************stop {} watch task*****************", serviceName);
                return true;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                LOGGER.warn("stop service watch failed:", e);
            }
        }

        return false;
    }


    public boolean resetIndex() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("reset " + serviceName + " consul index");
        }

        // reset consul index
        serviceHealthCache.updateIndex(BigInteger.valueOf(0));


        // reset modify index
        for (WatchTask.Filter<List<ServiceHealth>> filter : getAllFilters()) {
            if (filter instanceof ServiceModifyIndexFilter) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("reset " + serviceName + " modify index");
                }
                return ((ServiceModifyIndexFilter) filter).resetModifyIndex();
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("reset modify index.did not find filter:" + serviceName);
        }

        return false;
    }
}
