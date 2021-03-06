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

package org.apache.polygene.envisage.school.domain.school.assembly;

import org.apache.polygene.api.association.Association;
import org.apache.polygene.api.identity.HasIdentity;
import org.apache.polygene.api.identity.Identity;
import org.apache.polygene.api.injection.scope.This;
import org.apache.polygene.api.mixin.Mixins;
import org.apache.polygene.api.property.Property;
import org.apache.polygene.envisage.school.domain.school.School;
import org.apache.polygene.envisage.school.domain.school.Subject;

@Mixins( SubjectEntity.SubjectMixin.class )
public interface SubjectEntity
    extends Subject, HasIdentity
{
    class SubjectMixin
        implements Subject
    {
        @This
        private SubjectState state;

        @Override
        public String name()
        {
            return state.name().get();
        }

        @Override
        public String description()
        {
            return state.description().get();
        }

        @Override
        public School school()
        {
            return state.school().get();
        }
    }

    interface SubjectState
    {
        Property<String> name();

        Property<String> description();

        Property<Identity> schoolId();

        Association<School> school();
    }

}
