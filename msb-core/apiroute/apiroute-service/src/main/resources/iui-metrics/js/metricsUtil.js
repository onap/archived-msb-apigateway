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

var metricsUtil = {};


metricsUtil.methodShortName=function(methodName){

    var methodNameArray=methodName.split(".");
    return methodNameArray[methodNameArray.length-2]+"."+methodNameArray[methodNameArray.length-1];

}

metricsUtil.formatSeconds=function (value) {
    var theTime = parseInt(value/1000);
    var theTime1 = 0;
    var theTime2 = 0;
    if(theTime > 60) {
        theTime1 = parseInt(theTime/60);
        theTime = parseInt(theTime%60);
            if(theTime1 > 60) {
            theTime2 = parseInt(theTime1/60);
            theTime1 = parseInt(theTime1%60);
            }
    }
        var result = ""+parseInt(theTime)+ $.i18n.prop("org_openo_msb_metrics_second");
        if(theTime1 > 0) {
        result = ""+parseInt(theTime1)+$.i18n.prop("org_openo_msb_metrics_minute")+result;
        }
        if(theTime2 > 0) {
        result = ""+parseInt(theTime2)+$.i18n.prop("org_openo_msb_metrics_hour")+result;
        }
    return result;
}
