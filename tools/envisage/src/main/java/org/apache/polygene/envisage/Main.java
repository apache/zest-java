/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package org.apache.polygene.envisage;

import org.apache.polygene.api.structure.ApplicationDescriptor;
import org.apache.polygene.bootstrap.ApplicationAssembler;
import org.apache.polygene.bootstrap.Energy4Java;

/**
 * Start Envisage with a specified application assembler. Specify assembler class
 * as first parameter.
 */
public class Main
{
    public static void main( String[] args )
        throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        String applicationAssemblerName = args[0];
        Class applicationAssemblerClass = Class.forName( applicationAssemblerName );
        ApplicationAssembler assembler = (ApplicationAssembler) applicationAssemblerClass.newInstance();

        Energy4Java polygene = new Energy4Java();

        ApplicationDescriptor application = polygene.newApplicationModel( assembler );

        new Envisage().run( application );
    }
}
