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
package org.openo.msb.wrapper.consul.cache;

import java.math.BigInteger;
import java.util.List;

import org.openo.msb.wrapper.consul.CatalogClient;
import org.openo.msb.wrapper.consul.async.ConsulResponseCallback;
import org.openo.msb.wrapper.consul.model.catalog.CatalogService;

import com.google.common.base.Function;

public class CatalogCache extends ConsulCache<String, CatalogService>{
    
    private  final String serviceName;

    private  CatalogCache(Function<CatalogService, String> keyConversion,
            ConsulCache.CallbackConsumer<CatalogService> callbackConsumer,String serviceName) {
        super(keyConversion, callbackConsumer);
        this.serviceName=serviceName;
        // TODO Auto-generated constructor stub
    }
    
    
    public static CatalogCache newCache(
        final CatalogClient catalogClient,
        final String serviceName,
        final int watchSeconds){
       Function<CatalogService,String> keyExtractor = new Function<CatalogService, String>() {
           @Override
           public String apply(CatalogService input) {
               //return input.getKey().substring(rootPath.length() + 1);
               return input.getServiceId();
           }
       };  
       
       final CallbackConsumer<CatalogService> callbackConsumer = new CallbackConsumer<CatalogService>() {
           @Override
           public void consume(BigInteger index, ConsulResponseCallback<List<CatalogService>> callback) {
               catalogClient.getService(serviceName,  watchParams(index, watchSeconds),callback);
           }
       };
       
        
       return new CatalogCache(keyExtractor, callbackConsumer,serviceName);
        
        
    }
    
    public String getServiceName(){
        return this.serviceName;
    }
    

}
