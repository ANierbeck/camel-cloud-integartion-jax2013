What is this
============

This is the sample project of the JAX 2013 talk about Integration in the cloud with Camel, Karaf and Cellar. 
Or the original german title "Integration in der Cloud mit Camel, Karaf und Cellar". 
This sample project is given to you by the Inovex GmbH. Without any warranties. 

Requirements
------------
Three separate Karaf instances, which are not supposed to know each other, a S3 storage. 

You'll also need a java compiler and maven installed, since you're going to need to compile this sample and possibly 
the latest version of Apache Karaf-Cellar. 

The initial setup contained of three instances, two of those instances where deployed two seperate Amazon EC2 Linux images. 
The third instance was run locally. The Exchange of the IP Adresses for the Karaf Cluster on the Amazon EC2 systems was achieved by using a S3 Bucket.  
For the CXF Webservice an external usable IP is needed, therefor an Elastic IP was attached to one of the two EC2 instances. 

How does it work
----------------

This Sample provides three modules, each module will be installed on a seperate Karaf Instance. 
The first instance will call a CXF Webservice on the first Amazon EC2 instance. This first
EC2 system will send the message retrieved to a hazelcast queue provided by the Karaf-Cellar infrastructure. 
The second Amazon EC2 instance will retrieve the message from the hazelcast queue and will write the result to the S3 bucket. 

![Showcase for "Integration in der Cloud mit Camel, Karaf and Cellar"](./Showcase-JAX2013.jpg "Showcase JAX-2013")


Using this Showcase
===================

Download Karaf 2.2.9 cause 2.2.10 is somehow a bit buggy and 2.2.11 isn't accessible right now (will be in future). 
You're going to find version 2.2.9 of Karaf at [this download location](http://archive.apache.org/dist/karaf/2.2.9/apache-karaf-2.2.9.zip) 
Along with Karaf you're going to need to use the currently not released version of Cellar 2.2.6 which is available at the ASF, you're going 
to need to build this on your own. 

Building Apache Karaf Cellar
----------------------------

Get the sources from SVN by using the following: 

`svn co https://svn.apache.org/repos/asf/karaf/cellar/branches/cellar-2.2.x`

change to the cellar-2.2.x folder and issue a maven build

`mvn clean install`

Building showcase
-----------------

Retrieve the sources from this location 

`git clone https://github.com/inovex/camel-cloud-integartion-jax2013`  

and just call the following command in the base folder: 

`mvn clean install`

Installing the showcase
-----------------------

Extract the retrieved Apache Karaf zip or tar.gz to location of your choice. 
Rename the jre.properties file in the etc folder to jre.properties.orig and copy the jre.properties.cxf file to jre.properties. 
This is needed for CXF to run smoothly with Apache Karaf, though this won't be needed with Apache Karaf >= 2.3.0. 
Start the Apache Karaf server by calling `karaf` from within the bin folder.  
It turned out that using equinox as osgi-framwork is runing more smoothly therfore you need to switch over to 
equinox as framework by altering the custom.properties. Add the following line at the end of the config file:

`karaf.framework=equinox`   

**Installing Karaf Cellar**

For the CXF-Consuming Node (the one with the EIP) you need to make sure certain bundles are installed before cellar is available since those 
packages needed where disabled in the jre.properties. So install the following: 

`install -s mvn:org.apache.geronimo.specs/geronimo-annotation_1.0_spec/1.1.1`   
`install -s mvn:org.apache.servicemix.specs/org.apache.servicemix.specs.activation-api-1.1/2.0.0`  
`install -s mvn:org.apache.servicemix.specs/org.apache.servicemix.specs.stax-api-1.0/1.9.0`   
`install -s mvn:org.apache.servicemix.specs/org.apache.servicemix.specs.jaxb-api-2.2/1.9.0`  


Install Apache Karaf Cellar by first registering the features.xml: 

`features:addurl mvn:org.apache.karaf.cellar/apache-karaf-cellar/2.2.6-SNAPSHOT/xml/features`

and second installing the required feature: 

`features:install -v -c cellar-cloud`

**Installing the full showcase features**

Installing the full features skips the demonstration of farming parts of this features since every node needs a different setup in this showcase.  
If you want to see how the farming works take a look at the "Installing the light showcase features". 

Before installing the showcase feature we need to make sure the correct white and blacklist entries are set. 
Since the showcase features contain references to the required camel features it's also best to disable synchronization of the features. 

`config:edit org.apache.karaf.cellar.groups`  
`config:propappend default.features.blacklist.outbound ,showcase-cxf-consumer`   
`config:propappend default.features.blacklist.outbound ,showcase-hazelcast-consumer`    
`config:propappend default.features.blacklist.inbound ,showcase-cxf-consumer`     
`config:propappend default.features.blacklist.inbound ,showcase-hazelcast-consumer`
`config:propset default.bundle.sync false`    
`config:propset default.features.sync false`   
`config:update`   

All machines need to know of the showcase features.xml to be added to the features repository, 
therefore issue the following command. 

`features:addurl mvn:de.inovex.jax2013.showcase/feature/1.0.0/xml/feaures`

Now you're able to install the following features dependent on their location. 
The private or local machine does only retrieve the cxf-producer module and therefore
only the following needs to be installed on this one: 

`features:install showcase-cxf-producer`

On the first EC2 node which is accessible through an elastic IP (EIP) install the CXF consuming route module: 

`features:install showcase-cxf-consumer`

On the second EC2 node which only consumes internally but accesses the S3 bucket install the followin: 

`features:install showcase-hazelcast-consumer`


**Installing the light showcase features**

This part also shows you how the farming is working along, still you need to make sure certain parts are not distributed accross the cluster. 
Therefore before installment change the configuration as followed: 

`config:edit org.apache.karaf.cellar.groups`   
`config:propappend default.features.blacklist.outbound ,showcase-cxf-consumer`   
`config:propappend default.features.blacklist.outbound ,showcase-hazelcast-consumer`  
`config:propappend default.features.blacklist.outbound ,camel-aws`  
`config:propappend default.features.blacklist.inbound ,showcase-cxf-consumer`  
`config:propappend default.features.blacklist.inbound ,showcase-haelcast-consumer`  
`config:propappend default.features.blacklist.inbound ,camel-aws`  
`config:propset default.bundle.sync false`  
`config:update`   



All machines need to know of the showcase features.xml to be added to the features repository, 
therefore issue the following command at one of the cluster nodes and on the local machine. 

`features:addurl mvn:de.inovex.jax2013.showcase/feature/0.1.0-SNAPSHOT/xml/features`

Now you're able to install the following features dependent on their location. 
The private or local machine does only retrieve the cxf-producer module and therefore
only the following needs to be installed on this one: 

`features:install showcase-cxf-producer`

On one of the EC2 nodes the camel features need to be deployed. So let's take the node with the elastic IP (EIP)
as "master" right now. And issue the following command: 

`features:install camel-blueprint`

wait for it to complete and also check the second node on how the feature is installed.
After the successfull deployment of the feature and also farming, let's start installing the 
second neeeded feature. 

`features:install camel-cxf`

again wait for this feature to be installed and farmed accross the cluster.  
Now you're able to deploy the dedicated light features to the two EC nodes. 

On the first EC2 node which is accessible through an elastic IP (EIP) install the CXF consuming route module: 

`features:install showcase-cxf-consumer-light`

On the second EC2 node which only consumes internally but accesses the S3 bucket install the following features:

`features:install camel-aws`

after this is successfully deployed check the other node for existance, you'll see the blacklisting did work. 
Now install the showcase feature.  

`features:install showcase-hazelcast-consumer`


***Configuration***  

All of the three nodes need some extra configuration after installation. The features install process already placed those
configuration files in the `etc` folder of the Apach Karaf server. 

***Local Node***  

Edit the `de.inovex.jax2013.showcase.cxf.producer.cfg` file. 
and change the `cxf.remote.url` to the external CXF server to your accessible EIP.
Also change the `file.location` parameter to a valid folder in your system.  

For example one might use the `config` commands as previously used for the black and white-lists. 

`config:edit de.inovex.jax2013.showcase.cxf.producer`  
`config:propset cxf.remote.url http://<elastic-ip>:8181/cxf/messenger`  
`config:propset file.location /User/someone/Documents/importDir/`  
`config:update`  

***First EC2 Node***  

No extra configuration is needed. 

***Second EC2 Node***

Edit the `de.inovex.jax2013.showcase.s3.producer.cfg` file.a 
and change the aws key and security key to your own keys. 

Again use the shell commands to do so: 

`config:edit de.inovex.jax2013.showcase.s3.producer.cfg`   
`config:propset aws.accessKey <access-key>`   
`config:propset aws.secretKey <secret-key>`  
`config:propset bucket.name <bucket-name>`   
`config:update`   

Running this Showcase
=====================
Now only place a text file to the configured Folder and you'll shortly will see a `messageOut.txt` file in your S3 bucket. 

Using pre-configured Karaf Containers
=====================================
For convenience this showcase Project also consists of pre-configured Karaf containers for easy deployment. 
Ever showcase does have a connected container setup.
You're going to find those beneath the assemblies sub-module of this project. 

*cxf-producer-karaf*
*cxf-consumer-karaf*
*hazelcast-consumer-karaf*