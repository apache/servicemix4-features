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

import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.Exchange;
import org.apache.servicemix.nmr.api.Channel;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.Pattern;
import org.apache.servicemix.nmr.api.Status;
import org.apache.servicemix.nmr.api.service.ServiceHelper;

/**
 * A {@link Producer} that receives exchanges from the ServiceMix NMR and sends {@link org.apache.camel.Exchange}s into the Camel route
 */
public class ServiceMixProducer extends DefaultProducer {
	
    private static final String OPERATION_NAME = "operationName"; 
    private Channel client;

    public ServiceMixProducer(ServiceMixEndpoint endpoint) {
        super(endpoint);
    }

    public ServiceMixEndpoint getEndpoint() {
        return (ServiceMixEndpoint) super.getEndpoint();
    }

    public void process(Exchange exchange) throws Exception {

        NMR nmr = getEndpoint().getComponent().getNmr();

        org.apache.servicemix.nmr.api.Exchange e 
        	= getEndpoint().getComponent().getBinding().populateNmrExchangeFromCamelExchange(exchange, client);
            
        try {
            e.setTarget(nmr.getEndpointRegistry().lookup(
                ServiceHelper.createMap(org.apache.servicemix.nmr.api.Endpoint.NAME, 
                getEndpoint().getEndpointName())));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
                       
        client.sendSync(e);
                
        handleResponse(exchange, client, e);
    }

	private void handleResponse(Exchange exchange, Channel client,
			org.apache.servicemix.nmr.api.Exchange e) {
		if (e.getPattern() != Pattern.InOnly) {
            if (e.getError() != null) {
                exchange.setException(e.getError());
            } else {
            	exchange.getProperties().putAll(e.getProperties());
            	if (e.getFault().getBody() != null) {
                    exchange.getOut().setFault(true);
                    getEndpoint().getComponent().getBinding().copyNmrMessageToCamelMessage(e.getFault(), exchange.getOut());
            	} else {
            		getEndpoint().getComponent().getBinding().copyNmrMessageToCamelMessage(e.getOut(), exchange.getOut());
                }
            	e.setStatus(Status.Done);
            	client.send(e);
            }
            	
    	}
	}

    @Override
    protected void doStart() throws Exception {
        NMR nmr = getEndpoint().getComponent().getNmr();
        client = nmr.createChannel();
    }

    @Override
    protected void doStop() throws Exception {
        client.close();
        client = null;
    }
    
}
