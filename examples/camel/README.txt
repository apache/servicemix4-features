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

Welcome to the ServiceMix Camel Example
=======================================

This example shows how to use Apache Camel to deploy EIP routes

You can run this example using an embedded ServiceMix install via
  cd camel-sa
  mvn jbi:servicemix

Or if you prefer you can start a ServiceMix server (if not already started) by running
  bin/servicemix
in the root dir of this ditribution.

To start this sample, run:
  mvn install

You can deploy the example on ServiceMix 4 in two different ways:
- using hotdeploy: 
   copy the camel-sa/target/camel-sa-${version}.zip to <servicemix_home>/deploy
- using the ServiceMix console:
   osgi/install -s mvn:org.apache.servicemix.examples.camel/camel-sa/${version}/zip

When the example is deployed, use 'log/d' on the ServiceMix console to see the logged messages
  Exchange[BodyType:String, Body:Hello World!]
  
For more information on running this example please see:
  http://servicemix.apache.org/camel-example.html

