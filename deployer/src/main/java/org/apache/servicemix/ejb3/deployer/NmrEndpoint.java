package org.apache.servicemix.ejb3.deployer;

import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.interceptor.MustUnderstandInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.handler.logical.LogicalHandlerInInterceptor;
import org.apache.cxf.jaxws.handler.soap.SOAPHandlerInterceptor;
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
import org.apache.cxf.transport.http.WSDLQueryHandler;
import org.apache.openejb.DeploymentInfo;
import org.apache.openejb.core.webservices.PortData;
import org.apache.openejb.server.cxf.CxfEndpoint;
import org.apache.openejb.server.cxf.CxfServiceConfiguration;
import org.apache.openejb.server.cxf.JaxWsImplementorInfoImpl;
import org.apache.openejb.server.cxf.ejb.EjbMethodInvoker;
import org.apache.servicemix.cxf.binding.nmr.NMRConstants;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Jan 31, 2008
 * Time: 1:20:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class NmrEndpoint extends CxfEndpoint {

    private DeploymentInfo deploymentInfo;

    public NmrEndpoint(Bus bus, PortData portData, DeploymentInfo deploymentInfo) {
        super(bus, portData, deploymentInfo.getJndiEnc(), deploymentInfo.getBeanClass());
        this.deploymentInfo = deploymentInfo;

        String bindingURI = NMRConstants.NS_NMR_BINDING;
        implInfo = new JaxWsImplementorInfoImpl((Class) implementor, bindingURI);

        serviceFactory = new JaxWsServiceFactoryBean(implInfo);
        serviceFactory.setBus(bus);

        // install as first to overwrite annotations (wsdl-file, wsdl-port, wsdl-service)
        CxfServiceConfiguration configuration = new CxfServiceConfiguration(portData);
        serviceFactory.getConfigurations().add(0, configuration);

        service = serviceFactory.create();
    }

    protected Class getImplementorClass() {
        return (Class) this.implementor;
    }

    protected void init() {
        // configure handlers
        try {
            initHandlers();
        } catch (Exception e) {
            throw new WebServiceException("Error configuring handlers", e);
        }

        // Set service to invoke the target ejb
        service.setInvoker(new EjbMethodInvoker(this.bus, deploymentInfo));

        // Remove interceptors that perform handler processing since
        // handler processing must happen within the EJB container.
        Endpoint endpoint = getEndpoint();
        removeHandlerInterceptors(bus.getInInterceptors());
        removeHandlerInterceptors(endpoint.getInInterceptors());
        removeHandlerInterceptors(endpoint.getBinding().getInInterceptors());
        removeHandlerInterceptors(endpoint.getService().getInInterceptors());

        // Install SAAJ interceptor
        if (endpoint.getBinding() instanceof SoapBinding && !this.implInfo.isWebServiceProvider()) {
            endpoint.getService().getInInterceptors().add(new SAAJInInterceptor());
        }

        WSDLQueryHandler wsdl = new WSDLQueryHandler(bus);
        wsdl.writeResponse("http://localhost/service?wsdl", null, endpoint.getEndpointInfo(), System.err);
    }

    private static void removeHandlerInterceptors(List<Interceptor> interceptors) {
        for (Interceptor interceptor : interceptors) {
            if (interceptor instanceof MustUnderstandInterceptor || interceptor instanceof LogicalHandlerInInterceptor || interceptor instanceof SOAPHandlerInterceptor) {
                interceptors.remove(interceptor);
            }
        }
    }

    public void stop() {
        // call handler preDestroy
        destroyHandlers();

        // shutdown server
        super.stop();
    }
}
