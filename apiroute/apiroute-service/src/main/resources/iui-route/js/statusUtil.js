var statusUtil = {};

statusUtil.statisticsPollTime=60000;
statusUtil.connectionsPollTime=5000;
statusUtil.connectionsXAxisCount=12;
statusUtil.statisticsXAxisCount=60;
statusUtil.connection=true;

statusUtil.initChart= function(){
	statusUtil.init_statistics_requestChart();
	statusUtil.init_status_requestChart();
	statusUtil.init_connectionChart();

}

statusUtil.getQueryString= function(name) { 
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
    var r = window.location.search.substr(1).match(reg); 
    if (r != null) 
        return unescape(r[2]);
    return null; 
} 

statusUtil.init_statistics_requestChart= function(){


	var times = [];
	var datas=[];
	var latestTime="";
	$.ajax({
        "type": 'get',
        "async": false,
        "timeout" : 2000, 
        "url":  apiBasePath+"/statistics/request?latestNum=10",
        "dataType": "json",
        success: function (resp) { 
   
         var initDatas = (resp==null)?[]:resp;
         for(var i=0;i<initDatas.length;i++){
         	var dataArray=initDatas[i].split("|");
         	times.push(statusUtil.getLocalTime(dataArray[0]));
         	datas.push(dataArray[1]);
         }

         if(initDatas.length>0){
         	latestTime=initDatas[initDatas.length-1];
      	}

        			                                       	
        },
         error: function(XMLHttpRequest, textStatus, errorThrown) {
           statusUtil.connection=false;
			     alert("get Chart Data fails："+textStatus+":"+errorThrown); 
         }
    });


	var requestChart = echarts.init(document.getElementById('statisticsLineChartDiv'), 'macarons');

        var option = {

             tooltip : {
		        trigger: 'axis'
		    },
          	toolbox: {
		        feature: {
		            saveAsImage: {
                  name:'MSB历史访问次数统计图'
                }
		        }
		    },
            legend: {
                data:['每分钟处理请求数']
            },             
		xAxis: 
        {
            type: 'category',
            boundaryGap: false,
            data: times,
            name: '采集点'

        },
		yAxis:
        {
            type: 'value',
            scale: true,
            name: '请求数',
            minInterval: 1,
            min: 0,
            boundaryGap: [0.1, 0.1]
        },
        series: [
	        {
	            name:'每分钟处理请求数',
	            type:'line',
	            stack: 'status',
	            data:datas
	        }	        
        ]
 	};

        // 使用刚指定的配置项和数据显示图表。
        requestChart.setOption(option);
        window.onresize = requestChart.resize;
 if(statusUtil.connection==true){
      setInterval(function () {

      $.ajax({
        "type": 'get',
        "async": true,
        "timeout" : 3000, 
        "url":  apiBasePath+"/statistics/request",
        "dataType": "json",
        success: function (resp) { 
   
         var latestData = (resp==null)?[]:resp;
         
         if(latestData.length>0){
         	var dataArray=latestData[0].split("|");
         	
         	var data = option.series[0].data;
    		
    		if(latestTime!=dataArray[0]){	 
    			  data.push(dataArray[1]);    			  
				    option.xAxis.data.push(statusUtil.getLocalTime(dataArray[0]));
      			if(data.length>=statusUtil.statisticsXAxisCount){
			    	  data.shift();			    	
			    	  option.xAxis.data.shift();
				    }

           
           var maxVaule=Math.max.apply(null, data);
           if(maxVaule<5){
            option.yAxis.max=5;
           }
           else{
            option.yAxis.max=null;
           }  
				  requestChart.setOption(option);

          latestTime=dataArray[0];
  			}
		                                       	
           }
       }
   });

		   
  }, statusUtil.statisticsPollTime);  
 }
}

var statusLineChart;
var statusLineChartOption;
statusUtil.init_status_requestChart= function(){

statusLineChart = echarts.init(document.getElementById('statusLineChartDiv'), 'macarons');

statusLineChartOption = {
             color:[ "#2ec7c9",
            "#b6a2de",
            "#ffb980"],           
             tooltip : {
		        trigger: 'axis'
		    },
          	toolbox: {
		        feature: {
		            saveAsImage: {
                  name:'MSB正在处理请求数图'
                }
		        }
		    },
            legend: {
                data:['已转发等待响应','已接收待转发','收到响应待返回']
            },             
		xAxis: 
        {
            type: 'category',
            boundaryGap: false,
            name: '采集点',
            data: []
        },
		yAxis:
        {
            type: 'value',
            scale: true,
            name: '请求数',
            min: 0,
            minInterval: 1,
            boundaryGap: [0.1, 0.1]
        },
        series: [
	        {
	            name:'已转发等待响应',
	            type:'line',
	            data:[]
	        },
	        {
	            name:'已接收待转发',
	            type:'line',
	            data:[]
	        },
	        {
	            name:'收到响应待返回',
	            type:'line',
	            data:[]
	        }
        
        ]
 	};

        // 使用刚指定的配置项和数据显示图表。
        statusLineChart.setOption(statusLineChartOption);
        window.onresize = statusLineChart.resize;
 if(statusUtil.connection==true){  

    statusUtil.getStatusLineChart4Data();

     setInterval(function () {
      statusUtil.getStatusLineChart4Data();
  	}, statusUtil.connectionsPollTime);  
  }
}

statusUtil.getStatusLineChart4Data=function(){

      $.ajax({
        "type": 'get',
        "async": true,
        "timeout" : 3000, 
        "url":  apiBasePath+"/status/request",
        "dataType": "json",
        success: function (resp) { 
   
         if(resp!=null){
          var forward_data= statusLineChartOption.series[0].data;
          var accept_data = statusLineChartOption.series[1].data;
          var receive_data = statusLineChartOption.series[2].data;
      


          if(accept_data.length>=statusUtil.statisticsXAxisCount){
              accept_data.shift();
              forward_data.shift();
              receive_data.shift();
              statusLineChartOption.xAxis.data.shift();
          }
          
           
           accept_data.push(resp.accept_preparing_forward);           
           forward_data.push(resp.forward_waiting_response);
           receive_data.push(resp.receive_resp_not_return);

            var allValue=accept_data.concat(forward_data).concat(receive_data);
           var maxVaule=Math.max.apply(null, allValue);
           if(maxVaule<5){
            statusLineChartOption.yAxis.max=5;
           }
           else{
            statusLineChartOption.yAxis.max=null;
           }
          
       
          statusLineChartOption.xAxis.data.push(statusUtil.getCurrentTime());

          statusLineChart.setOption(statusLineChartOption);
         }                                    
           }
       });
}

var connectionChart;
var connectionChartOption;

statusUtil.init_connectionChart= function(){



	  connectionChart = echarts.init(document.getElementById('connectionBarChartDiv'), 'macarons');
     
       
     connectionChartOption = {
                      
            tooltip : {
  		        trigger: 'axis',
  		        axisPointer : {           
  		            type : 'shadow'       
  		        }
		        },
          	toolbox: {
		        feature: {
		            saveAsImage: {
                  name:'MSB当前连接数统计图'
                }
		        }
		    },
            legend: {
                data:['Active','Waiting','Writing','Reading']
            },             
		xAxis: 
        {
            type: 'category',
            boundaryGap: true,
            name: '采集点',
           // axisLabel:{
           //    interval:0,
           //    rotate:45
           //  },
            data: []
        },
		yAxis:
        {
            type: 'value',
            scale: true,
            name: '连接数',
            min: 0,
            minInterval: 1,
            boundaryGap: [0.1, 0.1]
          
        },
        series: [
        {
            name:'Active',
            type:'bar',
            data:[]
        },
        {
            name:'Waiting',
            type:'bar',
            stack: 'connection',
            data:[]
        },
        {
            name:'Writing',
            type:'bar',
            stack: 'connection',
            data:[]
        },
        {
            name:'Reading',
            type:'bar',
            stack: 'connection',
            data:[]
        }
        ]
 	};

        // 使用刚指定的配置项和数据显示图表。
        connectionChart.setOption(connectionChartOption);
        window.onresize = connectionChart.resize;
        
      if(statusUtil.connection==true){    
          statusUtil.getConnectionChart4Data();

            setInterval(function () {
            statusUtil.getConnectionChart4Data();
         }, statusUtil.connectionsPollTime);   
      }
}

statusUtil.getConnectionChart4Data=function(){
$.ajax({
        "type": 'get',
        "async": true,
        "url":  apiBasePath+"/status/connection",
        "dataType": "json",
        success: function (resp) { 
   
         if(resp!=null){
            var active_data =  connectionChartOption.series[0].data;
            var waiting_data = connectionChartOption.series[1].data;
          var writing_data = connectionChartOption.series[2].data;
          var reading_data = connectionChartOption.series[3].data;


          if(active_data.length>=statusUtil.connectionsXAxisCount){
              active_data.shift();
              waiting_data.shift();
              writing_data.shift();
              reading_data.shift();
              connectionChartOption.xAxis.data.shift();
          }
          
           active_data.push(resp.connections_active); 
           waiting_data.push(resp.connections_waiting);           
           writing_data.push(resp.connections_writing);
           reading_data.push(resp.connections_reading);

           var allValue=active_data.concat(waiting_data).concat(writing_data).concat(reading_data)
           var maxVaule=Math.max.apply(null, allValue)
           if(maxVaule<5){
            connectionChartOption.yAxis.max=5;
           }
           else{
            connectionChartOption.yAxis.max=null;
           }


          connectionChartOption.xAxis.data.push(statusUtil.getCurrentTime());

          connectionChart.setOption(connectionChartOption);
                                            
           }
         }
       });

}


statusUtil.getCurrentTime=function(){
          var date = new Date();          
          var axisData = [statusUtil.addZero(date.getHours()),statusUtil.addZero(date.getMinutes()),statusUtil.addZero(date.getSeconds())].join(":");
          return axisData;
  }

statusUtil.addZero=function(s) {
        return s < 10 ? '0' + s: s;
    }


statusUtil.getLocalTime=function(nS) {  
          var date = new Date(parseInt(nS)*1000);    
          var fullDate= [statusUtil.addZero(date.getFullYear()),statusUtil.addZero(date.getMonth() + 1 ) ,statusUtil.addZero(date.getDate())].join("-");
              fullDate+=" ";   
              fullDate += [statusUtil.addZero(date.getHours()),statusUtil.addZero(date.getMinutes()),statusUtil.addZero(date.getSeconds())].join(":");
          return fullDate;

}