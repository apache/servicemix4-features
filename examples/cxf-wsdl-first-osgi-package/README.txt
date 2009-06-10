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

This example is exactly same functionality as cxf-wsdl-first example, but use osgi bundle pakage instead.

First start a ServiceMix server (if not already started) by running
  bin/servicemix
in the root dir of this distribution.

To run this sample, launch the following commands:
  mvn install

When inside the console, just run the following commands to install the
example:

  features/addUrl
mvn:org.apache.servicemix.nmr/apache-servicemix-nmr/${servicemix.nmr.version}/xml/features
  features/addUrl
mvn:org.apache.servicemix.features/apache-servicemix/${version}/xml/features
  features/install examples-cxf-wsdl-first-osgi-package

You can browse the WSDL at
  http://localhost:8092/PersonService?wsdl
  
You can also open the client.html page in a browser
to send a request to the service.
Or you can launch a java client to send request
cd client; mvn compile exec:java

