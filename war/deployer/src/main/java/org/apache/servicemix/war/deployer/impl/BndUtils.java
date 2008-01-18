/*
 * Copyright 2008 Alin Dreghiciu.
 * Copyright 2008 Peter Kriens.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicemix.war.deployer.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.jar.Manifest;

import aQute.lib.osgi.Analyzer;
import aQute.lib.osgi.Jar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Wrapper over PeterK's bnd lib.
 *
 * @author Alin Dreghiciu
 * @since 0.1.0, January 14, 2008
 */
public class BndUtils
{

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog( BndUtils.class );

    /**
     * Utility class. Ment to be used using static methods
     */
    private BndUtils()
    {
        // utility class
    }

    /**
     * Precesses the input jar and generates the necessary OSGi headers using specified instructions.
     *
     * @param input          input file for the jar to be processed. Cannot be null.
     * @param output         output file for the new jar. Cannot be null.
     * @param instructions   bnd specific processing instructions. Cannot be null.
     * @param jarInfo        information about the jar to be processed. Usually the jar url. Cannot be null or empty.
     *
     * @return an input strim for the generated bundle
     *
     * @throws IOException           re-thron during jar processing
     */
    public static void createBundle( final File input,
                                     final File output,
                                     final Properties instructions,
                                     final String jarInfo )
        throws IOException
    {
        LOG.debug( "Creating bundle for [" + jarInfo + "]" );
        LOG.trace( "Using instructions " + instructions );

        final Jar jar = new Jar( "dot", input );
        final Manifest manifest = jar.getManifest();
        final OutputStream outputStream = new FileOutputStream(output);

        // Make the jar a bundle if it is not already a bundle
        if( manifest == null
            || ( manifest.getMainAttributes().getValue( Analyzer.EXPORT_PACKAGE ) == null
                 && manifest.getMainAttributes().getValue( Analyzer.IMPORT_PACKAGE ) == null )
            )
        {
            final Properties properties = new Properties( instructions );
            properties.put( "Generated-By-Ops4j-Pax-From", jarInfo );
            final Analyzer analyzer = new Analyzer();
            analyzer.setJar( jar );
            analyzer.setProperties( properties );
            checkMandatoryProperties( analyzer, jar, jarInfo );
            analyzer.mergeManifest( manifest );
            analyzer.calcManifest();
        }

        try {
            jar.write( outputStream );
        }
        catch( IOException e ) {
            throw new RuntimeException( "Bundle cannot be generated, e" );
        }
        finally {
            try {
                jar.close();
                outputStream.close();
            }
            catch( IOException ignore ) {
                // if we get here something is very wrong
                LOG.error( "Bundle cannot be generated, e" );
            }
        }
    }

    /**
     * Check if manadatory properties are present, otherwise generate default.
     *
     * @param analyzer     bnd analyzer
     * @param jar          bnd jar
     * @param symbolicName bundle symbolic name
     */
    private static void checkMandatoryProperties( final Analyzer analyzer,
                                                  final Jar jar,
                                                  final String symbolicName )
    {
        final String importPackage = analyzer.getProperty( Analyzer.IMPORT_PACKAGE );
        if( importPackage == null || importPackage.trim().length() == 0 )
        {
            analyzer.setProperty( Analyzer.IMPORT_PACKAGE, "*;resolution:=optional" );
        }
        final String exportPackage = analyzer.getProperty( Analyzer.EXPORT_PACKAGE );
        if( exportPackage == null || exportPackage.trim().length() == 0 )
        {
            analyzer.setProperty( Analyzer.EXPORT_PACKAGE, analyzer.calculateExportsFromContents( jar ) );
        }
        final String localSymbolicName = analyzer.getProperty( Analyzer.BUNDLE_SYMBOLICNAME, symbolicName );
        analyzer.setProperty( Analyzer.BUNDLE_SYMBOLICNAME, generateSymbolicName( localSymbolicName ) );
    }

    /**
     * Processes symbolic name and replaces osgi spec invalid characters with "_".
     *
     * @param symbolicName bundle symbolic name
     *
     * @return a valid symbolic name
     */
    private static String generateSymbolicName( final String symbolicName )
    {
        return symbolicName.replaceAll( "[^a-zA-Z_0-9.-]", "_" );
    }

}
