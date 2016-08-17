@REM
@REM Copyright (C) 2016 ZTE, Inc. and others. All rights reserved. (ZTE)
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM         http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM
@echo off
title redis-server
@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set DIRNAME=.

if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%

set HOME=%DIRNAME%
set _REDISCMD=%DIRNAME%redis-server.exe
set _REDISCONF=%DIRNAME%redis.conf


echo ================Redis config info =============================================
echo Redis_HOME=%HOME%
echo config file=$_REDISCONF
echo Help:use $_REDISCMD --help for help
echo ===============================================================================


cd /d "%HOME%"
echo @WORK_DIR@%HOME%
echo @C_CMD@ "%_REDISCMD% %_REDISCONF%"
%_REDISCMD% %_REDISCONF%
IF ERRORLEVEL 1 goto showerror
exit
:showerror
echo WARNING: Error occurred during startup or Server abnormally stopped by way of killing the process,Please check!
echo After checking, press any key to close 
pause
exit
