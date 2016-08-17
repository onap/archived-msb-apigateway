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

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.openo.msb.wrapper.consul.CatalogClient;
import org.openo.msb.wrapper.consul.async.ConsulResponseCallback;

public class ServiceCache  extends ConsulCache4Map<String, Map<String, List<String>>> {
    private  ServiceCache( ConsulCache4Map.CallbackConsumer<Map<String, List<String>>> callbackConsumer) {
                      super(callbackConsumer);
                      // TODO Auto-generated constructor stub
                  }
                  
                  
                  public static ServiceCache newCache(
                      final CatalogClient catalogClient,
                      final int watchSeconds){
                   
                     
                     final CallbackConsumer<Map<String, List<String>>> callbackConsumer = new CallbackConsumer<Map<String, List<String>>>() {
                         @Override
                         public void consume(BigInteger index, ConsulResponseCallback<Map<String, List<String>>> callback) {
                             catalogClient.getService(watchParams(index, watchSeconds),callback);
                         }
                     };
                     
                      
                     return new ServiceCache(callbackConsumer);
                      
                      
                  }
}
