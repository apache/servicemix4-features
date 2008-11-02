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
import org.apache.servicemix.nmr.api.service.ServiceHelper;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Sep 19, 2007
 * Time: 8:59:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceMixProducer extends DefaultProducer<ServiceMixExchange> {
	
	private static final String OPERATION_NAME = "operationName"; 

    public ServiceMixProducer(ServiceMixEndpoint endpoint) {
        super(endpoint);
    }

    public ServiceMixEndpoint getEndpoint() {
        return (ServiceMixEndpoint) super.getEndpoint();
    }

    public void process(Exchange exchange) throws Exception {
    	
    	NMR nmr = getEndpoint().getComponent().getNmr();
    	Channel client = nmr.createChannel();
    	
        org.apache.servicemix.nmr.api.Exchange e = client.createExchange(
        		Pattern.fromWsdlUri(exchange.getPattern().getWsdlUri()));
        
        try {
        	e.setTarget(nmr.getEndpointRegistry().lookup(
        		ServiceHelper.createMap(org.apache.servicemix.nmr.api.Endpoint.NAME, 
        				getEndpoint().getEndpointName())));
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        e.getIn().setBody(exchange.getIn().getBody());
        e.getIn().setHeader(OPERATION_NAME, 
        		exchange.getIn().getHeader(OPERATION_NAME));
                
        client.sendSync(e);
        if (e.getPattern() != Pattern.InOnly) {
        	if (e.getError() != null) {
        		exchange.setException(e.getError());
        	} else if (e.getFault().getBody() != null) {
        		exchange.getFault().setBody(e.getFault().getBody());
        	} else {
        		exchange.getOut().setBody(e.getOut().getBody());
    		}
    	}
    }
    
}
