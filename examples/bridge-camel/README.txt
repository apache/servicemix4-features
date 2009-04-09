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

Welcome to the ServiceMix Bridge Camel Example
========================================

This example shows a bridge between the HTTP and JMS protocols,
with an XSLT transformation using Camel in between.

First start a ServiceMix server (if not already started) by running
  bin/servicemix
in the root dir of this ditribution.

To start this sample, run:
  mvn install

You can deploy the example on ServiceMix 4 in two different ways:
- using hotdeploy:
   copy the bridge-camel-sa/target/bridge-camel-sa-${version}.zip to
<servicemix_home>/deploy
- using the ServiceMix console:
   osgi/install -s
mvn:org.apache.servicemix.examples.bridge-camel/bridge-camel-sa/${version}/zip

You can then launch the client.html in your favorite browser
and send an HTTP request which will be transformed in a JMS
message.  After the JMS message has been sent to the bridge.output queue
successfully,
you will receive an HTTP STATUS 202 response code from the ESB.
Or you can launch java code client to send the request
cd client; mvn compile exec:java

For more information on running this example please see:
  http://servicemix.apache.org/bridge.html

