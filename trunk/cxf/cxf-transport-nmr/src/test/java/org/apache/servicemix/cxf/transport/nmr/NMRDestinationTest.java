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

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.transport.MessageObserver;
import org.apache.servicemix.nmr.api.Channel;
import org.apache.servicemix.nmr.api.EndpointRegistry;
import org.apache.servicemix.nmr.api.Status;
import org.easymock.EasyMock;


import org.junit.Test;

public class NMRDestinationTest extends AbstractJBITest {
    static final Logger LOG = LogUtils.getLogger(NMRDestinationTest.class);
    @Test
    public void testDestination() throws Exception {
        LOG.info("JBI destination test");
    }
    
    @Test
    public void testOutputStreamSubstitutionDoesntCauseExceptionInDoClose() throws Exception {
        //Create enough of the object structure to get through the code.
        org.apache.servicemix.nmr.api.Message normalizedMessage = control.createMock(org.apache.servicemix.nmr.api.Message.class);
        Channel channel = control.createMock(Channel.class);
        Exchange exchange = new ExchangeImpl();
        exchange.setOneWay(false);
        Message message = new MessageImpl();
        message.setExchange(exchange);
        
        
        org.apache.servicemix.nmr.api.Exchange messageExchange = control.createMock(org.apache.servicemix.nmr.api.Exchange.class);
        EasyMock.expect(messageExchange.getOut()).andReturn(normalizedMessage).times(2);
        message.put(org.apache.servicemix.nmr.api.Exchange.class, messageExchange);
        channel.send(messageExchange);
        EasyMock.replay(channel);
        
        NMRDestinationOutputStream jbiOS = new NMRDestinationOutputStream(message, new MessageImpl(), channel);
        
        //Create array of more than what is in threshold in CachedOutputStream, 
        //though the threshold in CachedOutputStream should be made protected 
        //perhaps so it can be referenced here in case it ever changes.
        int targetLength = 64 * 1025;
        StringBuffer sb = new StringBuffer();
        sb.append("<root>");
        while (sb.length() < targetLength) {
            sb.append("<dummy>some xml</dummy>");
        }
        sb.append("</root>");
        byte[] testBytes = sb.toString().getBytes();
        
        jbiOS.write(testBytes);        
        jbiOS.doClose();
        
        //Verify send method was called.
        EasyMock.verify(channel);
    }
    
    
    @Test
    public void testNMRDestination() throws Exception {
        EndpointInfo ei = new EndpointInfo();
        ei.setAddress("nmr://dumy");
        ei.setName(new QName("http://test", "endpoint"));
        ServiceInfo si = new ServiceInfo();
        si.setName(new QName("http://test", "service"));
        InterfaceInfo interInfo = new InterfaceInfo(si, new QName("http://test", "interface"));
        si.setInterface(interInfo);
        ei.setService(si);
        org.apache.servicemix.nmr.api.NMR nmr = control.createMock(org.apache.servicemix.nmr.api.NMR.class);
        nmrTransportFactory.setNmr(nmr);
        NMRDestination destination = (NMRDestination) nmrTransportFactory.getDestination(ei);
        assertNotNull(destination);
        String destName = ei.getService().getName().toString()
        + ei.getInterface().getName().toString();
        try {
            nmrTransportFactory.putDestination(destName, destination);
            fail();
        } catch (Exception e) {
            //should catch exception here since try put duplicated destination  
        }
        assertEquals(destination, nmrTransportFactory.getDestination(destName));
        nmrTransportFactory.removeDestination(destName);
        nmrTransportFactory.putDestination(destName, destination);
        
        org.apache.servicemix.nmr.api.Exchange xchg = control.createMock(org.apache.servicemix.nmr.api.Exchange.class);
        
        org.apache.servicemix.nmr.api.Message inMsg = control.createMock(org.apache.servicemix.nmr.api.Message.class);
        EasyMock.expect(xchg.getStatus()).andReturn(Status.Active);
        EasyMock.expect(xchg.getIn()).andReturn(inMsg);
        EasyMock.expect(inMsg.getAttachments()).andReturn(new HashMap<String, Object>());
        EasyMock.expect(inMsg.getHeaders()).andReturn(new HashMap<String, Object>());
        Source source = new StreamSource(new ByteArrayInputStream(
                            "<message>TestHelloWorld</message>".getBytes()));
        EasyMock.expect(inMsg.getBody(Source.class)).andReturn(source);
        EndpointRegistry endpoints = control.createMock(EndpointRegistry.class);
        EasyMock.expect(nmr.getEndpointRegistry()).andReturn(endpoints);
        EasyMock.expect(nmrTransportFactory.getNmr().getEndpointRegistry()).andReturn(endpoints);
        control.replay();
        observer = new MessageObserver() {
            public void onMessage(Message m) {                    
                inMessage = m;
            }
        };
        destination.setMessageObserver(observer);
        destination.process(xchg);
        assertNotNull(inMessage);
    }
}
