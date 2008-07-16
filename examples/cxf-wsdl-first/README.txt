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

Welcome to ServiceMix WSDL design first example
===============================================

This example shows how to expose a service in servicemix-cxf-se, beginning from the
WSDL, and exposing it over servicemix-cxf-bc.

First start a ServiceMix server (if not already started) by running
  bin/servicemix
in the root dir of this distribution.

To run this sample, launch the following commands:
  mvn install

You can deploy the example on ServiceMix 4 in two different ways:
- using hotdeploy: 
   copy the wsdl-first-cxf-sa/target/wsdl-first-cxf-sa-${version}.jar to <servicemix_home>/deploy
- using the ServiceMix console:
   osgi install -s mvn:org.apache.servicemix.examples.cxf-wsdl-first/wsdl-first-cxf-sa/${version}/zip

You can browse the WSDL at
  http://localhost:8092/PersonService?wsdl
  
You can also open the client.html page in a browser
to send a request to the service.

