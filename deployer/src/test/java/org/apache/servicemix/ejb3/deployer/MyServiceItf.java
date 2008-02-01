package org.apache.servicemix.ejb3.deployer;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Jan 29, 2008
 * Time: 10:02:18 AM
 * To change this template use File | Settings | File Templates.
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public interface MyServiceItf {

    @XmlType
    @XmlAccessorType(XmlAccessType.FIELD)
    public class HelloRequest {
        @XmlElement
        public String message;
    }

    @XmlType
    @XmlAccessorType(XmlAccessType.FIELD)
    public class HelloResponse {
        @XmlElement
        public String message;
    }

    @WebMethod
    @ResponseWrapper(className = "org.apache.servicemix.ejb3.deployer.MyServiceItf$HelloRequest", localName = "helloResponse")
    @RequestWrapper(className = "org.apache.servicemix.ejb3.deployer.MyServiceItf$HelloRequest", localName = "hello")
    public String hello(String message);

}
