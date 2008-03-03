/**
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
package org.apache.servicemix.management;

import javax.management.remote.JMXAuthenticator;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

/**
 * 
 */
public class SimpleAuthenticationManager implements JMXAuthenticator {

    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Subject authenticate(Object credentials) {
        if (!(credentials instanceof String[])) {
            throw new IllegalArgumentException("Expected String[2], got " + (credentials != null ? credentials.getClass().getName() : null));
        }
        String[] params = (String[]) credentials;
        if (params.length != 2) {
            throw new IllegalArgumentException("Expected String[2] but length was " + params.length);
        }
        Subject subject = new Subject();
        try {
            authenticate(subject, params[0], params[1]);
        } catch (Exception e) {
            throw new SecurityException("Error occured while authenticating", e);
        }
        return subject;
    }

    protected void authenticate(Subject subject, String login, String password) {
        if (login == null && this.login == null) {
            // TODO
        }
    }


}
