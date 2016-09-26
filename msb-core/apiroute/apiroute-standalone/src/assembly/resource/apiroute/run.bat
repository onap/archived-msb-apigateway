@REM
@REM Copyright 2016 ZTE Corporation.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM     http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM
@REM     Author: Zhaoxing Meng
@REM     email: meng.zhaoxing1@zte.com.cn
@REM

@echo off
title apiroute-service

set RUNHOME=%~dp0
echo ### RUNHOME: %RUNHOME%

echo ### Starting apiroute-service
rem cd /d %RUNHOME%

rem set JAVA_HOME=D:\JDK1.7\jdk\jdk\windows
set JAVA="%JAVA_HOME%\bin\java.exe"
set port=8777

setlocal enabledelayedexpansion
set index=1
FOR /F "tokens=3 delims= " %%a in ('"%JAVA% -version 2>&1"')do (
  if !index! equ 1 set JAVA_VERSION=%%a
  set /a index=index+1
)
set JAVA_VERSION=%JAVA_VERSION:~1,3%
if "%JAVA_VERSION%"=="1.8" (
   set jvm_opts=-Xms16m -Xmx128m -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m  -XX:NewRatio=2 
) else (
   set jvm_opts=-Xms16m -Xmx128m -XX:+UseSerialGC  -XX:MaxPermSize=64m -XX:NewRatio=2
)

set jvm_opts=%jvm_opts% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=%port%,server=y,suspend=n
set class_path=%RUNHOME%;%RUNHOME%apiroute-service.jar
echo ### jvm_opts: %jvm_opts%
echo ### class_path: %class_path%

%JAVA% -classpath %class_path% %jvm_opts% org.openo.msb.ApiRouteApp  server %RUNHOME%conf/apiroute.yml

IF ERRORLEVEL 1 goto showerror
exit
:showerror
echo WARNING: Error occurred during startup or Server abnormally stopped by way of killing the process,Please check!
echo After checking, press any key to close 
pause
exit