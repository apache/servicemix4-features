package org.apache.servicemix.ejb3.deployer;

import org.apache.openejb.server.cxf.ejb.EjbWsContainer;
import org.apache.openejb.server.httpd.HttpRequest;
import org.apache.openejb.server.httpd.HttpResponse;
import org.apache.openejb.core.webservices.PortData;
import org.apache.openejb.DeploymentInfo;
import org.apache.cxf.Bus;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Jan 28, 2008
 * Time: 3:39:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyEjbWsContainer extends EjbWsContainer {

    public MyEjbWsContainer(Bus bus, PortData port, DeploymentInfo deploymentInfo) {
        super(bus, port, deploymentInfo);
    }

    public void onMessage(HttpRequest request, HttpResponse response) throws Exception {
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());        
        super.onMessage(request, response);
    }
}
