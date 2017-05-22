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

package org.apache.polygene.runtime.structure;

import org.junit.Test;
import org.apache.polygene.bootstrap.ApplicationAssembler;
import org.apache.polygene.bootstrap.ApplicationAssemblerAdapter;
import org.apache.polygene.bootstrap.ApplicationAssembly;
import org.apache.polygene.bootstrap.ApplicationAssemblyFactory;
import org.apache.polygene.bootstrap.Assembler;
import org.apache.polygene.bootstrap.AssemblyException;
import org.apache.polygene.bootstrap.Energy4Java;
import org.apache.polygene.bootstrap.LayerAssembly;
import org.apache.polygene.bootstrap.ModuleAssembly;

/**
 * JAVADOC
 */
public class StructureTest
{

    @Test
    public void createApplicationUsingApplicationAssembly()
        throws AssemblyException
    {
        Energy4Java polygene = new Energy4Java();
        polygene.newApplication( new ApplicationAssembler()
        {
            public ApplicationAssembly assemble( ApplicationAssemblyFactory applicationFactory )
                throws AssemblyException
            {
                ApplicationAssembly applicationAssembly = applicationFactory.newApplicationAssembly();
                // Application Layer
                LayerAssembly applicationLayer = applicationAssembly.layer( "Application" );
                ModuleAssembly applicationModule = applicationLayer.module( "Application" );
                new DomainApplicationAssembler().assemble( applicationModule );

                // View Layer
                LayerAssembly viewLayer = applicationAssembly.layer( "View" );
                ModuleAssembly viewModule = viewLayer.module( "View" );
                new ViewAssembler().assemble( viewModule );
                viewLayer.uses( applicationLayer );
                return applicationAssembly;
            }
        } );
    }

    @Test
    public void createApplicationUsingArrayOfAssemblers()
        throws AssemblyException
    {
        Energy4Java polygene = new Energy4Java();
        Assembler[][][] assemblers = new Assembler[][][]
            {
                { // User Interface layer
                  {
                      new ViewAssembler()
                  }
                },
                { // Application layer
                  {
                      new DomainApplicationAssembler()
                  }
                },
                { // Domain layer
                  {
                      new DomainModelAssembler()
                  }
                },
                { // Infrastructure layer
                  {
                      new InfrastructureAssembler()
                  }
                }
            };

        polygene.newApplication( new ApplicationAssemblerAdapter( assemblers )
        {
        } );
    }

    static class ViewAssembler
        implements Assembler
    {
        public void assemble( ModuleAssembly module )
            throws AssemblyException
        {
        }
    }

    static class DomainApplicationAssembler
        implements Assembler
    {
        public void assemble( ModuleAssembly module )
            throws AssemblyException
        {
        }
    }

    static class DomainModelAssembler
        implements Assembler
    {
        public void assemble( ModuleAssembly module )
            throws AssemblyException
        {
        }
    }

    static class InfrastructureAssembler
        implements Assembler
    {
        public void assemble( ModuleAssembly module )
            throws AssemblyException
        {
        }
    }
}
