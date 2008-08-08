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

Welcome to the ServiceMix simple xml deployment examples
========================================================

This example leverages ServiceMix 4 support for deploying simple XML files to add new endpoints.

Quick steps to install the sample
---------------------------------

Launch the ServiceMix Kernel by running
  bin/servicemix
in the root dir of this distribution.

Just copy any of the XML files in this directory to the /deploy directory in the root dir of this distribution 
to deploy the sample.


Testing the example
-------------------

When the groovy.xml file is copied to the deploy directory, you should see output in the console:

Starting JSR-223 groovy processor
org.apache.servicemix.jbi.runtime.impl.InOnlyImpl@41a330e4
Hello, I got an input message <?xml version="1.0" encoding="UTF-8" standalone="no"?><timer><name>{http://servicemix.apache.org/examples/groovy}service:endpoint</name><group>DEFAULT</group><fullname>DEFAULT.{http://servicemix.apache.org/examples/groovy}service:endpoint</fullname><description/><fireTime>Fri Aug 08 13:50:16 CEST 2008</fireTime></timer>


When the quartz.xml file is copied to the deploy directory, you can use the 'log d' command in the console to see this output in the log files:

14:04:53,709 | INFO  | x-camel-thread-3 | test                             | rg.apache.camel.processor.Logger   88 | Exchange[null]


How does it work?
-----------------

The installation leverages ServiceMix Kernel by installing a plain Spring XML file.
The JBI endpoints in the Spring XML file will automatically be registered in the NMR.  
The quartz.xml file also shows how you can deploy a Camel route together with JBI endpoints from the same XML file.

Both files have a servicemix-quartz endpoint that sends a new message exchange on a regular interval.
In groovy.xml, this exchange is handled by a Groovy script through the servicemix-scripting SE.
In quartz.xml, the exchange is sent directly to a Camel route.
