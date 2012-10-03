/**
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
package org.apache.servicemix.logging.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.logging.service.internal.PaxLoggingEventImpl;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;

import javax.naming.Context;

/**
 * Test cases for {@link JMSAppender}
 */
public class JMSAppenderTest extends CamelTestSupport {

    private static final String BROKER_URL = "vm://test.broker?broker.persistent=false";
    private static final String EVENTS_TOPIC = "Events";

    private JMSAppender appender;

    @Before
    public void setupBrokerAndAppender() throws Exception {
        appender = new JMSAppender();
        appender.setJmsConnectionFactory(new ActiveMQConnectionFactory(BROKER_URL));
        appender.setDestinationName(EVENTS_TOPIC);
        appender.init();
    }

    @Test
    public void testLogstashAppender() throws InterruptedException {
        MockEndpoint events = getMockEndpoint("mock:events");
        events.expectedMessageCount(1);

        appender.doAppend(MockEvents.createInfoEvent());

        assertMockEndpointsSatisfied();
    }

    @Override
    protected Context createJndiContext() throws Exception {
        Context context = super.createJndiContext();
        context.bind("amq", ActiveMQComponent.activeMQComponent(BROKER_URL));
        return context;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("amq:topic://" + EVENTS_TOPIC).to("mock:events");
            }
        };
    }
}
