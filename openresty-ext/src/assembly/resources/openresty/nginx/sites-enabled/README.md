README
===============

The directory to store configuration files that configs another server except the default listening server(e.g 10080).
The config file must be a *.conf file. For example:
#server1.conf
~~~
server {
	listen 20080;
	
	location =/info {
	   echo "another server 20080";  
	}
}
~~~