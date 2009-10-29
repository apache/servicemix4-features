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
package org.apache.servicemix.samples.loanbroker;

import java.util.concurrent.CountDownLatch;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.logicblaze.lingo.jms.Requestor;
import org.logicblaze.lingo.jms.JmsProducerConfig;
import org.logicblaze.lingo.jms.impl.MultiplexingRequestor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * @version $Revision: 666120 $
 */
public class JMSClient implements Runnable {

    private static ConnectionFactory factory;
    private static CountDownLatch latch;
    private static Requestor requestor;

    /**
     * main ...
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Connecting to JMS server.");
        factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Destination inQueue = new ActiveMQQueue("demo.org.servicemix.source");
        Destination outQueue = new ActiveMQQueue("demo.org.servicemix.output" + (int)(1000*Math.random()));
        requestor = MultiplexingRequestor.newInstance(factory, new JmsProducerConfig(), inQueue, outQueue); 

        if (args.length == 0) {
            new JMSClient().run();
        } else {
            int nb = Integer.parseInt(args[0]);
            int th = 30;
            if (args.length > 1) {
                th = Integer.parseInt(args[1]);
            }
            latch = new CountDownLatch(nb);
            ExecutorService threadPool = Executors.newFixedThreadPool(th);
            for (int i = 0; i < nb; i++) {
                threadPool.submit(new JMSClient());
            }
            latch.await();
        }
        System.out.println("Closing.");
        requestor.close();
        System.exit(0);
    }

    public void run() {
        try {
            System.out.println("Sending request.");
            double r = Math.random();

            String request =
                "<getLoanQuoteRequest xmlns='urn:logicblaze:soa:loanbroker'>\n" +
                "  <ssn>102-24532-53254</ssn>\n" +
                "  <amount>" + r * 100000 + "</amount>\n" +
                "  <duration>" + (int) r * 48 + "</duration>\n" +
                "  <score>" + (int) r * 48 + "</score>\n" +
                "  <length>" + (int) r * 48 + "</length>\n" +
                "</getLoanQuoteRequest>";

            TextMessage out = requestor.getSession().createTextMessage(request);

            TextMessage in = (TextMessage) requestor.request(null, out); 
            if (in == null) {
                System.out.println("Response timed out.");
            }
            else {
                System.out.println("Response was: " + in.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (latch != null) {
                latch.countDown();
            }
        }
    }

}
