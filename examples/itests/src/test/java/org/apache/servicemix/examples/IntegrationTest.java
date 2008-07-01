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
package org.apache.servicemix.examples;

import java.io.File;
import java.util.Properties;
import java.util.List;

import org.apache.servicemix.kernel.testing.support.AbstractIntegrationTest;
import org.springframework.osgi.test.platform.OsgiPlatform;

public class IntegrationTest extends AbstractIntegrationTest {

    private Properties dependencies;

    /**
	 * The manifest to use for the "virtual bundle" created
	 * out of the test classes and resources in this project
	 *
	 * This is actually the boilerplate manifest with one additional
	 * import-package added. We should provide a simpler customization
	 * point for such use cases that doesn't require duplication
	 * of the entire manifest...
	 */
	protected String getManifestLocation() {
		return "classpath:org/apache/servicemix/MANIFEST.MF";
	}

    /**
	 * The location of the packaged OSGi bundles to be installed
	 * for this test. Values are Spring resource paths. The bundles
	 * we want to use are part of the same multi-project maven
	 * build as this project is. Hence we use the localMavenArtifact
	 * helper method to find the bundles produced by the package
	 * phase of the maven build (these tests will run after the
	 * packaging phase, in the integration-test phase).
	 *
	 * JUnit, commons-logging, spring-core and the spring OSGi
	 * test bundle are automatically included so do not need
	 * to be specified here.
	 */
	protected String[] getTestBundlesNames() {
        return new String[] {
            getBundle("org.apache.felix", "org.apache.felix.prefs"),
            getBundle("org.apache.geronimo.specs", "geronimo-activation_1.1_spec"),
            getBundle("org.apache.geronimo.specs", "geronimo-annotation_1.0_spec"),
            getBundle("org.apache.geronimo.specs", "geronimo-javamail_1.4_spec"),
            getBundle("org.apache.geronimo.specs", "geronimo-servlet_2.5_spec"),
            getBundle("org.apache.geronimo.specs", "geronimo-ws-metadata_2.0_spec"),
            getBundle("org.apache.servicemix.specs", "org.apache.servicemix.specs.stax-api-1.0"),
            getBundle("org.apache.servicemix.specs", "org.apache.servicemix.specs.saaj-api-1.3"),
            getBundle("org.apache.servicemix.specs", "org.apache.servicemix.specs.jaxb-api-2.1"),
            getBundle("org.apache.servicemix.specs", "org.apache.servicemix.specs.jaxws-api-2.1"),
            getBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.asm"),
            getBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.jaxb-impl"),
            getBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.neethi"),
            getBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.woodstox"),
            getBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.wsdl4j"),
            getBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.xmlschema"),
            getBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.xmlresolver"),
            getBundle("org.mortbay.jetty", "jetty-util"),
            getBundle("org.mortbay.jetty", "jetty"),
            getBundle("org.ops4j.pax.web", "pax-web-bundle"),
            getBundle("org.ops4j.pax.web-extender", "pax-web-ex-whiteboard"),
            getBundle("org.apache.cxf", "cxf-bundle"),
            getBundle("org.apache.servicemix.cxf", "org.apache.servicemix.cxf.transport.osgi"),
            getBundle("org.apache.servicemix.cxf", "org.apache.servicemix.cxf.transport.nmr"),
            getBundle("org.apache.servicemix.nmr", "org.apache.servicemix.nmr.api"),
            getBundle("org.apache.servicemix.nmr", "org.apache.servicemix.nmr.core"),
            getBundle("org.apache.servicemix.nmr", "org.apache.servicemix.nmr.spring"),
            getBundle("org.apache.servicemix.nmr", "org.apache.servicemix.nmr.osgi"),
            getBundle("org.apache.servicemix.document", "org.apache.servicemix.document")
		};
	}

    public void testJbiComponent() throws Exception {
        Thread.sleep(5000);
        installBundle("org.apache.servicemix.examples", "cxf-osgi", null, "jar");
        Thread.sleep(5000);
    }


}
