/*
 * Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var vm = avalon
		.define({
			$id : "metricsController",
			routeTargetTitle:$.i18n.prop("org_openo_msb_route_content_title"),		
			$metricsUrl : '/admin/metrics',		
			metricsLoading:false,		
			metricsArray :  [],	
			threadNum:"",
			isErr:false,
			jvmTime:"",
			restArray:[],
					
        	 $dataTableLanguage: {
                "sProcessing": "<img src='../img/loading-spinner-grey.gif'/><span>&nbsp;&nbsp;Loadding...</span>",   
                "sLengthMenu": $.i18n.prop("org_openo_msb_route-table-sLengthMenu"),
                "sZeroRecords": $.i18n.prop("org_openo_msb_route-table-sZeroRecords"),
                "sInfo": "<span class='seperator'>  </span>" + $.i18n.prop("org_openo_msb_route-table-sInfo"),
                "sInfoEmpty": $.i18n.prop("org_openo_msb_route-table-sInfoEmpty"),
                "sGroupActions": $.i18n.prop("org_openo_msb_route-table-sGroupActions"),
                "sAjaxRequestGeneralError": $.i18n.prop("org_openo_msb_route-table-sAjaxRequestGeneralError"),
                "sEmptyTable": $.i18n.prop("org_openo_msb_route-table-sEmptyTable"),
                "oPaginate": {
                    "sPrevious": $.i18n.prop("org_openo_msb_route-table-sPrevious"),
                    "sNext": $.i18n.prop("org_openo_msb_route-table-sNext"),
                    "sPage": $.i18n.prop("org_openo_msb_route-table-sPage"),
                    "sPageOf": $.i18n.prop("org_openo_msb_route-table-sPageOf")
                },
                "sSearch": $.i18n.prop("org_openo_msb_route-table-search"),
                "sInfoFiltered": $.i18n.prop("org_openo_msb_route-table-infofilter") 
            },		
			initMetrics : function() {
 			
			    
		 $.ajax({
            "type": 'get',
            "url": url,
            "dataType": "json",
            success: function (resp) { 
            	 vm.isErr=false; 
               var restJson = resp;  
               // var restJson = metricsUtil.restJson;                   	
	              
	           var testRestJson=restJson.timers;
	           var gaugesJson=restJson.gauges;

	           //jvm Time
	           var jvmTime=gaugesJson["jvm.attribute.uptime"].value;

	           vm.jvmTime=metricsUtil.formatSeconds(jvmTime);


	           //Initialize the JVM memory usage
	           var Eden_Space_usage;
	           	if(gaugesJson["jvm.memory.pools.Eden-Space.usage"]==null){
	           		if(gaugesJson["jvm.memory.pools.PS-Eden-Space.usage"]==null)
	           		{
	           			Eden_Space_usage=0;
	           		}
	           		else{
	           			Eden_Space_usage=gaugesJson["jvm.memory.pools.PS-Eden-Space.usage"].value;
	           		}
	           	}
	           	else{
	           		Eden_Space_usage=gaugesJson["jvm.memory.pools.Eden-Space.usage"].value;
	           	}




	           	var Perm_Gen_usage;
	           	if(gaugesJson["jvm.memory.pools.Perm-Gen.usage"]==null){
	           		if(gaugesJson["jvm.memory.pools.PS-Perm-Gen.usage"]==null)
	           		{
	           			Perm_Gen_usage=0;
	           		}
	           		else{
	           			Perm_Gen_usage=gaugesJson["jvm.memory.pools.PS-Perm-Gen.usage"].value;
	           		}
	           	}
	           	else{
	           		Perm_Gen_usage=gaugesJson["jvm.memory.pools.Perm-Gen.usage"].value;
	           	}


	           	var Survivor_Space_usage;
	           	if(gaugesJson["jvm.memory.pools.Survivor-Space.usage"]==null){
	           		if(gaugesJson["jvm.memory.pools.PS-Survivor-Space.usage"]==null)
	           		{
	           			Survivor_Space_usage=0;
	           		}
	           		else{
	           			Survivor_Space_usage=gaugesJson["jvm.memory.pools.PS-Survivor-Space.usage"].value;
	           		}
	           	}
	           	else{
	           		Survivor_Space_usage=gaugesJson["jvm.memory.pools.Survivor-Space.usage"].value;
	           	}


	           	var Tenured_Gen_usage;
	           	if(gaugesJson["jvm.memory.pools.Tenured-Gen.usage"]==null){
	           		if(gaugesJson["jvm.memory.pools.PS-Old-Gen.usage"]==null)
	           		{
	           			Tenured_Gen_usage=0;
	           		}
	           		else{
	           			Tenured_Gen_usage=gaugesJson["jvm.memory.pools.PS-Old-Gen.usage"].value;
	           		}
	           	}
	           	else{
	           		Tenured_Gen_usage=gaugesJson["jvm.memory.pools.Tenured-Gen.usage"].value;
	           	}	


	           var memoryPieMetrics_data={
	           	CodeCache:(gaugesJson["jvm.memory.pools.Code-Cache.usage"].value*100).toFixed(1),
	           	EdenSpace:(Eden_Space_usage*100).toFixed(1),
	           	PermGen:(Perm_Gen_usage*100).toFixed(1),
	           	SurvivorSpace:(Survivor_Space_usage*100).toFixed(1),
	           	TenuredGen:(Tenured_Gen_usage*100).toFixed(1)
	           	};
	           metricsChart.memoryPieMetrics(memoryPieMetrics_data);

	           // initialize the JVM memory map
	           var heap_init=Math.round(gaugesJson["jvm.memory.heap.init"].value/1000000);
	           var non_heap_init=Math.round(gaugesJson["jvm.memory.non-heap.init"].value/1000000);

	           var heap_used=Math.round(gaugesJson["jvm.memory.heap.used"].value/1000000);
	           var non_heap_used=Math.round(gaugesJson["jvm.memory.non-heap.used"].value/1000000);

	           var heap_max=Math.round(gaugesJson["jvm.memory.heap.max"].value/1000000);
	           var non_heap_max=Math.round(gaugesJson["jvm.memory.non-heap.max"].value/1000000);

	           var memoryBarMetrics_data={
	           	init:[
	           		heap_init,
	           		non_heap_init,
	           		non_heap_init+heap_init
	           		],
	           	used:[
	           		heap_used,
	           		non_heap_used,
	           		non_heap_used+heap_used
	           	   	],
	           	max:[
	           		heap_max,
	           		non_heap_max,
	           		non_heap_max+heap_max
	           		]
	           };
	           metricsChart.memoryBarMetrics(memoryBarMetrics_data);

	           //Initializes the thread profile
	           var threadsMetrics_data= [{value:gaugesJson["jvm.threads.runnable.count"].value, name:'Runnable'},
                {value:gaugesJson["jvm.threads.timed_waiting.count"].value, name:'Timed waiting'},
                {value:gaugesJson["jvm.threads.waiting.count"].value, name:'Waiting'},
                {value:gaugesJson["jvm.threads.blocked.count"].value, name:'Blocked'}];
                vm.threadNum=gaugesJson["jvm.threads.count"].value;
 				metricsChart.threadsMetrics(threadsMetrics_data);

 				//Initialize the Rest interface traffic map
 				var restMetrics_data={restName:[],restCount:[]};	
 				  $.each(testRestJson,function(name,value) {
 				  	if(name=="io.dropwizard.jetty.MutableServletContextHandler.connect-requests") return false;
		           	var nameArray=name.split(".");

					restMetrics_data.restName.push(nameArray[nameArray.length-1]);
					restMetrics_data.restCount.push(value.count);

				});
 				

	           metricsChart.restMetrics(restMetrics_data); 


	           //Initialize the HTTP traffic
	           var requestsMetrics_data={get:"",post:"",put:"",delete:"",other:""};
	           requestsMetrics_data.get=testRestJson["io.dropwizard.jetty.MutableServletContextHandler.get-requests"].count;
	           requestsMetrics_data.post=testRestJson["io.dropwizard.jetty.MutableServletContextHandler.post-requests"].count;
	           requestsMetrics_data.put=testRestJson["io.dropwizard.jetty.MutableServletContextHandler.put-requests"].count;
	           requestsMetrics_data.delete=testRestJson["io.dropwizard.jetty.MutableServletContextHandler.delete-requests"].count;
	           requestsMetrics_data.other=testRestJson["io.dropwizard.jetty.MutableServletContextHandler.other-requests"].count;
	           metricsChart.requestsMetrics(requestsMetrics_data);

	           //Initialize the HTTP access list in detail
	          $.each(testRestJson,function(name,value) {
	          	if(name.indexOf("org.eclipse.jetty.server.HttpConnectionFactory") == 0) return true;
	           	var obj=value;
	           	obj.name=name;
				vm.restArray.push(obj);
				});

	         	$('#restTable').DataTable({
			      responsive: true,
				  destroy: true,
				  "oLanguage": vm.$dataTableLanguage
				});
	           
				 },
		     error: function(XMLHttpRequest, textStatus, errorThrown) {
				                  
		            vm.isErr=true;
		          }
		       });

			}			
			

	});
avalon.scan();
vm.initMetrics();

