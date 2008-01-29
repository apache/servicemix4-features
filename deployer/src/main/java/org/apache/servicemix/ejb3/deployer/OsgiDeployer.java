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

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openejb.config.ConfigurationFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.util.BundleDelegatingClassLoader;

/**
 * 
 */
public class OsgiDeployer extends AbstractBundleWatcher {

    private static final Log LOGGER = LogFactory.getLog(OsgiDeployer.class);

    private Deployer deployer = new Deployer();

    protected void register(Bundle bundle) {
        try {
            Thread.currentThread().setContextClassLoader(ConfigurationFactory.class.getClassLoader());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Checking bundle: " + bundle.getSymbolicName());
            }

            ClassLoader classLoader = BundleDelegatingClassLoader.createBundleClassLoaderFor(
                                            bundle, BundleContext.class.getClassLoader());
            deployer.deploy(classLoader, bundle.getLocation());
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void unregister(Bundle bundle) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}