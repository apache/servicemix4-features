/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

WSDL First OSGi Bundles Example
===================================

Purpose
-------
Publish a WSDL-defined web service, packaged as OSGi bundles, 
using CXF.

This example is the same as the cxf-wsdl-first example except it
is deployed as OSGi bundles, whereas the cxf-wsdl-first example
is deployed as a service assembly.


Explanation
-----------
The CXF service engine and CXF binding component are used to expose the
web service. Each one is packaged in as an OSGi bundle, as follows:

1. CXF service engine (see the wsdl-first-cxfse-bundle directory):
   
   - Contains a copy of the service WSDL file, person.wsdl, in the
     src/main/resources directory.
   
   - The service implementation file, PersonImpl.java, in the
     src/main/java/org/apache/servicemix/samples/wsdl_first directory.
     It contains JAX-WS annotations that specify which web service
     it implements:

       @WebService(serviceName = "PersonService", 
           targetNamespace = "http://servicemix.apache.org/samples/wsdl-first",
           endpointInterface = "org.apache.servicemix.samples.wsdl_first.Person")
   
   - A configuration file, beans.xml, located in the src/main/resources/
     META-INF/spring directory, which configures the CXF endpoint:
     
       <cxfse:endpoint>
           <cxfse:pojo>
             <bean class="org.apache.servicemix.samples.wsdl_first.PersonImpl" />
           </cxfse:pojo>
       </cxfse:endpoint>
    
2. CXF binding component (see the wsdl-first-cxfbc-bundle directory):

   - Contains a copy of the service WSDL file, person.wsdl, in the
     src/main/resources directory.
    
   - A configuration file, beans.xml, located in the src/main/resources/
     META-INF/spring directory, which specifies a CXF consumer that will
     accept incoming calls for that web service and pass them to the NMR:

       <cxfbc:consumer wsdl="classpath:person.wsdl"
                      targetService="person:PersonService"
                      targetInterface="person:Person"/>

       <bean class="org.apache.servicemix.common.osgi.EndpointExporter" />


Prerequisites for Running the Example
-------------------------------------
1. You must have the following installed on your machine:

   - JDK 1.5 or higher
   
   - Maven 2.0.9 or higher
   
  For more information, see the README in the top-level examples
  directory.


2. Start ServiceMix by running the following command:

  <servicemix_home>/bin/servicemix          (on UNIX)
  <servicemix_home>\bin\servicemix          (on Windows)


Running the Example
-------------------
You can run the example in two ways:

- A. Using a Prebuilt Deployment Bundle: Quick and Easy
This option is useful if you want to see the example up and
running as quickly as possible.

- B. Building the Example Bundle Yourself
This option is useful if you want to change the example in any
way. It tells you how to build and deploy the example. This
option might be slower than option A because, if you do not
already have the required bundles in your local Maven
repository, Maven will have to download the bundles it needs.

A. Using a Prebuilt Deployment Bundle: Quick and Easy
-----------------------------------------------------
To install and run a prebuilt version of this example, enter
the following command in the ServiceMix console:

  features:install examples-cxf-wsdl-first-osgi-package
  
This command makes use of the ServiceMix features facility. For
more information about the features facility, see the README.txt
file in the examples parent directory.

You can browse the WSDL at:

  http://localhost:8092/PersonService?wsdl

Note, if you use Safari, right click the window and select
'Show Source'.

Running a Client
----------------
To run the web client:

1. Open the client.html, which is located in the same directory as
   this README file, in your favorite browser.

2. Click the Send button to send a request.

To run the java code client:

1. Change to the <servicemix_home>/examples/cxf-wsdl-first-osgi-package
   directory.

2. Run the following command:

     mvn compile exec:java

     
B. Building the Example Bundle Yourself
---------------------------------------
To install and run the example where you build the example bundle
yourself, complete the following steps:

1. If you have already run the example using the prebuilt version as
   described above, you must first uninstall the
   examples-cxf-wsdl-first-osgi-package feature by entering the
   following command in the ServiceMix console:

     features:uninstall examples-cxf-wsdl-first-osgi-package

2. Build the example by opening a command prompt, changing directory
   to examples/cxf-wsdl-first-osgi-package (this example) and entering
   the following Maven command:

     mvn install
   
   If all of the required OSGi bundles are available in your local
   Maven repository, the example will build very quickly. Otherwise
   it may take some time for Maven to download everything it needs.
   
   The mvn install command builds the example deployment bundle and
   copies it to your local Maven repository and to the target directory
   of this example.
     
3. Install the example by entering the following command in
   the ServiceMix console:
   
     features:install examples-cxf-wsdl-first-osgi-package
     
   It makes use of the ServiceMix features facility. For more
   information about the features facility, see the README.txt file
   in the examples parent directory.

You can browse the WSDL at:

  http://localhost:8092/PersonService?wsdl

Note, if you use Safari, right click the window and select
'Show Source'.

You can try running a client against your service by following the
instructions in the "Running a Client" section above.


Stopping and Uninstalling the Example
-------------------------------------
To stop the example, you must first know the bundle ID that ServiceMix
has assigned to it. To get the bundle ID, enter the following command
in the ServiceMix console (Note, the text you are typing will
intermingle with the output being logged. This is nothing to worry
about.):

  osgi:list

At the end of the listing, you should see an entry similar to the
following:

  [172] [Active     ] [Started] [  60] Apache ServiceMix Example :: CXF OSGi (4.1.0)

In this case, the bundle ID is 172.

To stop the example, enter the following command in the ServiceMix
console:

  osgi:stop <bundle_id>

For example:

  osgi:stop 172

To uninstall the example, enter one of the following commands in
the ServiceMix console:

  features:uninstall examples-cxf-wsdl-first-osgi-package
 
or
 
  osgi:uninstall <bundle_id>
  

Viewing the Log Entries
-----------------------
You can view the entries in the log file in the data/log
directory of your ServiceMix installation, or by typing
the following command in the ServiceMix console:

  log:display


More Information
----------------
For more information, see:

  http://cwiki.apache.org/SMX4/creating-an-osgi-bundle-for-deploying-jbi-endpoints.html
