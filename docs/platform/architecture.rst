.. This work is licensed under a Creative Commons Attribution 4.0 International License.


Architecture
------------

Holmes comprises three modules: the rule management module, the engine management module and the data source adapter.

- Holmes
    - Rule Management Module
    - Engine Management Module
    - Data Source Adapter
	
ONAP-level Architecture
^^^^^^^^^^^^^^^^^^^^^^^

Basically, Holmes itself is an independent component in ONAP, which means it could be deployed as an ONAP-level component. In the Amsterdam release, Holmes is more generally a DCAE analytic application. It is deployed by DCAE and run as an analytic application on top of it. Also, it could be considered as a filter of the Policy component because it reduces the number of the input messages of Policy.

.. image:: images/overall-architecture-in-onap.png

Holmes Architecture
^^^^^^^^^^^^^^^^^^^

Take a deep dive into Holmes, we could see it mainly consists of three modules, which are the rule management module, the engine management module and the data source adapter module respectively. 

The rule management module provides interfaces for the operations (e.g. creating, updating and deleting) on the rules.

The data source adapter consists of subscribers and publishers, which are used to convert the data format into the one that could be digested by Holmes and vice versa. 

The engine management module is the core of Holmes. All the rules are deployed here. When alarms gets into Holmes, they will be pushed into the Drools engine and analyzed by the enabled rules one after another. When processing the alarms, a couple of attributes, such as the alarm name, the occurrence time of the alarm and so on, are utilized. Also, the topological information from A&AI is used in combination of the alarm attributes. After the root cause is identified, it will be converted into a control loop event and published to a specific DMaaP topic which is subscribed to by the Policy component.

.. image:: images/holmes-architecture.png
