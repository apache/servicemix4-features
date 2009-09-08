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
package org.apache.servicemix.war.deployer;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.fileinstall.ArtifactTransformer;


public class WarDeploymentListener implements ArtifactTransformer {
	
	private static final Log LOGGER = LogFactory.getLog(WarDeploymentListener.class);
	
	public boolean canHandle(File artifact) {
		try {
			JarFile jar = new JarFile(artifact);
			JarEntry entry = jar.getJarEntry("WEB-INF/web.xml");
            // Only handle WAR artifacts
            if (entry == null) {
				return false;
			}
            // Only handle non OSGi bundles
            Manifest m = jar.getManifest();
            if (m.getMainAttributes().getValue(new Attributes.Name("Bundle-SymbolicName")) != null &&
                m.getMainAttributes().getValue(new Attributes.Name("Bundle-Version")) != null) {
                return false;
            }
            return true;
		} catch (Exception e) {
			return false;
		}
	}

	public File transform(File artifact, File tmpDir) {
		try {
            URL war = new URL("war:" + artifact.toURL().toString());
            File outFile = new File(tmpDir, artifact.getName());
            IOUtils.copyAndClose(war.openStream(), new FileOutputStream(outFile));
            return outFile;

        } catch (Exception e) {
			LOGGER.error("Failed to transform the WAR artifact into an OSGi bundle");
			return null;
		}
	}

}
