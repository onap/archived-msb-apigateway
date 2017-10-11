.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Installation
------------
MSB can run as docker, it's very handy to try it at your laptop. For production, MSB supports to be deployed as a cluster to provide a scalable microservice communication infrastructure with kubernetes.
 
Run MSB on the a single host using host network
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This deployment approach is only for testing. MSB is consist of multiple microservices, for testing, the easiest way is to run them in the same host by using host network.  

- Run the Consul dockers

::

  sudo docker run -d --net=host --name msb_consul consul 

- Run the MSB dockers

Login the ONAP docker registry first: 

::

  docker login -u docker -p docker nexus3.onap.org:10001

Run MSB dockers

::

  docker run -d --net=host --name msb_discovery nexus3.onap.org:10001/onap/msb/msb_discovery
  docker run -d --net=host -e "ROUTE_LABELS=visualRange:1" --name msb_internal_apigateway nexus3.onap.org:10001/onap/msb/msb_apigateway

- Register your REST service to MSB via curl

For testing, we can register the services via curl. 

::

    curl -X POST \
    -H "Content-Type: application/json" \
    -d '{"serviceName": "aai", "version": "v8", "url": "/aai/v8","protocol": "REST", "path": "/aai/v8", "nodes": [ {"ip": "10.74.215.65","port": "8443"}]}' \
    "http://127.0.0.1:10081/api/microservices/v1/services”

- Access the rest service via api gateway

::

    curl http://127.0.0.1/api/aai/v8/cloud-infrastructure/cloud-regions

- Browse the registered services

  Open MSB Web GUI portal in your browser: http://127.0.0.1/msb, you can see all the registered services. If the registered service support swagger, you can see the REST API documentation and test the registered services via the swagger UI integrated in MSB.

Run MSB using default docker network
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In case that you don't want to use host network to set up the MSB dockers, you need to export the port and specify the environment variables to let msb components know each other.

- Run the Consul dockers

::

    sudo docker run -d -p 8500:8500  --name msb_consul consul 
    CONSUL_IP=`sudo docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' msb_consul`


- Run the MSB dockers

Login the ONAP docker registry first: 

::

  docker login -u docker -p docker nexus3.onap.org:10001

Run MSB dockers

::

  sudo docker run -d  -p 10081:10081  -e CONSUL_IP=$CONSUL_IP --name msb_discovery nexus3.onap.org:10001/onap/msb/msb_discovery
  DISCOVERY_IP=`sudo docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' msb_discovery`
  sudo docker run -d -p 80:80 -e CONSUL_IP=$CONSUL_IP -e SDCLIENT_IP=$DISCOVERY_IP -e "ROUTE_LABELS=visualRange:1" --name msb_internal_apigateway nexus3.onap.org:10001/onap/msb/msb_apigateway

Deploy MSB with kubernetes
^^^^^^^^^^^^^^^^^^^^^^^^^^

The chart for MSB running in kubernetes cluster is available here: 

https://gerrit.onap.org/r/gitweb?p=oom.git;a=tree;f=kubernetes/msb;h=17f8fd89791b81f1e981716dcffdb3e2e90299ae;hb=refs/heads/master
