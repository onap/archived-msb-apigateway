.. This work is licensed under a Creative Commons Attribution 4.0 International License.

MSB(Microservices Bus)
------------------------------------------------

Microservices Bus(MSB) provides a reliable, resilient and scalable communication and governance infrastructure to support ONAP Microservice Architecture including service registration/discovery, external API gateway, internal API gateway, client SDK, Swagger SDK, etc. It's a pluggable architecture, plugins can be added to MSB to provide whatever functionalities you need, such as an auth plugin can be used to provide centralized authentication & authorization. MSB also provides a service portal to manage the REST APIs.

MSB is platform independent, while it is integrated with Kubernetes(OOM) to provide transparent service registration for ONAP microservices, MSB also supports OpenStack(Heat) and bare metal deployment.

This document is aimed to give the users some brief introductions on MSB from different aspects. The users could find useful information, e.g. the architecuture, the APIs and the installation steps, etc. as well as the release notes of MSB in this document.


.. toctree::
   :maxdepth: 1
   
   platform/index.rst
   release-notes.rst

