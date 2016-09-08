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

import org.openo.msb.wrapper.consul.HealthClient;
import org.openo.msb.wrapper.consul.async.ConsulResponseCallback;
import org.openo.msb.wrapper.consul.model.health.ServiceHealth;

import com.google.common.base.Function;

public class HealthCache extends ConsulCache<String, ServiceHealth>{
    
    private  final String serviceName;

    private  HealthCache(Function<ServiceHealth, String> keyConversion,
            ConsulCache.CallbackConsumer<ServiceHealth> callbackConsumer,String serviceName) {
        super(keyConversion, callbackConsumer);
        this.serviceName=serviceName;
        // TODO Auto-generated constructor stub
    }
    
    
    public static HealthCache newCache(
        final HealthClient healthClient,
        final String serviceName,
        final int watchSeconds){
       Function<ServiceHealth,String> keyExtractor = new Function<ServiceHealth, String>() {
           @Override
           public String apply(ServiceHealth input) {
               //return input.getKey().substring(rootPath.length() + 1);
               return input.getService().getId();
           }
       };  
       
       final CallbackConsumer<ServiceHealth> callbackConsumer = new CallbackConsumer<ServiceHealth>() {
           @Override
           public void consume(BigInteger index, ConsulResponseCallback<List<ServiceHealth>> callback) {
               healthClient.getHealthyServiceInstances(serviceName,  watchParams(index, watchSeconds),callback);
           }
       };
       
        
       return new HealthCache(keyExtractor, callbackConsumer,serviceName);
        
        
    }
    
    public String getServiceName(){
        return this.serviceName;
    }
    

}