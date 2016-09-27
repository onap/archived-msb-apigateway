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
title stopping msb

set RUNHOME=%~dp0
echo ##RUNHOME %RUNHOME%

rem dir /B /S stop.bat > %~dp0\stop.tmp
rem For /f %%i in (%~dp0\stop.tmp) DO start %%i

echo ### Stopping openresty
start /D %RUNHOME%openresty stop.bat  

echo ### Stopping external API gateway
start /D %RUNHOME%eag stop.bat

echo ### Stopping apiroute
start /D %RUNHOME%apiroute stop.bat

echo ### Stopping redis
start /D %RUNHOME%redis stop.bat

echo "Closing signal has been sent!";
echo "Stopping in background,wait for a moment";
rem del stop.tmp
:finalend