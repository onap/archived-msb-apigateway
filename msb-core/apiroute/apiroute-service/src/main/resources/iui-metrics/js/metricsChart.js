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
var metricsChart = {};


metricsChart.memoryPieMetrics = function(data){
 var memoryPieChart = echarts.init(document.getElementById('memoryPieChartDiv')); 

 var labelTop = {
    normal : {
        label : {
            show : true,
            position : 'center',
            formatter : '{b}',
            textStyle: {
                baseline : 'bottom'
            }
        },
        labelLine : {
            show : false 
        }
    }
};
var labelFromatter = {
    normal : {
        label : {
            formatter : function (params){
                return (100 - params.value).toFixed(1) + '%'
            },
            textStyle: {
                baseline : 'top'
            }
        }
    },
}
var labelBottom = {
    normal : {
        color: '#ccc',
        label : {
            show : true,
            position : 'center'
        },
        labelLine : {
            show : false
        }
    },
    emphasis: {
        color: 'rgba(0,0,0,0)'
    }
};
var radius = [40, 55];
option = {
    legend: {
        x : 'center',
        y:"bottom",
        data:[
            'Code-Cache','Eden-Space','Perm-Gen','Survivor-Space','Tenured-Gen'
        ]
    },
    title : {
        text: $.i18n.prop('org_openo_msb_metrics_jvm_memory_radius'),
        subtext: '',
        x: 'center'
    },   
    toolbox: {
        show : true,
        feature : {
                    
            saveAsImage : {
            show : true,
            title : $.i18n.prop('org_openo_msb_metrics_chart_save_picture'),
            type : 'png',
            lang : [$.i18n.prop('org_openo_msb_metrics_chart_click_save')]
            }
        }
    },
    series : [
        {
            type : 'pie',
            center : ['10%', '55%'],
            radius : radius,
            x: '0%', // for funnel
            itemStyle : labelFromatter,
            data : [
                {name:'other', value:100-data.CodeCache, itemStyle : labelBottom},
                {name:'Code-Cache', value:data.CodeCache,itemStyle : labelTop}
            ]
        },
        {
            type : 'pie',
            center : ['30%', '55%'],
            radius : radius,
            x:'20%', // for funnel
            itemStyle : labelFromatter,
            data : [
                {name:'other', value:100-data.EdenSpace, itemStyle : labelBottom},
                {name:'Eden-Space', value:data.EdenSpace,itemStyle : labelTop}
            ]
        },
        {
            type : 'pie',
            center : ['50%', '55%'],
            radius : radius,
            x:'40%', // for funnel
            itemStyle : labelFromatter,
            data : [
                {name:'other', value:100-data.PermGen, itemStyle : labelBottom},
                {name:'Perm-Gen', value:data.PermGen,itemStyle : labelTop}
            ]
        },
         {
            type : 'pie',
            center : ['70%', '55%'],
            radius : radius,
            x:'60%', // for funnel
            itemStyle : labelFromatter,
            data : [
                {name:'other', value:100-data.SurvivorSpace, itemStyle : labelBottom},
                {name:'Survivor-Space', value:data.SurvivorSpace,itemStyle : labelTop}
            ]
        },
         {
            type : 'pie',
            center : ['90%', '55%'],
            radius : radius,
            x:'80%', // for funnel
            itemStyle : labelFromatter,
            data : [
                {name:'other', value:100-data.TenuredGen, itemStyle : labelBottom},
                {name:'Tenured-Gen', value:data.TenuredGen,itemStyle : labelTop}
            ]
        }
    ]
};
                    
       
        // load data for echarts objects
         memoryPieChart.setOption(option); 
         window.onresize = memoryPieChart.resize;


}


metricsChart.memoryBarMetrics = function(data){
 var memoryBarChart = echarts.init(document.getElementById('memoryBarChartDiv')); 
var option = {
    title : {
        text: $.i18n.prop('org_openo_msb_metrics_jvm_memory_bar'),
        x:'center'
    },
    tooltip : {
        trigger: 'axis'
    },
    legend: {
         data:[
            $.i18n.prop('org_openo_msb_metrics_jvm_memory_bar_init'),$.i18n.prop('org_openo_msb_metrics_jvm_memory_bar_used'),$.i18n.prop('org_openo_msb_metrics_jvm_memory_bar_total')
        ],
        x:'left'
    },
    toolbox: {
        show : true,
        feature : {
            
            saveAsImage : {
            show : true,
            title : $.i18n.prop('org_openo_msb_metrics_chart_save_picture'),
            type : 'png',
            lang : [$.i18n.prop('org_openo_msb_metrics_chart_click_save')]
            }
        }
    },
    yAxis : [
        {
            type : 'category',
            data : [$.i18n.prop('org_openo_msb_metrics_jvm_memory_bar_heap'),$.i18n.prop('org_openo_msb_metrics_jvm_memory_bar_non-heap'),$.i18n.prop('org_openo_msb_metrics_jvm_memory_bar_total-heap')]
        }
    
    ],
    xAxis : [
        {
            type : 'value',
            axisLabel:{formatter:'{value} M'}
        }
    ],
    series : [
        {
            name:$.i18n.prop('org_openo_msb_metrics_jvm_memory_bar_init'),
            type:'bar',
            stack:'barGroup',
            itemStyle: {normal: {color:'rgba(92, 184, 92, 0.24)', label:{show:true}}},
            data:data.init
        },
        
        {
            name:$.i18n.prop('org_openo_msb_metrics_jvm_memory_bar_used'),
            type:'bar',
            stack:'barGroup',
            itemStyle: {normal: {color:'rgba(73, 163, 73, 0.56)', label:{show:true,formatter:function(p){return p.value > 0 ? (p.value +'\n'):'';}}}},
            data:data.used
        },

        {
            name:$.i18n.prop('org_openo_msb_metrics_jvm_memory_bar_total'),
            type:'bar',
            stack:'barGroup',
            itemStyle: {normal: {color:'#49A349', label:{show:true,formatter:function(p){return p.value > 0 ? (p.value +'\n'):'';}}}},
            data:data.max
        },
        
        
    ]
};


                    

 memoryBarChart.setOption(option); 
 window.onresize = memoryBarChart.resize;

}




metricsChart.threadsMetrics = function(data){

 var threadsChart = echarts.init(document.getElementById('threadsChartDiv')); 


 var option = {
    title : {
        text: $.i18n.prop('org_openo_msb_metrics_jvm_thread_chart'),
        subtext: '',
        x:'center'
    },
    tooltip : {
        trigger: 'item',
        formatter: "{b}{a}: <br/> {c} ({d}%)"
    },
    color:["#ff7f50","#6495ed","#da70d6","#32cd32"],
    legend: {
        orient : 'vertical',
        x : 'left',
        data:['Blocked','Waiting','Timed waiting','Runnable']
    },
    toolbox: {
        show : true,
        feature : {
                     
            saveAsImage : {
            show : true,
            title : $.i18n.prop('org_openo_msb_metrics_chart_save_picture'),
            type : 'png',
            lang : [$.i18n.prop('org_openo_msb_metrics_chart_click_save')]
            }
        }
    },
    calculable : true,
    series : [
        {
            name:$.i18n.prop('org_openo_msb_metrics_thread'),
            type:'pie',
            radius : '55%',
            center: ['50%', '60%'],
            data:data
        }
    ]
};


 threadsChart.setOption(option); 
 window.onresize = threadsChart.resize;

                    

}


metricsChart.restMetrics = function(data){


  var labelFromatter=function (value){
       if(value.length>12) return value.substring(0,12)+"\n"+value.substring(12);
       else return value; 
    }


var restChart = echarts.init(document.getElementById('restChartDiv')); 
var option = {
    title : {
        text: 'top10',
        subtext: ''
    },
     color:["#1790cf"],
    tooltip : {
        trigger: 'axis'
    },
    legend: {
        data:[$.i18n.prop('org_openo_msb_metrics_rest_bar_count')]
    },
    toolbox: {
        show : true,
        feature : {
           
            magicType: {show: true, title:$.i18n.prop('org_openo_msb_metrics_chart_line_change'),type: ['line', 'bar']},
            saveAsImage : {
            show : true,
            title : $.i18n.prop('org_openo_msb_metrics_chart_save_picture'),
            type : 'png',
            lang : [$.i18n.prop('org_openo_msb_metrics_chart_click_save')]
            }
        }
    },
    calculable : true,
    xAxis : [
        {
            type : 'value',
            boundaryGap : [0, 0.01]
        }
    ],
    yAxis : [
        {
            type : 'category',
            data : data.restName,
            axisLabel:{
                 margin:4,
                 clickable:true,
                 formatter:labelFromatter

            }
        }
    ],
    series : [
        {
            name:$.i18n.prop('org_openo_msb_metrics_rest_bar_count'),
            type:'bar',
            data:data.restCount
        }
        
    ]
};
                    
 restChart.setOption(option); 
  window.onresize = restChart.resize;
}


metricsChart.requestsMetrics = function(data){
var requestsChart = echarts.init(document.getElementById('requestsChartDiv')); 

option = {
    title : {
        text: '',
        subtext: ''
    },
    tooltip : {
        trigger: 'axis'
    },
    legend: {
        data:[$.i18n.prop('org_openo_msb_metrics_rest_bar_count')]
    },
    toolbox: {
        show : true,
        feature : {
          
            magicType : {show: true,title:$.i18n.prop('org_openo_msb_metrics_chart_line_change'), type: ['line', 'bar']},
            saveAsImage : {
            show : true,
            title : $.i18n.prop('org_openo_msb_metrics_chart_save_picture'),
            type : 'png',
            lang : [$.i18n.prop('org_openo_msb_metrics_chart_click_save')]
            }
        }
    },
    calculable : true,
    yAxis : [
        {
            type : 'category',
            data : ['get','post','put','delete','other']
        }
    ],
    xAxis : [
        {
            type : 'value'
        }
    ],
    series : [
        {
            name:$.i18n.prop('org_openo_msb_metrics_rest_bar_count'),
            type:'bar',
            data:[data.get, data.post, data.put, data.delete, data.other] 
        }
    ]
};


 requestsChart.setOption(option); 
   window.onresize = requestsChart.resize;
}



  
