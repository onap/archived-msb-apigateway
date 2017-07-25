package org.onap.msb.apiroute.wrapper.consulextend;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public class Consul {
	/**
	 * Default Consul HTTP API host.
	 */
	public static final String DEFAULT_HTTP_HOST = "localhost";

	/**
	 * Default Consul HTTP API port.
	 */
	public static final int DEFAULT_HTTP_PORT = 8500;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Consul.class);
	
	private final CatalogClient catalogClient;
	private final HealthClient healthClient;

	private Consul(CatalogClient catalogClient, HealthClient healthClient) {
		this.catalogClient = catalogClient;
		this.healthClient = healthClient;
	}

	/**
	 * Get the Catalog HTTP client.
	 * <p>
	 * /v1/catalog
	 * 
	 * @return The Catalog HTTP client.
	 */
	public CatalogClient catalogClient() {
		return catalogClient;
	}

	/**
	 * Get the Health HTTP client.
	 * <p>
	 * /v1/health
	 * 
	 * @return The Health HTTP client.
	 */
	public HealthClient healthClient() {
		return healthClient;
	}
	
	/**
	 * Creates a new {@link Builder} object.
	 * 
	 * @return A new Consul builder.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Used to create a default Consul client.
	 * 
	 * @return A default {@link Consul} client.
	 */
	@VisibleForTesting
	public static Consul newClient() {
		return builder().build();
	}

	public static class Builder {

		private HttpHost targetHost;

		{
			targetHost = new HttpHost(DEFAULT_HTTP_HOST, DEFAULT_HTTP_PORT);
		}

		Builder() {

		}

		public Builder withHostAndPort(String hostname, int port) {
			this.targetHost = new HttpHost(hostname, port);
			return this;
		}

		public Consul build() {
			LOGGER.info("********build consul:"+targetHost.toString()+"****************");
			CatalogClient catalogClient = new CatalogClient(targetHost);
			HealthClient healthClient = new HealthClient(targetHost);
			return new Consul(catalogClient,healthClient);
		}

	}
}
