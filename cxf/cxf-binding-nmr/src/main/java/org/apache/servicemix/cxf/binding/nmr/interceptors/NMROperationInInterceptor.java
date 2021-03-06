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
package org.apache.servicemix.cxf.binding.nmr.interceptors;

import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.common.i18n.Message;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.servicemix.cxf.binding.nmr.NMRMessage;

public class NMROperationInInterceptor extends AbstractPhaseInterceptor<NMRMessage> {

    private static final Logger LOG = LogUtils.getL7dLogger(NMROperationInInterceptor.class);

    private static final ResourceBundle BUNDLE = LOG.getResourceBundle();

    public NMROperationInInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    public void handleMessage(NMRMessage message) throws Fault {
        Exchange ex = message.getExchange();
        Endpoint ep = ex.get(Endpoint.class);
        BindingOperationInfo boi = ex.get(BindingOperationInfo.class);
        if (boi == null && message.getNmrExchange()!= null 
                && message.getNmrExchange().getOperation() != null) {
            BindingInfo service = ep.getEndpointInfo().getBinding();
            boi = getBindingOperationInfo(service, message.getNmrExchange().getOperation());
            if (boi == null) {
                throw new Fault(new Message("UNKNOWN_OPERATION", BUNDLE, 
                        message.getNmrExchange().getOperation().toString()));
            }
            ex.put(BindingOperationInfo.class, boi);
            ex.put(OperationInfo.class, boi.getOperationInfo());
            ex.setOneWay(boi.getOperationInfo().isOneWay());
            message.put(MessageInfo.class, boi.getInput().getMessageInfo());
        }
    }

    protected BindingOperationInfo getBindingOperationInfo(BindingInfo service, QName operation) {
        return service.getOperation(operation);
    }

}
