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

#!/bin/sh

DIRNAME=`dirname $0`
RUNHOME=`cd $DIRNAME/; pwd`
echo @RUNHOME@ $RUNHOME


if [ -f "$RUNHOME/setenv.sh" ]; then
  . "$RUNHOME/setenv.sh"
else
echo "can not found $RUNHOME/setenv.sh"
fi


echo ================== ENV_INFO  =============================================
echo RUNHOME=$RUNHOME
echo JAVA_BASE=$JAVA_BASE
echo Main_Class=$Main_Class
echo APP_INFO=$APP_INFO
echo ==========================================================================


cd $RUNHOME; pwd

function save_app_pid(){
	app_id=`ps -ef | grep $Main_Class| grep $RUNHOME | grep -v grep | awk '{print $2}'`
	echo @app_id@ $app_id
}

function kill_app_process(){
	ps -p $app_id
	if [ $? == 0 ]; then
		kill -9 $app_id
	fi
}

save_app_pid;
echo @C_CMD@ kill -9 $app_id
kill_app_process;




