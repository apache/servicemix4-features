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

Welcome to the ServiceMix camel nmr example
==========================================

This example demonstrates using Apache Camel to deploy EIP routes in
Servicemix.

Quick steps to install the sample
---------------------------------

Launch the ServiceMix Kernel by running
  bin/servicemix
in the root dir of this distribution.

When inside the console, if you haven't already done so, addUrls for the
nmr and kernel features:
  features/addUrl mvn:org.apache.servicemix.nmr/apache-servicemix-nmr/${servicemix.nmr.version}/xml/features
  features/addUrl mvn:org.apache.servicemix.features/apache-servicemix/${version}/xml/features

Next install the examples-camel-nmr feature:
  features/install examples-camel-nmr

If you have all the bundles available in your local repo, the installation
of the example will be very fast, otherwise it may take some time to
download everything needed.

Testing the example
-------------------

Once the example feature is installed, periodic events are routed to the
NMR to the transform method of the MyTransform class and finally to the
ExampleRouter log endpoint.  The transform method will print the messages
to the console: 

>>>> MyTransform set body:  Tue Jun 10 16:56:47 NDT 2008

You can view the route configuration in META-INF/spring/beans.xml.

Finally, uninstall the examples-camel-nmr feature:
  features/uninstall examples-camel-nmr

As well, you can view the log entries in the ServiceMix log:
  log d

How does it work?
-----------------

The installation leverages ServiceMix Kernel by installing what's called
'features'. You can see the features definition file using the following
command inside the ServiceMix console:

optional/cat mvn:org.apache.servicemix.features/apache-servicemix/${version}/xml/features

The list of available features can be obtained using:

features/list


