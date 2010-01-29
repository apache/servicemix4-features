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

JPA OSGi Bundles Example
===============================

Purpose
-------
Demostrate how to use JPA/hibernate with SMX4.

This example is based on cxf-wsdl-first-osgi-package example, and
in cxf se endpoint bundle, use JPA hibernate implementation to persist
and retrieve PersonEntity.


Explanation
-----------
The CXF service engine and CXF binding component are used to expose the
web service. Each one is packaged in as an OSGi bundle, as follows:

1. CXF service engine (see the wsdl-first-cxfse-bundle directory):
   
   - Contains a copy of the service WSDL file, person.wsdl, in the
     src/main/resources directory.
   
   - The service implementation file, PersonImpl.java, in the
     src/main/java/org/apache/servicemix/samples/wsdl_first directory.
     It contains JAX-WS annotations that specify which web service
     it implements:

       @WebService(serviceName = "PersonService", 
           targetNamespace = "http://servicemix.apache.org/samples/wsdl-first",
           endpointInterface = "org.apache.servicemix.samples.wsdl_first.Person")
       
       In the constructor of PersonImpl, use JPA to persist two PersonEntity with 
       injected EntityManagerFactory.
       And in the getPerson method, also use JPA to query PersonEntity with personId
       from database.
      
   - A configuration file, beans.xml, located in the src/main/resources/
     META-INF/spring directory, which configures the CXF endpoint:
     
       <cxfse:endpoint>
        <cxfse:pojo>
          <bean class="org.apache.servicemix.samples.wsdl_first.PersonImpl">
              <constructor-arg>
                  <ref bean="entityManagerFactory"/>
              </constructor-arg>
          </bean>
        </cxfse:pojo>
       </cxfse:endpoint>
       <bean class="org.apache.servicemix.common.osgi.EndpointExporter" />
       also configure JPA entityManagerFacotry in this file
  <bean id="transactionTemplate" class="org.springframework.transaction.support.TransactionTemplate">
    <property name="transactionManager">
      <bean class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"/>
      </bean>
    </property>
  </bean>

  <bean id="jpaTemplate" class="org.springframework.orm.jpa.JpaTemplate">
    <property name="entityManagerFactory" ref="entityManagerFactory"/>
  </bean>

   <bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
    <property name="persistenceUnitName" value="smx4"/>
    <property name="jpaVendorAdapter" ref="jpaAdapter"/>
    <property name="dataSource" ref="dataSource" />
  </bean>
  
  <bean id="jpaAdapter"
        class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
        <property name="databasePlatform" value="org.hibernate.dialect.HSQLDialect" />
  </bean>
  
  <!--  DataSource Definition -->
  <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="org.hsqldb.jdbcDriver" />
        <property name="url" value="jdbc:hsqldb:mem:smx4_jpa" />
        <property name="username" value="sa" />
        <property name="password" value="" />
  </bean>

  And there's a JPA required persistence.xml in src/main/resources/META-INF/ folder
  <persistence xmlns="http://java.sun.com/xml/ns/persistence"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">

        <persistence-unit name="smx4" transaction-type="RESOURCE_LOCAL">
                <class>org.apache.servicemix.samples.wsdl_first.PersonEntity</class>

                <!-- Hibernate -->
                <properties>
                        <property name="hibernate.dialect" value="org.hibernate.dialect.HSQLDialect"/>
                        <property name="hibernate.hbm2ddl.auto" value="create"/>
                </properties>

        </persistence-unit>
  </persistence>
 
2. CXF binding component (see the wsdl-first-cxfbc-bundle directory):

   - Contains a copy of the service WSDL file, person.wsdl, in the
     src/main/resources directory.
    
   - A configuration file, beans.xml, located in the src/main/resources/
     META-INF/spring directory, which specifies a CXF consumer that will
     accept incoming calls for that web service and pass them to the NMR:

       <cxfbc:consumer wsdl="classpath:person.wsdl"
                      targetService="person:PersonService"
                      targetInterface="person:Person"/>

       <bean class="org.apache.servicemix.common.osgi.EndpointExporter" />


Prerequisites for Running the Example
-------------------------------------
1. You must have the following installed on your machine:

   - JDK 1.5 or higher
   
   - Maven 2.0.9 or higher
   
  For more information, see the README in the top-level examples
  directory.

2. Start ServiceMix by running the following command:

  <servicemix_home>/bin/servicemix          (on UNIX)
  <servicemix_home>\bin\servicemix          (on Windows)


Building and Running the Example
--------------------------------
To build and run the example, complete the following steps:

1. Build the example by opening a command prompt, changing directory
   to examples/jpa-osgi (this example) and entering
   the following Maven command:

     mvn install
   
   If all of the required OSGi bundles are available in your local
   Maven repository, the example will build very quickly. Otherwise
   it may take some time for Maven to download everything it needs.
   
   The mvn install command builds the example deployment bundle and
   copies it to your local Maven repository and to the target directory
   of this example.
     
2. Install the example by entering the following command in
   the ServiceMix console:
   
     features:install examples-jpa-osgi
     
   It makes use of the ServiceMix features facility. For more
   information about the features facility, see the README.txt file
   in the examples parent directory.
   The examples-jpa-osgi example mainly include jpa-hibernate feature, 
   which include necessary bundles to use JPA/hibernate in SMX4.

You can browse the WSDL at:

  http://localhost:8092/PersonService?wsdl

Note, if you use Safari, right click the window and select
'Show Source'.


Running a Client
----------------
To run the web client:

1. Open the client.html, which is located in the same directory as
   this README file, in your favorite browser.

2. Click the Send button to send a request.

   Once the request has been successfully sent, a response similar
   to the following should appear in the right-hand panel of the
   web page:
   
   STATUS: 200
   <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
     <soap:Body><GetPersonResponse xmlns="http://servicemix.apache.org/
     samples/wsdl-first/types"><personId>ffang</personId>
     <ssn>000-000-0000</ssn><name>Freeman Fang</name></GetPersonResponse>
     </soap:Body>
   </soap:Envelope>

   As in the database we only save two pre-stored PersonEntity with personId ffang and gnodet,
   so if change personId in the web to others, users will get fault response like
   STATUS: 500
   <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
     <soap:Body><soap:Fault><faultcode>soap:Server</faultcode><faultstring>No entity found for query</faultstring><detail><fault>No entity found for query</fault></detail></soap:Fault>
     </soap:Body>
   </soap:Envelope>

To run the java code client:

1. Change to the <servicemix_home>/examples/jpa-osgi/
   client directory.

2. Run the following command:

     mvn compile exec:java
     
   If the client request is successful, a response similar to the
   following should appear in the ServiceMix console:
        
   <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
     <soap:Body><GetPersonResponse xmlns="http://servicemix.apache.org/
       samples/wsdl-first/types"><personId>ffang</personId>
       <ssn>000-000-0000</ssn><name>Freeman Fang</name></GetPersonResponse>
     </soap:Body>
   </soap:Envelope>
   

Stopping and Uninstalling the Example
-------------------------------------
To stop the example, you must first know the bundle ID that ServiceMix
has assigned to it. To get the bundle ID, enter the following command
in the ServiceMix console:

  osgi:list

At the end of the listing, you should see an entry similar to the
following:

  [180] [Active     ] [     ] [  60] ServiceMix :: Samples :: JPA OSGI :: CXF BC BUNDLE (4.2.0)
  [181] [Active     ] [     ] [  60] ServiceMix :: Samples :: JPA OSGI :: CXF SE BUNDLE (4.2.0)


In this case, the bundle IDs are 180 and 181.

To stop the example, enter the following command in the ServiceMix
console:

  osgi:stop <bundle_id>

For example:

  osgi:stop 180
  osgi:stop 181

To uninstall the example, enter one of the following commands in
the ServiceMix console:

  features:uninstall examples-jpa-osgi
 
or
 
  osgi:uninstall <bundle_id>
  

Viewing the Log Entries
-----------------------
You can view the entries in the log file in the data/log
directory of your ServiceMix installation, or by typing
the following command in the ServiceMix console:

  log:display


Changing the Example
--------------------
If you want to change the code or configuration, just use 'mvn install'
to rebuild the OSGi bundles and deploy as before.


More Information
----------------
For more information, see:

  http://cwiki.apache.org/SMX4/creating-an-osgi-bundle-for-deploying-jbi-endpoints.html

Notes:
1. As some springsource hibernate bundles are fragment, which is not supported by felix, so
must use equinox as underlying framework. Users can edit $SMX_HOME/etc/config.properties to 
specify the framework if the distribution kit not use equinox as default one.

2. Users can see some exception from karaf.log like
15:00:58,805 | WARN  | xtenderThread-62 | InputStreamZippedJarVisitor      | ging.InputStreamZippedJarVisitor   41 | Unable to find file (ignored): bundleresource://188.fwk5604828/
java.lang.NullPointerException: in is null
	at java.util.zip.ZipInputStream.<init>(ZipInputStream.java:55)
	at java.util.jar.JarInputStream.<init>(JarInputStream.java:57)
	at java.util.jar.JarInputStream.<init>(JarInputStream.java:43)
	at org.hibernate.ejb.packaging.InputStreamZippedJarVisitor.doProcessElements(InputStreamZippedJarVisitor.java:37)
	at org.hibernate.ejb.packaging.AbstractJarVisitor.getMatchingEntries(AbstractJarVisitor.java:139)
	at org.hibernate.ejb.Ejb3Configuration.addScannedEntries(Ejb3Configuration.java:287)
	at org.hibernate.ejb.Ejb3Configuration.scanForClasses(Ejb3Configuration.java:614)
	at org.hibernate.ejb.Ejb3Configuration.configure(Ejb3Configuration.java:360)
	at org.hibernate.ejb.HibernatePersistence.createContainerEntityManagerFactory(HibernatePersistence.java:131)
	at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.createNativeEntityManagerFactory(LocalContainerEntityManagerFactoryBean.java:224)
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.afterPropertiesSet(AbstractEntityManagerFactoryBean.java:291)

This is known hibernate issue tracked by [1], it's something about hibernate trying to retrieve Entity class inside bundle with jar entry vistor but hit invalid resource path(bundle root path on equinox which is not a real resource path in this case), the exception is a little bit noisy but totally harmless. 

[1]http://opensource.atlassian.com/projects/hibernate/browse/HHH-4194
