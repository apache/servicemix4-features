/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.servicemix.cxf.transport.nmr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.AbstractConduit;
import org.apache.cxf.transport.AbstractDestination;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.MessageObserver;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl.EndpointReferenceUtils;
import org.apache.servicemix.nmr.api.Channel;
import org.apache.servicemix.nmr.api.Endpoint;
import org.apache.servicemix.nmr.api.Exchange;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.ServiceMixException;

public class NMRDestination extends AbstractDestination implements Endpoint {
    
    private static final Logger LOG = LogUtils.getL7dLogger(NMRDestination.class);
    private NMR nmr;
    private Channel channel;
    private Map<String, Object> properties;

    public NMRDestination(EndpointInfo info, NMR nmr) {
        super(getTargetReference(info, null), info);
        this.nmr = nmr;
        this.properties = new HashMap<String, Object>();
        String address = info.getAddress();
        if (address != null && address.startsWith("nmr:")) {
            this.properties.put(Endpoint.NAME, address.substring(4, info.getAddress().length()));
        } else {
            this.properties.put(Endpoint.NAME, info.getName().toString());
        }
        
        this.properties.put(Endpoint.SERVICE_NAME, info.getService().getName());
        this.properties.put(Endpoint.INTERFACE_NAME, info.getInterface().getName());
    }

    public void setChannel(Channel dc) {
        this.channel = dc;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    protected Logger getLogger() {
        return LOG;
    }
    
    /**
     * @param inMessage the incoming message
     * @return the inbuilt backchannel
     */
    protected Conduit getInbuiltBackChannel(Message inMessage) {
        return new BackChannelConduit(EndpointReferenceUtils.getAnonymousEndpointReference(),
                                      inMessage);
    }
    
    public void shutdown() {
    }

    public void deactivate() {
        nmr.getEndpointRegistry().unregister(this, properties);
    }

    public void activate()  {
        nmr.getEndpointRegistry().register(this, properties);
    }

    public void process(Exchange exchange) {
        QName opName = exchange.getOperation();
        getLogger().fine("dispatch method: " + opName);

        org.apache.servicemix.nmr.api.Message nm = exchange.getIn();
        try {

            MessageImpl inMessage = new MessageImpl();
            inMessage.put(Exchange.class, exchange);

            final InputStream in = NMRMessageHelper.convertMessageToInputStream(nm.getBody(Source.class));
            inMessage.setContent(InputStream.class, in);
            inMessage.setDestination(this);
            getMessageObserver().onMessage(inMessage);

        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, new org.apache.cxf.common.i18n.Message("ERROR.PREPARE.MESSAGE", getLogger()).toString(), ex);
            throw new ServiceMixException(ex);
        }
    }


    protected class BackChannelConduit extends AbstractConduit {
        
        protected Message inMessage;
        protected NMRDestination nmrDestination;
                
        BackChannelConduit(EndpointReferenceType ref, Message message) {
            super(ref);
            inMessage = message;
        }
        
        /**
         * Register a message observer for incoming messages.
         * 
         * @param observer the observer to notify on receipt of incoming
         */
        public void setMessageObserver(MessageObserver observer) {
            // shouldn't be called for a back channel conduit
        }

        /**
         * Send an outbound message, assumed to contain all the name-value
         * mappings of the corresponding input message (if any). 
         * 
         * @param message the message to be sent.
         */
        public void prepare(Message message) throws IOException {
            // setup the message to be send back
            Channel dc = channel;
            message.put(Exchange.class, inMessage.get(Exchange.class));
            message.setContent(OutputStream.class, new NMRDestinationOutputStream(inMessage, dc));
        }        

        protected Logger getLogger() {
            return LOG;
        }
    }
    
}
