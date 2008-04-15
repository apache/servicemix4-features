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

import org.apache.camel.Message;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultMessage;
import org.apache.camel.util.UuidGenerator;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Sep 19, 2007
 * Time: 9:06:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceMixMessage extends DefaultMessage {

    private static final UuidGenerator DEFALT_ID_GENERATOR = new UuidGenerator();

    private ServiceMixExchange exchange;
    private org.apache.servicemix.nmr.api.Message message;
    private String messageId = DEFALT_ID_GENERATOR.generateId();

    public ServiceMixMessage(ServiceMixExchange exchange, org.apache.servicemix.nmr.api.Message message) {
    	this.exchange = exchange;
        this.message = message;
    }
    
    public ServiceMixMessage(org.apache.servicemix.nmr.api.Message message) {
    	this.message = message;
    }
    
    public String getMessageId() {
        return messageId;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public Object getHeader(String name) {
        return message.getHeader(name);
    }

    public <T> T getHeader(Class<T> type) {
        return message.getHeader(type);
    }

    public <T> T getHeader(String name, Class<T> type) {
        return message.getHeader(name, type);
    }

    public void setHeader(String name, Object value) {
        message.setHeader(name, value);
    }

    public Object removeHeader(String name) {
        return message.removeHeader(name);
    }

    public <T> void setHeader(Class<T> type, T value) {
        message.setHeader(type, value);
    }

    public Map<String, Object> getHeaders() {
        return message.getHeaders();
    }

    public void setHeaders(Map<String, Object> headers) {
        message.setHeaders(headers);
    }

    public Object getBody() {
        return message.getBody();
    }

    public <T> T getBody(Class<T> type) {
        return message.getBody(type);
    }

    public void setBody(Object content) {
        message.setBody(content);
    }

    public <T> void setBody(Object content, Class<T> type) {
        message.setBody(content, type);
    }

    public Message copy() {
        Message msg = new ServiceMixMessage(null, message.copy());
        msg.setMessageId(getMessageId());
        return msg;
    }

    public void copyFrom(Message message) {
        setMessageId(message.getMessageId());
        setBody(message.getBody());
        setHeaders(message.getHeaders());
    }

}
