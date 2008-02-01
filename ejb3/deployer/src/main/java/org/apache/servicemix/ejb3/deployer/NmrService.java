package org.apache.servicemix.ejb3.deployer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.BindingFactory;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.openejb.DeploymentInfo;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.assembler.classic.DeploymentListener;
import org.apache.openejb.assembler.classic.EjbJarInfo;
import org.apache.openejb.assembler.classic.EnterpriseBeanInfo;
import org.apache.openejb.assembler.classic.StatelessBeanInfo;
import org.apache.openejb.core.CoreContainerSystem;
import org.apache.openejb.core.webservices.PortData;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.server.SelfManaging;
import org.apache.openejb.server.ServerService;
import org.apache.openejb.server.ServiceException;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.servicemix.cxf.binding.nmr.NMRBindingFactory;
import org.apache.servicemix.cxf.binding.nmr.NMRConstants;
import org.apache.servicemix.cxf.transport.nmr.NMRTransportFactory;
import org.apache.servicemix.nmr.api.NMR;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Jan 30, 2008
 * Time: 9:49:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class NmrService implements ServerService, SelfManaging, DeploymentListener {

    private Assembler assembler;
    private final Set<AppInfo> deployedApplications = new HashSet<AppInfo>();
    private NMR nmr;
    private Map<StatelessBeanInfo, Endpoint> endpoints = new HashMap<StatelessBeanInfo, Endpoint>();

    public void start() throws ServiceException {
        assembler = SystemInstance.get().getComponent(Assembler.class);
        if (assembler != null) {
            assembler.addDeploymentListener(this);
            for (AppInfo appInfo : assembler.getDeployedApplications()) {
                afterApplicationCreated(appInfo);
            }
        }
    }

    public void stop() throws ServiceException {
        if (assembler != null) {
            assembler.removeDeploymentListener(this);
            for (AppInfo appInfo : new ArrayList<AppInfo>(deployedApplications)) {
                beforeApplicationDestroyed(appInfo);
            }
            assembler = null;
        }
    }

    public void service(InputStream in, OutputStream out) throws ServiceException, IOException {
        throw new UnsupportedOperationException("NmrService can not be invoked directly");
    }

    public void service(Socket socket) throws ServiceException, IOException {
        throw new UnsupportedOperationException("NmrService can not be invoked directly");
    }

    public String getName() {
        return "nmr";
    }

    public String getIP() {
        return "n/a";
    }

    public int getPort() {
        return -1;
    }

    public NMR getNmr() {
        return nmr;
    }

    public void setNmr(NMR nmr) {
        this.nmr = nmr;
    }

    public void init(Properties props) throws Exception {
    }

    public void afterApplicationCreated(AppInfo appInfo) {
        if (deployedApplications.add(appInfo)) {
            System.out.println("Deploying new application to NMR");
            for (EjbJarInfo ejbJar : appInfo.ejbJars) {
                for (EnterpriseBeanInfo bean : ejbJar.enterpriseBeans) {
                    if (bean instanceof StatelessBeanInfo) {
                        StatelessBeanInfo statelessBeanInfo = (StatelessBeanInfo) bean;
                        Endpoint endpoint = createEndpoint(statelessBeanInfo);
                        endpoint.publish("nmr://endpoint");
                        endpoints.put(statelessBeanInfo, endpoint);
                    }
                }
            }
        }
    }

    public void beforeApplicationDestroyed(AppInfo appInfo) {
        if (deployedApplications.remove(appInfo)) {
            for (EjbJarInfo ejbJar : appInfo.ejbJars) {
                for (EnterpriseBeanInfo bean : ejbJar.enterpriseBeans) {
                    if (bean instanceof StatelessBeanInfo) {
                        StatelessBeanInfo statelessBeanInfo = (StatelessBeanInfo) bean;
                        Endpoint endpoint = endpoints.remove(statelessBeanInfo);
                        endpoint.stop();
                    }
                }
            }
        }
    }

    protected Endpoint createEndpoint(StatelessBeanInfo bean) {
        Bus bus = new ExtensionManagerBus();

        NMRTransportFactory transportFactory = new NMRTransportFactory();
        transportFactory.setBus(bus);
        transportFactory.setNmr(nmr);
        transportFactory.setTransportIds(Collections.singletonList(NMRTransportFactory.TRANSPORT_ID));
        DestinationFactoryManager dfm = bus.getExtension(DestinationFactoryManager.class);
        dfm.registerDestinationFactory(NMRTransportFactory.TRANSPORT_ID, transportFactory);

        NMRBindingFactory bindingFactory = new NMRBindingFactory();
        bindingFactory.setBus(bus);
        BindingFactoryManager bfm = bus.getExtension(BindingFactoryManager.class);
        bfm.registerBindingFactory(NMRConstants.NS_NMR_BINDING, bindingFactory);
        
        CoreContainerSystem containerSystem = (CoreContainerSystem) SystemInstance.get().getComponent(ContainerSystem.class);
        DeploymentInfo deploymentInfo = containerSystem.getDeploymentInfo(bean.ejbDeploymentId);
        PortData port = new PortData();
        NmrEndpoint ep = new NmrEndpoint(bus, port, deploymentInfo);
        return ep;
    }

}
