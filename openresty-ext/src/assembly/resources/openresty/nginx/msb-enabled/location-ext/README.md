README
===============

The directory to store configuration files that extends locations of the default listening server(e.g 10080).
The config file must be a *.conf file. For example:
#testlocation.conf
~~~
location = /test {
	echo "test ok";
}
~~~