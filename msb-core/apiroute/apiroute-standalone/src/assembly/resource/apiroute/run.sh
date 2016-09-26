#!/bin/bash
#
# Copyright 2016 ZTE Corporation.
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

#JAVA_HOME="/home/conductortest/jdk1.7/jdk/linux"

DIRNAME=`dirname $0`
RUNHOME=`cd $DIRNAME/; pwd`
echo @RUNHOME@ $RUNHOME

#JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:/bin/javac::")
echo @JAVA_HOME@ $JAVA_HOME
JAVA="$JAVA_HOME/bin/java"
echo @JAVA@ $JAVA
JAVA_VERSION=`$JAVA -version 2>&1 |awk 'NR==1{ sub(/"/,""); print substr($3,1,3)}'`
echo @JAVA_VERSION@ $JAVA_VERSION
if [ $JAVA_VERSION = "1.8" ]
then
    JAVA_OPTS="-Xms16m -Xmx128m -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m -XX:NewRatio=2"
else
    JAVA_OPTS="-Xms16m -Xmx128m -XX:+UseSerialGC -XX:MaxPermSize=64m -XX:NewRatio=2"
fi
port=8779
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=$port,server=y,suspend=n"
echo @JAVA_OPTS@ $JAVA_OPTS

class_path="$RUNHOME/:$RUNHOME/apiroute-service.jar"
echo @class_path@ $class_path

"$JAVA" $JAVA_OPTS -classpath "$class_path"  org.openo.msb.ApiRouteApp server "$RUNHOME/conf/apiroute.yml"
