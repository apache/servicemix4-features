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
package org.apache.servicemix.ejb3.deployer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.transaction.TransactionManager;

import org.apache.openejb.OpenEJB;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.assembler.classic.OpenEjbConfiguration;
import org.apache.openejb.assembler.classic.SecurityServiceInfo;
import org.apache.openejb.assembler.classic.TransactionServiceInfo;
import org.apache.openejb.assembler.classic.ProxyFactoryInfo;
import org.apache.openejb.assembler.dynamic.PassthroughFactory;
import org.apache.openejb.core.ServerFederation;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.ri.sp.PseudoSecurityService;
import org.apache.openejb.server.SelfManaging;
import org.apache.openejb.server.ServerService;
import org.apache.openejb.server.ServiceAccessController;
import org.apache.openejb.server.ServiceDaemon;
import org.apache.openejb.server.ServiceLogger;
import org.apache.openejb.server.webservices.WsRegistry;
import org.apache.openejb.spi.ApplicationServer;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Messages;
import org.apache.openejb.util.proxy.Jdk13ProxyFactory;
import org.apache.openejb.util.proxy.ProxyFactory;

/**
 * Factory for OpenEJB to intitialize everything.
 *
 */
public class OpenEjbFactory {

    private static Messages messages = new Messages("org.apache.openejb.util.resources");

    private Properties properties;
    private TransactionManager transactionManager;
    private WsRegistry wsRegistry;
    private SecurityService securityService;
    private ProxyFactory proxyFactory;
    private List<ServerService> serverServices;

    static {
        System.setProperty("openejb.log.factory", Log4jLogStreamFactory.class.getName());
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setWsRegistry(WsRegistry wsRegistry) {
        this.wsRegistry = wsRegistry;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public void setServerServices(List<ServerService> serverServices) {
        List<ServerService> services = new ArrayList<ServerService>();
        for (ServerService service : serverServices) {
            if (!(service instanceof SelfManaging)) {
                service = new ServiceLogger(service);
                service = new ServiceAccessController(service);
                service = new ServiceDaemon(service);
            }
            services.add(service);
        }
        this.serverServices = services;
    }

    public void init() throws Exception {
        Logger logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, "org.apache.openejb.util.resources");

        if (properties == null) {
            properties = new Properties();
        }

        try {
            SystemInstance.init(properties);
        } catch (Exception e) {
            throw new OpenEJBException(e);
        }
        SystemInstance system = SystemInstance.get();

        ApplicationServer appServer = new ServerFederation();
        system.setComponent(ApplicationServer.class, appServer);

        Assembler assembler = new Assembler();
        SystemInstance.get().setComponent(org.apache.openejb.spi.Assembler.class, assembler);

        ContainerSystem containerSystem = assembler.getContainerSystem();
        if (containerSystem == null) {
            String msg = messages.message("startup.assemblerReturnedNullContainer");
            logger.fatal(msg);
            throw new OpenEJBException(msg);
        }
        system.setComponent(ContainerSystem.class, containerSystem);

        if (proxyFactory == null)  {
            proxyFactory = new Jdk13ProxyFactory();
        }
        if (proxyFactory != null) {
            ProxyFactoryInfo proxyFactoryInfo = new ProxyFactoryInfo();
            PassthroughFactory.add(proxyFactoryInfo, proxyFactory);
            proxyFactoryInfo.id = "Default Proxy Factory";
            proxyFactoryInfo.service = "ProxyFactory";
            assembler.createProxyFactory(proxyFactoryInfo);
            system.setComponent(ProxyFactory.class, proxyFactory);
        }
        
        if (securityService == null) {
            securityService = new PseudoSecurityService();
        }
        if (securityService != null) {
            SecurityServiceInfo securityServiceInfo = new SecurityServiceInfo();
            PassthroughFactory.add(securityServiceInfo, securityService);
            securityServiceInfo.id = "Default Security Service";
            securityServiceInfo.service = "SecurityService";
            assembler.createSecurityService(securityServiceInfo);
            system.setComponent(SecurityService.class, securityService);
        }

        if (transactionManager != null) {
            TransactionServiceInfo transactionServiceInfo = new TransactionServiceInfo();
            PassthroughFactory.add(transactionServiceInfo, transactionManager);
            transactionServiceInfo.id = "Default Transaction Manager";
            transactionServiceInfo.service = "TransactionManager";
            assembler.createTransactionManager(transactionServiceInfo);
        }

        if (wsRegistry != null) {
            System.out.println("Using WSRegistry: " + wsRegistry);
            SystemInstance.get().setComponent(WsRegistry.class, wsRegistry);
        }

        OpenEjbConfiguration conf = SystemInstance.get().getComponent(OpenEjbConfiguration.class);
        for (ServerService service : serverServices) {
            service.start();
        }
    }

    public void destroy() throws Exception {
        for (ServerService service : serverServices) {
            service.stop();
        }
        OpenEJB.destroy();
    }
}
