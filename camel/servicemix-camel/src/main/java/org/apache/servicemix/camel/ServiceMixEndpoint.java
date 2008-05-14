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
package org.apache.servicemix.camel;

import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Producer;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.servicemix.nmr.api.Exchange;

/**
 * Created by IntelliJ IDEA. User: gnodet Date: Sep 19, 2007 Time: 8:54:34 AM To
 * change this template use File | Settings | File Templates.
 * 
 * @org.apache.xbean.XBean element="smxEndpoint"
 */
public class ServiceMixEndpoint extends DefaultEndpoint<ServiceMixExchange> {

	private String endpointName;

	public ServiceMixEndpoint(ServiceMixComponent component, String uri, String endpointName) {
		super(uri, component);
		this.endpointName = endpointName;
	}

	public ServiceMixComponent getComponent() {
		return (ServiceMixComponent) super.getComponent();
	}

	public boolean isSingleton() {
		return true;
	}

	public Producer<ServiceMixExchange> createProducer() throws Exception {
		return new ServiceMixProducer(this);
	}

	public Consumer<ServiceMixExchange> createConsumer(Processor processor) throws Exception {
		return new ServiceMixConsumer(this, processor);
	}

	public ServiceMixExchange createExchange(Exchange exchange) {
		return new ServiceMixExchange(getCamelContext(), getExchangePattern(), exchange);
	}

	public ServiceMixExchange createExchange(ExchangePattern pattern, Exchange exchange) {
		return new ServiceMixExchange(getCamelContext(), pattern, exchange);
	}

	public ServiceMixExchange createExchange(org.apache.servicemix.nmr.api.Message inMessage, Exchange exchange) {
		return new ServiceMixExchange(getCamelContext(), getExchangePattern(), inMessage, exchange);
	}

	public void setEndpointName(String endpointName) {
		this.endpointName = endpointName;
	}

	public String getEndpointName() {
		return endpointName;
	}
}
