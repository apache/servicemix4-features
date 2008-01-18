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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.kernel.filemonitor.DeploymentListener;


public class WarDeploymentListener implements DeploymentListener {
	
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

	public File handle(File artifact, File tmpDir) {
		try {
            final Properties instructions = getInstructions();
            generateClassPathInstruction(instructions, artifact);

            File outFile = new File(tmpDir, artifact.getName());

            BndUtils.createBundle(
                artifact,
                outFile,
                instructions,
                tmpDir.toURI().toString()
            );

            return outFile;

        } catch (Exception e) {
			LOGGER.error("Failed to transform the WAR artifact into an OSGi bundle");
			return null;
		}
	}

    private static List<String> extractJarListFromWar(File artifact) throws IOException {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(artifact);
            final List<String> list = new ArrayList<String>();
            Enumeration entries = jarFile.entries();
            while( entries.hasMoreElements() ) {
                JarEntry entry = (JarEntry) entries.nextElement();
                String name = entry.getName();
                if( !name.startsWith( "WEB-INF/lib/" ) ) {
                    continue;
                }
                if( !name.endsWith( ".jar" ) ) {
                    continue;
                }
                list.add( name );
            }
            return list;
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Creates a set of default instructions.
     */
    protected Properties getInstructions() {
        final Properties instructions = new Properties();
        // war file to be processed
        // default import packages
        instructions.setProperty(
            "Import-Package",
            "javax.*; resolution:=optional,"
            + "org.xml.*; resolution:=optional,"
            + "org.w3c.*; resolution:=optional"
        );
        // default no export packages
        instructions.setProperty(
            "Export-Package",
            "!*"
        );
        // remove unnecessary headers
        instructions.setProperty(
            "-removeheaders",
            "Private-Package,"
            + "Ignore-Package"
        );
        return instructions;
    }

    /**
     * Generates the Bundle-ClassPath header by merging the Original classpath with:<br/>
     * .<br/>
     * WEB-INF/classes<br/>
     * all jars found in WEB-INF/lib
     *
     * @param instructions instructions
     *
     * @throws java.io.IOException re-thrown from extractJarListFromWar()
     */
    private static void generateClassPathInstruction(final Properties instructions, final File jarFile) throws IOException {
        final List<String> bundleClassPath = new ArrayList<String>();
        // first take the bundle class path if present
        bundleClassPath.addAll(toList(instructions.getProperty("Bundle-ClassPath"), ","));
        // then get the list of jars in WEB-INF/lib
        bundleClassPath.addAll(extractJarListFromWar(jarFile));
        // check if we have a "WEB-INF/classpath" entry
        if (!bundleClassPath.contains("WEB-INF/classes")) {
            bundleClassPath.add(0, "WEB-INF/classes");
        }
        // check if we have a "." entry
        if (!bundleClassPath.contains( "." )) {
            bundleClassPath.add(0, ".");
        }
        // set back the new bundle classpath
        instructions.setProperty( "Bundle-ClassPath", join( bundleClassPath, "," ) );
    }

    /**
     * Splits a delimiter separated string into a list.
     *
     * @param separatedString string to be split
     * @param delimiter       delimiter
     *
     * @return list composed out of the string segments
     */
    protected static List<String> toList(final String separatedString, final String delimiter) {
        final List<String> list = new ArrayList<String>();
        if (separatedString != null) {
            list.addAll(Arrays.asList(separatedString.split(delimiter)));
        }
        return list;
    }

    /**
     * Joins elements from a collection into a delimiter separated string.
     *
     * @param strings   collection of ellements
     * @param delimiter delimiter
     *
     * @return string composed from the collection elements delimited by the delimiter
     */
    protected static String join(final Collection<String> strings, final String delimiter) {
        final StringBuffer buffer = new StringBuffer();
        final Iterator<String> iter = strings.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

}
