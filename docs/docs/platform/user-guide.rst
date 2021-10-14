.. This work is licensed under a Creative Commons Attribution 4.0
   International License.
.. http://creativecommons.org/licenses/by/4.0

MSB User-guide
==============


Overview
--------

Micro-Service Bus (MSB) provides a facility to register and
expose ONAP Rest API.

It is particulary usefull to expose some API endpoints
that are not published by default when installing ONAP with OOM installer.

There are two methods to register an API in MSB:

- via MSB Portal/GUI
- via MSB Rest API


Using MSB portal
----------------

MSB Portal/GUI is available on the following URL
(adapt to your ONAP installation)

..

    http://msb.api.discovery.simpledemo.onap.org:30282/iui/microservices/default.html


The following screen should appears

.. figure:: images/home.png
   :align: center


click on the "Service Discover" icon to get the list of
registred API (it will take several seconds to get the next screen)

.. figure:: images/service-list-2.png
   :align: center

Note: majority of Registred API have been declared using MSB API and
they can not be modified/deleted via MSB Portal/GUI.


click on "view" icon in "Control" column to have a more
detailed description of the registred API

.. figure:: images/service-detail.png
   :align: center

click on "Service Register" icon to start the register process for a new API

.. figure:: images/service-register.png
   :align: center

"Service Name" will be the new API name in MSB and will be used
to build the URL that will be exposed.

"Url" is the url of the existing ONAP API that we are registering

"Protocol" must be selected in the list (usualy REST)

"Version" will be part of the exposed URL

"Visual Range" must be selected (usualy inSystem)

click on "Add Host" to provide information about IP address and
port of the API that we are registering.

Tips: from an ONAP platform installed via OOM, to know about the IP/port,
use Kubctl commands

example: to know about IP addresses used by ONAP Policy components

::

    kubectl get svc -n onap | grep policy


Here after an example of Registered API for ONAP SO:


.. figure:: images/service-edit.png
   :align: center



Using MSB API
-------------

Get the list of registred API in MSB


::

    curl -X GET \
    http://msb.api.discovery.simpledemo.onap.org:30280/api/microservices/v1/services \
    -H 'Content-Type: application/json' \
    -H 'cache-control: no-cache'


response (small extract only):

::

        ],
        "metadata": [],
        "labels": [],
        "status": "1",
        "is_manual": false
    },
    {
        "serviceName": "aai-business",
        "version": "v16",
        "url": "/aai/v16/business",
        "protocol": "REST",
        "visualRange": "1",
        "lb_policy": "ip_hash",
        "publish_port": "",
        "namespace": "",
        "network_plane_type": "",
        "host": "",
        "path": "",
        "enable_ssl": true,
        "nodes": [
            {
                "ip": "10.233.69.5",
                "port": "8447",
                "checkType": "",
                "checkUrl": "",
                "tls_skip_verify": true,
                "ha_role": "",
                "nodeId": "_v16_aai-business_10.233.69.5_8447",
                "status": "passing"
            }
        ],
        "metadata": [],
        "labels": [],
        "status": "1",
        "is_manual": false
    },



Register a new API (example for ONAP policy-pap API)

"Service Name" will be the new API name in MSB and will be used
to build the URL that will be exposed.

"Url" is the url of the existing ONAP API that we are registering

"Protocol" must be selected in the list (usualy REST)

"Version" will be part of the exposed URL

"Visual Range" must be selected (usualy inSystem)

"enable_ssl" must be set to "true" is using
https between MSB and API service that we are registering.

"nodes" is a list  of IP addresses and port of the API that we are registering.

Tips: from an ONAP platform installed via OOM, to know about the IP/port,
use Kubctl commands
"enable_ssl" must be set to "true" is using
https between MSB and API service that we are registering.


::

    curl -X POST \
    http://msb.api.discovery.simpledemo.onap.org:30280/api/microservices/v1/services \
    -H 'Content-Type: application/json' \
    -H 'cache-control: no-cache' \
    -d '{
    "serviceName": "policy-api",
    "version": "v1",
    "url": "/policy/api/v1",
    "protocol": "REST",
    "visualRange": "1",
    "enable_ssl": true,
    "nodes": [

        {
        "ip": "10.233.35.125",
        "port": "6969",
        "ttl": 0
        }
    ]
    }
    '

Delete an API from MSB (=unregister an API):

::

    curl -X DELETE \
    http://msb.api.discovery.simpledemo.onap.org:30280/api/microservices/v1/services/policy-pap/version/v1/nodes/10.233.15.213/6969 \
    -H 'Content-Type: application/json' \
    -H 'cache-control: no-cache' \
    -d '{
    "serviceName": "policy-pdp-legacy",
    "version": "v1",
    "url": "/pdp/api",
    "protocol": "REST",
    "visualRange": "1",
    "enable_ssl": true,
    "nodes": [

        {
        "ip": "10.233.77.14",
        "port": "8081",
        "ttl": 0
        }
    ]
    }
    '


Using a registred API
---------------------


Once registered, the API can then be accessible using
the following URL/Port

http://msb.api.discovery.simpledemo.onap.org:30280/api/{{Service Name}}/{{Version}}/{{resource}}

where {{Service Name}} and {{Version}} are mapped to what have been registered

{{resource}} is to be replace by the object that the API is able to manage


some examples:

to get the service model list from SDC via MSB
(this API is pre-registred by default in ONAP):


::

    curl -X GET \
    http://msb.api.discovery.simpledemo.onap.org:30280/api/sdc/v1/catalog/services \
    -H 'Accept: application/json' \
    -H 'Authorization: Basic YWFpOktwOGJKNFNYc3pNMFdYbGhhazNlSGxjc2UyZ0F3ODR2YW9HR21KdlV5MlU=' \
    -H 'Content-Type: application/json' \
    -H 'USER_ID: cs0008' \
    -H 'X-FromAppId: ONAP-Test' \
    -H 'X-TransactionId: ONAP-Test' \
    -H 'cache-control: no-cache' \
    -H 'x-ecomp-instanceid: ONAP-Test'



to get the customer list from AAI via MSB
(this API is pre-registred by default in ONAP):


::

    curl -X GET \
    http://msb.api.discovery.simpledemo.onap.org:30280/api/aai-business/v16/customers \
    -H 'Accept: application/json' \
    -H 'Authorization: Basic QUFJOkFBSQ==' \
    -H 'Content-Type: application/json' \
    -H 'X-FromAppId: AAI' \
    -H 'X-TransactionId: 808b54e3-e563-4144-a1b9-e24e2ed93d4f' \
    -H 'cache-control: no-cache'


to get the list of policy models from Policy via MSB
(this policy API needs to be registred):

::

    curl -X GET \
    http://msb.api.discovery.simpledemo.onap.org:30280/api/policy-api/v1/policytypes \
    -H 'Authorization: Basic aGVhbHRoY2hlY2s6emIhWHp0RzM0' \
    -H 'X-ONAP-RequestID: 9ac7ce8e-a867-4269-bc52-c8236b7fdad6' \
    -H 'cache-control: no-cache'
