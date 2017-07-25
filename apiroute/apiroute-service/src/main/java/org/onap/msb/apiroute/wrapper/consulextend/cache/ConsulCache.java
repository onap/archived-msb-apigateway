package org.onap.msb.apiroute.wrapper.consulextend.cache;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseHeader;
import org.onap.msb.apiroute.wrapper.consulextend.async.OriginalConsulResponse;
import org.onap.msb.apiroute.wrapper.consulextend.util.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.orbitz.consul.ConsulException;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.option.ImmutableQueryOptions;
import com.orbitz.consul.option.QueryOptions;

/**
 * A cache structure that can provide an up-to-date read-only map backed by
 * consul data
 * 
 * @param <V>
 */
public class ConsulCache<T> {

	enum State {
		latent, starting, started, stopped
	}

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ConsulCache.class);

	@VisibleForTesting
	static final String BACKOFF_DELAY_PROPERTY = "com.orbitz.consul.cache.backOffDelay";
	private static final long BACKOFF_DELAY_QTY_IN_MS = getBackOffDelayInMs(System
			.getProperties());

	private final AtomicReference<BigInteger> latestIndex = new AtomicReference<BigInteger>(
			null);
	private final AtomicReference<State> state = new AtomicReference<State>(
			State.latent);
	private final CountDownLatch initLatch = new CountDownLatch(1);
	private final ScheduledExecutorService executorService = Executors
			.newSingleThreadScheduledExecutor();
	private final CopyOnWriteArrayList<Listener<T>> listeners = new CopyOnWriteArrayList<Listener<T>>();

	private final CallbackConsumer<T> callBackConsumer;
	private final ConsulResponseCallback<T> responseCallback;

	ConsulCache(CallbackConsumer<T> callbackConsumer) {

		this.callBackConsumer = callbackConsumer;

		this.responseCallback = new ConsulResponseCallback<T>() {
			@Override
			public void onComplete(ConsulResponse<T> consulResponse) {

				if (consulResponse.isKnownLeader()) {
					if (!isRunning()) {
						return;
					}
					updateIndex(consulResponse);

					for (Listener<T> l : listeners) {
						l.notify(consulResponse);
					}

					if (state.compareAndSet(State.starting, State.started)) {
						initLatch.countDown();
					}

					runCallback();
				} else {
					onFailure(new ConsulException(
							"Consul cluster has no elected leader"));
				}
			}

			@Override
			public void onDelayComplete(
					OriginalConsulResponse<T> originalConsulResponse) {

				try {
					// get header
					ConsulResponseHeader consulResponseHeader = Http
							.consulResponseHeader(originalConsulResponse
									.getResponse());

					if (consulResponseHeader.isKnownLeader()) {
						if (!isRunning()) {
							return;
						}

						boolean isConuslIndexChanged = isConuslIndexChanged(consulResponseHeader
								.getIndex());
						// consul index different
						if (isConuslIndexChanged) {

							updateIndex(consulResponseHeader.getIndex());

							// get T type data
							ConsulResponse<T> consulResponse = Http
									.consulResponse(originalConsulResponse
											.getResponseType(),
											originalConsulResponse
													.getResponse());

							// notify customer to custom T data
							for (Listener<T> l : listeners) {
								l.notify(consulResponse);
							}
						}

						if (state.compareAndSet(State.starting, State.started)) {
							initLatch.countDown();
						}

						runCallback();

					} else {
						onFailure(new ConsulException(
								"Consul cluster has no elected leader"));
					}
				} catch (Exception e) {
					onFailure(e);
				}

			}

			@Override
			public void onFailure(Throwable throwable) {

				if (!isRunning()) {
					return;
				}
				LOGGER.error(
						String.format(
								"Error getting response from consul. will retry in %d %s",
								BACKOFF_DELAY_QTY_IN_MS, TimeUnit.MILLISECONDS),
						throwable);

				executorService.schedule(new Runnable() {
					@Override
					public void run() {
						runCallback();
					}
				}, BACKOFF_DELAY_QTY_IN_MS, TimeUnit.MILLISECONDS);
			}
		};
	}

	@VisibleForTesting
	static long getBackOffDelayInMs(Properties properties) {
		String backOffDelay = null;
		try {
			backOffDelay = properties.getProperty(BACKOFF_DELAY_PROPERTY);
			if (!Strings.isNullOrEmpty(backOffDelay)) {
				return Long.parseLong(backOffDelay);
			}
		} catch (Exception ex) {
			LOGGER.warn(
					backOffDelay != null ? String.format(
							"Error parsing property variable %s: %s",
							BACKOFF_DELAY_PROPERTY, backOffDelay) : String
							.format("Error extracting property variable %s",
									BACKOFF_DELAY_PROPERTY), ex);
		}
		return TimeUnit.SECONDS.toMillis(10);
	}

	public void start() throws Exception {
		checkState(state.compareAndSet(State.latent, State.starting),
				"Cannot transition from state %s to %s", state.get(),
				State.starting);
		runCallback();
	}

	public void stop() throws Exception {
		State previous = state.getAndSet(State.stopped);
		if (previous != State.stopped) {
			executorService.shutdownNow();
		}
	}

	private void runCallback() {
		if (isRunning()) {
			callBackConsumer.consume(latestIndex.get(), responseCallback);
		}
	}

	private boolean isRunning() {
		return state.get() == State.started || state.get() == State.starting;
	}

	public boolean awaitInitialized(long timeout, TimeUnit unit)
			throws InterruptedException {
		return initLatch.await(timeout, unit);
	}

	private void updateIndex(ConsulResponse<T> consulResponse) {
		if (consulResponse != null && consulResponse.getIndex() != null) {
			this.latestIndex.set(consulResponse.getIndex());
		}
	}

	public void updateIndex(BigInteger index) {
		if (index != null) {
			this.latestIndex.set(index);
		}
	}

	protected static QueryOptions watchParams(final BigInteger index,
			final int blockSeconds, QueryOptions queryOptions) {
		checkArgument(!queryOptions.getIndex().isPresent()
				&& !queryOptions.getWait().isPresent(),
				"Index and wait cannot be overridden");

		return ImmutableQueryOptions.builder()
				.from(watchDefaultParams(index, blockSeconds))
				.token(queryOptions.getToken())
				.consistencyMode(queryOptions.getConsistencyMode())
				.near(queryOptions.getNear()).build();
	}

	private static QueryOptions watchDefaultParams(final BigInteger index,
			final int blockSeconds) {
		if (index == null) {
			return QueryOptions.BLANK;
		} else {
			return QueryOptions.blockSeconds(blockSeconds, index).build();
		}
	}

	/**
	 * passed in by creators to vary the content of the cached values
	 * 
	 * @param <V>
	 */
	protected interface CallbackConsumer<T> {
		void consume(BigInteger index, ConsulResponseCallback<T> callback);
	}

	/**
	 * Implementers can register a listener to receive a new map when it changes
	 * 
	 * @param <V>
	 */
	public interface Listener<T> {
		void notify(ConsulResponse<T> newValues);
	}

	public boolean addListener(Listener<T> listener) {
		boolean added = listeners.add(listener);
		return added;
	}

	public List<Listener<T>> getListeners() {
		return Collections.unmodifiableList(listeners);
	}

	public boolean removeListener(Listener<T> listener) {
		return listeners.remove(listener);
	}

	@VisibleForTesting
	protected State getState() {
		return state.get();
	}

	private boolean isConuslIndexChanged(final BigInteger index) {

		if (index != null && !index.equals(latestIndex.get())) {

			if (LOGGER.isDebugEnabled()) {
				// 第一次不打印
				if (latestIndex.get() != null) {
					LOGGER.debug("consul index compare:new-" + index + "  old-"
							+ latestIndex.get());
				}

			}

			return true;
		}

		return false;
	}
}
