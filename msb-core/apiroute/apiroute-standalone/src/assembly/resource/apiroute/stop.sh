#!/bin/bash
#
# Copyright 2016 2015-2016 ZTE, Inc. and others. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#     Author: Zhaoxing Meng
#     email: meng.zhaoxing1@zte.com.cn
#

DIRNAME=`dirname $0`
HOME=`cd $DIRNAME/; pwd`
APIROUTE_Main_Class="org.openo.msb.ApiRouteApp"

echo ================== apiroute info  =============================================
echo HOME=$HOME
echo APIROUTE_Main_Class=$APIROUTE_Main_Class
echo ===============================================================================
cd $HOME; pwd

echo @WORK_DIR@ $HOME

function save_apiroute_pid(){
	apiroute_id=`ps -ef | grep $APIROUTE_Main_Class | grep $HOME | grep -v grep | awk '{print $2}'`
	echo $apiroute_id
}

function kill_apiroute_process(){
	ps -p $apiroute_id
	if [ $? == 0 ]; then
		kill -9 $apiroute_id
	fi
}

save_apiroute_pid;
echo @C_CMD@ kill -9 $apiroute_id
kill_apiroute_process;