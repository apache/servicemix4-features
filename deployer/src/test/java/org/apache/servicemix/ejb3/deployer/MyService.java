package org.apache.servicemix.ejb3.deployer;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.WebMethod;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Nov 14, 2007
 * Time: 7:37:05 PM
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@WebService
public class MyService implements MyServiceItf {

    @WebMethod

    public String hello(String s) {
        return "Hello " + s + "!";
    }

}
