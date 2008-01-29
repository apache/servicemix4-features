package org.apache.servicemix.ejb3.deployer;

import java.util.Map;
import java.util.TreeMap;
import java.net.URL;

import javax.naming.Context;

import org.apache.cxf.Bus;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.server.cxf.CxfWsContainer;
import org.apache.openejb.server.cxf.CxfCatalogUtils;
import org.apache.openejb.server.cxf.pojo.PojoWsContainer;
import org.apache.openejb.server.cxf.ejb.EjbWsContainer;
import org.apache.openejb.server.cxf.client.SaajInterceptor;
import org.apache.openejb.server.httpd.HttpListener;
import org.apache.openejb.core.webservices.PortData;
import org.apache.openejb.DeploymentInfo;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Jan 28, 2008
 * Time: 10:29:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class CxfService extends org.apache.openejb.server.cxf.CxfService {

    private final Map<String, CxfWsContainer> wsContainers = new TreeMap<String, CxfWsContainer>();

    public CxfService() {
        SaajInterceptor.registerInterceptors();
    }

    public String getName() {
        return "cxf";
    }

    protected HttpListener createEjbWsContainer(URL moduleBaseUrl, PortData port, DeploymentInfo deploymentInfo) {
        Bus bus = CxfWsContainer.getBus();

        CxfCatalogUtils.loadOASISCatalog(bus, moduleBaseUrl, "META-INF/jax-ws-catalog.xml");

        EjbWsContainer container = new MyEjbWsContainer(bus, port, deploymentInfo);
        container.start();
        wsContainers.put(deploymentInfo.getDeploymentID().toString(), container);
        return container;
    }

    protected void destroyEjbWsContainer(String deploymentId) {
        CxfWsContainer container = wsContainers.remove(deploymentId);
        if (container != null) {
            container.destroy();
        }
    }

    protected HttpListener createPojoWsContainer(URL moduleBaseUrl, PortData port, String serviceId, Class target, Context context, String contextRoot) {
        Bus bus = CxfWsContainer.getBus();

        CxfCatalogUtils.loadOASISCatalog(bus, moduleBaseUrl, "META-INF/jax-ws-catalog.xml");

        PojoWsContainer container = new PojoWsContainer(bus, port, context, target);
        container.start();
        wsContainers.put(serviceId, container);
        return container;
    }

    protected void destroyPojoWsContainer(String serviceId) {
        CxfWsContainer container = wsContainers.remove(serviceId);
        if (container != null) {
            container.destroy();
        }
    }

    public void afterApplicationCreated(AppInfo appInfo) {
        System.out.println("CxfService:afterApplicationCreated");
        Thread.currentThread().setContextClassLoader(Bus.class.getClassLoader());
        super.afterApplicationCreated(appInfo);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void beforeApplicationDestroyed(AppInfo appInfo) {
        System.out.println("CxfService:beforeApplicationDestroyed");
        Thread.currentThread().setContextClassLoader(Bus.class.getClassLoader());
        super.beforeApplicationDestroyed(appInfo);    //To change body of overridden methods use File | Settings | File Templates.
    }

}
