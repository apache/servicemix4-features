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

WSDL First JBI Service Assembly Example
=======================================

Purpose
-------
Publish a WSDL-defined web service, as a JBI service assembly, using CXF.

This example is the same as the cxf-wsdl-first-osgi example except it is
deployed as a JBI service assembly, whereas the cxf-wsdl-first-osgi
example is deployed as OSGi bundles.


Explanation
-----------
The CXF service engine and CXF binding component are used to expose the
web service. Each one is packaged in a service unit (SU), as follows:

1. CXF service engine (see the wsdl-first-cxfse-su directory):
   
   - Contains a copy of the service WSDL file, person.wsdl, in the
     src/main/resources directory.
   
   - The service implementation file, PersonImpl.java, in the
     src/main/java/org/apache/servicemix/samples/wsdl_first directory.
     It contains JAX-WS annotations that specify which web service
     it implements:

       @WebService(serviceName = "PersonService", 
           targetNamespace = "http://servicemix.apache.org/samples/wsdl-first",
           endpointInterface = "org.apache.servicemix.samples.wsdl_first.Person")
   
   - A configuration file, xbean.xml, located in the src/main/resources
     directory, which configures the CXF endpoint:
     
      <cxfse:endpoint>
          <cxfse:pojo>
            <bean class="org.apache.servicemix.samples.wsdl_first.PersonImpl" />
          </cxfse:pojo>
      </cxfse:endpoint>
    
2. CXF binding component (see the wsdl-first-cxfbc-su directory):

   - Contains a copy of the service WSDL file, person.wsdl, in the
     src/main/resources directory.
    
   - A configuration file, xbean.xml, located in the src/main/resources
     directory, which specifies a CXF consumer that will accept
     incoming calls for that web service and pass them to the NMR:

     <cxfbc:consumer wsdl="classpath:person.wsdl"
                      targetService="person:PersonService"
                      targetInterface="person:Person"/>

Lastly, Maven uses the pom.xml file, located in the wsdl-first-cxf-sa
directory, to package the SUs into a JBI service assembly ready for deployment.


Prerequisites for Building and Running this Example
---------------------------------------------------
1. You must have the following installed on your machine:

   - JDK 1.5 or higher.

   - Apache Maven 2.0.6 or higher.

   For more information, see the README in the top-level examples
   directory.

2. Launch ServiceMix by running the following command:

  <servicemix_home>/bin/karaf	(on UNIX)
  <servicemix_home>\bin\karaf   (on Windows)


Building and Deploying
----------------------
To build the example, run the following command (from the
directory that contains this README):

  mvn install
  
If all of the required OSGi bundles are available in your local Maven
repository, the example will build quickly. Otherwise it may take
some time for Maven to download everything it needs.

Once complete, you will find the SA, called cxf-wsdl-first-cxf-sa-
${version}.zip, in the wsdl-first-cxf-sa/target directory.

You can deploy the SA in two ways:

- Using Hot Deployment
  --------------------
  
  Copy the wsdl-first-cxf-sa/target/wsdl-first-cxf-sa-${version}.zip
  to the <servicemix_home>/deploy directory.
     
- Using the ServiceMix Console
  ----------------------------
  
  Enter the following command:

  osgi:install -s mvn:org.apache.servicemix.examples.cxf-wsdl-first/wsdl-first-cxf-sa/${version}/zip
 
You can browse the WSDL at:

  http://localhost:8092/PersonService?wsdl


Running a Client
----------------
To run the web client:

1. Open the client.html, which is located in the same directory
   as this README file, in your favorite browser.

2. Click the Send button to send a request.

To run the java code client:

1. Change to the <servicemix_home>/examples/cxf-wsdl-first/client
   directory.

2. Run the following command:

     mvn compile exec:java


Viewing the Log Entries
-----------------------
You can view the log entries in the karaf.log file in the
data/log directory of your ServiceMix installation, or by typing
the following command in the ServiceMix console:

  log:display


Changing the Example
--------------------
If you wish to change the code or configuration, just use 'mvn install'
to rebuild the JBI Service Assembly zip, and deploy it as before.
