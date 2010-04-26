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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.servicemix.nmr.api.Status;

/**
 * Test case for making sure that the component behaves properly if the Camel route is using
 * asynchronous elements (e.g. threads or seda queues)
 */
public class CamelAsyncRouteTest extends AbstractComponentTest {

    private static final String HANDLED_BY_THREAD = "HandledByThread";
    private static final int COUNT = 1000;

    /* Latch to count NMR Done Exchanges */
    private CountDownLatch done;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        done = new CountDownLatch(COUNT);
    }

    public void testCamelThreads() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:threads");
        mock.expectedMessageCount(COUNT);

        getMockEndpoint("mock:sent").expectedMessageCount(COUNT);

        for (int i = 0 ; i < COUNT ; i++) {
            template.asyncSendBody("direct:threads", "Simple message body");
        }

        assertMockEndpointsSatisfied();

        for (Exchange exchange : mock.getExchanges()) {
            Thread thread = exchange.getProperty(HANDLED_BY_THREAD, Thread.class);
            assertTrue("onCompletion should have been called from the Camel 'threads' thread pool",
                       thread.getName().contains("Camel") && thread.getName().contains("Threads"));
        }

        assertTrue("All NMR exchanges should have been marked DONE",
                   done.await(20, TimeUnit.SECONDS));        
    }

    public void testCamelSeda() throws InterruptedException {       
        getMockEndpoint("mock:sent").expectedMessageCount(COUNT);
        getMockEndpoint("mock:seda").expectedMessageCount(COUNT);

        for (int i = 0 ; i < COUNT ; i++) {
            template.asyncSendBody("seda:seda", "Simple message body");
        }

        assertMockEndpointsSatisfied();

        assertTrue("All NMR exchanges should have been marked DONE",
                   done.await(20, TimeUnit.SECONDS));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("direct:threads").to("mock:sent").to("nmr:threads");
                from("nmr:threads")
                    .onCompletion().process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            exchange.setProperty(HANDLED_BY_THREAD, Thread.currentThread());
                        }
                    })
                    .threads(5).to("mock:threads");

                from("seda:seda?concurrentConsumers=10").to("mock:sent").to("nmr:seda");
                from("nmr:seda").to("seda:seda-internal?waitForTaskToComplete=Never");
                from("seda:seda-internal").to("mock:seda");

            }
        };
    }

    @Override
    public void exchangeDelivered(org.apache.servicemix.nmr.api.Exchange exchange) {
        if (exchange.getStatus().equals(Status.Done)) {
            done.countDown();
        }
    }
}
