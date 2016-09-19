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

echo "### Stopping openresty...";
# nohup ./startup.sh >>./nohup.log 2>&1 &
cd ./openresty
./stop.sh &
cd $RUNHOME

echo "### Stopping external API gateway...";
# nohup ./startup.sh >>./nohup.log 2>&1 &
cd ./eag
./stop.sh &
cd $RUNHOME

echo "\n\n### Stopping apiroute"
cd ./apiroute
./stop.sh &
cd $RUNHOME

echo "### Stopping redis";
cd ./redis
./stop.sh &
cd $RUNHOME

echo "Closing signal has been sent!";
echo "Stopping in background,wait for a moment";
sleep 3;