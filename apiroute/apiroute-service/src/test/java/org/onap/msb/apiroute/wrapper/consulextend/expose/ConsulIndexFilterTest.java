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
