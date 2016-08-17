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

package org.openo.msb.wrapper.consul.cache;


import static com.google.common.base.Preconditions.checkState;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.openo.msb.wrapper.consul.async.ConsulResponseCallback;
import org.openo.msb.wrapper.consul.model.ConsulResponse;
import org.openo.msb.wrapper.consul.model.catalog.CatalogService;
import org.openo.msb.wrapper.consul.model.catalog.ServiceInfo;
import org.openo.msb.wrapper.consul.option.QueryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

/**
 * A cache structure that can provide an up-to-date read-only
 * map backed by consul data
 *
 * @param <V>
 */
public class ConsulCache4Map<K, V> {

    enum State {latent, starting, started, stopped }

    private final static Logger LOGGER = LoggerFactory.getLogger(ConsulCache4Map.class);

    private final AtomicReference<BigInteger> latestIndex = new AtomicReference<BigInteger>(null);
    private final AtomicReference<ImmutableList<ServiceInfo>> lastResponse = new AtomicReference<ImmutableList<ServiceInfo>>(ImmutableList.<ServiceInfo>of());
    private final AtomicReference<State> state = new AtomicReference<State>(State.latent);
    private final CountDownLatch initLatch = new CountDownLatch(1);
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final CopyOnWriteArrayList<Listener<K, V>> listeners = new CopyOnWriteArrayList<Listener<K, V>>();

    private final CallbackConsumer<V> callBackConsumer;
    private final ConsulResponseCallback<Map<String,List<String>>> responseCallback;

    ConsulCache4Map(CallbackConsumer<V> callbackConsumer) {
        this( callbackConsumer, 10, TimeUnit.SECONDS);
    }

    ConsulCache4Map(
            CallbackConsumer<V> callbackConsumer,
            final long backoffDelayQty,
            final TimeUnit backoffDelayUnit) {

        this.callBackConsumer = callbackConsumer;

        this.responseCallback = new ConsulResponseCallback<Map<String,List<String>>>() {
            @Override
            public void onComplete(ConsulResponse<Map<String,List<String>>> consulResponse) {

                if (!isRunning()) {
                    return;
                }
                updateIndex(consulResponse);
                ImmutableList<ServiceInfo> full = convertToList(consulResponse);
                List<ServiceInfo> oldList=lastResponse.get();
                boolean changed = !full.equals(lastResponse.get());
//                LOGGER.info("service changed:"+changed+"----"+full);
                if (changed) {
                    // changes
                    lastResponse.set(full);
                }

                if (changed) {
                    for (Listener<K, V> l : listeners) {
                        l.notify(oldList,full);
                    }
                }

                if (state.compareAndSet(State.starting, State.started)) {
                    initLatch.countDown();
                }
                runCallback();
            }

            @Override
            public void onFailure(Throwable throwable) {

                if (!isRunning()) {
                    return;
                }
                LOGGER.error(String.format("Error getting response from consul. will retry in %d %s", backoffDelayQty, backoffDelayUnit), throwable);

                executorService.schedule(new Runnable() {
                    @Override
                    public void run() {
                        runCallback();
                    }
                }, backoffDelayQty, backoffDelayUnit);
            }
        };
    }

    public void start() throws Exception {
        checkState(state.compareAndSet(State.latent, State.starting),"Cannot transition from state %s to %s", state.get(), State.starting);
        runCallback();
    }

    public void stop() throws Exception {
        State previous = state.getAndSet(State.stopped);
        if (previous != State.stopped) {
            executorService.shutdownNow();
        }
    }

    private void runCallback() {
        if (isRunning()) {
            callBackConsumer.consume(latestIndex.get(), responseCallback);
        }
    }

    private boolean isRunning() {
        return state.get() == State.started || state.get() == State.starting;
    }

    public boolean awaitInitialized(long timeout, TimeUnit unit) throws InterruptedException {
        return initLatch.await(timeout, unit);
    }

    public ImmutableList<ServiceInfo> getMap() {
        return lastResponse.get();
    }

    @VisibleForTesting
    ImmutableList<ServiceInfo> convertToList(final ConsulResponse<Map<String,List<String>>> response) {
        if (response == null || response.getResponse() == null || response.getResponse().isEmpty()) {
            return ImmutableList.of();
        }

        final ImmutableList.Builder<ServiceInfo> builder = ImmutableList.builder();
        final Set<String> keySet = new HashSet<>();
        
        for(Map.Entry<String,List<String>> entry : response.getResponse().entrySet()) {
 
            String key = entry.getKey();  
           
            if (key != null && !"consul".equals(key)) {
                if (!keySet.contains(key)) {
                    ServiceInfo serviceInfo=new ServiceInfo();
                    serviceInfo.setServiceName(key);
                    
                    List<String> value=entry.getValue();
                    for(String tag:value){
                       
                        if(tag.startsWith("version")){
                             String version; 
                            if(tag.split(":").length==2)
                            {
                            version = tag.split(":")[1];
                            }
                            else{
                                version=""; 
                            }
                            
                            serviceInfo.setVersion(version);
                            break;
                        }
                    }
                    
                    builder.add(serviceInfo);
                } else {
                    System.out.println(key.toString());
                    LOGGER.warn("Duplicate service encountered. May differ by tags. Try using more specific tags? " + key.toString());
                }
            }
            keySet.add(key);
         
        }  
        
        
        return builder.build();
    }

    private void updateIndex(ConsulResponse<Map<String,List<String>>> consulResponse) {
        if (consulResponse != null && consulResponse.getIndex() != null) {
            this.latestIndex.set(consulResponse.getIndex());
        }
    }

    protected static QueryOptions watchParams(BigInteger index, int blockSeconds) {
        if (index == null) {
            return QueryOptions.BLANK;
        } else {
            return QueryOptions.blockSeconds(blockSeconds, index).build();
        }
    }

    /**
     * passed in by creators to vary the content of the cached values
     *
     * @param <V>
     */
    protected interface CallbackConsumer<V> {
        void consume(BigInteger index, ConsulResponseCallback<Map<String,List<String>>> callback);
    }

    /**
     * Implementers can register a listener to receive
     * a new map when it changes
     *
     * @param <V>
     */
    public interface Listener<K, V> {
        void notify(List<ServiceInfo> oldValues,List<ServiceInfo> newValues);
    }

    public boolean addListener(Listener<K, V> listener) {
        boolean added = listeners.add(listener);
        if (state.get() == State.started) {
            listener.notify(lastResponse.get(),lastResponse.get());
        }
        return added;
    }

    public boolean removeListener(Listener<K, V> listener) {
        return listeners.remove(listener);
    }

    @VisibleForTesting
    protected State getState() {
        return state.get();
    }
    
 
    
}
