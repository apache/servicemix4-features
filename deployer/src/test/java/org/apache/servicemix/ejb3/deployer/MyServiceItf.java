package org.apache.servicemix.ejb3.deployer;

import javax.jws.WebService;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Jan 29, 2008
 * Time: 10:02:18 AM
 * To change this template use File | Settings | File Templates.
 */
@WebService
public interface MyServiceItf {

    String hello(String ms);

}
