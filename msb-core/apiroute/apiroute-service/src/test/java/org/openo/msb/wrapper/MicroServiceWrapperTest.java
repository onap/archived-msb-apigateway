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
package org.openo.msb.wrapper;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openo.msb.api.MicroServiceFullInfo;
import org.openo.msb.api.MicroServiceInfo;
import org.openo.msb.api.Node;
import org.openo.msb.api.exception.ExtendedInternalServerErrorException;
import org.openo.msb.api.exception.ExtendedNotFoundException;

public class MicroServiceWrapperTest {
 private static MicroServiceWrapper microServiceWrapper;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        microServiceWrapper=microServiceWrapper.getInstance();
    }
    
    @Before
    public void setUp() {
        MicroServiceInfo microServiceInfo=new MicroServiceInfo();
        microServiceInfo.setServiceName("testForJunit");
        microServiceInfo.setVersion("v1");
        microServiceInfo.setUrl("/api/test/v1");
        microServiceInfo.setProtocol("REST");
        microServiceInfo.setVisualRange("0");
        Set<Node> nodes=new HashSet<Node>();
        nodes.add(new Node("127.0.0.1","8080",0));
        microServiceInfo.setNodes(nodes);
        try {
            microServiceWrapper.saveMicroServiceInstance(microServiceInfo,true,"","");
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        }
        
    }
    
    @After
    public void tearDown() {        
        try {
            microServiceWrapper.deleteMicroService("testForJunit", "v1", "");      
        }catch(ExtendedInternalServerErrorException e){
        } catch (Exception e) {
            Assert.fail("Exception" + e.getMessage());
        }
    }
    
    @Test
    public void testGetAllMicroServiceInstances(){
        try {
         MicroServiceFullInfo[] MicroServiceInfoList=microServiceWrapper.getAllMicroServiceInstances();
        Assert.assertTrue(MicroServiceInfoList.length >= 0);
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        }
       
    }
    
    @Test
    public void testGetMicroServiceInstance_not_exist(){
        MicroServiceFullInfo microServiceInfo = null;
        try {
           microServiceInfo=microServiceWrapper.getMicroServiceInstance("testForJunit", "v2","");
           Assert.assertNotNull(microServiceInfo);
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(ExtendedNotFoundException e){
            Assert.assertNull(microServiceInfo);
        }
       
    }
    
    @Test
    public void testGetMicroServiceInstance_exist(){
        try {
        MicroServiceFullInfo microServiceInfo=microServiceWrapper.getMicroServiceInstance("testForJunit", "v1","");
        Assert.assertNotNull(microServiceInfo);
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        }
       
    }
    
    @Test
    public void testUpdateMicroServiceInstance(){
        try {
            
            MicroServiceInfo microServiceInfo=new MicroServiceInfo();
            microServiceInfo.setServiceName("testForJunit");
            microServiceInfo.setVersion("v1");
            microServiceInfo.setUrl("/api/test2/v1");
            microServiceInfo.setProtocol("REST");
            microServiceInfo.setVisualRange("0");
            Set<Node> nodes=new HashSet<Node>();
            nodes.add(new Node("127.0.0.1","8080",0));
            microServiceInfo.setNodes(nodes);
            
            MicroServiceFullInfo new_MicroServiceInfo= microServiceWrapper.updateMicroServiceInstance("testForJunit", "v1", microServiceInfo);
            Assert.assertEquals("/api/test2/v1", new_MicroServiceInfo.getUrl());
        }catch(ExtendedInternalServerErrorException e){
        }
        catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        
        }
    }
    
    @Test
    public void testUpdateMicroServiceStatus(){
        try { 
            MicroServiceFullInfo new_MicroServiceInfo= microServiceWrapper.updateMicroServiceStatus("testForJunit", "v1", "0");
            Assert.assertEquals("0",new_MicroServiceInfo.getStatus());
        }catch(ExtendedInternalServerErrorException e){
        }catch(Exception e){
            Assert.fail("Exception" + e.getMessage());
        
        }
    }
    
   
}
