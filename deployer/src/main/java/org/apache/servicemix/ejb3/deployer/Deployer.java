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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.config.ReadDescriptors;
import org.apache.openejb.config.WsModule;
import org.apache.openejb.config.ConfigurationFactory;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.JavaWsdlMapping;
import org.apache.openejb.jee.WebserviceDescription;
import org.apache.openejb.jee.Webservices;
import org.apache.xbean.finder.ResourceFinder;
import org.osgi.framework.Bundle;

/**
 * The Deployer contains the code to deploy EJBs given a classloader and a url location.
 *
 */
public class Deployer {

    private static final Log LOGGER = LogFactory.getLog(Deployer.class);

    private static final String EJB_DESCRIPTOR = "META-INF/ejb-jar.xml";

    public void deploy(ClassLoader classLoader, String location) throws Exception {

        // TODO: this does not work
        Map<String, URL> descriptors = new ResourceFinder(null, classLoader).getResourcesMap("META-INF/");

        URL ejbJarXmlUrl = classLoader.getResource(EJB_DESCRIPTOR);
        if (ejbJarXmlUrl == null) {
            LOGGER.debug("Descriptor ejb-jar.xml not found");
            return;
        }
        System.out.println("Descriptor ejb-jar.xml found!");
        EjbJar ejbJar = ReadDescriptors.readEjbJar(ejbJarXmlUrl);
        // create the EJB Module
        EjbModule ejbModule = new EjbModule(classLoader, location, ejbJar, null);
        ejbModule.getAltDDs().putAll(descriptors);

        // load webservices descriptor
        addWebservices(ejbModule);

        // wrap the EJB Module with an Application Module
        AppModule appModule = new AppModule(classLoader, ejbModule.getJarLocation());
        appModule.getEjbModules().add(ejbModule);
        ejbModule.setJarLocation(location);

        // Persistence Units
        addPersistenceUnits(appModule, classLoader);

        // Create application
        ConfigurationFactory configurationFactory = new ConfigurationFactory();
        Assembler assembler = (Assembler) SystemInstance.get().getComponent(org.apache.openejb.spi.Assembler.class);
        AppInfo appInfo = configurationFactory.configureApplication(appModule);
        assembler.createApplication(appInfo, classLoader);
        LOGGER.debug("EJB deployed");
    }

    private void addWebservices(WsModule wsModule) throws OpenEJBException {
        // get location of webservices.xml file
        Object webservicesObject = wsModule.getAltDDs().get("webservices.xml");
        if (!(webservicesObject instanceof URL)) {
            return;
        }
        URL webservicesUrl = (URL) webservicesObject;

        // determine the base url for this module (either file: or jar:)
        URL moduleUrl = null;
        try {
            File jarFile = new File(wsModule.getJarLocation());
            moduleUrl = jarFile.toURL();
            if (jarFile.isFile()) {
                moduleUrl = new URL("jar", "", -1, moduleUrl + "!/");
            }
        } catch (MalformedURLException e) {
            LOGGER.warn("Invalid module location " + wsModule.getJarLocation());
            return;
        }

        // parse the webservices.xml file
        Map<URL,JavaWsdlMapping> jaxrpcMappingCache = new HashMap<URL,JavaWsdlMapping>();
        Webservices webservices = ReadDescriptors.readWebservices(webservicesUrl);
        wsModule.setWebservices(webservices);
        if (webservicesUrl != null && "file".equals(webservicesUrl.getProtocol())) {
            wsModule.getWatchedResources().add(webservicesUrl.getPath());
        }

        // parse any jaxrpc-mapping-files mentioned in the webservices.xml file
        for (WebserviceDescription webserviceDescription : webservices.getWebserviceDescription()) {
            String jaxrpcMappingFile = webserviceDescription.getJaxrpcMappingFile();
            if (jaxrpcMappingFile != null) {
                URL jaxrpcMappingUrl = null;
                try {
                    jaxrpcMappingUrl = new URL(moduleUrl, jaxrpcMappingFile);
                    JavaWsdlMapping jaxrpcMapping = jaxrpcMappingCache.get(jaxrpcMappingUrl);
                    if (jaxrpcMapping == null) {
                        jaxrpcMapping = ReadDescriptors.readJaxrpcMapping(jaxrpcMappingUrl);
                        jaxrpcMappingCache.put(jaxrpcMappingUrl, jaxrpcMapping);
                    }
                    webserviceDescription.setJaxrpcMapping(jaxrpcMapping);
                    if (jaxrpcMappingUrl != null && "file".equals(jaxrpcMappingUrl.getProtocol())) {
                        wsModule.getWatchedResources().add(jaxrpcMappingUrl.getPath());
                    }
                } catch (MalformedURLException e) {
                    LOGGER.warn("Invalid jaxrpc-mapping-file location " + jaxrpcMappingFile);
                }
            }
        }

    }

    private void addPersistenceUnits(AppModule appModule, ClassLoader classLoader, URL... urls) {
        try {
            ResourceFinder finder = new ResourceFinder("", classLoader, urls);
            List<URL> persistenceUrls = finder.findAll("META-INF/persistence.xml");
            appModule.getAltDDs().put("persistence.xml", persistenceUrls);
        } catch (IOException e) {
            LOGGER.warn("Cannot load persistence-units from 'META-INF/persistence.xml' : " + e.getMessage(), e);
        }
    }

}
