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
package org.apache.servicemix.logging;

import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JMSAppender implements PaxAppender {

    private static final transient Logger LOG = LoggerFactory.getLogger(JMSAppender.class);

    private ConnectionFactory jmsConnectionFactory;
    private Connection connection;
    private Session session;
    private MessageProducer publisher;
    private Topic topic;
    private String destinationName;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public void init() {
        /*
        * Create connection. Create session from connection; false means
        * session is not transacted.
        * Finally, close connection.
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
            StringBuilder writer = new StringBuilder();

            writer.append("Error");
            writer.append(",\n  \"timestamp\" : " + formatDate(paxLoggingEvent.getTimeStamp()));
            writer.append(",\n  \"level\" : " + paxLoggingEvent.getLevel().toString());
            writer.append(",\n  \"logger\" : " + paxLoggingEvent.getLoggerName());
            writer.append(",\n  \"thread\" : " + paxLoggingEvent.getThreadName());
            writer.append(",\n  \"message\" : " + paxLoggingEvent.getMessage());

            String[] throwable = paxLoggingEvent.getThrowableStrRep();
            if (throwable != null) {
                writer.append(",\n  \"exception\" : [");
                for (int i = 0; i < throwable.length; i++) {
                    if (i != 0)
                        writer.append(", " + throwable[i]);
                }
                writer.append("]");
            }

            writer.append(",\n  \"properties\" : { ");
            boolean first = true;
            for (Object key : paxLoggingEvent.getProperties().keySet()) {
                if (first) {
                    first = false;
                } else {
                    writer.append(", ");
                }
                writer.append("key : " + key.toString());
                writer.append(": " + paxLoggingEvent.getProperties().get(key).toString());
            }
            writer.append(" }");
            writer.append("\n}");

            // Send message to the destination
            TextMessage message = session.createTextMessage();
            message.setText(writer.toString());
            publisher.send(message);

            // System.out.println(">> Message created : " + writer.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String formatDate(long timestamp) {
        return simpleDateFormat.format(new Date(timestamp));
    }

    public void setJmsConnectionFactory(ConnectionFactory jmsConnectionFactory) {
        this.jmsConnectionFactory = jmsConnectionFactory;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

}
