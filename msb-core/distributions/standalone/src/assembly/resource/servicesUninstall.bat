@REM
@REM Copyright 2016 2015-2016 ZTE, Inc. and others. All rights reserved.
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
title msb uninstall services

set RUNHOME=%~dp0

echo ### uninstall redis
cd "%RUNHOME%\redis"
redisService.exe stop
redisService.exe uninstall

echo ### uninstall apiroute
cd "%RUNHOME%\apiroute"
apirouteService.exe stop
apirouteService.exe uninstall

echo ### uninstall openresty
cd "%RUNHOME%\openresty\nginx"
openrestyService.exe stop
openrestyService.exe uninstall

:finalend
cd "%RUNHOME%"