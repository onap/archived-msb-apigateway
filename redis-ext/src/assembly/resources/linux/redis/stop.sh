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
HOME=`cd $DIRNAME/; pwd`
_REDISCLIENT="$HOME/redis-cli"

echo ===================== Redis info  =============================================
echo Redis_HOME=$HOME
echo TIP:This shell script close the Redis instance!
echo ===============================================================================
cd $HOME; pwd
_REDIS_PORT="6379"
if [ -n "${APIGATEWAY_MODE}" -a -n "${APIGATEWAY_REDIS_PORT}" ]; then
         _REDIS_PORT=${APIGATEWAY_REDIS_PORT}
fi
echo @C_CMD@ $_REDISCLIENT  -p $_REDIS_PORT shutdown
$_REDISCLIENT  -p $_REDIS_PORT shutdown