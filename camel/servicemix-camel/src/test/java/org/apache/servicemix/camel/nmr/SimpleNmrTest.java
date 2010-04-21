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
package org.apache.servicemix.camel.nmr;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;

/**
 * A very basic NMR test, just testing if the Exchange can flow through the NMR
 * from one Camel route to the next one
 */
public class SimpleNmrTest extends AbstractComponentTest {

    public void testSimpleExchange() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:simple");
        mock.expectedBodiesReceived("Simple message body");

        template.sendBody("direct:simple", "Simple message body");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("direct:simple").to("nmr:simple");
                from("nmr:simple").to("mock:simple");
            }
        };
    }
}
