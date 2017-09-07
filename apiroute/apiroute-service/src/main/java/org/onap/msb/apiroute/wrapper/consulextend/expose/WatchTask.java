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

import java.util.concurrent.CopyOnWriteArrayList;

import org.onap.msb.apiroute.wrapper.consulextend.cache.ConsulCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.model.ConsulResponse;

public abstract class WatchTask<T> {
    private final CopyOnWriteArrayList<Filter<T>> filters = new CopyOnWriteArrayList<Filter<T>>();
    private final CopyOnWriteArrayList<Handler<T>> handlers = new CopyOnWriteArrayList<Handler<T>>();
    private final static Logger LOGGER = LoggerFactory.getLogger(WatchTask.class);

    // start
    public abstract boolean startWatch();

    // stop
    public abstract boolean stopWatch();

    // filters
    public interface Filter<T> {
        public boolean filter(final ConsulResponse<T> object);
    }

    public boolean addFilter(Filter<T> filter) {
        boolean added = filters.add(filter);
        return added;
    }

    public void removeAllFilter() {
        filters.clear();
    }


    public final CopyOnWriteArrayList<Filter<T>> getAllFilters() {
        return filters;
    }

    // handlers
    public interface Handler<T> {
        void handle(final ConsulResponse<T> object);
    }

    public boolean addHandler(Handler<T> handler) {
        boolean added = handlers.add(handler);
        return added;
    }

    public void removeAllHandler() {
        handlers.clear();
    }

    // internal listener
    protected class InternalListener implements ConsulCache.Listener<T> {
        @Override
        public void notify(ConsulResponse<T> newValues) {

            long startTime = System.currentTimeMillis();

            // filter
            for (Filter<T> f : filters) {
                // false,return
                if (!f.filter(newValues)) {
                    return;
                }
            }

            // handle
            for (Handler<T> h : handlers) {
                h.handle(newValues);
            }

            long endTime = System.currentTimeMillis();

            if (endTime - startTime > 10 * 1000) {
                LOGGER.info("WatchTask THEAD WORK TIMEOUT");
            }
        }

    }


}
