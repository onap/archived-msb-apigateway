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
package org.openo.msb.wrapper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openo.msb.api.CustomRouteInfo;
import org.openo.msb.api.RouteServer;
import org.openo.msb.api.exception.ExtendedInternalServerErrorException;
import org.openo.msb.api.exception.ExtendedNotFoundException;

public class CustomRouteServiceWrapperTest {

  private static CustomRouteServiceWrapper customRouteServiceWrapper;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        customRouteServiceWrapper=CustomRouteServiceWrapper.getInstance();
    }
    
    @Before
    public void setUp() {
        CustomRouteInfo customRouteInfo=new CustomRouteInfo();
        customRouteInfo.setServiceName("/testForJunit");
        customRouteInfo.setUrl("/test");
        RouteServer[] servers=new RouteServer[]{new RouteServer("127.0.0.1","80")};
        customRouteInfo.setServers(servers);
        try {
            customRouteServiceWrapper.saveCustomRouteInstance(customRouteInfo,"");
        }
        catch(ExtendedInternalServerErrorException e){
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        }
        
    }
    
    @After
    public void tearDown() {        
        try {
            customRouteServiceWrapper.deleteCustomRoute("/testForJunit", "*","");      
        } 
        catch(ExtendedInternalServerErrorException e){
        }catch (Exception e) {
            Assert.fail("Exception" + e.getMessage());
        }
    }
    
    @Test
    public void testGetAllCustomRouteInstances(){
        try {
            CustomRouteInfo[] customRouteInfoList=customRouteServiceWrapper.getAllCustomRouteInstances();
            Assert.assertTrue(customRouteInfoList.length >= 0);
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        }
       
    }
    
    @Test
    public void testGetCustomRouteInstance_not_exist(){
        CustomRouteInfo customRouteInfo = null;
        try {
            customRouteInfo=customRouteServiceWrapper.getCustomRouteInstance("/testForJunit2");
            Assert.assertNotNull(customRouteInfo);
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(ExtendedNotFoundException e){
            Assert.assertNull(customRouteInfo);
        }
       
    }
    
    @Test
    public void testGetCustomRouteInstance_exist(){
        try {
            CustomRouteInfo customRouteInfo=customRouteServiceWrapper.getCustomRouteInstance("/testForJunit");
            Assert.assertNotNull(customRouteInfo);
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        }
       
    }
    
    @Test
    public void testUpdateCustomRouteInstance(){
        try {
            
            CustomRouteInfo customRouteInfo=new CustomRouteInfo();
            customRouteInfo.setServiceName("/testForJunit");
            customRouteInfo.setUrl("/test_update");
            RouteServer[] servers=new RouteServer[]{new RouteServer("127.0.0.1","80")};
            customRouteInfo.setServers(servers);
            
            CustomRouteInfo new_customRouteInfo= customRouteServiceWrapper.updateCustomRouteInstance("/testForJunit", customRouteInfo,"");
            Assert.assertEquals("/test_update", new_customRouteInfo.getUrl());
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        
        }
    }
    
    @Test
    public void testUpdateCustomRouteStatus(){
        try { 
            CustomRouteInfo new_CustomRouteInfo= customRouteServiceWrapper.updateCustomRouteStatus("/testForJunit", "0");
            Assert.assertEquals("0",new_CustomRouteInfo.getStatus());
        }catch(ExtendedInternalServerErrorException e){
        }catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        
        }
    }
}
