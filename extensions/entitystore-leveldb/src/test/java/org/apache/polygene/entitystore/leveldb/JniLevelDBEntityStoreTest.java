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
package org.apache.polygene.entitystore.leveldb;

import org.apache.polygene.api.common.Visibility;
import org.apache.polygene.bootstrap.ModuleAssembly;
import org.apache.polygene.entitystore.leveldb.assembly.LevelDBEntityStoreAssembler;
import org.apache.polygene.library.fileconfig.FileConfigurationAssembler;
import org.apache.polygene.library.fileconfig.FileConfigurationOverride;
import org.apache.polygene.test.entity.model.EntityStoreTestSuite;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class JniLevelDBEntityStoreTest extends EntityStoreTestSuite
{
    @Rule
    public final TemporaryFolder tmpDir = new TemporaryFolder();

    @Override
    protected void defineStorageModule( ModuleAssembly module )
    {
        module.defaultServices();
        new FileConfigurationAssembler()
            .withOverride( new FileConfigurationOverride().withConventionalRoot( tmpDir.getRoot() ) )
            .assemble( module );

        new LevelDBEntityStoreAssembler()
            .visibleIn( Visibility.application )
            .withConfig( configModule, Visibility.application )
            .identifiedBy( "jni-leveldb-entitystore" )
            .assemble( module );

        configModule.forMixin( LevelDBEntityStoreConfiguration.class ).declareDefaults().flavour().set( "jni" );
    }
}
