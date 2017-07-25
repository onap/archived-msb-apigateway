package org.onap.msb.apiroute.wrapper.consulextend.expose;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.model.ConsulResponse;

public class ConsulIndexFilter<T> implements WatchTask.Filter<T> {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ConsulIndexFilter.class);

	private final AtomicReference<BigInteger> latestIndex = new AtomicReference<BigInteger>(
			null);

	@Override
	public boolean filter(final ConsulResponse<T> object) {
		// TODO Auto-generated method stub
		return isChanged(object);
	}

	private boolean isChanged(final ConsulResponse<T> consulResponse) {

		if (consulResponse != null && consulResponse.getIndex() != null
				&& !consulResponse.getIndex().equals(latestIndex.get())) {
			
			if(LOGGER.isDebugEnabled()){
				//第一次不打印
				if (latestIndex.get()!=null) {
					LOGGER.debug("consul index compare:new-"
							+ consulResponse.getIndex() + "  old-"
							+ latestIndex.get());
				}
				
			}

			this.latestIndex.set(consulResponse.getIndex());
			return true;
		}

		return false;
	}
	
	
}
