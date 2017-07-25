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
$(function(){

   $('#msbTable tbody').on('click', 'td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = table.row( tr );
        if ( row.child.isShown() ) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
        }
        else {
            // Open this row
            var nodes=row.data()[1].match(/\d+.\d+.\d+.\d+:\d+:(-)?\d+/g) 
            
            row.child( routeUtil.formatDetail(nodes) ).show();
            tr.addClass('shown');
        }
    } );

 $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      //Get the name of the TAB has been activated
      var activeTab = $(e.target).text(); 
      if(this.id=="customTab" || this.id=="msbTab"){
          $("#serviceContent").hide();
          $("#upArrawDiv").hide();
      }
      else{
        $("#serviceContent").show();
        $("#upArrawDiv").show();
         $('#msbSubPage').attr("src","");
          $(".stats_box .routeDiv").removeClass("active");
          vm.selectedRouteType="";
          vm.routeTargetTitle=$.i18n.prop("org_onap_msb_route_content_title");
       
      }
   });


 $("[data-toggle='tooltip']").tooltip();

   $('#metricsFullurl').on('show.bs.collapse', function () {
        $('#metricsUrlCollapse').removeClass('fa-plus').addClass('fa-minus');
      
  });
  
  $('#metricsFullurl').on('hide.bs.collapse', function () {
        $('#metricsUrlCollapse').removeClass('fa-minus').addClass('fa-plus');
  });

   $('#apiJsonFullurl').on('show.bs.collapse', function () {
        $('#apiJsonCollapse').removeClass('fa-plus').addClass('fa-minus');
      
  });
  
  $('#apiJsonFullurl').on('hide.bs.collapse', function () {
        $('#apiJsonCollapse').removeClass('fa-minus').addClass('fa-plus');
  });



   $(".form-tip").blur(function(){
    if($.trim($(this).val())==""){
      $(this).removeClass("form-input-focus");
      $(this).prev().removeClass("item-tip-focus");
    }});

$(".form-tip").focus(function(){
  if(!$(this).hasClass("form-input-focus")){
  $(this).addClass("form-input-focus");
  $(this).prev().addClass("item-tip-focus");
  }});

$(".item-tip").click(function(){
  $(this).next().focus();
});



$("input[name='version']").blur(function(){
    $(this).val($(this).val().toLowerCase());
    routeUtil.changeTargetServiceUrl();
});


$("input[name='url']").blur(function(){

routeUtil.changeTargetServiceUrl();

});
$("input[name='serviceName']").blur(function(){

routeUtil.changeTargetServiceUrl();

});



$("select[name='protocol']").change(function(){

routeUtil.changeTargetServiceUrl();

});

/*

$("input[name='oriService']").blur(function(){
    var oriService=$.trim($(this).val());
    if(oriService==""){
      $("input[name='serviceName']").val("");
      $("input[name='version']").val("");
      $("input[name='server']").val("");
      $("input[name='url']").val("");
      return;
    }
    
    

    var reg=/^(|http:\/\/)(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([0-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5]))):(\d{1,5})\/.*$/   
        var reg_standard_match=/^(.*:\d{1,5})\/api\/(.*)\/(v.*)$/ 
    var reg_unstandard_match=/^(.*:\d{1,5})(\/.*)$/     
        if(reg.test(oriService)){  
       if(reg_standard_match.test(oriService)){  
          //标准api
          var group = oriService.match(reg_standard_match);
          $("input[name='serviceName']").val(group[2]);        
          $("input[name='server']").val(group[1].replace("http://",""));


         var version=group[3];
        var reg_endName_match=/^(.*?)\/.*$/
        if(reg_endName_match.test(version)){
            version = version.match(reg_endName_match)[1];
        }
          $("input[name='version']").val(version);
           $("input[name='url']").val("/api/"+group[2]+"/"+version);

        if(!$("input[name='version']").hasClass("form-input-focus")){
          $("input[name='version']").addClass("form-input-focus");
          $("input[name='version']").prev().addClass("item-tip-focus");
          }
          
    

       }
       else{
           //非标准api
          var group = oriService.match(reg_unstandard_match);
         var reg_endName_match=/^(.*?)\/$/
         var url=group[2];
        if(url!="/" && reg_endName_match.test(url)){
            url = url.match(reg_endName_match)[1];
        }


        $("input[name='serviceName']").val("");
        $("input[name='version']").val("");
        $("input[name='server']").val(group[1].replace("http://",""));
        $("input[name='url']").val(url);
      }

          if(!$("input[name='server']").hasClass("form-input-focus")){
          $("input[name='server']").addClass("form-input-focus");
          $("input[name='server']").prev().addClass("item-tip-focus");
          }
    }
  
    
});


$("input[name='iuioriService']").blur(function(){
    var oriService=$.trim($(this).val());
    if(oriService==""){
      $("input[name='iuiserviceName']").val("");
      $("input[name='iuiserver']").val("");
      $("input[name='iuiurl']").val("");
      return;
    }
    
    

    var reg=/^(|http:\/\/)(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([0-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5]))):(\d{1,5})\/.*$/   
        var reg_standard_match=/^(.*:\d{1,5})\/iui\/(.*|.*\/)$/ 
    var reg_unstandard_match=/^(.*:\d{1,5})(\/.*)$/     
   if(reg.test(oriService)){  
       if(reg_standard_match.test(oriService)){  
          //标准api
          var group = oriService.match(reg_standard_match);

        
        $("input[name='iuiserver']").val(group[1].replace("http://",""));
        

        var serviceName=group[2];
        var reg_endName_match=/^(.*?)\/.*$/
        if(reg_endName_match.test(serviceName)){
           serviceName = serviceName.match(reg_endName_match)[1];
        }
          $("input[name='iuiserviceName']").val(serviceName);
          $("input[name='iuiurl']").val("/iui/"+serviceName);
      
       }
       else{
           //非标准api
          var group = oriService.match(reg_unstandard_match);
       
         var reg_endName_match=/^(.*?)\/$/
         var url=group[2];
        if(url!="/" && reg_endName_match.test(url)){
            url = url.match(reg_endName_match)[1];
        }
        $("input[name='iuiserver']").val(group[1].replace("http://",""));
        $("input[name='iuiurl']").val(url);
      }

          if(!$("input[name='iuiserver']").hasClass("form-input-focus")){
          $("input[name='iuiserver']").addClass("form-input-focus");
          $("input[name='iuiserver']").prev().addClass("item-tip-focus");
          }
    }
  
    
});

$("input[name='customoriService']").blur(function(){
    var oriService=$.trim($(this).val());
    if(oriService==""){
      $("input[name='customserviceName']").val("");
      $("input[name='customserver']").val("");
      $("input[name='customurl']").val("");
      return;
    }
    
    

    var reg=/^(|http:\/\/)(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([0-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5]))):(\d{1,5})\/.*$/   
       
    var reg_unstandard_match=/^(.*:\d{1,5})(\/.*)$/     
   if(reg.test(oriService)){  
      

          var group = oriService.match(reg_unstandard_match);
       
         var reg_endName_match=/^(.*?)\/$/
         var url=group[2];
        if(url!="/" && reg_endName_match.test(url)){
            url = url.match(reg_endName_match)[1];
        }
        $("input[name='customserver']").val(group[1].replace("http://",""));
        $("input[name='customurl']").val(url);
        $("input[name='customserviceName']").val(url);
     

          if(!$("input[name='customserver']").hasClass("form-input-focus")){
          $("input[name='customserver']").addClass("form-input-focus");
          $("input[name='customserver']").prev().addClass("item-tip-focus");
          }
    }
  
    
});

*/

   jQuery.validator.addMethod("ip", function(value, element) {    
      return this.optional(element) || /^(([-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([0-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5]))):(\d{1,5})$/.test(value);    
    }, $.i18n.prop('org_onap_msb_route_validator_ip_format'));



  jQuery.validator.addMethod("url_head", function(value, element) {    
      return this.optional(element) || /^\/.+((?!\/).)$/i.test(value) || /^\/$/i.test(value);    
    }, $.i18n.prop('org_onap_msb_route_validator_url_head_format'));


jQuery.validator.addMethod("url_head_only", function(value, element) {    
      return this.optional(element) || /^\/.*$/i.test(value);    
    }, $.i18n.prop('org_onap_msb_route_validator_url_head_only_format'));
  
    jQuery.validator.addMethod("version", function(value, element) {    
      return this.optional(element) || /^v\d+(\.\d+)?$/i.test(value);    
    }, $.i18n.prop('org_onap_msb_route_form_version_tip'));

     jQuery.validator.addMethod("service_url", function(value, element) {    
    return this.optional(element) || /^(|http:\/\/)(([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.)(([0-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5])))\.){2}([1-9]|([1-9]\d)|(1\d\d)|(2([0-4]\d|5[0-5]))):(\d{1,5})\/.*$/.test(value);    
    }, $.i18n.prop('org_onap_msb_route_validator_url_format'));

     jQuery.validator.addMethod("url_line", function(value, element) {    
      return this.optional(element) || /^((?!\/).)*$/.test(value);    
    }, $.i18n.prop('org_onap_msb_route_validator_url_line_format'));

     jQuery.validator.addMethod("content", function(value, element) {    
      return this.optional(element) ||  /^([0-9a-zA-Z]|-|_)*$/i.test(value);    
    }, $.i18n.prop('org_onap_msb_route_validator_content_format'));

     jQuery.validator.addMethod("custom_content", function(value, element) {    
      return this.optional(element) ||  /^([0-9a-zA-Z]|-|_|\/)*$/i.test(value);    
    }, $.i18n.prop('org_onap_msb_route_validator_content_format'));


 });

  var form = $('#routeForm');
  var error = $('.alert-danger', form);
  var success = $('.alert-success', form);

    var iuiform = $('#iuirouteForm');
     var iuierror = $('.alert-danger', iuiform);
      var iuisuccess = $('.alert-success', iuiform);


      var customform = $('#customrouteForm');
     var customerror = $('.alert-danger', customform);
      var customsuccess = $('.alert-success', customform);

      var msbform = $('#msbForm');
     var msberror = $('.alert-danger', msbform);
      var msbsuccess = $('.alert-success', msbform);


    form.validate({
    doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
    errorElement: 'span', //default input error message container
    errorClass: 'help-block', // default input error message class
    focusInvalid: false, // do not focus the last invalid input
    rules: {     
     /* oriService:{
    service_url:true,
    maxlength:100
    },*/
      serviceName:{
        required: true,
        url_line:true,
        content:true,
        maxlength:50
      },
      version:{
       // required: true,
        maxlength:50,
        version:true
      },
       url:{
        required: true,
        url_head:true,
        maxlength:50
      },
      metricsUrl:{
        url_head:true,
        maxlength:50
      },
      server:{
        ip:true,
        maxlength:50
      }
    },
    messages: { 
        serviceName:{
        required: "Please enter the service name"
      },      
       url:{
        required: "Please enter the URL address"
       
      },     
       server:{
        required:"Please enter the Host address"
       
      }

    },
    errorPlacement: function (error, element) { // render error placement for each input type
      error.insertAfter(element); // for other inputs, just perform default behavior
    },

    invalidHandler: function (event, validator) { //display error alert on form submit   
      success.hide();
      error.show();
      //ZteFrameWork.scrollTo(error, -200);
    },

    highlight: function (element) { // hightlight error inputs
      $(element)
        .closest('.form-group').removeClass('has-success').addClass('has-error'); // set error class to the control group
    },

    unhighlight: function (element) { // revert the change done by hightlight
      $(element)
        .closest('.form-group').removeClass('has-error'); // set error class to the control group
    },

    success: function (label) {
      label
        .addClass('valid') // mark the current input as valid and display OK icon
        .closest('.form-group').removeClass('has-error'); // set success class to the control group
    },
    submitHandler: function (form) {
      success.show();
      error.hide();
      //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
    }

   });

    iuiform.validate({
    doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
    errorElement: 'span', //default input error message container
    errorClass: 'help-block', // default input error message class
    focusInvalid: false, // do not focus the last invalid input
    rules: {     
    /*iuioriService:{
    service_url:true,
    maxlength:100
    },*/
    iuiserviceName:{
        required: true,
        url_line:true,
        maxlength:50,
        content:true
      },     
    iuiurl:{
        required: true,
        url_head:true,
        maxlength:50
      },
   iuiserver:{
        ip:true,
        maxlength:50
      }
    },
    messages: { 
      iuiserviceName:{
        required: "Please enter the service name"
      },
      iuiurl:{
        required:"Please enter the URL address"
       
      },
      iuiserver:{
        required: "Please enter the Host address"
      }
    },
    errorPlacement: function (iuierror, element) { // render error placement for each input type
      iuierror.insertAfter(element); // for other inputs, just perform default behavior
    },

    invalidHandler: function (event, validator) { //display error alert on form submit   
      iuisuccess.hide();
      iuierror.show();
      //ZteFrameWork.scrollTo(error, -200);
    },

    highlight: function (element) { // hightlight error inputs
      $(element)
        .closest('.form-group').removeClass('has-success').addClass('has-error'); // set error class to the control group
    },

    unhighlight: function (element) { // revert the change done by hightlight
      $(element)
        .closest('.form-group').removeClass('has-error'); // set error class to the control group
    },

    success: function (label) {
      label
        .addClass('valid') // mark the current input as valid and display OK icon
        .closest('.form-group').removeClass('has-error'); // set success class to the control group
    },
    submitHandler: function (form) {
      iuisuccess.show();
      iuierror.hide();
      //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
    }

   });

    customform.validate({
    doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
    errorElement: 'span', //default input error message container
    errorClass: 'help-block', // default input error message class
    focusInvalid: false, // do not focus the last invalid input
    rules: {     
    /*customoriService:{
    service_url:true,
    maxlength:100
    },*/
    customserviceName:{
        required: false,
        url_head:true,
        maxlength:100,
        custom_content:true
      },     
    customurl:{
        required: true,
        url_head:true,
        maxlength:50
      },
   customserver:{
        ip:true,
        maxlength:50
      }
    },
    messages: { 
      customserviceName:{
        required: "Please enter the service name"
      },     
      customurl:{
        required: "Please enter the URL address"
       
      }

    },
    errorPlacement: function (customerror, element) { // render error placement for each input type
      customerror.insertAfter(element); // for other inputs, just perform default behavior
    },

    invalidHandler: function (event, validator) { //display error alert on form submit   
      customsuccess.hide();
      customerror.show();
      //ZteFrameWork.scrollTo(error, -200);
    },

    highlight: function (element) { // hightlight error inputs
      $(element)
        .closest('.form-group').removeClass('has-success').addClass('has-error'); // set error class to the control group
    },

    unhighlight: function (element) { // revert the change done by hightlight
      $(element)
        .closest('.form-group').removeClass('has-error'); // set error class to the control group
    },

    success: function (label) {
      label
        .addClass('valid') // mark the current input as valid and display OK icon
        .closest('.form-group').removeClass('has-error'); // set success class to the control group
    },
    submitHandler: function (form) {
      customsuccess.show();
      customerror.hide();
      //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
    }

   });


    msbform.validate({
    doNotHideMessage: true, //this option enables to show the error/success messages on tab switch.
    errorElement: 'span', //default input error message container
    errorClass: 'help-block', // default input error message class
    focusInvalid: false, // do not focus the last invalid input
    rules: {     
    
      serviceName:{
        required: true,
       // url_line:true,
        custom_content:true,
        maxlength:50
      },
      version:{
        maxlength:50,
        version:true
      },
       url:{
        url_head:true,
        maxlength:50
      },
      newHost:{
        ip:true,
        maxlength:50
      },
      newttl:{
        digits:true,
        min:0
      },
      protocol:{
        required: true
      },
      type:{
        required: true
      }
    },
    messages: { 
        serviceName:{
        required: "Please enter the service name"
      },
       protocol:{
        required:  "Please select a service protocol"
      },     
       type:{
        required: "Please select a service type"
       
      },
      newttl:{
        digits:"Please enter an integer",
        min: "Not a negative" 
      }

    },
    errorPlacement: function (msberror, element) { // render error placement for each input type
      msberror.insertAfter(element); // for other inputs, just perform default behavior
    },

    invalidHandler: function (event, validator) { //display error alert on form submit   
      msbsuccess.hide();
      msberror.show();
      //ZteFrameWork.scrollTo(error, -200);
    },

    highlight: function (element) { // hightlight error inputs
      $(element)
        .closest('.form-group').removeClass('has-success').addClass('has-error'); // set error class to the control group
    },

    unhighlight: function (element) { // revert the change done by hightlight
      $(element)
        .closest('.form-group').removeClass('has-error'); // set error class to the control group
    },

    success: function (label) {
      label
        .addClass('valid') // mark the current input as valid and display OK icon
        .closest('.form-group').removeClass('has-error'); // set success class to the control group
    },
    submitHandler: function (form) {
      msbsuccess.show();
      msberror.hide();
      //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
    }

   });


