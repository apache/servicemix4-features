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

Welcome to the Servicemix Loan Broker example
=============================================

This example is based on the EIP book 
(http://www.enterpriseintegrationpatterns.com/ComposedMessagingExample.html).
It leverages the BPEL service engine, JMS binding component and some 
lightweight components provided by ServiceMix.

Prerequisites for Running this Example
--------------------------------------
1. You must have the following installed on your machine:
   - JDK 1.5 or higher.
   - Apache Maven 2.0.9 or higher.

For more information, see the README in the top-level examples directory.

2. Start ServiceMix by running the following command:
   <servicemix_home>/bin/servicemix    (on UNIX)
   <servicemix_home>\bin\servicemix    (on Windows)

Installing ODE
--------------

This example depends on the Apache ODE JBI service engine. This component
has not yet been released with support ServiceMix 4 but a SNAPSHOT is
available.

Install ODE from the ServiceMix Console with the following commands:

   features:install ode

Building and Deploying
----------------------
This example uses the ServiceMix JBI Maven plugin to build the SUs and the SA.
To build the example, run the following command (from the directory that contains
this README):

  mvn install

If all of the required bundles are available in your local Maven repository, 
the example will build quickly. Otherwise it may take some time for Maven
to download everything it needs.

Once complete, you will find the SA, called
loan-broker-sa-${version}.zip, in the loan-broker-sa/target directory
of this example.

You can deploy the SA in two ways:

- Using Hot Deployment
  --------------------
   
  Copy the loan-broker-sa-${version}.zip file to the
  <servicemix_home>/deploy directory.

- Using the ServiceMix Console
  ----------------------------

  Type the following command:

  osgi:install -s mvn:org.apache.servicemix.examples.loan-broker/loan-broker-sa/${version}/zip


Running the Client
------------------
This example provides a simple JMS client. To run the client:

1. Change to the <servicemix_home>/examples/loan-broker-bpel/client directory.

2. Run the following command:

   mvn compile exec:java

Changing the Example
--------------------
If you change the code or configuration in the example, use 'mvn install' to rebuild the
JBI SA zip, and deploy it as described above.
