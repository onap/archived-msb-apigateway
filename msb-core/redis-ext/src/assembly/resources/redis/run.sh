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
HOME=`cd $DIRNAME/; pwd`
_REDISCMD="$HOME/redis-server"
_REDISCONF="$HOME/redis.conf"

echo =========== Redis config info  =============================================
echo Redis_HOME=$HOME
echo config file=$_REDISCONF
echo  Help:use $_REDISCMD --help for help
echo ===============================================================================
cd $HOME; pwd

echo @C_CMD@ $_REDISCMD $_REDISCONF
$_REDISCMD $_REDISCONF
