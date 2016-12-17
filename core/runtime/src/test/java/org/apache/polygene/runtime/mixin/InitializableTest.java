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

package org.apache.polygene.runtime.mixin;

import org.junit.Test;
import org.apache.polygene.api.composite.TransientComposite;
import org.apache.polygene.api.mixin.Initializable;
import org.apache.polygene.api.mixin.Mixins;
import org.apache.polygene.bootstrap.AssemblyException;
import org.apache.polygene.bootstrap.ModuleAssembly;
import org.apache.polygene.test.AbstractPolygeneTest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test of Initializable interface
 */
public class InitializableTest
    extends AbstractPolygeneTest
{
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.objects( TestObject.class );
        module.transients( TestComposite.class );
    }

    @Test
    public void givenCompositeWithInitializableMixinWhenInstantiatedThenInvokeInitialize()
    {
        TestComposite instance = transientBuilderFactory.newTransient( TestComposite.class );
        assertThat( "mixin has been initialized", instance.ok(), equalTo( true ) );
    }

    @Test
    public void givenObjectImplementingInitializableWhenInstantiatedThenInvokeInitialize()
    {
        TestObject instance = objectFactory.newObject( TestObject.class );
        assertThat( "object has been initialized", instance.ok(), equalTo( true ) );
    }

    @Mixins( TestMixin.class )
    public interface TestComposite
        extends TransientComposite
    {
        boolean ok();
    }

    public abstract static class TestMixin
        implements TestComposite, Initializable
    {
        boolean ok = false;

        public void initialize()
        {
            ok = true;
        }

        public boolean ok()
        {
            return ok;
        }
    }

    public static class TestObject
        implements Initializable
    {
        boolean ok = false;

        public void initialize()
        {
            ok = true;
        }

        public boolean ok()
        {
            return ok;
        }
    }
}