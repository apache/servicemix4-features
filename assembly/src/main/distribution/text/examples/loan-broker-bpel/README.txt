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

This example is based on the great EIP book 
(http://www.enterpriseintegrationpatterns.com/ComposedMessagingExample.html).
It leverages the BPEL service engine, JMS binding component and some 
lightweight components provided by ServiceMix.

First start a ServiceMix server (if not already started) by running
  bin/servicemix
in the root dir of this ditribution.

This example depends on Apache Ode JBI Service Engine which has not been
released yet.  You will need to build it yourself (more informations at
http://incubator.apache.org/ode/getting-ode.html).  You will need to copy
the Service Engine installer to the install directory of this distribution
prior to the following instructions.

To run this sample, launch the following commands:
  mvn install

You can deploy the example on ServiceMix 4 in two different ways:
- using hotdeploy: 
   copy the loan-broker-sa/target/loan-broker-sa-3.3-SNAPSHOT.jar to <servicemix_home>/deploy
- using the ServiceMix console:
   osgi install -s mvn:org.apache.servicemix.samples.loan-broker/loan-broker-sa/3.3-SNAPSHOT/zip

To test this sample, launch the following commands:
  ant run

For more information on this example please see
  http://servicemix.apache.org/loan-broker-bpel.html
