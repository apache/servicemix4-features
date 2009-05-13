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

Welcome to the ServiceMix cxf JAX-RS example
============================================

This example leverages CXF and Spring-DM to create a RESTful web service, 
and then expose it through the OSGi HTTP Service.

Quick steps to install the sample
---------------------------------

Launch the ServiceMix Kernel by running

  bin/servicemix

in the root dir of this distribution.

When inside the console, just run the following commands to install the
example:

  features/install examples-cxf-jaxrs

If you have all the bundles available in your local repo, the installation
of the example will be very fast, otherwise it may take some time to
download everything needed.

Testing the example
-------------------

When the feature is installed, output for publishing the cxf endpoint
is displayed to the console.

Now, just open your browser and go to the following url:

  http://localhost:8080/cxf/crm/customerservice/customers/123

It should display an XML representation for customer 123 (if you use Safari, 
make sure to right click the window and select 'Show Source', else the page
will be blank).

Or you can also test it from ServiceMix console using:

  utils/cat http://localhost:8080/cxf/crm/customerservice/customers/123

Or you can launch a programmatic Java client via:

  mvn compile exec:java

which makes a sequence of RESTful invocations and displays the results.

Alternatively, you can use a command line utility such as curl or wget to make 
the invocations. Here are some example usages with curl, run from 
features/<branch>/examples/cxf-jaxrs:

# Create a customer
#
#
curl -X POST -T src/main/resources/org/apache/servicemix/examples/cxf/jaxrs/client/add_customer.xml -H "Content-Type: text/xml" http://localhost:8080/cxf/crm/customerservice/customers

# Retrieve the customer instance with id 123
#
curl http://localhost:8080/cxf/crm/customerservice/customers/123

# Update the customer instance with id 123
#
curl -X PUT -T src/main/resources/org/apache/servicemix/examples/cxf/jaxrs/client/update_customer.xml -H "Content-Type: text/xml" http://localhost:8080/cxf/crm/customerservice/customers

# Delete the customer instance with id 123
#
curl -X DELETE http://localhost:8079/cxf/crm/customerservice/customers/123
