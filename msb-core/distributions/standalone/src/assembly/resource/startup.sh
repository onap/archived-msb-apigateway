#!/bin/sh
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
RUNHOME=`cd $DIRNAME/; pwd`
echo @RUNHOME@ $RUNHOME

echo "### Starting redis";
#nohup ./wso2bps/bin/wso2server.sh >>./OpenTOSCA/nohup.log 2>&1 &
cd ./redis
./run.sh &
cd $RUNHOME


echo "\n\n### Starting apiroute"
cd ./apiroute
./run.sh &
cd $RUNHOME


echo "### Starting openresty...";
# nohup ./startup.sh >>./nohup.log 2>&1 &
cd ./openresty
./run.sh &
cd $RUNHOME

echo "### Starting external API gateway...";
# nohup ./startup.sh >>./nohup.log 2>&1 &
cd ./eag
./run.sh &
cd $RUNHOME

echo "Startup will be finished in background...";
echo " + Run 'tail ./apiroute-works/logs/application.log -f' to see what's happening";
echo " + Wait a minute";
echo " + Open 'http://<HOST>' in your browser to access the microservice bus
stem";
