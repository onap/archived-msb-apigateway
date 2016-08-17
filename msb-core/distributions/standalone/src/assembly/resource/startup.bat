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
title msb

set RUNHOME=%~dp0
echo ##RUNHOME %RUNHOME%

rem dir /B /S run.bat > %~dp0\run.tmp
rem For /f %%i in (%~dp0\run.tmp) DO start %%i

echo ### Starting redis
start /D %RUNHOME%redis run.bat

echo ### Starting apiroute
start /D %RUNHOME%apiroute run.bat run

echo ### Starting openresty
start /D %RUNHOME%openresty run.bat  

echo Startup will be finished in background...
echo  + Run "start .\apiroute-works\logs\application.log" to see what's happening
echo  + Wait a minute
echo  + Open "http://<HOST>" in your browser to access the microservice bus !
rem del run.tmp
:finalend