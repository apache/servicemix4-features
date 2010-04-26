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

import org.apache.camel.ContextTestSupport;
import org.apache.camel.impl.JndiRegistry;
import org.apache.servicemix.executors.ExecutorFactory;
import org.apache.servicemix.executors.impl.ExecutorConfig;
import org.apache.servicemix.executors.impl.ExecutorFactoryImpl;
import org.apache.servicemix.nmr.api.Channel;
import org.apache.servicemix.nmr.api.Exchange;
import org.apache.servicemix.nmr.api.event.ExchangeListener;
import org.apache.servicemix.nmr.api.service.ServiceHelper;
import org.apache.servicemix.nmr.core.ServiceMix;

/**
 * Abstract base class for building NMR component unit tests
 * - the NMR component is available with URI prefix nmr:
 * - a client channel to the NMR can be obtained with the {@link #getChannel()} method
 */
public abstract class AbstractComponentTest extends ContextTestSupport implements ExchangeListener {

    private ServiceMix nmr;
    private ServiceMixComponent component;
    private Channel channel;

    @Override
    protected void setUp() throws Exception {
        nmr = new ServiceMix();
        nmr.setExecutorFactory(createExecutorFactory());
        nmr.init();
        
        nmr.getListenerRegistry().register(this, ServiceHelper.createMap());

        component = new ServiceMixComponent();
        component.setNmr(nmr);

        super.setUp();
    }

    /*
     * Create the ExecutorFactory for the unit test
     * based on the default configuration used in ServiceMix 4
     */
    protected ExecutorFactory createExecutorFactory() {
        ExecutorFactoryImpl factory = new ExecutorFactoryImpl();

        ExecutorConfig config = factory.getDefaultConfig();
        config.setCorePoolSize(1);
        config.setMaximumPoolSize(16);
        config.setQueueSize(0);
        config.setBypassIfSynchronous(true);

        return factory;
    };

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("nmr", component);
        return registry;
    }

    /**
     * Get a client channel to access the NMR used for testing
     *
     * @return the client channel
     */
    protected Channel getChannel() {
        if (channel == null) {
            channel = nmr.createChannel();
        }

        return channel;
    }

    public void exchangeSent(Exchange exchange) {
        // graciously do nothing
    }

    public void exchangeDelivered(Exchange exchange) {
        // graciously do nothing
    }

    public void exchangeFailed(Exchange exchange) {
        // graciously do nothing
    }
}
