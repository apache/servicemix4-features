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
package org.apache.servicemix.activemq.commands;

import java.io.File;

import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.karaf.shell.console.OsgiCommandSupport;

/**
 * @version $Rev$ $Date$
 */
@Command(scope="activemq", name="destroy-broker", description="Creates a broker instance.")
public class DestroyBrokerCommand extends OsgiCommandSupport {

    @Option(name = "-n", aliases = {"--name"}, description = "The name of the broker (defaults to localhost).")
    private String name = "localhost";

    protected Object doExecute() throws Exception {

        try {
            String name = getName();
            File base = new File(System.getProperty("karaf.base"));
            File deploy = new File(base, "deploy");
            File configFile = new File(deploy, name + "-broker.xml");

            configFile.delete();

            System.out.println("");
            System.out.println("Default ActiveMQ Broker (" + name + ") configuration file created at: "
                           + configFile.getPath() + " removed.");
            System.out.println("");

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return 0;
    }

    public String getName() {
        if (name == null) {
            File base = new File(System.getProperty("karaf.base"));
            name = base.getName();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
