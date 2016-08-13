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
var routeUtil = {};

routeUtil.growl=function(title,message,type){
      $.growl({
		icon: "fa fa-envelope-o fa-lg",
		title: "&nbsp;&nbsp;"+$.i18n.prop('org_openo_msb_route_property_ttl')+title,
		message: message+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
			},{
				type: type
			});
}


routeUtil.refreshRoute=function(){
     $(".stats_box .routeDiv div[data-name='route_click_zone']").on({
        click: function(){
        $(".stats_box .routeDiv").removeClass("active");
        $(this).parent().addClass("active");
        }
      });

     $(".form-title a").click(function(){
         $(this).parent().parent().next().collapse('toggle');
        });

        $('.collapseContent').on('show.bs.collapse', function () {
          var icon = $(this).prev().find('i:first');
         icon.removeClass('fa-chevron-down').addClass("fa-chevron-up");
        });

        $('.collapseContent').on('hidden.bs.collapse', function () {
          var icon = $(this).prev().find('i:first');
          icon.removeClass('fa-chevron-up').addClass("fa-chevron-down");
        });


}

//Sorting grouping custom service
routeUtil.groupRoute=function(resp){
    var routeArray=new Array();
    var routeGroupArray=[[]];

     for(var i=0;i<resp.length;i++){ 

          var fullServiceName=resp[i].serviceName;
          var groupServiceName;

          var reg_match1=/(\/.*?)\/.*$/
           var reg_match2=/(\/.*?)$/

           //Canonical decomposition grouping service name
          if(reg_match1.test(fullServiceName)){  
            
              groupServiceName = fullServiceName.match(reg_match1)[1];
          }
          else if(reg_match2.test(fullServiceName)){  
            
              groupServiceName = fullServiceName.match(reg_match2)[1];
          }
          else{
            groupServiceName=$.i18n.prop('org_openo_msb_route_property_root');
          }


            //Loop all packet classification
            if(routeArray[groupServiceName] == undefined){  
                var list = [];  
                list.push(resp[i]);  
                routeArray[groupServiceName] = list;  
            }else{  
                routeArray[groupServiceName].push(resp[i]);  
            }  

        }

           

            //Sorting through all quantity > 1
      
           
              for(var groupServiceName in routeArray){  
                 var routeGroup = routeArray[groupServiceName]; 

                 if(routeGroup.length>1){
                   
                    routeGroupArray.push(routeGroup);
                    
                }
                else{
                   routeGroupArray[0].push(routeGroup[0]); 
                }

          }


          //Sort + place other grouping in the final
          var defaultGroupRoute=routeGroupArray[0];
             defaultGroupRoute.sort(function(a,b){return a.serviceName>b.serviceName?1:-1}); 
          for(var i=0;i<routeGroupArray.length-1;i++){
            routeGroupArray[i]=routeGroupArray[i+1];
            routeGroupArray[i].sort(function(a,b){return a.serviceName>b.serviceName?1:-1}); 
          }

         
             routeGroupArray[routeGroupArray.length-1]=defaultGroupRoute;

          


        vm.customGroupRouteArray=routeGroupArray;


 
}

routeUtil.showGroupName=function(index,serviceArray){

  
    var maxGroupSN=vm.customGroupRouteArray.length-1;
    if(index==maxGroupSN){
        return $.i18n.prop('org_openo_msb_route_property_other_group');
    }
    else{
        var serviceName=serviceArray[0].serviceName;
        var reg_match1=/(\/.*?)\/.*$/
        var reg_match2=/(\/.*?)$/

           //Canonical decomposition grouping service name
          if(reg_match1.test(serviceName)){  
            
              return serviceName.match(reg_match1)[1];
          }
          else if(reg_match2.test(serviceName)){  
            
              return serviceName.match(reg_match2)[1];
          }
    }

}

routeUtil.currentTime=function()
    { 
        var now = new Date();
       
        var year = now.getFullYear();       //year
        var month = now.getMonth() + 1;     //month
        var day = now.getDate();            //date
       
        var hh = now.getHours();            //hour
        var mm = now.getMinutes();          //minu
       
        var clock = year + "-";
       
        if(month < 10)
            clock += "0";
       
        clock += month + "-";
       
        if(day < 10)
            clock += "0";
           
        clock += day + " ";
       
        if(hh < 10)
            clock += "0";
           
        clock += hh + ":";
        if (mm < 10) clock += '0'; 
        clock += mm; 
        return clock; 
    } 



routeUtil.showStatus=function(status){
   if(status === '1'){
      return " <span class='label label-success'>"+$.i18n.prop('org_openo_msb_route_property_normal')+"</span>";
   }
   else if(status === '0'){
      return " <span class='label label-danger'>"+$.i18n.prop('org_openo_msb_route_property_disable')+"</span>";
   }
   else {
      return " <span class='label label-info'>"+$.i18n.prop('org_openo_msb_route_property_unknown')+"</span>";
   }


}

routeUtil.showVisualRange=function(visualRange){

    var rangArray=visualRange.split("|");

    var visualRangeText="";

    for(var i=0;i<rangArray.length;i++){
        if(rangArray[i] === '0'){
              visualRangeText+= $.i18n.prop('org_openo_msb_route_form_intersystem')+"   ";
           }
         else if(rangArray[i] === '1'){
              visualRangeText+= $.i18n.prop('org_openo_msb_route_form_insystem')+"   ";
           }

    }

return visualRangeText;

}




routeUtil.formatDetail=function(nodes){

 var tableDetail='<table class="table table-striped hostTable">'
                +'<thead><tr><th>IP</th><th>PORT</th><th>TTL</th><th></th></tr></thead>'
                +' <tbody>';

 for(var i=0;i<nodes.length;i++){ 
        var node=nodes[i].split(":");
        tableDetail+='<tr><td>'+node[0]+'</td><td>'+node[1]+'</td><td>'+node[2]+'</td><td></td>';
 }                                     
                                     
                                   


tableDetail+=' </tbody></table>';
return tableDetail;

}

routeUtil.ifAPIUrl=function(url){
  if(url=="" || url ==null) return false;
    
    var reg_api_match =new RegExp("^(\/"+apiRootPath+"\/.*?)$","im"); // re为/^\d+bl$/gim   
   // var reg_api_match=/^(\/api\/.*?)$/;
   return reg_api_match.test(url);
      
}


routeUtil.changeTargetServiceUrl=function(){
var serviceName=vm.msbRouteInfo.serviceName==""?"serviceName":vm.msbRouteInfo.serviceName;

  if(vm.msbRouteInfo.protocol=='UI'){
    vm.targetFullServiceUrl=vm.targetServiceUrl+"/"+iuiRootPath+"/"+serviceName;
  }
  else if(vm.msbRouteInfo.protocol=='REST'){
    if(routeUtil.ifAPIUrl(vm.msbRouteInfo.url)){
      var version=vm.msbRouteInfo.version==""?"":"/"+vm.msbRouteInfo.version
       vm.targetFullServiceUrl=vm.targetServiceUrl+"/"+apiRootPath+"/"+serviceName+version;
    }
    else{
       var reg_customName_match=/^(\/.*?)$/;
        if(!reg_customName_match.test(serviceName)) serviceName="/"+serviceName;
       vm.targetFullServiceUrl=vm.targetServiceUrl+serviceName;

    }
  }
}





