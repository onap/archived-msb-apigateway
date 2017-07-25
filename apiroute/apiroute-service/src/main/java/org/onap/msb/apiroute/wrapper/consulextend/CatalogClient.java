package org.onap.msb.apiroute.wrapper.consulextend;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;
import org.onap.msb.apiroute.wrapper.consulextend.util.Http;
import org.onap.msb.apiroute.wrapper.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.orbitz.consul.option.CatalogOptions;
import com.orbitz.consul.option.QueryOptions;

/**
 * HTTP Client for /v1/catalog/ endpoints.
 */
public class CatalogClient {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CatalogClient.class);

	private static final TypeReference<HttpEntity> TYPE_SERVICES_MAP = new TypeReference<HttpEntity>() {
	};


	private static final String CATALOG_URI_8500 = "/v1/catalog";
	private static final String CATAlOG_URI_10081 = "/api/catalog/v1";

	private static final String GET_SERVICES_URI = "/services";

	private static final Http httpClient = Http.getInstance();

	private HttpHost targetHost = null;
	private String catalogUri = CATAlOG_URI_10081;

	CatalogClient(final HttpHost targetHost) {
		this.targetHost = targetHost;
		if (targetHost.getPort() == 8500) {
			catalogUri = CATALOG_URI_8500;
		}
	}

	/**
	 * Retrieves all services for a given datacenter with
	 * {@link com.orbitz.consul.option.QueryOptions}.
	 * 
	 * GET /v1/catalog/services?dc={datacenter}
	 * 
	 * @param catalogOptions
	 *            Catalog specific options to use.
	 * @param queryOptions
	 *            The Query Options to use.
	 * @return A {@link com.orbitz.consul.model.ConsulResponse} containing a map
	 *         of service name to list of tags.
	 */
	public void getServices(CatalogOptions catalogOptions,
			QueryOptions queryOptions,
			ConsulResponseCallback<HttpEntity> callback) {

		// prepare access path
		// path:10081 vs 8500
		String path = targetHost.toString() + catalogUri + GET_SERVICES_URI;

		// params:wait,index,dc......
		String params = Http.optionsFrom(catalogOptions, queryOptions);

		// node meta: ns,external,internal.....
		String node_meta = ConfigUtil.getInstance().getNodeMetaQueryParam();

		// add params
		path = (params != null && !params.isEmpty()) ? path += "?" + params
				: path;

		// add node_meta
		if (node_meta != null && !node_meta.isEmpty()) {
			path = path.contains("?") ? path +"&"+ node_meta : path + "?"
					+ node_meta;
		}

		// async watch services
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("get all services:" + path);
		}
		httpClient.asyncGetDelayHandle(path, TYPE_SERVICES_MAP, callback);
	}	
}
