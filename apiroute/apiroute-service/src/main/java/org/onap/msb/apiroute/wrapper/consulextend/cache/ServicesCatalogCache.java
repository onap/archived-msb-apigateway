package org.onap.msb.apiroute.wrapper.consulextend.cache;

import java.math.BigInteger;

import org.apache.http.HttpEntity;
import org.onap.msb.apiroute.wrapper.consulextend.CatalogClient;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;

import com.orbitz.consul.option.CatalogOptions;
import com.orbitz.consul.option.QueryOptions;

public class ServicesCatalogCache extends ConsulCache<HttpEntity> {
	
    private ServicesCatalogCache(CallbackConsumer<HttpEntity> callbackConsumer) {
        super(callbackConsumer);
    }

    public static ServicesCatalogCache newCache(
            final CatalogClient catalogClient,
            final CatalogOptions catalogOptions,
            final QueryOptions queryOptions,
            final int watchSeconds) {
    	
        CallbackConsumer<HttpEntity> callbackConsumer = new CallbackConsumer<HttpEntity>() {
            @Override
            public void consume(BigInteger index, ConsulResponseCallback<HttpEntity> callback) {
            	QueryOptions params = watchParams(index, watchSeconds, queryOptions);
            	catalogClient.getServices(catalogOptions, params,callback);
            }
        };

        return new ServicesCatalogCache(callbackConsumer);

    }
    
    public static ServicesCatalogCache newCache(final CatalogClient catalogClient) {
        return newCache(catalogClient, CatalogOptions.BLANK, QueryOptions.BLANK, 10);
    }
}
