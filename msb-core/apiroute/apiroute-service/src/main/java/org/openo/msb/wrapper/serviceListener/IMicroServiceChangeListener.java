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
package org.openo.msb.wrapper.serviceListener;

import org.openo.msb.api.MicroServiceInfo;
import org.openo.msb.api.Service;


public interface IMicroServiceChangeListener {
    public void onSave(Service microServiceInfo,String serverPort);
    
    public void onChange(String serviceName,String version,Service microServiceInfo,String serverPort);
    
    public void onStatusChange(String serviceName,String url,String version,String protocol,String status);
    
    public void onDelete(String serviceName, String url,String version,String protocol,String serverPort);

}
