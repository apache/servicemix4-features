package org.apache.servicemix.ejb3.deployer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.apache.openejb.server.ServerService;
import org.apache.servicemix.nmr.api.Channel;
import org.apache.servicemix.nmr.api.Exchange;
import org.apache.servicemix.nmr.api.Pattern;
import org.apache.servicemix.nmr.api.Reference;
import org.apache.servicemix.nmr.core.ServiceMix;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Nov 14, 2007
 * Time: 12:31:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test extends TestCase {

    public void testWebService() throws Exception {
        System.setProperty("openejb.deployments.classpath", "false");

        final List<String> servlets = new ArrayList<String>();
        HttpService http = new HttpService() {
            public void registerServlet(String s, Servlet servlet, Dictionary dictionary, HttpContext httpContext) throws ServletException, NamespaceException {
                servlets.add(s);
            }
            public void registerResources(String s, String s1, HttpContext httpContext) throws NamespaceException {
            }
            public void unregister(String s) {
            }
            public HttpContext createDefaultHttpContext() {
                return null;
            }
        };

        OsgiWsRegistry registry = new OsgiWsRegistry();
        registry.setHttpService(http);;
        OpenEjbFactory factory = new OpenEjbFactory();
        factory.setWsRegistry(registry);
        List<ServerService> services = new ArrayList<ServerService>();
        CxfService svc = new CxfService();
        services.add(svc);
        factory.setServerServices(services);
        factory.init();

        URL url = getClass().getResource("Test.class");
        File f = new File(url.toURI());
        while (!f.getName().equals("test-classes")) {
            f = f.getParentFile();
        }
        url = f.getAbsoluteFile().toURL();
        URLClassLoader cl = new URLClassLoader(new URL[] {url}, Test.class.getClassLoader());
        System.out.println(url.toString());
        //new Deployer().deploy(cl, null);
        new Deployer().deploy(cl, url.toString());

        assertEquals(1, servlets.size());
    }

    public void testEjb() throws Exception {
        System.setProperty("openejb.deployments.classpath", "false");

        ServiceMix nmr = new ServiceMix();
        nmr.init();

        OpenEjbFactory factory = new OpenEjbFactory();
        List<ServerService> services = new ArrayList<ServerService>();
        NmrService svc = new NmrService();
        svc.setNmr(nmr);
        services.add(svc);
        factory.setServerServices(services);
        factory.setTransactionManager(new GeronimoTransactionManager());
        factory.init();

        URL url = getClass().getResource("Test.class");
        File f = new File(url.toURI());
        while (!f.getName().equals("test-classes")) {
            f = f.getParentFile();
        }
        url = f.getAbsoluteFile().toURL();
        URLClassLoader cl = new URLClassLoader(new URL[] {url}, Test.class.getClassLoader());
        System.out.println(url.toString());
        //new Deployer().deploy(cl, null);
        new Deployer().deploy(cl, url.toString());

        Channel channel = nmr.createChannel();
        Reference ref = nmr.getEndpointRegistry().lookup(new HashMap<String, Object>());
        Exchange e = channel.createExchange(Pattern.InOut);
        e.setTarget(ref);
        e.setOperation(new QName("http://deployer.ejb3.servicemix.apache.org/", "hello"));
        e.getIn().setBody(new StreamSource(new ByteArrayInputStream("<jbi:message xmlns:jbi='http://java.sun.com/xml/ns/jbi/wsdl-11-wrapper'><jbi:part><hello xmlns='http://deployer.ejb3.servicemix.apache.org/'><arg0>world</arg0></hello></jbi:part></jbi:message>".getBytes())));
        channel.sendSync(e);

        Source src = (Source) e.getOut().getBody();
        TransformerFactory.newInstance().newTransformer().transform(src, new StreamResult(System.err));

        assertEquals(1, nmr.getEndpointRegistry().getServices().size());
    }


}
