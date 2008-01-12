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
package org.apache.servicemix.war.deployer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.runtime.filemonitor.DeploymentListener;


public class WarDeploymentListener implements DeploymentListener {
	
	private static final Log LOGGER = LogFactory.getLog(WarDeploymentListener.class);
	
	public boolean canHandle(File artifact) {
		try {
            // Accept wars
            if (!artifact.getName().endsWith(".war")) {
                return false;
            }
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

	public File handle(File artifact, File tmpDir) {
		try {
	        JarFile jar = new JarFile(artifact);
	        Manifest m = jar.getManifest();

	        String version = m.getMainAttributes().getValue("Implementation-Version");
	        String name = m.getMainAttributes().getValue("Implementation-Title");
            if (name == null) {
                name = artifact.getName();
                int idx = name.lastIndexOf('/');
                if (idx > 0) {
                    name = name.substring(idx + 1);
                }
                idx = name.lastIndexOf('.');
                if (idx > 0) {
                    name = name.substring(0, idx - 1);
                }
            }
            m.getMainAttributes().put(new Attributes.Name("Bundle-SymbolicName"), name);
	        m.getMainAttributes().put(new Attributes.Name("Bundle-Version"), version);
            m.getMainAttributes().put(new Attributes.Name("Bundle-ClassPath"), getClassPath(jar));
            m.getMainAttributes().put(new Attributes.Name("Import-Package"), "javax.servlet,javax.servlet.http");
            m.getMainAttributes().put(new Attributes.Name("DynamicImport-Package"), "javax.*,org.xml.*,org.w3c.*");

            return generateWARArtifactBundle(artifact, tmpDir, m);
		} catch (Exception e) {
			LOGGER.error("Failed in transforming the WAR artifact to be OSGified");
			return null;
		}
	}

    private String getClassPath(JarFile jar) {
        StringBuilder sb = new StringBuilder();
        sb.append(".,WEB-INF/classes");
        for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements();) {
            JarEntry j = e.nextElement();
            if (j.getName().startsWith("WEB-INF/lib/")) {
                sb.append(",");
                sb.append(j.getName());
            }
        }
        return sb.toString();
    }

    private File generateWARArtifactBundle(File artifact, File tmpDir, Manifest m) throws Exception {
		String bundleName = artifact.getName().substring(0, artifact.getName().length() -4 ) + ".jar";
		File destFile = new File(tmpDir, bundleName);
		if (destFile.exists()) {
			destFile.delete();
		}
		
		JarInputStream jis = new JarInputStream(new FileInputStream(artifact));
		JarOutputStream jos = new JarOutputStream(new FileOutputStream(destFile), m);
		
		JarEntry entry = jis.getNextJarEntry();
		while (entry != null) {
		    jos.putNextEntry(entry);
		    copyInputStream(jis, jos);
		    jos.closeEntry();
		    entry = jis.getNextJarEntry();
		}
		
		jos.close();
		jis.close();
		
		LOGGER.debug("Converted the WAR artifact to OSGified bundle [" + destFile.getAbsolutePath() + "]");
		return destFile;
	}

    protected void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
    }

}
