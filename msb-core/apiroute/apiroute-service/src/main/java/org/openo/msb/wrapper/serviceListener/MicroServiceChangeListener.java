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

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openo.msb.api.ApiRouteInfo;
import org.openo.msb.api.CustomRouteInfo;
import org.openo.msb.api.IuiRouteInfo;
import org.openo.msb.api.Node;
import org.openo.msb.api.RouteServer;
import org.openo.msb.api.Service;
import org.openo.msb.wrapper.ApiRouteServiceWrapper;
import org.openo.msb.wrapper.CustomRouteServiceWrapper;
import org.openo.msb.wrapper.IuiRouteServiceWrapper;
import org.openo.msb.wrapper.util.RegExpTestUtil;
import org.openo.msb.wrapper.util.RouteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MicroServiceChangeListener implements IMicroServiceChangeListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MicroServiceChangeListener.class);
    
    @Override
    public void onSave(Service microServiceInfo,String serverPort) {
        
        if("UI".equals(microServiceInfo.getProtocol())){
            IuiRouteInfo iuiRouteInfo = this.buildIuiRouteInfo(microServiceInfo);
            if(null != iuiRouteInfo){
                IuiRouteServiceWrapper.getInstance().saveIuiRouteInstance(iuiRouteInfo);
            }
        }
        else{        
        
            if(ifApiRouteUrl(microServiceInfo.getUrl())){
                ApiRouteInfo apiRouteInfo = this.buildApiRouteInfo(microServiceInfo);
                if(null != apiRouteInfo){
                    ApiRouteServiceWrapper.getInstance().saveApiRouteInstance(apiRouteInfo,serverPort);
                }
            }
            else{
                CustomRouteInfo customRouteInfo = this.buildCustomRouteInfo(microServiceInfo);
                if(null != customRouteInfo){
                    CustomRouteServiceWrapper.getInstance().saveCustomRouteInstance(customRouteInfo,serverPort);
                }
            }
        }
        
        
    }
    
    @Override
    public void onChange(String serviceName, String version, Service microServiceInfo,String serverPort) {
        
        if("UI".equals(microServiceInfo.getProtocol())){
            if(serviceName.startsWith("iui_")||serviceName.startsWith("IUI_")){
                serviceName=serviceName.substring(4);
            }
            IuiRouteInfo iuiRouteInfo = this.buildIuiRouteInfo(microServiceInfo);
            if(null != iuiRouteInfo){
                IuiRouteServiceWrapper.getInstance().updateIuiRouteInstance(serviceName, iuiRouteInfo);
            }
        }
        else{
        
            if(ifApiRouteUrl(microServiceInfo.getUrl())){
                ApiRouteInfo apiRouteInfo = this.buildApiRouteInfo(microServiceInfo);
                if(null != apiRouteInfo){
                    ApiRouteServiceWrapper.getInstance().updateApiRouteInstance(serviceName, version, apiRouteInfo,serverPort);
                }
            }
            else{
                if(!serviceName.startsWith("/")){
                    serviceName="/"+serviceName;
                }
                CustomRouteInfo customRouteInfo = this.buildCustomRouteInfo(microServiceInfo);
                if(null != customRouteInfo){
                    CustomRouteServiceWrapper.getInstance().updateCustomRouteInstance(serviceName,customRouteInfo,serverPort);
                }
            }
        }
        
    }
    
    @Override
    public void onStatusChange(String serviceName,String url,String version,String protocol,String status) {
        if("UI".equals(protocol)){

            if(serviceName.startsWith("iui_")||serviceName.startsWith("IUI_")){
                serviceName=serviceName.substring(4);
            } 
           IuiRouteServiceWrapper.getInstance().updateIuiRouteStatus(serviceName, status);
            
        }
        else{
            if(ifApiRouteUrl(url)){
                ApiRouteServiceWrapper.getInstance().updateApiRouteStatus(serviceName, version, status);
            }
            else{
                if(!serviceName.startsWith("/")){
                    serviceName="/"+serviceName;
                }
                CustomRouteServiceWrapper.getInstance().updateCustomRouteStatus(serviceName, status); 
            }
        }
        
      

    }

    @Override
    public void onDelete(String serviceName,String url, String version,String protocol,String serverPort) {
        
        if("UI".equals(protocol)){
            if(serviceName.startsWith("iui_")||serviceName.startsWith("IUI_")){
                serviceName=serviceName.substring(4);
            } 
           IuiRouteServiceWrapper.getInstance().deleteIuiRoute(serviceName, "*");
            
        }
        else{
            if(ifApiRouteUrl(url)){
                ApiRouteServiceWrapper.getInstance().deleteApiRoute(serviceName, version, "*",serverPort);
            }
            else{

              
                if(!serviceName.startsWith("/")){
                    serviceName="/"+serviceName;
                }
                
                CustomRouteServiceWrapper.getInstance().deleteCustomRoute(serviceName, "*",serverPort); 
            }
        }
    }
    
    
    /** 
    * @Title ifApiRouteUrl 
    * @Description TODO(According to judge whether the API registration URL format) 
    * @param url
    * @return      
    * @return boolean    
    */
    private boolean ifApiRouteUrl(String url){
        return RegExpTestUtil.apiRouteUrlRegExpTest(url);
    }
    
   
    /**
     * From MicroServiceInfo to ApiRouteInfo
     * @param microServiceInfo
     * @return
     */
    private ApiRouteInfo buildApiRouteInfo(Service microServiceInfo){
 
        ApiRouteInfo apiRouteInfo = new ApiRouteInfo();
        apiRouteInfo.setUrl(microServiceInfo.getUrl());
        
        Set<Node> nodes=microServiceInfo.getNodes();
        RouteServer[] routeServers=new RouteServer[nodes.size()];
         
        
        int i=0;
        for(Node node:nodes){
            RouteServer routeServer = new RouteServer(node.getIp(),node.getPort()); 
            routeServers[i]=routeServer;
            i++;
        }      
        
               
        apiRouteInfo.setServers(routeServers);
        String[] rangs=StringUtils.split(microServiceInfo.getVisualRange(), "|");
        if(RouteUtil.contain(rangs, "0")){
            apiRouteInfo.setVisualRange("0");
        }
        else{
            apiRouteInfo.setVisualRange("1");
        }
       
        
      if("ip_hash".equals(microServiceInfo.getLb_policy())){
          apiRouteInfo.setUseOwnUpstream("1");
    }
       
        
        
        apiRouteInfo.setServiceName(microServiceInfo.getServiceName());
        apiRouteInfo.setVersion(microServiceInfo.getVersion());
        //TODO:set json and metrics defaultValue
        String version="".equals(microServiceInfo.getVersion())?"":"/"+microServiceInfo.getVersion();
        apiRouteInfo.setApiJson(microServiceInfo.getUrl()+"/swagger.json");
        apiRouteInfo.setMetricsUrl("/admin/metrics");
        return apiRouteInfo;
    }
    
    
    /**
     * From MicroServiceInfo to CustomRouteInfo
     * @param microServiceInfo
     * @return
     */
    private CustomRouteInfo buildCustomRouteInfo(Service microServiceInfo){
      
        CustomRouteInfo customRouteInfo = new CustomRouteInfo();
        customRouteInfo.setUrl(microServiceInfo.getUrl());
        
        Set<Node> nodes=microServiceInfo.getNodes();
        RouteServer[] routeServers=new RouteServer[nodes.size()];
         
        
        int i=0;
        for(Node node:nodes){
            RouteServer routeServer = new RouteServer(node.getIp(),node.getPort()); 
            routeServers[i]=routeServer;
            i++;
        }      
               
        customRouteInfo.setServers(routeServers);
        String[] rangs=StringUtils.split(microServiceInfo.getVisualRange(), "|");
        if(RouteUtil.contain(rangs, "0")){
            customRouteInfo.setVisualRange("0");
        }
        else{
            customRouteInfo.setVisualRange("1");
        }
        
        if("ip_hash".equals(microServiceInfo.getLb_policy())){
            customRouteInfo.setUseOwnUpstream("1");
        }
        
        String serviceName;
        if(!microServiceInfo.getServiceName().startsWith("/")){
            serviceName="/"+microServiceInfo.getServiceName();
        }
        else{
            serviceName=microServiceInfo.getServiceName();
        }
        customRouteInfo.setServiceName(serviceName);
       
        return customRouteInfo;
    }
    
    
    /**
     * From MicroServiceInfo to IuiRouteInfo
     * @param microServiceInfo
     * @return
     */
    private IuiRouteInfo buildIuiRouteInfo(Service microServiceInfo){
      
        IuiRouteInfo iuiRouteInfo = new IuiRouteInfo();
        iuiRouteInfo.setUrl(microServiceInfo.getUrl());
               
        Set<Node> nodes=microServiceInfo.getNodes();
        RouteServer[] routeServers=new RouteServer[nodes.size()];
         
        
        int i=0;
        for(Node node:nodes){
            RouteServer routeServer = new RouteServer(node.getIp(),node.getPort()); 
            routeServers[i]=routeServer;
            i++;
        }      
               
        iuiRouteInfo.setServers(routeServers);
        String[] rangs=StringUtils.split(microServiceInfo.getVisualRange(), "|");
        if(RouteUtil.contain(rangs, "0")){
            iuiRouteInfo.setVisualRange("0");
        }
        else{
            iuiRouteInfo.setVisualRange("1");
        }
        
        if("ip_hash".equals(microServiceInfo.getLb_policy())){
            iuiRouteInfo.setUseOwnUpstream("1");
        }

        String serviceName=microServiceInfo.getServiceName();
        if(serviceName.startsWith("iui_")||serviceName.startsWith("IUI_")){
            serviceName=serviceName.substring(4);
        }
        
        
        iuiRouteInfo.setServiceName(serviceName);
       
        return iuiRouteInfo;
    }
}
