#!/bin/bash
#
# Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
DIRNAME=`dirname $0`
HOME=`cd $DIRNAME/nginx; pwd`
_NGINXCMD="$HOME/sbin/nginx"

echo =========== openresty config info  =============================================
echo HOME=$HOME
echo _NGINXCMD=$_NGINXCMD
echo ===============================================================================
cd $HOME; pwd

echo @WORK_DIR@ $HOME
echo @C_CMD@ $_NGINXCMD -p $HOME/ -s stop

function save_nginx_pid(){
	nginx_id=`ps -ef | grep nginx | grep $_NGINXCMD | grep -v grep | awk '{print $2}'`
	echo $nginx_id
	worker_id_list=`ps -ef | grep nginx | grep $nginx_id | grep "worker process" | awk '{print $2}'`
	echo $worker_id_list
}

function kill_nginx_process(){
	ps -p $nginx_id
	if [ $? == 0 ]; then
		kill -9 $nginx_id
	fi

	for worker_id in $worker_id_list
	do
		ps -p $worker_id
		if [ $? == 0 ]; then
			echo kill -9 $worker_id
			kill -9 $worker_id
		fi
	done
}
save_nginx_pid;
$_NGINXCMD -p $HOME/ -s stop
sleep 5
kill_nginx_process;