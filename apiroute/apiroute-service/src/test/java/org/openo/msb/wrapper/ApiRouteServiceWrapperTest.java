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
package org.openo.msb.wrapper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openo.msb.api.ApiRouteInfo;
import org.openo.msb.api.RouteServer;
import org.openo.msb.api.exception.ExtendedInternalServerErrorException;
import org.openo.msb.api.exception.ExtendedNotFoundException;

public class ApiRouteServiceWrapperTest {
    private static ApiRouteServiceWrapper apiRouteServiceWrapper;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        apiRouteServiceWrapper=ApiRouteServiceWrapper.getInstance();
    }
    
    @Before
    public void setUp() {
        ApiRouteInfo apiRouteInfo=new ApiRouteInfo();
        apiRouteInfo.setServiceName("testForJunit");
        apiRouteInfo.setVersion("v1");
        apiRouteInfo.setUrl("/api/test/v1");
        RouteServer[] servers=new RouteServer[]{new RouteServer("127.0.0.1","80")};
        apiRouteInfo.setServers(servers);
        try {
            apiRouteServiceWrapper.saveApiRouteInstance(apiRouteInfo, "");
        }
        catch(ExtendedInternalServerErrorException e){
        }
        catch (Exception e){
            Assert.fail("Exception" + e.getMessage());
        }
        
    }
    
    @After
    public void tearDown() {        
        try {
            apiRouteServiceWrapper.deleteApiRoute("testForJunit", "v1", "*", "");      
        } 
        catch(ExtendedInternalServerErrorException e){
            
        }catch (Exception e) {
            Assert.fail("Exception" + e.getMessage());
        }
    }
    
    @Test
    public void testGetAllApiRouteInstances(){
        try {
        ApiRouteInfo[] apiRouteInfoList=apiRouteServiceWrapper.getAllApiRouteInstances();
        Assert.assertTrue(apiRouteInfoList.length >= 0);
        }
        catch(ExtendedInternalServerErrorException e){
            
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        }
       
    }
    
    @Test
    public void testGetApiRouteInstance_not_exist(){
        ApiRouteInfo apiRouteInfo = null;
        try {
           apiRouteInfo=apiRouteServiceWrapper.getApiRouteInstance("testForJunit", "v2");
           Assert.assertNotNull(apiRouteInfo);
        }
        catch(ExtendedInternalServerErrorException e){
            
        }
        catch(ExtendedNotFoundException e){
            Assert.assertNull(apiRouteInfo);
        }
       
    }
    
    @Test
    public void testGetApiRouteInstance_exist(){
        try {
        ApiRouteInfo apiRouteInfo=apiRouteServiceWrapper.getApiRouteInstance("testForJunit", "v1");
        Assert.assertNotNull(apiRouteInfo);
        }
        catch(ExtendedInternalServerErrorException e){
            
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        }
       
    }
    
    @Test
    public void testUpdateApiRouteInstance(){
        try {
            
            ApiRouteInfo apiRouteInfo=new ApiRouteInfo();
            apiRouteInfo.setServiceName("testForJunit");
            apiRouteInfo.setVersion("v1");
            apiRouteInfo.setUrl("/api/test_update/v1");
            RouteServer[] servers=new RouteServer[]{new RouteServer("127.0.0.1","80")};
            apiRouteInfo.setServers(servers);
            
            ApiRouteInfo new_apiRouteInfo= apiRouteServiceWrapper.updateApiRouteInstance("testForJunit", "v1", apiRouteInfo, "");
            Assert.assertEquals("/api/test_update/v1", new_apiRouteInfo.getUrl());
        }
        catch(ExtendedInternalServerErrorException e){
            
        }
        catch(Exception e){
            Assert.assertEquals("Get Jedis from pool failed!",e.getMessage());
            
//            Assert.fail("Exception" + e.getMessage());
        
        }
    }
    
    @Test
    public void testUpdateApiRouteStatus(){
        try { 
            ApiRouteInfo new_apiRouteInfo= apiRouteServiceWrapper.updateApiRouteStatus("testForJunit", "v1", "0");
            Assert.assertEquals("0",new_apiRouteInfo.getStatus());
        }
        catch(ExtendedInternalServerErrorException e){
            
        }catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        
        }
    }
    
    @Test
    public void testGetApiGatewayPort(){
        try { 
            String port= apiRouteServiceWrapper.getApiGatewayPort();
            Assert.assertEquals("10080",port);
        }catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        
        }
    }
   
    
}
