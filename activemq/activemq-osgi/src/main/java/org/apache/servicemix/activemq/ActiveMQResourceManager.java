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
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import javax.jms.ConnectionFactory;
import javax.transaction.TransactionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ActiveMQResourceManager {
    
    private static final Log LOGGER = LogFactory.getLog(ActiveMQResourceManager.class);
    
    private String resourceName;
    
    private TransactionManager transactionManager;
    
    private ConnectionFactory connectionFactory;
    
    public void recoverResource() {
        try {
            Class recoveryClass = getClass().getClassLoader().loadClass("org.apache.servicemix.activemq.Recovery");
            Method mth = recoveryClass.getMethod("recover", ActiveMQResourceManager.class);
            Object res = mth.invoke(null, this);
            if (!Boolean.TRUE.equals(res)) {
                LOGGER.info("Resource manager is unrecoverable");
            }
        } catch (ClassNotFoundException e) {
            LOGGER.info("Resource manager is unrecoverable due to missing classes: " + e);
        } catch (NoClassDefFoundError e) {
            LOGGER.info("Resource manager is unrecoverable due to missing classes: " + e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            LOGGER.warn("Error while recovering resource manager", e);
        }
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
