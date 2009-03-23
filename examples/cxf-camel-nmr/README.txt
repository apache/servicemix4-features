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

Welcome to the ServiceMix cxf camel nmr example
==========================================

This example demonstrates the use of CXF and CAMEL exposed to the ServiceMix
nmr.

Quick steps to install the sample
---------------------------------

Launch the ServiceMix Kernel by running
  bin/servicemix
in the root dir of this distribution.

When inside the console, just run the following commands to install the
example:

  features/addUrl mvn:org.apache.servicemix.nmr/apache-servicemix-nmr/${servicemix.nmr.version}/xml/features
  features/addUrl mvn:org.apache.servicemix.features/apache-servicemix/${version}/xml/features
  features/install examples-cxf-camel-nmr

If you have all the bundles available in your local repo, the installation
of the example will be very fast, otherwise it may take some time to
download everything needed.

Testing the example
-------------------

When the example is installed, periodic soap messages are displayed by
the transform method of the MyTransform class.  These messages are routed
to the CXF endpoint, and the responses are routed to the display method of
the MyTransform class.

Finally, uninstall the examples-camel-nmr feature:
  features/uninstall examples-cxf-camel-nmr

You can also examine the ServiceMix log to see the activity:
  log/display

How does it work?
-----------------

The installation leverages ServiceMix Kernel by installing what's called
'features'. You can see the features definition file using the following
command inside ServiceMix console:

optional/cat mvn:org.apache.servicemix.features/apache-servicemix/${version}/xml/features

The list of available features can be obtained using:

features/list


