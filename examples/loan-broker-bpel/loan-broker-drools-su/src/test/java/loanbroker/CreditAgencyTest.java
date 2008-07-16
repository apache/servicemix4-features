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
package loanbroker;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.InOut;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import org.apache.servicemix.client.DefaultServiceMixClient;
import org.apache.servicemix.client.ServiceMixClient;
import org.apache.servicemix.drools.DroolsComponent;
import org.apache.servicemix.drools.DroolsEndpoint;
import org.apache.servicemix.jbi.container.JBIContainer;
import org.apache.servicemix.jbi.jaxp.SourceTransformer;
import org.apache.servicemix.jbi.jaxp.StringSource;
import org.apache.servicemix.tck.ReceiverComponent;
import org.springframework.core.io.ClassPathResource;

import junit.framework.TestCase;

public class CreditAgencyTest extends TestCase {

    private JBIContainer jbi;
    private DroolsComponent drools;
    private ServiceMixClient client;
    
    protected void setUp() throws Exception {
        super.setUp();
        jbi = new JBIContainer();
        jbi.setEmbedded(true);
        jbi.init();
        client = new DefaultServiceMixClient(jbi);
    }
    
    protected void tearDown() throws Exception {
        jbi.shutDown();
    }
    
    public void testCreditHistory() throws Exception {
        drools = new DroolsComponent();
        DroolsEndpoint endpoint = new DroolsEndpoint(drools.getServiceUnit(),
                                                     new QName("drools"), "endpoint");
        endpoint.setRuleBaseResource(new ClassPathResource("credit-agency.drl"));
        drools.setEndpoints(new DroolsEndpoint[] {endpoint });
        jbi.activateComponent(drools, "servicemix-drools");
        
        jbi.start();
        
        InOut me = client.createInOutExchange();
        me.setService(new QName("drools"));
        me.setOperation(new QName("urn:logicblaze:soa:creditagency", "getCreditHistoryLength"));
        me.getInMessage().setContent(new StringSource("<getCreditHistoryLengthRequest xmlns='urn:logicblaze:soa:creditagency'><ssn>123456</ssn></getCreditHistoryLengthRequest>"));
        client.sendSync(me);
        Element e = new SourceTransformer().toDOMElement(me.getOutMessage());
        assertEquals("getCreditHistoryLengthResponse", e.getLocalName());
        client.done(me);
        
        Thread.sleep(50);
    }

}
