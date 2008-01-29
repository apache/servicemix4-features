package org.apache.servicemix.ejb3.deployer;

import javax.ejb.Stateless;
import javax.jws.WebService;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Nov 14, 2007
 * Time: 7:37:05 PM
 * To change this template use File | Settings | File Templates.
 */
@WebService
@Stateless
public class MyService implements MyServiceItf {

    public String hello(String msg) {
        return "Hello " + msg + "!";
    }

}
