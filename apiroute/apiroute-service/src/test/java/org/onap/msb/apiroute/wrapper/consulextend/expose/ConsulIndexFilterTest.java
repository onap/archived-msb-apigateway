/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.onap.msb.apiroute.wrapper.consulextend.expose;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.msb.apiroute.wrapper.consulextend.expose.ConsulIndexFilter;

import com.orbitz.consul.model.ConsulResponse;

public class ConsulIndexFilterTest {
	@BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }
	
	@Test
	public void testfilter()
	{
		ConsulIndexFilter<Integer> filter = new ConsulIndexFilter<Integer>();
		
		int response = 1;
		long lastContact= 1;
		boolean knownLeader = true;

		ConsulResponse<Integer> object = new ConsulResponse<Integer>(response,lastContact,knownLeader,BigInteger.valueOf(1));

		//index 1;the first time,return true
		Assert.assertTrue(filter.filter(object));


		//index 1;same index,return false
		Assert.assertFalse(filter.filter(object));
		
		ConsulResponse<Integer> object1 = new ConsulResponse<Integer>(response,lastContact,knownLeader,BigInteger.valueOf(2));
		
		//index 2;different index,return true
		Assert.assertTrue(filter.filter(object1));
		
	}
}
