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
package org.apache.servicemix.activemq;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.transaction.TransactionManager;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geronimo.transaction.manager.NamedXAResource;
import org.apache.geronimo.transaction.manager.RecoverableTransactionManager;
import org.apache.geronimo.transaction.manager.WrapperNamedXAResource;


public class ActiveMQResourceManager {
    
    private static final Log LOGGER = LogFactory.getLog(ActiveMQResourceManager.class);
    
    private String resourceName;
    
    private TransactionManager transactionManager;
    
    private ConnectionFactory connectionFactory;
    
    public void recoverResource() {        
        if (isRecoverable()) {
            try {
                ActiveMQConnectionFactory connFactory = (ActiveMQConnectionFactory)connectionFactory;
                ActiveMQConnection activeConn = (ActiveMQConnection)connFactory.createConnection();
                ActiveMQSession session = (ActiveMQSession)activeConn.createSession(true, Session.SESSION_TRANSACTED);
                NamedXAResource namedXaResource = new WrapperNamedXAResource(session.getTransactionContext(), resourceName);
                
                RecoverableTransactionManager rtxManager = (RecoverableTransactionManager) transactionManager;
                rtxManager.recoverResourceManager(namedXaResource);
                
            } catch (JMSException e) {
              IOExceptionSupport.create(e);
            }
        } else {
            LOGGER.warn("The ActiveMQResourceManager did not recover resource since it is unRecoverable");
        }
    }

    private boolean isRecoverable() {
        return  connectionFactory instanceof ActiveMQConnectionFactory && 
                transactionManager instanceof RecoverableTransactionManager && 
                resourceName != null && !"".equals(resourceName);
    }
    
    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
    

}
