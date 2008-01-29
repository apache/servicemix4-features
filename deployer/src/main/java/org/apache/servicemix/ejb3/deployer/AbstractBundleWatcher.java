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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.context.BundleContextAware;

/**
 */
public abstract class AbstractBundleWatcher implements BundleContextAware, InitializingBean, DisposableBean {

    private BundleContext bundleContext;
    private SynchronousBundleListener bundleListener;
    private List<Bundle> bundles = new ArrayList<Bundle>();

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void afterPropertiesSet() throws Exception {
        bundleContext.addBundleListener(bundleListener = new SynchronousBundleListener() {
            public void bundleChanged(BundleEvent event) {
                switch (event.getType()) {
                    case BundleEvent.STARTED:
                        onBundleStarted(event.getBundle());
                        break;
                    case BundleEvent.STOPPED:
                        onBundleStopped(event.getBundle());
                        break;
                }
            }
        });
        Bundle[] bundles = bundleContext.getBundles();
        if (bundles != null) {
            for (Bundle bundle : bundles) {
                onBundleStarted(bundle);
            }
        }
    }

    public void destroy() throws Exception {
        bundleContext.removeBundleListener(bundleListener);
        for (Bundle bundle : bundles.toArray(new Bundle[bundles.size()])) {
            if (bundle.getState() == Bundle.ACTIVE) {
                onBundleStopped(bundle);
            }
        }
    }

    private void onBundleStarted(Bundle bundle) {
        if (match(bundle) && !bundles.contains(bundle)) {
            register(bundle);
            bundles.add(bundle);
        }
    }

    private void onBundleStopped(Bundle bundle) {
        if (bundles.remove(bundle)) {
            unregister(bundle);
        }
    }

    protected boolean match(Bundle bundle) {
        return true;
    }

    protected abstract void register(Bundle bundle);

    protected abstract void unregister(Bundle bundle);

}
