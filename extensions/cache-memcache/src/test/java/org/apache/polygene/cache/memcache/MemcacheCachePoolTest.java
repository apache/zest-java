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
package org.apache.polygene.cache.memcache;

import org.apache.polygene.api.common.Visibility;
import org.apache.polygene.bootstrap.AssemblyException;
import org.apache.polygene.bootstrap.ModuleAssembly;
import org.apache.polygene.cache.memcache.assembly.MemcacheAssembler;
import org.apache.polygene.test.EntityTestAssembler;
import org.apache.polygene.test.cache.AbstractCachePoolTest;
import org.apache.polygene.test.docker.DockerRule;
import org.junit.ClassRule;

/**
 * Memcache CachePool Test.
 */
public class MemcacheCachePoolTest
    extends AbstractCachePoolTest
{
    @ClassRule
    public static final DockerRule DOCKER = new DockerRule( "memcached", 11211 );

    @Override
    // START SNIPPET: assembly
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        // END SNIPPET: assembly
        ModuleAssembly confModule = module.layer().module( "confModule" );
        new EntityTestAssembler().visibleIn( Visibility.layer ).assemble( confModule );
        // START SNIPPET: assembly
        new MemcacheAssembler().
            visibleIn( Visibility.module ).
            withConfig( confModule, Visibility.layer ).
            assemble( module );
        // END SNIPPET: assembly
        MemcacheConfiguration memcacheConf = confModule.forMixin( MemcacheConfiguration.class ).declareDefaults();
        String dockerHost = DOCKER.getDockerHost();
        int dockerPort = DOCKER.getExposedContainerPort( "11211/tcp" );
        memcacheConf.addresses().set( dockerHost + ':' + dockerPort );
        memcacheConf.protocol().set( "binary" );
        // START SNIPPET: assembly
    }
    // END SNIPPET: assembly
}
