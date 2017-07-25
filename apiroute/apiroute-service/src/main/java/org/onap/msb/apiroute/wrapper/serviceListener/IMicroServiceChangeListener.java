/**
 * Copyright 2016 ZTE, Inc. and others.
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


package org.onap.msb.apiroute.wrapper.serviceListener;

import org.onap.msb.apiroute.api.MicroServiceFullInfo;
import org.onap.msb.apiroute.api.Node;


public interface IMicroServiceChangeListener {
    public void onSave(MicroServiceFullInfo microServiceInfo) throws Exception;
    
    public void onDelete(MicroServiceFullInfo microServiceInfo) throws Exception;
    
    public void onChange(String serviceName,String version,MicroServiceFullInfo microServiceInfo) throws Exception;
    
    public void onStatusChange(String serviceName,String version,String host, String protocol,String publish_port,
        String status);
    



}
