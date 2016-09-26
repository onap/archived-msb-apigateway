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
title close openresty-server
@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set DIRNAME=.

if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%

set ARGS=
:loop
if [%1] == [] goto endloop
        set ARGS=%ARGS% %1
        shift
        goto loop
:endloop

set HOME=%DIRNAME%nginx
set _NGINXCMD=%HOME%\nginx.exe


echo =========== openresty config info  ============================================
echo HOME=%HOME%
echo _NGINXCMD=%_NGINXCMD%
echo ===============================================================================


cd /d "%HOME%"
echo @WORK_DIR@%HOME%
echo @C_CMD@ "%_NGINXCMD% -s stop"

%_NGINXCMD% -s stop
echo closing signal has been sent,stopping in background,WAIT...
timeout /t 5 /nobreak > nul
exit
