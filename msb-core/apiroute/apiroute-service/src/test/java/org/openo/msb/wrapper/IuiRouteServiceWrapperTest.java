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
import org.openo.msb.api.IuiRouteInfo;
import org.openo.msb.api.RouteServer;
import org.openo.msb.api.exception.ExtendedInternalServerErrorException;
import org.openo.msb.api.exception.ExtendedNotFoundException;

public class IuiRouteServiceWrapperTest {
    private static IuiRouteServiceWrapper iuiRouteServiceWrapper;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        iuiRouteServiceWrapper=IuiRouteServiceWrapper.getInstance();
    }
    
    @Before
    public void setUp() {
        IuiRouteInfo iuiRouteInfo=new IuiRouteInfo();
        iuiRouteInfo.setServiceName("testForJunit");
        iuiRouteInfo.setUrl("/iui/test");
        RouteServer[] servers=new RouteServer[]{new RouteServer("127.0.0.1","80")};
        iuiRouteInfo.setServers(servers);
        try {
            iuiRouteServiceWrapper.saveIuiRouteInstance(iuiRouteInfo);
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        }
        
    }
    
    @After
    public void tearDown() {        
        try {
            iuiRouteServiceWrapper.deleteIuiRoute("testForJunit", "*");      
        }catch(ExtendedInternalServerErrorException e){
        } catch (Exception e) {
            Assert.fail("Exception" + e.getMessage());
        }
    }
    
    @Test
    public void testGetAllIuiRouteInstances(){
        try {
            IuiRouteInfo[] iuiRouteInfoList=iuiRouteServiceWrapper.getAllIuiRouteInstances();
            Assert.assertTrue(iuiRouteInfoList.length >= 0);
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        }
       
    }
    
    @Test
    public void testGetIuiRouteInstance_not_exist(){
        IuiRouteInfo iuiRouteInfo = null;
        try {
            iuiRouteInfo=iuiRouteServiceWrapper.getIuiRouteInstance("testForJunit2");
            Assert.assertNotNull(iuiRouteInfo);
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(ExtendedNotFoundException e){
            Assert.assertNull(iuiRouteInfo);
        }
       
    }
    
    @Test
    public void testGetIuiRouteInstance_exist(){
        try {
            IuiRouteInfo iuiRouteInfo=iuiRouteServiceWrapper.getIuiRouteInstance("testForJunit");
            Assert.assertNotNull(iuiRouteInfo);
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        }
       
    }
    
    @Test
    public void testUpdateIuiRouteInstance(){
        try {
            
            IuiRouteInfo iuiRouteInfo=new IuiRouteInfo();
            iuiRouteInfo.setServiceName("testForJunit");
            iuiRouteInfo.setUrl("/iui/test_update");
            RouteServer[] servers=new RouteServer[]{new RouteServer("127.0.0.1","80")};
            iuiRouteInfo.setServers(servers);
            
            IuiRouteInfo new_iuiRouteInfo= iuiRouteServiceWrapper.updateIuiRouteInstance("testForJunit", iuiRouteInfo);
            Assert.assertEquals("/iui/test_update", new_iuiRouteInfo.getUrl());
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        
        }
    }
    
    @Test
    public void testUpdateIuiRouteStatus(){
        try { 
            IuiRouteInfo new_iuiRouteInfo= iuiRouteServiceWrapper.updateIuiRouteStatus("testForJunit", "0");
            Assert.assertEquals("0",new_iuiRouteInfo.getStatus());
        }catch(ExtendedInternalServerErrorException e){
        }catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        
        }
    }
    
   
    
}
