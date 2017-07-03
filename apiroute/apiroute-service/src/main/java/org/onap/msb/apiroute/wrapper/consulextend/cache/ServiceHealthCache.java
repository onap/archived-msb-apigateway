package org.onap.msb.apiroute.wrapper.consulextend.cache;

import java.math.BigInteger;
import java.util.List;

import org.onap.msb.apiroute.wrapper.consulextend.HealthClient;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;
import org.onap.msb.apiroute.wrapper.consulextend.model.health.ServiceHealth;

import com.orbitz.consul.option.CatalogOptions;
import com.orbitz.consul.option.QueryOptions;

public class ServiceHealthCache extends ConsulCache<List<ServiceHealth>> {
    private ServiceHealthCache(CallbackConsumer<List<ServiceHealth>> callbackConsumer) {
        super(callbackConsumer);
    }

    /**
     * Factory method to construct a string/{@link ServiceHealth} map for a particular service.
     * <p/>
     * Keys will be a {@link HostAndPort} object made up of the service's address/port combo
     *
     * @param healthClient the {@link HealthClient}
     * @param serviceName  the name of the service
     * @param passing      include only passing services?
     * @return a cache object
     */
    public static ServiceHealthCache newCache(
            final HealthClient healthClient,
            final String serviceName,
            final boolean passing,
            final CatalogOptions catalogOptions,
            final int watchSeconds,
            final QueryOptions queryOptions) {

        CallbackConsumer<List<ServiceHealth>> callbackConsumer = new CallbackConsumer<List<ServiceHealth>>() {
			@Override
			public void consume(BigInteger index,
					ConsulResponseCallback<List<ServiceHealth>> callback) {
				// TODO Auto-generated method stub
                QueryOptions params = watchParams(index, watchSeconds, queryOptions);
                if (passing) {
                    healthClient.getHealthyServiceInstances(serviceName, catalogOptions, params, callback);
                } else {
                    healthClient.getAllServiceInstances(serviceName, catalogOptions, params, callback);
                }
			}
        };

        return new ServiceHealthCache(callbackConsumer);
    }

    public static ServiceHealthCache newCache(
            final HealthClient healthClient,
            final String serviceName,
            final boolean passing,
            final CatalogOptions catalogOptions,
            final int watchSeconds) {
        return newCache(healthClient, serviceName, passing, catalogOptions, watchSeconds, QueryOptions.BLANK);
    }

    public static ServiceHealthCache newCache(final HealthClient healthClient, final String serviceName) {
        return newCache(healthClient, serviceName, true, CatalogOptions.BLANK, 10);
    }
}
