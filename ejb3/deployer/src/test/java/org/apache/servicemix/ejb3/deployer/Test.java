package org.apache.servicemix.ejb3.deployer;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import junit.framework.TestCase;
import org.apache.openejb.server.ServerService;
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

    public void test() throws Exception {
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

        File f = new File("target/test-classes");
        URL url = f.getAbsoluteFile().toURL();
        URLClassLoader cl = new URLClassLoader(new URL[] {url}, Test.class.getClassLoader());
        System.out.println(url.toString());
        //new Deployer().deploy(cl, null);
        new Deployer().deploy(cl, url.toString());

        assertEquals(1, servlets.size());
    }

}
