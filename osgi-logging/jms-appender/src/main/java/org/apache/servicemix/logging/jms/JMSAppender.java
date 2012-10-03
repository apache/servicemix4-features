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

import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class JMSAppender implements PaxAppender {

    private static final transient Logger LOG = LoggerFactory.getLogger(JMSAppender.class);

    private static final String DEFAULT_EVENT_FORMAT = "default";
    private static final String LOGSTASH_EVENT_FORMAT = "logstash";


    private ConnectionFactory jmsConnectionFactory;
    private Connection connection;
    private Session session;
    private MessageProducer publisher;
    private Topic topic;
    private String destinationName;

    private LoggingEventFormat format = new DefaultLoggingEventFormat();



    public void init() {
        /*
        * Create connection. Create session from connection; false means
        * session is not transacted.
        */
        try {
            connection = jmsConnectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic(destinationName);
            publisher = session.createProducer(topic);
            publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            LOG.debug("Connection created with ActiveMQ for JMS Pax Appender.");

        } catch (JMSException e) {
            LOG.error(e.getMessage());
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                LOG.debug("Connection closed with ActiveMQ for JMS Pax Appender.");
            } catch (JMSException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    public void doAppend(PaxLoggingEvent paxLoggingEvent) {
        try {
            // Send message to the destination
            TextMessage message = session.createTextMessage();
            message.setText(format.toString(paxLoggingEvent));
            publisher.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void setJmsConnectionFactory(ConnectionFactory jmsConnectionFactory) {
        this.jmsConnectionFactory = jmsConnectionFactory;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public void setFormat(String name) {
        if (LOGSTASH_EVENT_FORMAT.equals(name)) {
            format = new LogstashEventFormat();
        } else {
            format = new DefaultLoggingEventFormat();
        }
    }
}
