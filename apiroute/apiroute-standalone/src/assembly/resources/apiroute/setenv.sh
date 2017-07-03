#!/bin/sh

Main_Class="org.onap.msb.apiroute.ApiRouteApp"
Main_JAR="apiroute-service.jar"
Main_Conf="conf/apiroute.yml"
APP_INFO="msb-apiroute-service"

if [ -f "/etc/timezone" ]; then
 export TZ=`cat /etc/timezone`
fi