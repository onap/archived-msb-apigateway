/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.onap.msb.apiroute.wrapper.consulextend.cache;

import java.math.BigInteger;

import org.apache.http.HttpEntity;
import org.onap.msb.apiroute.wrapper.consulextend.CatalogClient;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;

import com.orbitz.consul.option.CatalogOptions;
import com.orbitz.consul.option.QueryOptions;

public class ServicesCatalogCache extends ConsulCache<HttpEntity> {
	
    private ServicesCatalogCache(CallbackConsumer<HttpEntity> callbackConsumer) {
        super(callbackConsumer);
    }

    public static ServicesCatalogCache newCache(
            final CatalogClient catalogClient,
            final CatalogOptions catalogOptions,
            final QueryOptions queryOptions,
            final int watchSeconds) {
    	
        CallbackConsumer<HttpEntity> callbackConsumer = new CallbackConsumer<HttpEntity>() {
            @Override
            public void consume(BigInteger index, ConsulResponseCallback<HttpEntity> callback) {
            	QueryOptions params = watchParams(index, watchSeconds, queryOptions);
            	catalogClient.getServices(catalogOptions, params,callback);
            }
        };

        return new ServicesCatalogCache(callbackConsumer);

    }
    
    public static ServicesCatalogCache newCache(final CatalogClient catalogClient) {
        return newCache(catalogClient, CatalogOptions.BLANK, QueryOptions.BLANK, 10);
    }
}
