package org.onap.msb.apiroute;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.msb.apiroute.SyncDataManager;
import org.onap.msb.apiroute.wrapper.consulextend.async.ConsulResponseCallback;
import org.onap.msb.apiroute.wrapper.consulextend.util.Http;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.core.type.TypeReference;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Http.class })
@PowerMockIgnore({ "javax.net.ssl.*" })
public class SyncDataManagerTest {

	@SuppressWarnings("unchecked")
	@Before
	public void setUpBeforeTest() {
		Http http = PowerMockito.mock(Http.class);

		PowerMockito
				.doNothing()
				.when(http)
				.asyncGet(Mockito.anyString(),
						Mockito.any(TypeReference.class),
						Mockito.any(ConsulResponseCallback.class));

		PowerMockito
				.doNothing()
				.when(http)
				.asyncGetDelayHandle(Mockito.anyString(),
						Mockito.any(TypeReference.class),
						Mockito.any(ConsulResponseCallback.class));

		//
		PowerMockito.spy(Http.class);
		PowerMockito.when(Http.getInstance()).thenReturn(http);

	}
	
	@Test
	public void testSyncDataManager()
	{
		SyncDataManager.initSyncTask("127.0.0.1",8500);
		SyncDataManager.startWatchService("huangleibo");
		SyncDataManager.resetIndex("huangleibo");
		SyncDataManager.stopWatchService("huangleibo");
		SyncDataManager.stopWatchServiceList();
	}
}
