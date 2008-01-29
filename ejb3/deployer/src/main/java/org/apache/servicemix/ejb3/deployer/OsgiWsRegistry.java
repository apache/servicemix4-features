/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicemix.ejb3.deployer;

import java.util.Arrays;
import java.util.List;

import org.apache.openejb.server.httpd.HttpListener;
import org.apache.openejb.server.webservices.WsRegistry;
import org.apache.openejb.server.webservices.WsServlet;
import org.osgi.service.http.HttpService;

/**
 * Created by IntelliJ IDEA.
 * User: gnodet
 * Date: Nov 14, 2007
 * Time: 11:59:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class OsgiWsRegistry implements WsRegistry {

    private HttpService httpService;

    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    public List<String> setWsContainer(String virtualHost, String contextRoot, String servletName, HttpListener wsContainer) throws Exception {
        // TODO: implement
        System.out.println("OsgiWsRegistry:setWsContainer");
        return null;
    }

    public void clearWsContainer(String virtualHost, String contextRoot, String servletName) {
        // TODO: implement
        System.out.println("OsgiWsRegistry:clearWsContainer");
    }

    public List<String> addWsContainer(String path, HttpListener httpListener, String virtualHost, String realmName, String transportGuarantee, String authMethod, ClassLoader classLoader) throws Exception {
        System.out.println("OsgiWsRegistry:addWsContainer");
        if (path == null) throw new NullPointerException("contextRoot is null");
        if (httpListener == null) throw new NullPointerException("httpListener is null");

        // assure context root with a leading slash
        if (!path.startsWith("/")) path = "/" + path;

        httpService.registerServlet(path, new WsServlet(httpListener), null, httpService.createDefaultHttpContext());

        // TODO: return a correct list
        return Arrays.asList("http://localhost" + path);
    }

    public void removeWsContainer(String path) {
        System.out.println("OsgiWsRegistry:removeWsContainer");
        httpService.unregister(path);
    }
}
