.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0


MSB Release Notes
=============================

.. note::
	* This Release Notes must be updated each time the team decides to Release new artifacts.
	* The scope of this Release Notes is for this particular component. In other words, each ONAP component has its Release Notes.
	* This Release Notes is cumulative, the most recently Released artifact is made visible in the top of this Release Notes.
	* Except the date and the version number, all the other sections are optional but there must be at least one section describing the purpose of this new release.
	* This note must be removed after content has been added.

Microservices Bus provide key infrastructure functionalities to support ONAP microservice architecture including service registration/discovery, service gateway, service load balancer and service governance. It's a pluggable architecture so it can be extended with plugins to provide value added services such as centralized authentication for APIs. Microservices Platform also provides a GUI portal for service management.



Version: 1.0.0
--------------


:Release Date: 2017-11-04



**New Features**
Initial release of　Microservices Bus(MSB) for Open Network Automation Platform (ONAP). MSB provides core functionalities to support ONAP microservices architecture, including SDK for rapid microservie development, infrastructure for service communication and tools for service governance.

The current release of MSB is mianly compose of the following components：

**msb/apigateway**

Provides client request routing, client request load balancing, transformation, such as https to http, authentication & authorization for service request with plugin of auth service provider,service request logging,service request rate-limiting,service monitoring,request result cache,solve cross-domain issue for web application and other functionalities with the pluggable architecture capability.

**msb/discovery**

Provides service registration and discovery for ONAP microservices, which leverage Consul and build an abstract layer on top of it to make it agnostic to the registration provider and add needed extension.
 
**msb/java-sdk**

Provides a JAVA SDK for rapid microservices development, including service registration, service discovery, request routing, load balancing, retry, etc.

**msb/swagger-sdk**

Swagger sdk helps to generate swagger.json and java client sdk during the build time, it also helps to provide the swagger.json at the given URI in the run time.

In the future release, MSB plans to provide service mesh for ONAP.

**Bug Fixes**

`MSB-94 <https://jira.onap.org/browse/MSB-94>`_
Vendor name(ZTE) on the MSB Portal tiltle

`MSB-91 <https://jira.onap.org/browse/MSB-91>`_
Duplicate class variable in service sub-classes

`MSB-88 <https://jira.onap.org/browse/MSB-88>`_
The path parameter has been lost when register services in demo project

`MSB-87 <https://jira.onap.org/browse/MSB-87>`_
MSB JAVA SDK dosen't release stage binary

`MSB-85 <https://jira.onap.org/browse/MSB-85>`_
API Gateway UT coverage doesn't show up in Sonar

`MSB-74 <https://jira.onap.org/browse/MSB-74>`_
Jenkins Integration Test job failed

`MSB-73 <https://jira.onap.org/browse/MSB-73>`_
Can't register service by using MSB Api gateway 80 port

`MSB-72 <https://jira.onap.org/browse/MSB-72>`_	
Unit test coverage data is incorrect

`MSB-71 <https://jira.onap.org/browse/MSB-71>`_	
API Gateway service Registration and discovery api causes confusion

`MSB-70 <https://jira.onap.org/browse/MSB-70>`_	
Swagger SDK site job build failed

`MSB-69 <https://jira.onap.org/browse/MSB-69>`_	
Discovery checkstyle issue

`MSB-68 <https://jira.onap.org/browse/MSB-68>`_	
Discovery daily build jenkins job failed 

`MSB-67 <https://jira.onap.org/browse/MSB-67>`_
API Gateway check style warnning

`MSB-66 <https://jira.onap.org/browse/MSB-66>`_
API Gateway daily build failed

`MSB-60 <https://jira.onap.org/browse/MSB-60>`_	
Api gateway test coverage data not in snoar

`MSB-59 <https://jira.onap.org/browse/MSB-59>`_	
Swagger SDK build failed

`MSB-58 <https://jira.onap.org/browse/MSB-58>`_	
MSB Java SDK Jenkins merge job failed

`MSB-57 <https://jira.onap.org/browse/MSB-57>`_	
Discovery site jenkins job failed
 
`MSB-55 <https://jira.onap.org/browse/MSB-55>`_	
Discovery site jenkins job failed 

`MSB-54 <https://jira.onap.org/browse/MSB-54>`_	
API Gateway site jenkins job failed
 
`MSB-21 <https://jira.onap.org/browse/MSB-21>`_
Merge and daily jenkins job failed 

`MSB-17 <https://jira.onap.org/browse/MSB-17>`_
release version java daily job failed

**Known Issues**

`MSB-92 <https://jira.onap.org/browse/MSB-92>`_
microservice delete is reporting 500, though it deleted the service

**Security Issues**
None

**Upgrade Notes**
This is an initial release

**Deprecation Notes**

**Other**
N/A

===========

End of Release Notes
