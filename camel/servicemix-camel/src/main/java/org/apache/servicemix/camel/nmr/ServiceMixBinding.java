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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.jbi.messaging.MessagingException;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.apache.servicemix.nmr.api.Pattern;

/**
 * The binding object will help us to deal with copying between the NMR exchange and camel exchange
 */
public class ServiceMixBinding {
    public static final String NMR_MESSAGE = "nmrMessage";
    public static final String NMR_EXCHANGE = "nmrExchange";
    public static final String NMR_OPERATION = "nmrOperation";
    
    public void copyCamelMessageToNmrMessage(org.apache.servicemix.nmr.api.Message nmrMessage, Message camelMessage) throws MessagingException {
        nmrMessage.setBody(camelMessage.getBody());
        nmrMessage.getHeaders().clear();
        addNmrHeaders(nmrMessage, camelMessage);
        nmrMessage.getAttachments().clear();
        nmrMessage.getAttachments().putAll(camelMessage.getAttachments());
        addSecuritySubject(nmrMessage, camelMessage);
    }

    public void copyNmrMessageToCamelMessage(org.apache.servicemix.nmr.api.Message nmrMessage, Message camelMessage) {
        camelMessage.setBody(nmrMessage.getBody());
        camelMessage.setHeader(NMR_MESSAGE, nmrMessage);
        camelMessage.getHeaders().putAll(nmrMessage.getHeaders());
        addCamelAttachments(nmrMessage, camelMessage);        
    }
    
    public Exchange populateCamelExchangeFromNmrExchange(CamelContext context, org.apache.servicemix.nmr.api.Exchange nmrExchange) {
        Exchange answer = new DefaultExchange(context);
        answer.setPattern(ExchangePattern.fromWsdlUri(nmrExchange.getPattern().toString()));
        
        // copy the nmrExchange's properties
        answer.getProperties().putAll(nmrExchange.getProperties());
        
        org.apache.servicemix.nmr.api.Message inMessage = nmrExchange.getIn();
        if (inMessage != null) {
            Message message = new DefaultMessage();
            copyNmrMessageToCamelMessage(inMessage, message);
            answer.setIn(message);
        }
        
        answer.setProperty(NMR_EXCHANGE, nmrExchange);
        if (nmrExchange != null && nmrExchange.getOperation() != null) {
            answer.setProperty(NMR_OPERATION, nmrExchange.getOperation().toString());
        }
        
        if (nmrExchange.getPattern() != Pattern.InOnly && nmrExchange.getOut(false)!= null) {
            Message message = new DefaultMessage();
            copyNmrMessageToCamelMessage(nmrExchange.getOut(), message);
            answer.setOut(message);
        } 
        
        if (nmrExchange.getFault(false) != null) {
            Message message = new DefaultMessage();
            copyNmrMessageToCamelMessage(nmrExchange.getFault(), message);
            answer.setOut(message);
            answer.getOut().setFault(true);
        }  
        return answer;
    }
    
    public org.apache.servicemix.nmr.api.Exchange detachExchange(Exchange camelExchange) throws MessagingException {
        org.apache.servicemix.nmr.api.Exchange nmrExchange = 
            (org.apache.servicemix.nmr.api.Exchange) camelExchange.removeProperty(NMR_EXCHANGE);
        if (nmrExchange == null) {
            return null;
        }
        // copy back the Exchange properties
        nmrExchange.getProperties().putAll(camelExchange.getProperties());
        // set the normalized message back
        org.apache.servicemix.nmr.api.Message inMessage = nmrExchange.getIn();
        copyCamelMessageToNmrMessage(inMessage, camelExchange.getIn());
        // deal with the out message
        if (camelExchange.hasOut()) {
            if (camelExchange.isFailed()) {
                if (camelExchange.getException() != null) {
                    nmrExchange.setError(camelExchange.getException());
                } else {
                    org.apache.servicemix.nmr.api.Message fault = nmrExchange.getFault(true);
                    fault.setBody(camelExchange.getOut().getBody());
                    
                }
            } else {
               if (nmrExchange.getPattern() != Pattern.InOnly) {
                   org.apache.servicemix.nmr.api.Message outMessage = nmrExchange.getOut(true);
                   copyCamelMessageToNmrMessage(outMessage, camelExchange.getOut());
               }    
            }
        }
        return nmrExchange;
    }
    
    @SuppressWarnings("unchecked")
    protected void addNmrHeaders(org.apache.servicemix.nmr.api.Message nmrMessage, Message camelMessage) {
        // get headers from the Camel in message
        Set<Map.Entry<String, Object>> entries = camelMessage.getHeaders().entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            // skip the camel header's normalized Message
            if (entry.getKey().equals(NMR_MESSAGE)) {
                continue;
            }
            //check if value is Serializable, and if value is Map or collection,
            //just exclude it since the entry of it may not be Serializable as well
            if (entry.getValue() instanceof Serializable
                    && !(entry.getValue() instanceof Map)
                    && !(entry.getValue() instanceof Collection)) {
                nmrMessage.setHeader(entry.getKey(), entry.getValue());
            }
        }
       
    }
  
    protected void addCamelAttachments(org.apache.servicemix.nmr.api.Message nmrMessage, Message camelMessage) {
        Set<String> names = nmrMessage.getAttachments().keySet();
        for (String name : names) {
            if (nmrMessage.getAttachment(name) instanceof DataHandler) {
                DataHandler dataHandler = (DataHandler) nmrMessage.getAttachment(name);             
                camelMessage.addAttachment(name, dataHandler);
            }
        }
        
    }
    
    protected void addSecuritySubject(org.apache.servicemix.nmr.api.Message nmrMessage, Message camelMessage) {
        org.apache.servicemix.nmr.api.Message from = getNmrMessage(camelMessage);
        if (from != null) {
            // copy the security subject
            nmrMessage.setSecuritySubject(from.getSecuritySubject());
        }       
                
   }
        
    protected org.apache.servicemix.nmr.api.Message getNmrMessage(Message message) {
        if (message.getHeader(NMR_MESSAGE) != null) {
            return (org.apache.servicemix.nmr.api.Message)message.getHeader(NMR_MESSAGE);
        }
        return null;
    }
    
    
}
