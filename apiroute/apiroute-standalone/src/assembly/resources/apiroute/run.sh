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
echo @RUNHOME@  $RUNHOME
echo @JAVA_HOME@  $JAVA_HOME
echo @Main_Class@  $Main_Class
echo @APP_INFO@  $APP_INFO
echo @Main_JAR@  $Main_JAR
echo @Main_Conf@ $Main_Conf
echo ==========================================================================

echo start $APP_INFO ...

JAVA="$JAVA_HOME/bin/java"

JAVA_VERSION=`$JAVA -version 2>&1 |awk 'NR==1{ sub(/"/,""); print substr($3,1,3)}'`
echo @JAVA_VERSION@ $JAVA_VERSION
if [ $JAVA_VERSION = "1.8" ]
then
    JAVA_OPTS="-Xms16m -Xmx128m -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m -XX:NewRatio=2"
else
    JAVA_OPTS="-Xms16m -Xmx128m -XX:+UseSerialGC -XX:MaxPermSize=64m -XX:NewRatio=2"
fi

JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$RUNHOME/../apiroute-works/logs/dump-msb-$(date +%Y%m%d%H%M%S).hprof"
port=8777
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=$port,server=y,suspend=n"

CLASS_PATH="$RUNHOME/:$RUNHOME/$Main_JAR"

echo ================== RUN_INFO  =============================================
echo @JAVA_HOME@ $JAVA_HOME
echo @JAVA@ $JAVA
echo @JAVA_OPTS@ $JAVA_OPTS
echo @CLASS_PATH@ $CLASS_PATH
echo @EXT_DIRS@ $EXT_DIRS
echo ==========================================================================

echo @JAVA@ $JAVA
echo @JAVA_CMD@
"$JAVA"  $JAVA_OPTS -classpath "$CLASS_PATH" $Main_Class server "$RUNHOME/$Main_Conf"


