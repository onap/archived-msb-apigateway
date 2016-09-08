/**
 * Copyright 2016 2015-2016 ZTE, Inc. and others. All rights reserved.
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
package org.openo.msb.api;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsInfo{
	private Gauges gauges;
	private Timers timers;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Gauges {
	
	@JsonProperty("jvm.attribute.uptime") 
	private JVMMetrics jvm_attribute_uptime;
	
	@JsonProperty("jvm.memory.pools.Eden-Space.usage") 
	private JVMMetrics jvm_memory_pools_Eden_Space_usage;
	
	@JsonProperty("jvm.memory.pools.PS-Eden-Space.usage") 
	private JVMMetrics jvm_memory_pools_PS_Eden_Space_usage;
	
	@JsonProperty("jvm.memory.pools.Perm-Gen.usage") 
	private JVMMetrics jvm_memory_pools_Perm_Gen_usage;
	
	@JsonProperty("jvm.memory.pools.PS-Perm-Gen.usage")
	private JVMMetrics jvm_memory_pools_PS_Perm_Gen_usage;
	
	@JsonProperty("jvm.memory.pools.Survivor-Space.usage")
	private JVMMetrics jvm_memory_pools_Survivor_Space_usage;
	
	@JsonProperty("jvm.memory.pools.PS-Survivor-Space.usage")
	private JVMMetrics jvm_memory_pools_PS_Survivor_Space_usage;
	
	@JsonProperty("jvm.memory.pools.Tenured-Gen.usage")
	private JVMMetrics jvm_memory_pools_Tenured_Gen_usage;
	
	@JsonProperty("jvm.memory.pools.PS-Old-Gen.usage")
	private JVMMetrics jvm_memory_pools_PS_Old_Gen_usage;
	
	@JsonProperty("jvm.memory.pools.Code-Cache.usage")
	private JVMMetrics jvm_memory_pools_Code_Cache_usage;
	
	@JsonProperty("jvm.memory.heap.init")
	private JVMMetrics jvm_memory_heap_init;
	
	@JsonProperty("jvm.memory.non-heap.init")
	private JVMMetrics jvm_memory_non_heap_init;
	
	@JsonProperty("jvm.memory.heap.used")
	private JVMMetrics jvm_memory_heap_used;
	
	@JsonProperty("jvm.memory.non-heap.used")
	private JVMMetrics jvm_memory_non_heap_used;
	
	@JsonProperty("jvm.memory.heap.max")
	private JVMMetrics jvm_memory_heap_max;
	
	@JsonProperty("jvm.threads.runnable.count")
	private JVMMetrics jvm_threads_runnable_count;	
	
	@JsonProperty("jvm.threads.timed_waiting.count")
	private JVMMetrics jvm_threads_timed_waiting_count;
	
	@JsonProperty("jvm.threads.waiting.count")
	private JVMMetrics jvm_threads_waiting_count;
	
	@JsonProperty("jvm.threads.blocked.count")
	private JVMMetrics jvm_threads_blocked_count;
	
	@JsonProperty("jvm.threads.count")
	private JVMMetrics jvm_threads_count;
	
	
	
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Timers{
	
	@JsonProperty("com.zte.ums.nfv.eco.hsif.msb.resources.ApiRouteResource.addApiRoute")
	private HttpMetrics com_zte_ums_nfv_eco_hsif_msb_resources_ApiRouteResource_addApiRoute;
	
	@JsonProperty("com.zte.ums.nfv.eco.hsif.msb.resources.ApiRouteResource.deleteApiRoute")
	private HttpMetrics com_zte_ums_nfv_eco_hsif_msb_resources_ApiRouteResource_deleteApiRoute;
	
	@JsonProperty("com.zte.ums.nfv.eco.hsif.msb.resources.ApiRouteResource.getApiDocs")
	private HttpMetrics com_zte_ums_nfv_eco_hsif_msb_resources_ApiRouteResource_getApiDocs;
	
	@JsonProperty("com.zte.ums.nfv.eco.hsif.msb.resources.ApiRouteResource.getApiRoute")
	private HttpMetrics com_zte_ums_nfv_eco_hsif_msb_resources_ApiRouteResource_getApiRoute;
	
	@JsonProperty("com.zte.ums.nfv.eco.hsif.msb.resources.ApiRouteResource.getApiRoutes")
	private HttpMetrics com_zte_ums_nfv_eco_hsif_msb_resources_ApiRouteResource_getApiRoutes;
	
	@JsonProperty("com.zte.ums.nfv.eco.hsif.msb.resources.ApiRouteResource.getServerIP")
	private HttpMetrics com_zte_ums_nfv_eco_hsif_msb_resources_ApiRouteResource_getServerIP;
	
	@JsonProperty("com.zte.ums.nfv.eco.hsif.msb.resources.ApiRouteResource.updateApiRoute")
	private HttpMetrics com_zte_ums_nfv_eco_hsif_msb_resources_ApiRouteResource_updateApiRoute;
	
	@JsonProperty("com.zte.ums.nfv.eco.hsif.msb.resources.IuiRouteResource.addIuiRoute")
	private HttpMetrics com_zte_ums_nfv_eco_hsif_msb_resources_IuiRouteResource_addIuiRoute;
	
	@JsonProperty("com.zte.ums.nfv.eco.hsif.msb.resources.IuiRouteResource.deleteIuiRoute")
	private HttpMetrics com_zte_ums_nfv_eco_hsif_msb_resources_IuiRouteResource_deleteIuiRoute;
	
	@JsonProperty("com.zte.ums.nfv.eco.hsif.msb.resources.IuiRouteResource.getIuiRoute")
	private HttpMetrics com_zte_ums_nfv_eco_hsif_msb_resources_IuiRouteResource_getIuiRoute;
	
	@JsonProperty("com.zte.ums.nfv.eco.hsif.msb.resources.IuiRouteResource.getIuiRoutes")
	private HttpMetrics com_zte_ums_nfv_eco_hsif_msb_resources_IuiRouteResource_getIuiRoutes;
	
	@JsonProperty("com.zte.ums.nfv.eco.hsif.msb.resources.IuiRouteResource.updateIuiRoute")
	private HttpMetrics com_zte_ums_nfv_eco_hsif_msb_resources_IuiRouteResource_updateIuiRoute;
	
	@JsonProperty("io.dropwizard.jetty.MutableServletContextHandler.get-requests")
	private HttpMetrics io_dropwizard_jetty_MutableServletContextHandler_get_requests;
	
	@JsonProperty("io.dropwizard.jetty.MutableServletContextHandler.post-requests")
	private HttpMetrics io_dropwizard_jetty_MutableServletContextHandler_post_requests;
	
	@JsonProperty("io.dropwizard.jetty.MutableServletContextHandler.put-requests")
	private HttpMetrics io_dropwizard_jetty_MutableServletContextHandler_put_requests;
	
	@JsonProperty("io.dropwizard.jetty.MutableServletContextHandler.delete-requests")
	private HttpMetrics io_dropwizard_jetty_MutableServletContextHandler_delete_requests;
	
	@JsonProperty("io.dropwizard.jetty.MutableServletContextHandler.other-requests")
	private HttpMetrics io_dropwizard_jetty_MutableServletContextHandler_other_requests;
	
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class JVMMetrics{
	private double value;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class HttpMetrics{
	private int count;
	private double max;
	private double mean;
	private double min;
	private double p50;
	private double p75;
	private double p95;
	private double p98;
	private double p99;
	private double p999;
	private double stddev;
	private double m15_rate;
	private double m1_rate;
	private double m5_rate;
	private double mean_rate;
	private String duration_units;
	private String rate_units;
}


