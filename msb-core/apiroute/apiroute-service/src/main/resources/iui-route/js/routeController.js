/*
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
 *
 *     Author: Zhaoxing Meng
 *     email: meng.zhaoxing1@zte.com.cn
 */
var table;
var vm = avalon
		.define({
			$id : "routeController",
			targetServiceUrl:"",
			targetFullServiceUrl:"",
			iuiRootPath:iuiRootPath,
			apiRootPath:apiRootPath,
			apiIframeUrl:"",
			iuiIframeUrl:"",
			boxVisible:true,
			routeTargetTitle:$.i18n.prop("org_openo_msb_route_content_title"),			
			server_rtn:{
				info_block:false,
				warning_block:false,
				rtn_info:""
			},
			showAPIType:"0",
			showAPITypeName:[$.i18n.prop("org_openo_msb_route_swagger_type_predefined"),$.i18n.prop("org_openo_msb_route_swagger_type_custominput")],
			$msbProtocol :["REST","UI","HTTP","MQ","FTP","SNMP","TCP","UDP"],
			$msbType:["UI","NAF","SAF"],
			apiJson:{
				local:"",
				custom:""
			},
			setAPIType:function(type){
				vm.apiRouteInfo.apiJsonType=type;
				if(type==0){
					vm.apiJson.local=vm.jsonApiSelectList.selectItems[0];
				}

			},	
			jsonApiSelectList:  {
					condName : "type",
					component_type : 'select',
					selectItems : []
				},
			
        	 dataTableLanguage: {
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
			$apiRouteUrl :apiBasePath+'/apiRoute',
			$apiRouteInstanceUrl :apiBasePath+'/apiRoute/{serviceName}/version/{version}',	
			$apiRouteStatusUrl :apiBasePath+'/apiRoute/{serviceName}/version/{version}/status/{status}',
			$apiDocsUrl :apiBasePath+'/apiRoute/apiDocs',	
			$apiGatewayPortUrl :apiBasePath+'/apiRoute/apiGatewayPort',	
			$discoverInfoUrl :apiBasePath+'/apiRoute/discoverInfo',	
			$iuiRouteUrl :apiBasePath+'/iuiRoute',
			$iuiRouteInstanceUrl :apiBasePath+'/iuiRoute/{serviceName}',
			$iuiRouteStatusUrl :apiBasePath+'/iuiRoute/{serviceName}/status/{status}',
			$customRouteUrl :apiBasePath+'/customRoute/all',
			$customRouteInstanceUrl :apiBasePath+'/customRoute/instance',
			$customRouteStatusUrl :apiBasePath+'/customRoute/status',	
			$msbRouteUrl:apiBasePath+'/services',	
			$msbRouteInstanceUrl :apiBasePath+'/services/{serviceName}/version/{version}',
			routeLoading:false,		
			apiRouteArray :  [],
			apiRouteInfo : {
			    oldServiceName:"",					
				serviceName : "",
				oldVersion:"",	
				version : "",
				status:"",
				url:"",
				metricsUrl:"/admin/metrics",
				apiJson:"/swagger.json",
				apiJsonType:"1",
				control:"",
				server:"",
				servers: []
			},	
			iuiRouteArray :  [],
			iuiRouteInfo : {	
			    oldServiceName:"",				
				serviceName : "",
				status:"",
				url:"",
				server:"",
				control:"",
				servers: []
			},	
			customRouteArray :  [],
			customGroupRouteArray :  [],
			customRouteInfo : {	
			    oldServiceName:"",		
				serviceName : "",
				status:"",
				url:"",
				server:"",
				control:"",
				servers: []
			},
			msbRouteArray :  [],
			msbRouteInfo : {	
			    oldServiceName:"",	
			    oldVersion:"",				
				serviceName : "",
				version:"",
				status:"0",
				nodes:[],
				newHost:"",
				newttl:0,
				url:"",
				protocol:"",
				visualRange:"",
				visualRangeArray:[]			
			},
			discoverInfo:{
				ip:"",
				port:"",
				enabled:false,
				deployMode:""
			},
			selectedRoute:"",
			selectedRouteType:"",
			routeDlgInfo:{
				titleName:"",
				saveType:""
			},												
			$initRoute : function() {
 			


			    vm.routeLoading=true;

			     $.ajax({
			                "type": 'get',
			                 async: false,
			                "url":  vm.$discoverInfoUrl,
			                "dataType": "json",
			                success: function (resp) { 
			           
			                 vm.discoverInfo = (resp==null)?"":resp;
			                			                                       	
			                },
			                 error: function(XMLHttpRequest, textStatus, errorThrown) {
								   bootbox.alert("get discoverInfo fails："+textStatus+":"+errorThrown); 
			                 }
			            });

				  $.ajax({
	                "type": 'get',
	                "url":  vm.$apiRouteUrl,
	                "dataType": "json",
	                success: function (resp) {  
	                     vm.apiRouteArray = (resp==null)?[]:resp;  
	                     vm.apiRouteArray.sort(function(a,b){return a.serviceName>b.serviceName?1:-1});                	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						   bootbox.alert("get api services fails："+textStatus+":"+errorThrown);                       
	                       return;
	                 },
	                 complete:function(){
	                 	vm.routeLoading=false;
	                 	routeUtil.refreshRoute();
	                 	 $.ajax({
			                "type": 'get',
			                "url":  vm.$apiGatewayPortUrl,
			                "dataType": "text",
			                success: function (resp) { 
			           
			                 vm.targetServiceUrl=location.hostname+":"+resp;
			                			                                       	
			                },
			                 error: function(XMLHttpRequest, textStatus, errorThrown) {
								   bootbox.alert("get apiGateway published port fails："+textStatus+":"+errorThrown); 
			                 }
			            });
	                  }
	            });



				  $.ajax({
	                "type": 'get',
	                "url":  vm.$iuiRouteUrl,
	                "dataType": "json",
	                success: function (resp) {  
	                     vm.iuiRouteArray = (resp==null)?[]:resp;  
	                     vm.iuiRouteArray.sort(function(a,b){return a.serviceName>b.serviceName?1:-1});                  	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						   bootbox.alert("get iui services fails："+textStatus+":"+errorThrown);                       
	                       return;
	                 },
	                 complete:function(){	                 	
	                 	routeUtil.refreshRoute();	                 	
	                  }
	            });



				  $.ajax({
	                "type": 'get',
	                "url":  vm.$customRouteUrl,
	                "dataType": "json",
	                success: function (resp) {  
	                      vm.customRouteArray = (resp==null)?[]:resp;   
	                
	                    if(resp!=null && resp.length>0)
	                    {
	                     routeUtil.groupRoute(resp);
	                    }

	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						   bootbox.alert("get custom services fails："+textStatus+":"+errorThrown);                       
	                       return;
	                 },
	                 complete:function(){	                 	
	                 	routeUtil.refreshRoute();	                 	
	                  }
	            });


				  $.ajax({
	                "type": 'get',
	                "url":  vm.$apiDocsUrl,
	                "dataType": "json",
	                success: function (resp) {  
	                     vm.jsonApiSelectList.selectItems= (resp==null)?[]:resp;                  	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						   bootbox.alert("get local apiDocs  fails："+textStatus+":"+errorThrown); 
	                 }
	            });



				  vm.initMSBRoute();


		

			},
			initMSBRoute:function(){
				vm.initIUIfori18n();

				$.ajax({
	                "type": 'get',
	                "url":  vm.$msbRouteUrl,
	                "dataType": "json",
	                success: function (resp) {  
	                      vm.msbRouteArray = (resp==null)?[]:resp;  
	                
	                     vm.msbRouteArray.sort(function(a,b){return a.serviceName>b.serviceName?1:-1}); 
	                                    	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						   bootbox.alert("get msb services fails："+XMLHttpRequest.responseText);                       
	                       return;
	                 },
	                 complete:function(){

	                 	table=$('#msbTable').DataTable({
						     
							  "oLanguage": vm.dataTableLanguage,
							  columnDefs: [ {
								      targets: [ 0,8 ],
								      "searchable": false,
								      "bSortable": false,
								    }],								     
								   "order": [[2, 'asc']]
							});
	                 	
	            		}
				});
	       
	           
			},
			
			clickDisplayGraphAlink: function () {
            	vm.boxVisible = !vm.boxVisible;
        	},
        	addmsbHost:function(){
        		if(vm.msbRouteInfo.newHost==""){
        			vm.server_rtn.warning_block=true;
				    vm.server_rtn.info_block=false; 
				    vm.server_rtn.rtn_info= $.i18n.prop("org_openo_msb_route_err_host_empty");
					return;
        		}

        		if(vm.msbRouteInfo.newttl==""){
        			vm.server_rtn.warning_block=true;
				    vm.server_rtn.info_block=false; 
				    vm.server_rtn.rtn_info= $.i18n.prop("org_openo_msb_route_err_ttl_empty");
					return;
        		}

        		  var reg=/^(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([0-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5]))):(\d{1,5})$/   
         		   var ip,port;	
				   if(!reg.test(vm.msbRouteInfo.newHost)){  
				   	vm.server_rtn.warning_block=true;
				    vm.server_rtn.info_block=false; 
				    vm.server_rtn.rtn_info= $.i18n.prop("org_openo_msb_route_err_host_format");
					return;
				   }
				   else{
			
					var hosts=vm.msbRouteInfo.newHost.split(":");  
					ip=hosts[0];
					port=hosts[1];
				   }

                var reg_ttl=/^\d+$/
         
				 if(!reg_ttl.test(vm.msbRouteInfo.newttl)){  
				   	vm.server_rtn.warning_block=true;
				    vm.server_rtn.info_block=false; 
				    vm.server_rtn.rtn_info= $.i18n.prop("org_openo_msb_route_err_ttl_format");
					return;
				   }  

				  

        		// determine whether host repeated
				for(var i=0;i<vm.msbRouteInfo.nodes.length;i++){
				
						if(vm.msbRouteInfo.nodes[i].ip==ip && vm.msbRouteInfo.nodes[i].port==port )
						{
							vm.server_rtn.warning_block=true;
						    vm.server_rtn.info_block=false; 
						    vm.server_rtn.rtn_info=  $.i18n.prop('org_openo_msb_route_err_host_repeat',[vm.msbRouteInfo.newHost]); 
						    return;
						}
					
				}

				vm.msbRouteInfo.nodes.push({"ip":ip,"port":port,"ttl":vm.msbRouteInfo.newttl});
				vm.msbRouteInfo.newHost="";
				vm.msbRouteInfo.newttl="";
				vm.server_rtn.warning_block=false;
			    vm.server_rtn.info_block=false; 
				vm.server_rtn.rtn_info= "";


        	},
        	delmsbHost:function(ip,port){
        		
        		bootbox.confirm( $.i18n.prop('org_openo_msb_route_err_host_del',[ip],[port]),function(result){
				if(result){
					
					 for(var i=0;i<vm.msbRouteInfo.nodes.length;i++){
			                       if(vm.msbRouteInfo.nodes[i].ip == ip && vm.msbRouteInfo.nodes[i].port == port){
			                            vm.msbRouteInfo.nodes.splice(i, 1);
			                            break;
			                        }
			                    }	

			       }
	
	  		 	});

				vm.server_rtn.warning_block=false;
			    vm.server_rtn.info_block=false; 
				vm.server_rtn.rtn_info= "";

        	},
        	addapiHost:function(){
        		if(vm.apiRouteInfo.server==""){
        			vm.server_rtn.warning_block=true;
				    vm.server_rtn.info_block=false; 
				    vm.server_rtn.rtn_info= $.i18n.prop("org_openo_msb_route_err_host_empty");
					return;
        		}

        		  var reg=/^(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([0-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5]))):(\d{1,5})$/   
         		  var ip,port;
				   if(!reg.test(vm.apiRouteInfo.server)){  
				   	vm.server_rtn.warning_block=true;
				    vm.server_rtn.info_block=false; 
				    vm.server_rtn.rtn_info=  $.i18n.prop("org_openo_msb_route_err_host_format");
					return;
				   }
				   else{
				   	var hosts=vm.apiRouteInfo.server.split(":")
				   	ip=hosts[0];
				   	port=hosts[1];
				   }

        		// determine whether host repeated
				for(var i=0;i<vm.apiRouteInfo.servers.length;i++){
				
						if(vm.apiRouteInfo.servers[i].ip==ip && vm.apiRouteInfo.servers[i].port==port)
						{
							vm.server_rtn.warning_block=true;
						    vm.server_rtn.info_block=false; 
						    vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_host_repeat',[vm.apiRouteInfo.server]); 
							return;
						}
					
				}
				var server={ip:ip,port:port,weight: 0};
				vm.apiRouteInfo.servers.push(server);
				vm.apiRouteInfo.server="";
				vm.server_rtn.warning_block=false;
			    vm.server_rtn.info_block=false; 
				vm.server_rtn.rtn_info= "";


        	},
        	delapiHost:function(ip,port){
        		
        		bootbox.confirm( $.i18n.prop('org_openo_msb_route_err_host_del',[ip],[port]),function(result){
				if(result){
					
					 for(var i=0;i<vm.apiRouteInfo.servers.length;i++){
			                       if(vm.apiRouteInfo.servers[i].ip == ip && vm.apiRouteInfo.servers[i].port == port ){
			                            vm.apiRouteInfo.servers.splice(i, 1);
			                            break;
			                        }
			                    }	

			       }
	
	  		 	});

				vm.server_rtn.warning_block=false;
			    vm.server_rtn.info_block=false; 
				vm.server_rtn.rtn_info= "";

        	},
        	addcustomHost:function(){
        		if(vm.customRouteInfo.server==""){
        			vm.server_rtn.warning_block=true;
				    vm.server_rtn.info_block=false; 
				    vm.server_rtn.rtn_info=  $.i18n.prop("org_openo_msb_route_err_host_empty");
					return;
        		}

        		  var reg=/^(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([0-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5]))):(\d{1,5})$/   
         		  var ip,port;
				   if(!reg.test(vm.customRouteInfo.server)){  
				   	vm.server_rtn.warning_block=true;
				    vm.server_rtn.info_block=false; 
				    vm.server_rtn.rtn_info= $.i18n.prop("org_openo_msb_route_err_host_format");
					return;
				   }
				   else{
				   	var hosts=vm.customRouteInfo.server.split(":")
				   	ip=hosts[0];
				   	port=hosts[1];
				   }

        		//判断host是否重复
				for(var i=0;i<vm.customRouteInfo.servers.length;i++){
				
						if(vm.customRouteInfo.servers[i].ip==ip && vm.customRouteInfo.servers[i].port==port)
						{
							vm.server_rtn.warning_block=true;
						    vm.server_rtn.info_block=false; 
						    vm.server_rtn.rtn_info=  $.i18n.prop('org_openo_msb_route_err_host_repeat',[vm.apiRouteInfo.server]);
							return;
						}
					
				}
				var server={ip:ip,port:port,weight: 0};
				vm.customRouteInfo.servers.push(server);
				vm.customRouteInfo.server="";
				vm.server_rtn.warning_block=false;
			    vm.server_rtn.info_block=false; 
				vm.server_rtn.rtn_info= "";


        	},
        	delcustomHost:function(ip,port){
        		
        		bootbox.confirm( $.i18n.prop('org_openo_msb_route_err_host_del',[ip],[port]),function(result){
				if(result){
					
					 for(var i=0;i<vm.customRouteInfo.servers.length;i++){
			                       if(vm.customRouteInfo.servers[i].ip == ip && vm.customRouteInfo.servers[i].port == port ){
			                            vm.customRouteInfo.servers.splice(i, 1);
			                            break;
			                        }
			                    }	

			       }
	
	  		 	});

				vm.server_rtn.warning_block=false;
			    vm.server_rtn.info_block=false; 
				vm.server_rtn.rtn_info= "";

        	},
        	addiuiHost:function(){
        		if(vm.iuiRouteInfo.server==""){
        			vm.server_rtn.warning_block=true;
				    vm.server_rtn.info_block=false; 
				    vm.server_rtn.rtn_info= $.i18n.prop("org_openo_msb_route_err_host_empty");
					return;
        		}

        		  var reg=/^(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([0-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5]))):(\d{1,5})$/   
         		  var ip,port;
				   if(!reg.test(vm.iuiRouteInfo.server)){  
				   	vm.server_rtn.warning_block=true;
				    vm.server_rtn.info_block=false; 
				    vm.server_rtn.rtn_info= $.i18n.prop("org_openo_msb_route_err_host_format");
					return;
				   }
				   else{
				   	var hosts=vm.iuiRouteInfo.server.split(":")
				   	ip=hosts[0];
				   	port=hosts[1];
				   }

        		//判断host是否重复
				for(var i=0;i<vm.iuiRouteInfo.servers.length;i++){
				
						if(vm.iuiRouteInfo.servers[i].ip==ip && vm.iuiRouteInfo.servers[i].port==port)
						{
							vm.server_rtn.warning_block=true;
						    vm.server_rtn.info_block=false; 
						    vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_host_repeat',[vm.apiRouteInfo.server]);
							return;
						}
					
				}
				var server={ip:ip,port:port,weight: 0};
				vm.iuiRouteInfo.servers.push(server);
				vm.iuiRouteInfo.server="";
				vm.server_rtn.warning_block=false;
			    vm.server_rtn.info_block=false; 
				vm.server_rtn.rtn_info= "";


        	},
        	deliuiHost:function(ip,port){
        		
        		bootbox.confirm($.i18n.prop('org_openo_msb_route_err_host_del',[ip],[port]),function(result){
				if(result){
					
					 for(var i=0;i<vm.iuiRouteInfo.servers.length;i++){
			                       if(vm.iuiRouteInfo.servers[i].ip == ip && vm.iuiRouteInfo.servers[i].port == port ){
			                            vm.iuiRouteInfo.servers.splice(i, 1);
			                            break;
			                        }
			                    }	

			       }
	
	  		 	});

				vm.server_rtn.warning_block=false;
			    vm.server_rtn.info_block=false; 
				vm.server_rtn.rtn_info= "";

        	},
        	$showmsbRouteDlg:function() {
        		vm.msbRouteInfo.serviceName="";	
				vm.msbRouteInfo.version="";
				vm.msbRouteInfo.url="";
				vm.msbRouteInfo.protocol="";
				vm.msbRouteInfo.visualRange="";
				vm.msbRouteInfo.visualRangeArray=[];
				vm.msbRouteInfo.newHost="";
				vm.msbRouteInfo.nodes=[];					
				vm.msbRouteInfo.status="1";	
			

				vm.routeDlgInfo.saveType = "add";
				vm.routeDlgInfo.titleName=$.i18n.prop('org_openo_msb_route_title_add_msb');
				vm.server_rtn.warning_block=false;
				vm.server_rtn.info_block=false;

		

				$(".form-group").each(function () {
						$(this).removeClass('has-success');
						$(this).removeClass('has-error');
						$(this).find(".help-block[id]").remove();
						$(this).find(".form-tip").removeClass('form-input-focus');
  						$(this).find(".item-tip").removeClass('item-tip-focus');
					});

				$("#msbrouteDlg").modal("show");
        	},
        	$showiuiRouteDlg:function() {

				vm.iuiRouteInfo.serviceName="";
				vm.iuiRouteInfo.url="";
				vm.iuiRouteInfo.server="";
				vm.iuiRouteInfo.servers=[];	
				vm.iuiRouteInfo.control="0";	
				vm.iuiRouteInfo.status="1";	
			

				vm.routeDlgInfo.saveType = "add";
				vm.routeDlgInfo.titleName=$.i18n.prop('org_openo_msb_route_title_add_iui');
				vm.server_rtn.warning_block=false;
				vm.server_rtn.info_block=false;

				//$("input[name='iuioriService']").val("");

				$(".form-group").each(function () {
						$(this).removeClass('has-success');
						$(this).removeClass('has-error');
						$(this).find(".help-block[id]").remove();
						$(this).find(".form-tip").removeClass('form-input-focus');
  						$(this).find(".item-tip").removeClass('item-tip-focus');
					});

				$("#iuirouteDlg").modal("show");
			},
			$showcustomRouteDlg:function() {

				vm.customRouteInfo.serviceName="";
				vm.customRouteInfo.url="";
				vm.customRouteInfo.server="";
				vm.customRouteInfo.servers=[];	
				vm.customRouteInfo.control="0";	
				vm.customRouteInfo.status="1";		
			

				vm.routeDlgInfo.saveType = "add";
				vm.routeDlgInfo.titleName=$.i18n.prop('org_openo_msb_route_title_add_custom');
				vm.server_rtn.warning_block=false;
				vm.server_rtn.info_block=false;

				//$("input[name='customoriService']").val("");

				$(".form-group").each(function () {
						$(this).removeClass('has-success');
						$(this).removeClass('has-error');
						$(this).find(".help-block[id]").remove();
						$(this).find(".form-tip").removeClass('form-input-focus');
  						$(this).find(".item-tip").removeClass('item-tip-focus');
					});

				$("#customrouteDlg").modal("show");
			},
			$showApiRouteDlg : function() {

 				$('#apiTab a:first').tab('show');
				vm.apiRouteInfo.serviceName="";
				vm.apiRouteInfo.version="";
				vm.apiRouteInfo.url="";
				vm.apiRouteInfo.metricsUrl="/admin/metrics";
				vm.apiRouteInfo.control="0";
				vm.apiRouteInfo.status="1";
				// vm.apiRouteInfo.apiJson="/service/swagger.json";
				vm.apiRouteInfo.apiJsonType="1";				
				vm.apiRouteInfo.server="";
				vm.apiRouteInfo.servers=[];	
			
				vm.apiJson.custom="/swagger.json";

				//$("input[name='oriService']").val("");

				$('#metricsFullurl').collapse('hide');
				$('#apiJsonFullurl').collapse('hide');

			

				vm.routeDlgInfo.saveType = "add";
				vm.routeDlgInfo.titleName=$.i18n.prop('org_openo_msb_route_title_add_api');
				vm.server_rtn.warning_block=false;
				vm.server_rtn.info_block=false;

				$(".form-group").each(function () {
						$(this).removeClass('has-success');
						$(this).removeClass('has-error');
						$(this).find(".help-block[id]").remove();
						$(this).find(".form-tip").removeClass('form-input-focus');
  						$(this).find(".item-tip").removeClass('item-tip-focus');
					});


				$("#routeDlg").modal("show");
			},
			$saveiuiRoute : function() {

				iuisuccess.hide();
				iuierror.hide();
				if (iuiform.valid() == false) {
					return false;
				}

				vm.server_rtn.warning_block=false;
				vm.server_rtn.info_block=true;

				vm.iuiRouteInfo.serviceName=$.trim(vm.iuiRouteInfo.serviceName);
				
				var servers=[];
				for(var i=0;i<vm.iuiRouteInfo.servers.length;i++){
					var server={ip:vm.iuiRouteInfo.servers[i].ip,port:vm.iuiRouteInfo.servers[i].port,weight: 0};
					servers.push(server);
				}



				var iuiRouteUrl=vm.iuiRouteInfo.url=="/"?"":vm.iuiRouteInfo.url;
				var data= JSON.stringify({
							 "serviceName": vm.iuiRouteInfo.serviceName,
							  "url": iuiRouteUrl,
							  "control":vm.iuiRouteInfo.control,
							  "status":vm.iuiRouteInfo.status,
							  "servers": servers
						});

				if(vm.routeDlgInfo.saveType=="add")
				{

					//Determine whether to repeat
					for(var i=0;i<vm.iuiRouteArray.length;i++){
					
							if(vm.iuiRouteArray[i].serviceName==vm.iuiRouteInfo.serviceName)
							{
								vm.server_rtn.warning_block=true;
							    vm.server_rtn.info_block=false; 
							    vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_service_repeat',[vm.iuiRouteInfo.serviceName]);
								return;
							}
						
					}

                 //save service
                 
              
                   $.ajax({
	                "type": 'POST',
	                "url":  vm.$iuiRouteUrl,
	                "data" : data,
	                "dataType": "json",
	                "contentType":"application/json",
	                success: function (resp) {  
	                        
						vm.iuiRouteArray.push(JSON.parse(data)); 
						vm.iuiRouteArray.sort(function(a,b){return a.serviceName>b.serviceName?1:-1}); 

						routeUtil.refreshRoute();	
						$('#iuirouteDlg').modal('hide');							
						routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_iui'),$.i18n.prop('org_openo_msb_route_service_save'),"success");               	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
						   vm.server_rtn.warning_block=true;
					       vm.server_rtn.info_block=false; 
					       vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_service_save')+textStatus+":"+errorThrown;                
	                      
	                 }
	            });
               }
               else{  //update

               		 var url= vm.$iuiRouteInstanceUrl;
                        url=url.replace("{serviceName}",vm.iuiRouteInfo.oldServiceName);

                           //Determine whether to repeat
               	  if(vm.iuiRouteInfo.serviceName!=vm.iuiRouteInfo.oldServiceName)  //Has been modified service name
               	  {
					for(var i=0;i<vm.iuiRouteArray.length;i++){
					
							if(vm.iuiRouteArray[i].serviceName==vm.iuiRouteInfo.serviceName)
							{
								vm.server_rtn.warning_block=true;
							    vm.server_rtn.info_block=false; 
							    vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_service_repeat',[vm.iuiRouteInfo.serviceName]);
								return;
							}
						
					}
				}

               	$.ajax({
	                "type": 'PUT',
	                "url": url,
	                 "data" :data,
	                "dataType": "json",
	                "contentType":"application/json",
	                success: function (resp) {  
	                  
						 	for(var i=0;i<vm.iuiRouteArray.length;i++){
			                    if(vm.iuiRouteArray[i].serviceName == vm.iuiRouteInfo.oldServiceName)
			                    { 
			                        vm.iuiRouteArray[i].serviceName=vm.iuiRouteInfo.serviceName;			                      
			                        vm.iuiRouteArray[i].url=iuiRouteUrl;
			                        vm.iuiRouteArray[i].servers=vm.iuiRouteInfo.servers;
			                       
			                        break;
			                    }
			                 }
			      


						$('#iuirouteDlg').modal('hide');							
						routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_iui'),$.i18n.prop('org_openo_msb_route_service_update'),"success"); 
						

	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
						   vm.server_rtn.warning_block=true;
					       vm.server_rtn.info_block=false; 
					       vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_service_save')+textStatus+":"+errorThrown;                
	                      
	                 }
	            });

              
			   }
			},
			$savecustomRoute : function() {

				customsuccess.hide();
				customerror.hide();
				if (customform.valid() == false) {
					return false;
				}

				if(vm.customRouteInfo.servers.length==0){
					vm.server_rtn.warning_block=true;
					vm.server_rtn.info_block=false; 
					vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_host_leastone');
					return;
				}

				vm.server_rtn.warning_block=false;
				vm.server_rtn.info_block=true;

				vm.customRouteInfo.serviceName=$.trim(vm.customRouteInfo.serviceName);
				
				var servers=[];
				for(var i=0;i<vm.customRouteInfo.servers.length;i++){
					var server={ip:vm.customRouteInfo.servers[i].ip,port:vm.customRouteInfo.servers[i].port,weight: 0};
					servers.push(server);
				}

				var customRouteUrl=vm.customRouteInfo.url=="/"?"":vm.customRouteInfo.url;
				var data= JSON.stringify({
							 "serviceName": vm.customRouteInfo.serviceName,
							  "url": customRouteUrl,
							  "control":vm.customRouteInfo.control,
							  "status":vm.customRouteInfo.status,
							  "servers": servers
						});

				if(vm.routeDlgInfo.saveType=="add")
				{

					//Determine whether to repeat
					for(var i=0;i<vm.customRouteArray.length;i++){
					
							if(vm.customRouteArray[i].serviceName==vm.customRouteInfo.serviceName)
							{
								vm.server_rtn.warning_block=true;
							    vm.server_rtn.info_block=false; 
							    vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_service_repeat',[vm.customRouteInfo.serviceName]);
								return;
							}
						
					}

                 //save 
                 
              
                   $.ajax({
	                "type": 'POST',
	                "url":  vm.$customRouteInstanceUrl,
	                "data" : data,
	                "dataType": "json",
	                "contentType":"application/json",
	                success: function (resp) {  
	                        
						vm.customRouteArray.push(JSON.parse(data)); 
						routeUtil.groupRoute(vm.customRouteArray);


						routeUtil.refreshRoute();	
						$('#customrouteDlg').modal('hide');							
						routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_custom'),$.i18n.prop('org_openo_msb_route_service_save'),"success");               	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
						   vm.server_rtn.warning_block=true;
					       vm.server_rtn.info_block=false; 
					       vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_service_save')+textStatus+":"+errorThrown;                
	                      
	                 }
	            });
               }
               else{  //update

               	    //Determine whether to repeat
               	  if(vm.customRouteInfo.serviceName!=vm.customRouteInfo.oldServiceName)  //Has been modified service name
               	  {
					for(var i=0;i<vm.customRouteArray.length;i++){
					
							if(vm.customRouteArray[i].serviceName==vm.customRouteInfo.serviceName)
							{
								vm.server_rtn.warning_block=true;
							    vm.server_rtn.info_block=false; 
							    vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_service_repeat',[vm.customRouteInfo.serviceName]);
								return;
							}
						
					}
				}

               		 var url= vm.$customRouteInstanceUrl+"?serviceName="+vm.customRouteInfo.oldServiceName;
               
               	$.ajax({
	                "type": 'PUT',
	                "url": url,
	                 "data" :data,
	                "dataType": "json",
	                "contentType":"application/json",
	                success: function (resp) {  
	             
						 	for(var i=0;i<vm.customRouteArray.length;i++){
			                    if(vm.customRouteArray[i].serviceName == vm.customRouteInfo.oldServiceName)
			                    {
			                        vm.customRouteArray[i].serviceName=vm.customRouteInfo.serviceName;			                      
			                        vm.customRouteArray[i].url=customRouteUrl;
			                        vm.customRouteArray[i].servers=vm.customRouteInfo.servers;
			                        break;
			                    }
			                 }
						
						routeUtil.groupRoute(vm.customRouteArray);
			      


						$('#customrouteDlg').modal('hide');							
						routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_custom'),$.i18n.prop('org_openo_msb_route_service_update'),"success"); 
						

	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
						   vm.server_rtn.warning_block=true;
					       vm.server_rtn.info_block=false; 
					       vm.server_rtn.rtn_info=$.i18n.prop('org_openo_msb_route_err_service_save')+textStatus+":"+errorThrown;                
	                      
	                 }
	            });

              
			   }
			},
			$saveApiRoute : function() {

				success.hide();
				error.hide();
				if (form.valid() == false) {
					return false;
				}

				if(vm.apiRouteInfo.servers.length==0){
					vm.server_rtn.warning_block=true;
					vm.server_rtn.info_block=false; 
					vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_host_leastone');
					return;
				}

				vm.server_rtn.warning_block=false;
				vm.server_rtn.info_block=true;

				
				var apiRouteUrl=vm.apiRouteInfo.url=="/"?"":vm.apiRouteInfo.url;
				var apiJson=vm.apiRouteInfo.apiJsonType=="1"?vm.apiJson.custom:vm.apiJson.local;	

				var servers=[];
				for(var i=0;i<vm.apiRouteInfo.servers.length;i++){
					var server={ip:vm.apiRouteInfo.servers[i].ip,port:vm.apiRouteInfo.servers[i].port,weight: 0};
					servers.push(server);
				}


				var data= JSON.stringify({
							 "serviceName": vm.apiRouteInfo.serviceName,
							  "version": vm.apiRouteInfo.version,
							  "url": apiRouteUrl,
							  "metricsUrl":vm.apiRouteInfo.metricsUrl,
							  "apiJson": apiJson,
							  "apiJsonType":vm.apiRouteInfo.apiJsonType,
							  "control":vm.apiRouteInfo.control,
							  "status":vm.apiRouteInfo.status,
							  "servers": servers
						
						});

				if(vm.routeDlgInfo.saveType=="add")
				{

					 //Determine whether to repeat
					for(var i=0;i<vm.apiRouteArray.length;i++){
					
							if(vm.apiRouteArray[i].serviceName==vm.apiRouteInfo.serviceName 
								&& vm.apiRouteArray[i].version==vm.apiRouteInfo.version )
							{
								vm.server_rtn.warning_block=true;
							    vm.server_rtn.info_block=false; 
							    vm.server_rtn.rtn_info=$.i18n.prop('org_openo_msb_route_err_api_repeat',[vm.apiRouteInfo.serviceName],[vm.apiRouteInfo.version]);
								return;
							}
						
					}

                 //save
                 
              
                   $.ajax({
	                "type": 'POST',
	                "url":  vm.$apiRouteUrl,
	                "data" : data,
	                "dataType": "json",
	                "contentType":"application/json",
	                success: function (resp) {  
	                        
						vm.apiRouteArray.push(JSON.parse(data)); 
						vm.apiRouteArray.sort(function(a,b){return a.serviceName>b.serviceName?1:-1}); 

						routeUtil.refreshRoute();	
						$('#routeDlg').modal('hide');							
						routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_api'),$.i18n.prop('org_openo_msb_route_service_save'),"success");               	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
						   vm.server_rtn.warning_block=true;
					       vm.server_rtn.info_block=false; 
					       vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_service_save')+textStatus+":"+errorThrown;                
	                      
	                 }
	            });
               }
               else{  //update
               		

                //Determine whether to repeat
               	  if(vm.apiRouteInfo.serviceName!=vm.apiRouteInfo.oldServiceName || 
               	  vm.apiRouteInfo.version!=vm.apiRouteInfo.oldVersion)  //Has been modified service name
               	  {
					for(var i=0;i<vm.apiRouteArray.length;i++){
					
							if(vm.apiRouteArray[i].serviceName==vm.apiRouteInfo.serviceName 
								&& vm.apiRouteArray[i].version==vm.apiRouteInfo.version )
							{
								vm.server_rtn.warning_block=true;
							    vm.server_rtn.info_block=false; 
							    vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_api_repeat',[vm.apiRouteInfo.serviceName],[vm.apiRouteInfo.version]);
								return;
							}
						
					}
				}

				 var url= vm.$apiRouteInstanceUrl;
               		 var version=vm.apiRouteInfo.oldVersion==""?"null":vm.apiRouteInfo.oldVersion;
                        url=url.replace("{serviceName}",vm.apiRouteInfo.oldServiceName).replace("{version}",version);


               		 $.ajax({
	                "type": 'PUT',
	                "url": url,
	                 "data" :data,
	                "dataType": "json",
	                "contentType":"application/json",
	                success: function (resp) {  
	                  
						 	for(var i=0;i<vm.apiRouteArray.length;i++){
			                    if(vm.apiRouteArray[i].serviceName == vm.apiRouteInfo.oldServiceName && 
			                    	vm.apiRouteArray[i].version == vm.apiRouteInfo.oldVersion)
			                    {
			                        //vm.apiRouteArray[i] = JSON.parse(data);
			      					vm.apiRouteArray[i].serviceName=vm.apiRouteInfo.serviceName;
									vm.apiRouteArray[i].version= vm.apiRouteInfo.version;
									vm.apiRouteArray[i].url=apiRouteUrl;
									vm.apiRouteArray[i].metricsUrl=vm.apiRouteInfo.metricsUrl;
									vm.apiRouteArray[i].apiJson=apiJson;
									vm.apiRouteArray[i].apiJsonType=vm.apiRouteInfo.apiJsonType;

									vm.apiRouteArray[i].servers=vm.apiRouteInfo.servers;

									
			                        break;
			                    }
			                 }
			      


						$('#routeDlg').modal('hide');							
						routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_api'),$.i18n.prop('org_openo_msb_route_service_update'),"success"); 
						

	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
						   vm.server_rtn.warning_block=true;
					       vm.server_rtn.info_block=false; 
					       vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_service_save')+textStatus+":"+errorThrown;                
	                      
	                 }
	            });

              
			   }
			},
			$savemsbRoute : function() {

				msbsuccess.hide();
				msberror.hide();
				if (msbform.valid() == false) {
					return false;
				}

				if(vm.msbRouteInfo.nodes.length==0){
					vm.server_rtn.warning_block=true;
					vm.server_rtn.info_block=false; 
					vm.server_rtn.rtn_info=$.i18n.prop('org_openo_msb_route_err_host_leastone');
					return;
				}

				if(vm.msbRouteInfo.visualRangeArray.length==0){
					vm.server_rtn.warning_block=true;
					vm.server_rtn.info_block=false; 
					vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_visualrange_empty');
					return;
				}

				vm.server_rtn.warning_block=false;
				vm.server_rtn.info_block=true;

				var msbUrl=vm.msbRouteInfo.url=="/"?"":vm.msbRouteInfo.url;
		

				var nodes=[];
				for(var i=0;i<vm.msbRouteInfo.nodes.length;i++){
					var node={ip:vm.msbRouteInfo.nodes[i].ip,port:vm.msbRouteInfo.nodes[i].port,ttl: vm.msbRouteInfo.nodes[i].ttl};
					nodes.push(node);
				}

				var data= JSON.stringify({
							 "serviceName": vm.msbRouteInfo.serviceName,
							  "version": vm.msbRouteInfo.version,
							  "url": msbUrl,							 
							  "nodes": nodes,
							  "protocol":vm.msbRouteInfo.protocol,
							  "visualRange":vm.msbRouteInfo.visualRangeArray.join("|")
						});

				if(vm.routeDlgInfo.saveType=="add")
				{

					//Determine whether to repeat
					for(var i=0;i<vm.msbRouteArray.length;i++){
					
							if(vm.msbRouteArray[i].serviceName==vm.msbRouteInfo.serviceName 
								&& vm.msbRouteArray[i].version==vm.msbRouteInfo.version )
							{
								vm.server_rtn.warning_block=true;
							    vm.server_rtn.info_block=false; 
							    vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_api_repeat',[vm.msbRouteInfo.serviceName],[vm.msbRouteInfo.version]);
							}
						
					}

                 //save
                 
              
                   $.ajax({
	                "type": 'POST',
	                "url":  vm.$msbRouteUrl,
	                "data" : data,
	                "dataType": "json",
	                "contentType":"application/json",
	                success: function (resp) {  
	                   
						 // vm.msbRouteArray.push(JSON.parse(data)); 
						  table.destroy();
						  vm.initMSBRoute();

					  //       table=$('#msbTable').DataTable({
						     
							//   "oLanguage": vm.$dataTableLanguage,
							//   columnDefs: [ {
							// 	      targets: [ 7 ],
							// 	      "searchable": false,
							// 	      "bSortable": false,
							// 	    }]
							// });
	
						$('#msbrouteDlg').modal('hide');							
						routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_msb'),$.i18n.prop('org_openo_msb_route_service_save'),"success");               	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
						   vm.server_rtn.warning_block=true;
					       vm.server_rtn.info_block=false; 
					       vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_service_save')+XMLHttpRequest.responseText;                
	                      
	                 }
	            });
               }
               else{  //update
               		

                //Determine whether to repeat
               	  if(vm.msbRouteInfo.serviceName!=vm.msbRouteInfo.oldServiceName || 
               	  vm.msbRouteInfo.version!=vm.msbRouteInfo.oldVersion)  //Has been modified service name or version
               	  {
					for(var i=0;i<vm.msbRouteArray.length;i++){
					
							if(vm.msbRouteArray[i].serviceName==vm.msbRouteInfo.serviceName 
								&& vm.msbRouteArray[i].version==vm.msbRouteInfo.version )
							{
								vm.server_rtn.warning_block=true;
							    vm.server_rtn.info_block=false; 
							    vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_api_repeat',[vm.msbRouteInfo.serviceName],[vm.msbRouteInfo.version]);
								return;
							}
						
					}
				}

				 var url= vm.$msbRouteInstanceUrl;
               		 var version=vm.msbRouteInfo.oldVersion==""?"null":vm.msbRouteInfo.oldVersion;
               		 var serviceName2= vm.msbRouteInfo.oldServiceName.replace(/\//g,"*");
                        url=url.replace("{serviceName}",serviceName2).replace("{version}",version);


               		 $.ajax({
	                "type": 'PUT',
	                "url": url,
	                 "data" :data,
	                "dataType": "json",
	                "contentType":"application/json",
	                success: function (resp) {  
	             
						 	for(var i=0;i<vm.msbRouteArray.length;i++){
			                    if(vm.msbRouteArray[i].serviceName == vm.msbRouteInfo.oldServiceName && 
			                    	vm.msbRouteArray[i].version == vm.msbRouteInfo.oldVersion)
			                    {
			                        //vm.apiRouteArray[i] = JSON.parse(data);
			      					vm.msbRouteArray[i].serviceName=vm.msbRouteInfo.serviceName;
									vm.msbRouteArray[i].version= vm.msbRouteInfo.version;
									vm.msbRouteArray[i].url=msbUrl;
									vm.msbRouteArray[i].protocol=vm.msbRouteInfo.protocol;
									vm.msbRouteArray[i].visualRange=vm.msbRouteInfo.visualRange;
									vm.msbRouteArray[i].visualRangeArray=vm.msbRouteInfo.visualRangeArray;
									vm.msbRouteArray[i].nodes=nodes;
											                     
			                        break;
			                    }
			                 }
			      


						$('#msbrouteDlg').modal('hide');							
						routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_msb'),$.i18n.prop('org_openo_msb_route_service_update'),"success"); 
						

	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
						   vm.server_rtn.warning_block=true;
					       vm.server_rtn.info_block=false; 
					       vm.server_rtn.rtn_info= $.i18n.prop('org_openo_msb_route_err_service_save')+XMLHttpRequest.responseText;                
	                      
	                 }
	            });

              
			   }
			},

			updateApiRouteStatus:function(serviceName,version,status){

				var url= vm.$apiRouteStatusUrl;
				    var version2=version==""?"null":version;
                        url=url.replace("{serviceName}",serviceName).replace("{version}",version2).replace("{status}",status);
	
					 $.ajax({
	                "type": 'PUT',
	                "url": url,
	                "dataType": "json",
	                success: function (resp) {  
	                        
	                      
						 	 for(var i=0;i<vm.apiRouteArray.length;i++){
			                       if(vm.apiRouteArray[i].serviceName == serviceName && 
			                    	vm.apiRouteArray[i].version == version){
			                            vm.apiRouteArray[i].status=status;
			                            break;
			                        }
			                    }	

			                  if(status=='1')   
			                  {
                   			    routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_api'),$.i18n.prop('org_openo_msb_route_status_enable'),"success");
                   			  }
                   			  else{
                   			  	routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_api'),$.i18n.prop('org_openo_msb_route_status_disable'),"success");
                   			  }
                   			

            	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
					        bootbox.alert($.i18n.prop('org_openo_msb_route_err_status')+textStatus+":"+errorThrown);                
	                      
	                 }
	            });

			},
			updateiuiRouteStatus:function(serviceName,status){

				var url= vm.$iuiRouteStatusUrl;
				
                     url=url.replace("{serviceName}",serviceName).replace("{status}",status);
	
					 $.ajax({
	                "type": 'PUT',
	                "url": url,
	                "dataType": "json",
	                success: function (resp) {  
	                        
	                       
						 	 for(var i=0;i<vm.iuiRouteArray.length;i++){
			                       if(vm.iuiRouteArray[i].serviceName == serviceName){
			                            vm.iuiRouteArray[i].status=status;
			                            break;
			                        }
			                    }	

			                  if(status=='1')   
			                  {
                   			    routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_iui'),$.i18n.prop('org_openo_msb_route_status_enable'),"success");
                   			  }
                   			  else{
                   			  	routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_iui'),$.i18n.prop('org_openo_msb_route_status_disable'),"success");
                   			  }
                   			
            	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
					        bootbox.alert($.i18n.prop('org_openo_msb_route_err_status')+textStatus+":"+errorThrown);                
	                      
	                 }
	            });

			},
			updatecustomRouteStatus:function(serviceName,status){

				
              var url= vm.$customRouteStatusUrl+"?serviceName="+serviceName+"&status="+status;

	
					 $.ajax({
	                "type": 'PUT',
	                "url": url,
	                "dataType": "json",
	                success: function (resp) {  
	                        
	                  
						 	 for(var i=0;i<vm.customRouteArray.length;i++){
			                       if(vm.customRouteArray[i].serviceName == serviceName){
			                            vm.customRouteArray[i].status=status;
			                            break;
			                        }
			                    }	


			            routeUtil.groupRoute(vm.customRouteArray);

			                  if(status=='1')   
			                  {
                   			    routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_custom'),$.i18n.prop('org_openo_msb_route_status_enable'),"success");
                   			  }
                   			  else{
                   			  	routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_custom'),$.i18n.prop('org_openo_msb_route_status_disable'),"success");
                   			  }
                   			

            	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
					        bootbox.alert($.i18n.prop('org_openo_msb_route_err_status')+textStatus+":"+errorThrown);                
	                      
	                 }
	            });

			},
			updateApiRoute:function(apiRouteInfo,type){
			

			

				$('#metricsFullurl').collapse('hide');
				$('#apiJsonFullurl').collapse('hide');
				$('#apiTab a:first').tab('show');
				
				vm.apiRouteInfo.serviceName=apiRouteInfo.serviceName;
				vm.apiRouteInfo.oldServiceName=apiRouteInfo.serviceName;
				vm.apiRouteInfo.version= apiRouteInfo.version;
				vm.apiRouteInfo.oldVersion=apiRouteInfo.version;
				vm.apiRouteInfo.url=apiRouteInfo.url==""?"/":apiRouteInfo.url;
				vm.apiRouteInfo.metricsUrl=apiRouteInfo.metricsUrl;
				vm.apiRouteInfo.apiJson=apiRouteInfo.apiJson;
				vm.apiRouteInfo.apiJsonType=apiRouteInfo.apiJsonType;
				vm.apiRouteInfo.control=apiRouteInfo.control;
				vm.apiRouteInfo.status= apiRouteInfo.status;

				if(vm.apiRouteInfo.apiJsonType=="1"){
					vm.apiJson.custom=apiRouteInfo.apiJson;
				}
				else{
					vm.apiJson.local=apiRouteInfo.apiJson;
					vm.apiJson.custom="/swagger.json";
					 $("#apiJsonSelect").val(apiRouteInfo.apiJson);
				}

				vm.apiRouteInfo.server="";
				vm.apiRouteInfo.servers=[];
				vm.apiRouteInfo.servers=apiRouteInfo.servers;

				


				// vm.apiRouteInfo= jQuery.extend({},apiRouteInfo);
		
				vm.routeDlgInfo.saveType = type;
				if(type=='update'){
					vm.routeDlgInfo.titleName=$.i18n.prop('org_openo_msb_route_title_edit_api');	
				}
				else if(type=='view'){
					vm.routeDlgInfo.titleName=$.i18n.prop('org_openo_msb_route_title_view_api');	
				}
				
				vm.server_rtn.warning_block=false;
				vm.server_rtn.info_block=false;


				$(".form-group").each(function () {
						$(this).removeClass('has-success');
						$(this).removeClass('has-error');
						$(this).find(".help-block[id]").remove();
						$(this).find(".item-tip").addClass("item-tip-focus");
						$(this).find(".form-tip").removeClass('form-input-focus');
					});

				$("#routeDlg").modal("show");
			},
			updateiuiRoute:function(iuiRouteInfo,type){
				
				
				vm.iuiRouteInfo.serviceName=iuiRouteInfo.serviceName;
				vm.iuiRouteInfo.oldServiceName=iuiRouteInfo.serviceName;
				vm.iuiRouteInfo.url=iuiRouteInfo.url==""?"/":iuiRouteInfo.url;
				vm.iuiRouteInfo.server="";
				vm.iuiRouteInfo.servers=[];
				vm.iuiRouteInfo.servers=iuiRouteInfo.servers;
				vm.iuiRouteInfo.control=iuiRouteInfo.control;
				vm.iuiRouteInfo.status=iuiRouteInfo.status;


				// vm.apiRouteInfo= jQuery.extend({},apiRouteInfo);
		
				
				vm.routeDlgInfo.saveType = type;
				if(type=='update'){
					vm.routeDlgInfo.titleName=$.i18n.prop('org_openo_msb_route_title_edit_iui');	
				}
				else if(type=='view'){
					vm.routeDlgInfo.titleName=$.i18n.prop('org_openo_msb_route_title_view_iui');	
				}

				vm.server_rtn.warning_block=false;
				vm.server_rtn.info_block=false;


				$(".form-group").each(function () {
						$(this).removeClass('has-success');
						$(this).removeClass('has-error');
						$(this).find(".help-block[id]").remove();
						$(this).find(".item-tip").addClass("item-tip-focus");
						$(this).find(".form-tip").removeClass('form-input-focus');
					});

				$("#iuirouteDlg").modal("show");
			},
			updatecustomRoute:function(customRouteInfo,type){
				
				
				vm.customRouteInfo.serviceName=customRouteInfo.serviceName;
				vm.customRouteInfo.oldServiceName=customRouteInfo.serviceName;
				
				vm.customRouteInfo.url=customRouteInfo.url==""?"/":customRouteInfo.url;
				vm.customRouteInfo.server="";
				vm.customRouteInfo.servers=[];
				vm.customRouteInfo.servers=customRouteInfo.servers;
				vm.customRouteInfo.control=customRouteInfo.control;
				vm.customRouteInfo.status=customRouteInfo.status;

				// vm.apiRouteInfo= jQuery.extend({},apiRouteInfo);
		
				vm.routeDlgInfo.saveType = type;
				if(type=='update'){
					vm.routeDlgInfo.titleName=$.i18n.prop('org_openo_msb_route_title_edit_custom');
				}
				else if(type=='view'){
					vm.routeDlgInfo.titleName=$.i18n.prop('org_openo_msb_route_title_view_custom');
				}
				vm.server_rtn.warning_block=false;
				vm.server_rtn.info_block=false;


				$(".form-group").each(function () {
						$(this).removeClass('has-success');
						$(this).removeClass('has-error');
						$(this).find(".help-block[id]").remove();
						$(this).find(".item-tip").addClass("item-tip-focus");
						$(this).find(".form-tip").removeClass('form-input-focus');
					});

				$("#customrouteDlg").modal("show");
			},
			updatemsbRoute:function(msbRouteInfo){
			
					vm.msbRouteInfo.serviceName=msbRouteInfo.serviceName;
					vm.msbRouteInfo.oldServiceName=msbRouteInfo.serviceName;
					vm.msbRouteInfo.version= msbRouteInfo.version;
					vm.msbRouteInfo.oldVersion= msbRouteInfo.version
					vm.msbRouteInfo.url=msbRouteInfo.url==""?"/":msbRouteInfo.url;
					vm.msbRouteInfo.protocol=msbRouteInfo.protocol;
					vm.msbRouteInfo.visualRange=msbRouteInfo.visualRange;
					vm.msbRouteInfo.visualRangeArray=msbRouteInfo.visualRange.split("|");
					vm.msbRouteInfo.nodes=msbRouteInfo.nodes;
					vm.msbRouteInfo.newHost="";
					vm.msbRouteInfo.newttl="";
			
				routeUtil.changeTargetServiceUrl();
		
				vm.routeDlgInfo.saveType = "update";
				vm.routeDlgInfo.titleName=$.i18n.prop('org_openo_msb_route_title_edit_msb');
				vm.server_rtn.warning_block=false;
				vm.server_rtn.info_block=false;


				$(".form-group").each(function () {
						$(this).removeClass('has-success');
						$(this).removeClass('has-error');
						$(this).find(".help-block[id]").remove();
						$(this).find(".item-tip").addClass("item-tip-focus");
						$(this).find(".form-tip").removeClass('form-input-focus');
					});

				$("#msbrouteDlg").modal("show");
			},
			delApiRoute:function(serviceName,version){
				bootbox.confirm($.i18n.prop('org_openo_msb_route_err_service_del_ask',[serviceName],[version]),function(result){
				if(result){
					var url= vm.$apiRouteInstanceUrl;
				    var version2=version==""?"null":version;
                        url=url.replace("{serviceName}",serviceName).replace("{version}",version2);
	
					 $.ajax({
	                "type": 'DELETE',
	                "url": url,
	                "dataType": "json",
	                success: function (resp) {  
	                        
						 	 for(var i=0;i<vm.apiRouteArray.length;i++){
			                       if(vm.apiRouteArray[i].serviceName == serviceName && 
			                    	vm.apiRouteArray[i].version == version){
			                            vm.apiRouteArray.splice(i, 1);
			                            break;
			                        }
			                    }	

                   			 routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_api'),$.i18n.prop('org_openo_msb_route_service_del_success'),"success");
                   			
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
					        bootbox.alert($.i18n.prop('org_openo_msb_route_service_del_fail')+textStatus+":"+errorThrown);                
	                      
	                 }
	            });


			       }
	
	  		 	});
				
			},
			deliuiRoute:function(serviceName){
				bootbox.confirm($.i18n.prop('org_openo_msb_route_err_service2_del_ask',[serviceName]),function(result){
				if(result){
					var url= vm.$iuiRouteInstanceUrl;
				 
                        url=url.replace("{serviceName}",serviceName);
	
					 $.ajax({
	                "type": 'DELETE',
	                "url": url,
	                "dataType": "json",
	                success: function (resp) {  
	                        
						 	 for(var i=0;i<vm.iuiRouteArray.length;i++){
			                       if(vm.iuiRouteArray[i].serviceName == serviceName){
			                            vm.iuiRouteArray.splice(i, 1);
			                            break;
			                        }
			                    }	

                   			 routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_iui'),$.i18n.prop('org_openo_msb_route_service_del_success'),"success");
                   			
            	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
					        bootbox.alert($.i18n.prop('org_openo_msb_route_service_del_fail')+textStatus+":"+errorThrown);                
	                      
	                 }
	            });


			       }
	
	  		 	});
				
			},
			delcustomRoute:function(serviceName){
				bootbox.confirm($.i18n.prop('org_openo_msb_route_err_service2_del_ask',[serviceName]),function(result){
				if(result){
					var url= vm.$customRouteInstanceUrl+"?serviceName="+serviceName;
	
					 $.ajax({
	                "type": 'DELETE',
	                "url": url,
	                "dataType": "json",
	                success: function (resp) {  
	                        
						 	 for(var i=0;i<vm.customRouteArray.length;i++){
			                       if(vm.customRouteArray[i].serviceName == serviceName){
			                            vm.customRouteArray.splice(i, 1);
			                            break;
			                        }
								}

			                     routeUtil.groupRoute(vm.customRouteArray);
			                    	

                   			 routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_custom'),$.i18n.prop('org_openo_msb_route_service_del_success'),"success");
                   			
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
					        bootbox.alert($.i18n.prop('org_openo_msb_route_service_del_fail')+textStatus+":"+errorThrown);                
	                      
	                 }
	            });


			       }
	
	  		 	});
				
			},
			delmsbRoute:function(serviceName,version,obj){
				bootbox.confirm($.i18n.prop('org_openo_msb_route_err_service_del_ask',[serviceName],[version]),function(result){
				if(result){
					var url= vm.$msbRouteInstanceUrl;
				    var version2=version==""?"null":version;
				    var serviceName2= serviceName.replace(/\//g,"*");
                        url=url.replace("{serviceName}",serviceName2).replace("{version}",version2);
	
					 $.ajax({
	                "type": 'DELETE',
	                "url": url,
	                "dataType": "json",
	                success: function (resp) {  
	                        $(obj).parent().parent().addClass('selected');
	                        
	                        for(var i=0;i<vm.msbRouteArray.length;i++){
			                       if(vm.msbRouteArray[i].serviceName == serviceName &&
			                       		vm.msbRouteArray[i].version==version ){
			                            vm.msbRouteArray.splice(i, 1);
			                            break;
			                        }
								}

							 table.row('.selected').remove().draw( false );

                   			 routeUtil.growl($.i18n.prop('org_openo_msb_route_tab_msb'),$.i18n.prop('org_openo_msb_route_service_del_success'),"success");
                   			
            	
	                },
	                 error: function(XMLHttpRequest, textStatus, errorThrown) {
						
					        bootbox.alert($.i18n.prop('org_openo_msb_route_service_del_fail')+XMLHttpRequest.responseText);                
	                      
	                 }
	                 
	                     
	            });


			       }
	
	  		 	});
				
			},
			exportServices:function(){
				var url=apiBasePath+"/apiRoute/export";
				window.open(url); 

			},
			gotoTarget:function(route,type){
				vm.selectedRoute=route;
				vm.selectedRouteType=type;

				if(type=="api")
				{
				  vm.gotoRestDoc(); 
				}
				else if(type=="iui"){
				  vm.gotoIUI(); 
				}
				
			},
			gotoRestDoc:function(){
				vm.routeTargetTitle=vm.selectedRoute.serviceName+"-"+$.i18n.prop("org_openo_msb_route_content_title");	

				var version=vm.selectedRoute.version==""?"":"/"+vm.selectedRoute.version;	

				    if(vm.selectedRoute.apiJsonType=="1")
				    {			
				     
				     var sourceUrl= "/apijson/"+vm.selectedRoute.serviceName+version;
					}
					else{
					 var sourceUrl= iuiBasePath+"/ext/initSwaggerJson/"+vm.selectedRoute.apiJson;	
					}
					//Local json data read from the real API service service address parameter
					var url=iuiBasePath+"/api-doc/index.html?url="+sourceUrl+"&api=/api/"+vm.selectedRoute.serviceName+version;

				 $('#msbSubPage').attr("src",url);  
				 vm.apiIframeUrl=url; 
			},
			gotoJVM:function(){
				vm.routeTargetTitle=vm.selectedRoute.serviceName+"-"+$.i18n.prop('org_openo_msb_route_metric_content_title');	
			
				var version=vm.selectedRoute.version==""?"":"/"+vm.selectedRoute.version;	

				var sourceUrl= "/admin/"+vm.selectedRoute.serviceName+version;
			 
				
				var url=iuiBasePath+"/metrics/index.html?url="+sourceUrl;

				 $('#msbSubPage').attr("src",url);   
			},
			gotoIUI:function(){
				vm.routeTargetTitle=vm.selectedRoute.serviceName+"-"+$.i18n.prop('org_openo_msb_route_iui_content_title');				
				var url= "/iui/"+vm.selectedRoute.serviceName+"/";
				 $('#msbSubPage').attr("src",url);   
				  vm.iuiIframeUrl=url; 
			},
			initIUIfori18n:function(){
				vm.routeTargetTitle=$.i18n.prop("org_openo_msb_route_content_title");	
				vm.showAPITypeName=[$.i18n.prop("org_openo_msb_route_swagger_type_predefined"),$.i18n.prop("org_openo_msb_route_swagger_type_custominput")];
 				vm.dataTableLanguage={
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
            };	

			}
			
		

	});


