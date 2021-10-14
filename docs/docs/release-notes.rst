.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. _release_notes:


Microservices Bus(MSB) provides a reliable, resilient and scalable communication and governance infrastructure to support ONAP Microservice Architecture including service registration/discovery, external API gateway, internal API gateway, client SDK, Swagger SDK, etc. It's a pluggable architecture, plugins can be added to MSB to provide whatever functionalities you need, such as an auth plugin can be used to provide centralized authentication & authorization. MSB also provides a service portal to manage the REST APIs.

MSB is platform independent, while it is integrated with Kubernetes(OOM) to provide transparent service registration for ONAP microservices, MSB also supports OpenStack(Heat) and bare metal deployment.

Release Notes
=============

Version: 1.3.1 Istanbul Release
-------------------------------

:Release Date: 2021-9-22

**New Features**

**Bug Fixes**

**Fixed Security Issues**

- MSB-593 PACKAGES UPGRADES IN DIRECT DEPENDENCIES FOR ISTANBUL
- MSB-519 MSB has python 2.7 pods
- MSB-520 MSB has java 8 pods
- MSB-521 MSB certificates are too long and have bad owner

**Known Issues**

- MSB-534 kube2msb fails to register SO services including braces in url

**Security Notes**

The remaining issues are supposed to be fixed in the next release.

Quick Links:
        - `MSB project page for Istanbul Release <https://wiki.onap.org/display/DW/Microservices+Bus+Project>`_

        - `Passing Badge information for MSB <https://bestpractices.coreinfrastructure.org/en/projects/1601>`_

**Upgrade Notes**

N/A

**Deprecation Notes**

Plain HTTP APIs have been removed from Frankfurt release, please use HTTPS instead.

N/A

**Other**

N/A

Version: 1.2.7 Guilin Release
------------------------------

:Release Date: 2020-11-19

**New Features**

**Bug Fixes**

- MSB-539 Fix nginx say 10000 worker_connections are not enough
- MSB-540 nginx in discovery deletes useless log printing
- MSB-541 Discovery supports blocking monitoring time exceeding 10min

**Fixed Security Issues**

**Known Issues**

- MSB-519 MSB has python 2.7 pods
- MSB-520 MSB has java 8 pods
- MSB-521 MSB certificates are too long and have bad owner
- MSB-529 MSB dockers contain GPLv3

**Security Notes**

The remaining issues are supposed to be fixed in the next release.

Quick Links:
        - `MSB project page for Guilin Release <https://wiki.onap.org/display/DW/Microservices+Bus+Project>`_

        - `Passing Badge information for MSB <https://bestpractices.coreinfrastructure.org/en/projects/1601>`_


**Upgrade Notes**

N/A

**Deprecation Notes**

Plain HTTP APIs have been removed from Frankfurt release, please use HTTPS instead.

N/A

**Other**

N/A

Version: 1.2.6 Frankfurt Release
--------------------------------

:Release Date: 2020-May-07

**New Features**

**Bug Fixes**

- MSB-470 Pairwise testing fails from Portal to MSB
- MSB-468 MSB verification job is unstable
- MSB-467 Certificate in MSB is set to use zte.com.cn
- MSB-451 Code coverage data can't show at sonarcloud

**Fixed Security Issues**

- MSB-465 Update alpine to the latest version
- MSB-462 Pods still run as root
- MSB-418 Solve MSB vulnerability onap-msb-discovery-jackson-databind
- MSB-417 Solve MSB vulnerability onap-msb-apigateway-jackson-databind
- MSB-410 Solve MSB vulnerability onap-msb-java-sdk-jackson-databind
- MSB-408 Solve MSB vulnerability onap-msb-java-sdk-commons-codec
- MSB-407 Solve MSB vulnerability onap-msb-java-sdk-okhttp 
- MSB-398 Remove plain HTTP Rest APIs
- MSB-386 General sonar fixes

**Known Issues**

**Security Notes**

MSB code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed.
The remaining issues are identified as false positive and their risk assessment have been documented.

Quick Links:
        - `MSB project page for Frankfurt Release <https://wiki.onap.org/display/DW/Microservices+Bus+Project>`_

        - `Passing Badge information for MSB <https://bestpractices.coreinfrastructure.org/en/projects/1601>`_

**Upgrade Notes**

N/A

**Deprecation Notes**

Plain HTTP APIs have been removed from Frankfurt release, please use HTTPS instead.

N/A

**Other**

N/A

Version: 1.2.5 EI-Alto Release
------------------------------

:Release Date: 2019-09-05

**New Features**

This release only contains a few bug fixes and small features.

**New Features**

- MSB-332 Delete custom services via the MSB management UI
- MSB-151 Support registering HTTPS service on the MSB management UI

**Bug Fixes**

- MSB-372 Empty pages that looks like they should be deleted
- MSB-374 MSB stripe of the headers with underscore and it blocks SDC API functionality

**Known Issues**

**Security Notes**

MSB code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed.
The remaining issues are identified as false positive and their risk assessment have been documented.

Quick Links:
 	- `MSB project page for EI-Alto Release <https://wiki.onap.org/display/DW/Microservices+Bus+Project>`_

 	- `Passing Badge information for MSB <https://bestpractices.coreinfrastructure.org/en/projects/1601>`_

 	- `Project Vulnerability Review Table for MSB EI-Alto Release <https://wiki.onap.org/pages/viewpage.action?pageId=68541445>`_

**Upgrade Notes**

N/A

**Deprecation Notes**

N/A

**Other**

N/A

Version: 1.2.4 Dublin Release
-----------------------------

:Release Date: 2019-06-10

**New Features**

This release only contains some security improvements and a few fixes.

**Security improvements**

- MSB MSB-295 Nexus IQ Issue: bootstrap
- MSB MSB-320 Run API Gateway as a non-root user
- MSBMSB-321 Run Discovery as a non-root user
- MSB MSB-322 Run Kube2msb as a non-root user
- MSB MSB-328 Security issue reported by Nexus-iq : jetty-http
- MSB MSB-329 Security issue reported by Nexus-iq : spring-core
- MSB MSB-330 Security issue reported by Nexus-iq : commons-beanutils
- MSB MSB-331 Jackson datatype security issue

**Bug Fixes**

- MSB-98  No information is available to select proper node IP from registered services
- MSB-281 improve CLM for swagger sdk
- MSB-325 consul container is outdated
- MSB-326 non STAGING version on master

**Known Issues**

**Security Notes**

MSB code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed.
The remaining issues are identified as false positive and their risk assessment have been documented.

Quick Links:
 	- `MSB project page for Dublin Release <https://wiki.onap.org/display/DW/Microservices+Bus+Project>`_

 	- `Passing Badge information for MSB <https://bestpractices.coreinfrastructure.org/en/projects/1601>`_

 	- `Project Vulnerability Review Table for MSB Dublin Release <https://wiki.onap.org/pages/viewpage.action?pageId=64003723>`_

**Upgrade Notes**

N/A

**Deprecation Notes**

N/A

**Other**

N/A

Version: 1.2.3
--------------

:Release Date: 2018-11-30


**New Features**

In Casablanca Release, MSB mainly focuses on the integration of Istio service mesh with ONAP to enhance OMSA, while keeping the Istio integration compatible with the existing MSB API Gateway approaches.

How to manage ONAP microservices with Istio service mesh:

- https://wiki.onap.org/display/DW/Manage+ONAP+Microservices+with+Istio+Service+Mesh
- https://wiki.onap.org/display/DW/Manage+ONAP+Microservices+with+Istio+Service+Mesh-Mutual+TLS+Authentication+Enabled

**Bug Fixes**

- `MSB-196 <https://jira.onap.org/browse/MSB-196>`_ IUI displays raw placeholder texts when failed to load translation
- `MSB-291 <https://jira.onap.org/browse/MSB-291>`_ Incomplete Apache-2.0 header
- `MSB-293 <https://jira.onap.org/browse/MSB-293>`_ Portal to MSB pairwise test failing in WindRiver with OOM deployment
- `MSB-294 <https://jira.onap.org/browse/MSB-294>`_ Nexus IQ Issue: okhttp3
- `MSB-296 <https://jira.onap.org/browse/MSB-296>`_ Nexus IQ Issue: guava
- `MSB-297 <https://jira.onap.org/browse/MSB-297>`_ MSB CSIT failed
- `MSB-298 <https://jira.onap.org/browse/MSB-298>`_ Release MSB artifact version 1.2.0
- `MSB-300 <https://jira.onap.org/browse/MSB-300>`_ Incomplete Apache-2.0 header
- `MSB-301 <https://jira.onap.org/browse/MSB-301>`_ Can't access aai resource http url via msb api gateway

**Known Issues**

- `MSB-295 <https://jira.onap.org/browse/MSB-295>`_ Nexus IQ Issue: bootstrap
- `MSB-198 <https://jira.onap.org/browse/MSB-198>`_ MSB GUI can not register a service mapped to an HTTPS endpoint

**Security Notes**

MSB code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and actions to be taken in future release.
The MSB open Critical security vulnerabilities and their risk assessment have been documented.

Quick Links:
 	- `MSB project page for Casablanca Release <https://wiki.onap.org/display/DW/Microservices+Bus+Project>`_

 	- `Passing Badge information for MSB <https://bestpractices.coreinfrastructure.org/en/projects/1601>`_

 	- `Project Vulnerability Review Table for MSB Casablanca Release <https://wiki.onap.org/pages/viewpage.action?pageId=45305668>`_

**Upgrade Notes**

N/A

**Deprecation Notes**

N/A

**Other**

N/A


Version: 1.1.0
--------------

:Release Date: 2018-06-07


**New Features**
In Beijing release, MSB project mainly focused on the Platform Maturity requirements of ONAP, including the scalability and security. Some new features which were requested when integrated with other projects, such as websocket support, service registration at K8S Pod level, multiple versions of services, etc. have also been added to this release.

- `MSB-117 <https://jira.onap.org/browse/MSB-146>`_ Support horizontal scaling
- `MSB-140 <https://jira.onap.org/browse/MSB-140>`_ Providing HTTPS endpoint at API gateway
- `MSB-146 <https://jira.onap.org/browse/MSB-146>`_ Support service registration at K8s Pod level
- `MSB-152 <https://jira.onap.org/browse/MSB-152>`_ MSB JAVA SDK supports HTTPS service registration
- `MSB-156 <https://jira.onap.org/browse/MSB-156>`_ Support websocket request forwarding
- `MSB-178 <https://jira.onap.org/browse/MSB-178>`_ Support registering multiple versions under a service name
- `MSB-179 <https://jira.onap.org/browse/MSB-179>`_ Integration MSB GUI to Portal project

**Bug Fixes**

- `MSB-92 <https://jira.onap.org/browse/MSB-92>`_ Microservice delete is reporting 500, though it deleted the service
- `MSB-102 <https://jira.onap.org/browse/MSB-102>`_ The msb client has heavy dependencies
- `MSB-150 <https://jira.onap.org/browse/MSB-150>`_ Kube2msb doesn't unregister service
- `MSB-153 <https://jira.onap.org/browse/MSB-153>`_ MSB kube2msb registrator does not register LoadBalancer type service
- `MSB-187 <https://jira.onap.org/browse/MSB-187>`_ MSB discovery API in swagger is not published
- `MSB-195 <https://jira.onap.org/browse/MSB-195>`_ HTTP protocol used over HTTPS port

**Known Issues**

N/A

**Security Notes**

MSB code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The MSB open Critical security vulnerabilities and their risk assessment have been documented.

Quick Links:

- `MSB project page for  Beijing Release <https://wiki.onap.org/display/DW/Microservices+Bus+Project>`_
- `Passing Badge information for MSB <https://bestpractices.coreinfrastructure.org/en/projects/1601>`_
- `Project Vulnerability Review Table for MSB Beijing Release <https://wiki.onap.org/pages/viewpage.action?pageId=40927271>`_

**Upgrade Notes**

N/A

**Deprecation Notes**

N/A

**Other**

N/A


Version: 1.0.0
--------------

:Release Date: 2017-11-16


**New Features**
Initial release of Microservices Bus (MSB) for Open Network Automation Platform (ONAP). MSB provides core functionalities to support ONAP microservices architecture, including SDK for rapid microservie development, infrastructure for service communication and tools for service governance.

The current release of MSB is mainly composed of the following components:

**msb/apigateway**

Provides client request routing, client request load balancing, transformation, such as https to http, authentication & authorization for service request with plugin of auth service provider, service request logging, service request rate-limiting, service monitoring, request result cache, solve cross-domain issue for web application and other functionalities with the pluggable architecture capability.

**msb/discovery**

Provides service registration and discovery for ONAP microservices, which leverage Consul and build an abstract layer on top of it to make it agnostic to the registration provider and add needed extension.

**msb/java-sdk**

Provides a JAVA SDK for rapid microservices development, including service registration, service discovery, request routing, load balancing, retry, etc.

**msb/swagger-sdk**

Swagger sdk helps to generate swagger.json and java client sdk during the build time, it also helps to provide the swagger.json at the given URI in the run time.

In the future release, MSB plans to provide service mesh for ONAP.

**Bug Fixes**

- `MSB-94 <https://jira.onap.org/browse/MSB-94>`_ Vendor name(ZTE) on the MSB Portal tiltle
- `MSB-91 <https://jira.onap.org/browse/MSB-91>`_ Duplicate class variable in service sub-classes
- `MSB-88 <https://jira.onap.org/browse/MSB-88>`_ The path parameter has been lost when register services in demo project
- `MSB-87 <https://jira.onap.org/browse/MSB-87>`_ MSB JAVA SDK dosen't release stage binary
- `MSB-85 <https://jira.onap.org/browse/MSB-85>`_ API Gateway UT coverage doesn't show up in Sonar
- `MSB-74 <https://jira.onap.org/browse/MSB-74>`_ Jenkins Integration Test job failed
- `MSB-73 <https://jira.onap.org/browse/MSB-73>`_ Can't register service by using MSB Api gateway 80 port
- `MSB-72 <https://jira.onap.org/browse/MSB-72>`_ Unit test coverage data is incorrect
- `MSB-71 <https://jira.onap.org/browse/MSB-71>`_ API Gateway service Registration and discovery api causes confusion
- `MSB-70 <https://jira.onap.org/browse/MSB-70>`_ Swagger SDK site job build failed
- `MSB-69 <https://jira.onap.org/browse/MSB-69>`_ Discovery checkstyle issue
- `MSB-68 <https://jira.onap.org/browse/MSB-68>`_ Discovery daily build jenkins job failed
- `MSB-67 <https://jira.onap.org/browse/MSB-67>`_ API Gateway check style warnning
- `MSB-66 <https://jira.onap.org/browse/MSB-66>`_ API Gateway daily build failed
- `MSB-60 <https://jira.onap.org/browse/MSB-60>`_ API gateway test coverage data not in snoar
- `MSB-59 <https://jira.onap.org/browse/MSB-59>`_ Swagger SDK build failed
- `MSB-58 <https://jira.onap.org/browse/MSB-58>`_ MSB Java SDK Jenkins merge job failed
- `MSB-57 <https://jira.onap.org/browse/MSB-57>`_ Discovery site jenkins job failed
- `MSB-55 <https://jira.onap.org/browse/MSB-55>`_ Discovery site jenkins job failed
- `MSB-54 <https://jira.onap.org/browse/MSB-54>`_ API Gateway site jenkins job failed
- `MSB-21 <https://jira.onap.org/browse/MSB-21>`_ Merge and daily jenkins job failed
- `MSB-17 <https://jira.onap.org/browse/MSB-17>`_ Release version java daily job failed

**Known Issues**

- `MSB-92 <https://jira.onap.org/browse/MSB-92>`_ Microservice delete is reporting 500, though it deleted the service

**Security Issues**

None

**Upgrade Notes**

This is an initial release

**Deprecation Notes**

N/A

**Other**

N/A

End of Release Notes
