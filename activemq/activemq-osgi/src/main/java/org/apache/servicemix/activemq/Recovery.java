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

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.geronimo.transaction.manager.NamedXAResource;
import org.apache.geronimo.transaction.manager.RecoverableTransactionManager;
import org.apache.geronimo.transaction.manager.WrapperNamedXAResource;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Jan 21, 2008
 * Time: 10:30:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class Recovery {

    public static boolean isRecoverable(ActiveMQResourceManager rm) {
        return  rm.getConnectionFactory() instanceof ActiveMQConnectionFactory &&
                rm.getTransactionManager() instanceof RecoverableTransactionManager &&
                rm.getResourceName() != null && !"".equals(rm.getResourceName());
    }

    public static boolean recover(ActiveMQResourceManager rm) throws IOException {
        if (isRecoverable(rm)) {
            try {
                ActiveMQConnectionFactory connFactory = (ActiveMQConnectionFactory) rm.getConnectionFactory();
                ActiveMQConnection activeConn = (ActiveMQConnection)connFactory.createConnection();
                ActiveMQSession session = (ActiveMQSession)activeConn.createSession(true, Session.SESSION_TRANSACTED);
                NamedXAResource namedXaResource = new WrapperNamedXAResource(session.getTransactionContext(), rm.getResourceName());

                RecoverableTransactionManager rtxManager = (RecoverableTransactionManager) rm.getTransactionManager();
                rtxManager.recoverResourceManager(namedXaResource);
                return true;
            } catch (JMSException e) {
              throw IOExceptionSupport.create(e);
            }
        } else {
            return false;
        }
    }
}
