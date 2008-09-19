/*
 * Copyright (c) 2008, Rickard Öberg. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.qi4j.runtime.entity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.qi4j.bootstrap.PropertyDeclarations;
import org.qi4j.composite.ConstraintViolationException;
import org.qi4j.property.GenericPropertyInfo;
import org.qi4j.property.Property;
import org.qi4j.runtime.composite.ConstraintsModel;
import org.qi4j.runtime.composite.ValueConstraintsInstance;
import org.qi4j.runtime.composite.ValueConstraintsModel;
import org.qi4j.runtime.property.PropertiesInstance;
import org.qi4j.runtime.property.PropertyModel;
import org.qi4j.spi.entity.EntityState;
import org.qi4j.spi.entity.PropertyType;
import org.qi4j.spi.property.PropertyDescriptor;
import org.qi4j.util.MetaInfo;

/**
 * TODO
 */
public final class EntityPropertiesModel
{
    private final Set<Method> methods = new HashSet<Method>();
    private final List<EntityPropertyModel> propertyModels = new ArrayList<EntityPropertyModel>();
    private final Map<Method, EntityPropertyModel> mapMethodPropertyModel = new HashMap<Method, EntityPropertyModel>();
    private final Map<String, Method> accessors = new HashMap<String, Method>();
    private final ConstraintsModel constraints;
    private PropertyDeclarations propertyDeclarations;

    public EntityPropertiesModel( ConstraintsModel constraints, PropertyDeclarations propertyDeclarations )
    {
        this.constraints = constraints;
        this.propertyDeclarations = propertyDeclarations;
    }

    public void addPropertyFor( Method method )
    {
        if( !methods.contains( method ) )
        {
            if( Property.class.isAssignableFrom( method.getReturnType() ) )
            {
                ValueConstraintsModel valueConstraintsModel = constraints.constraintsFor( method.getAnnotations(), GenericPropertyInfo.getPropertyType( method ), method.getName() );
                ValueConstraintsInstance valueConstraintsInstance = null;
                if( valueConstraintsModel.isConstrained() )
                {
                    valueConstraintsInstance = valueConstraintsModel.newInstance();
                }
                MetaInfo metaInfo = propertyDeclarations.getMetaInfo( method );
                Object defaultValue = propertyDeclarations.getDefaultValue( method );
                EntityPropertyModel propertyModel = new EntityPropertyModel( method, valueConstraintsInstance, metaInfo, defaultValue );
                final String qualifiedName = propertyModel.qualifiedName();
                if( !accessors.containsKey( qualifiedName ) )
                {
                    accessors.put( qualifiedName, propertyModel.accessor() );
                    propertyModels.add( propertyModel );
                    mapMethodPropertyModel.put( method, propertyModel );
                }
            }
            methods.add( method );
        }
    }

    public PropertiesInstance newDefaultInstance()
    {
        Map<Method, Property<?>> properties = new HashMap<Method, Property<?>>();
        for( PropertyModel propertyModel : propertyModels )
        {
            Property property = propertyModel.newInstance();
            properties.put( propertyModel.accessor(), property );
        }

        return new PropertiesInstance( properties );
    }

    public Property<?> newInstance( Method accessor, EntityState state )
    {
        return mapMethodPropertyModel.get( accessor ).newEntityInstance( state );
    }

    public PropertyDescriptor getPropertyByName( String name )
    {
        for( EntityPropertyModel propertyModel : propertyModels )
        {
            if( propertyModel.name().equals( name ) )
            {
                return propertyModel;
            }
        }
        return null;
    }

    public PropertyDescriptor getPropertyByQualifiedName( String name )
    {
        for( EntityPropertyModel propertyModel : propertyModels )
        {
            if( propertyModel.qualifiedName().equals( name ) )
            {
                return propertyModel;
            }
        }
        return null;
    }

    public void setState( PropertiesInstance properties, EntityState entityState )
        throws ConstraintViolationException
    {
        for( Map.Entry<Method, EntityPropertyModel> methodEntityPropertyModelEntry : mapMethodPropertyModel.entrySet() )
        {
            Property property = properties.propertyFor( methodEntityPropertyModelEntry.getKey() );
            if( !methodEntityPropertyModelEntry.getValue().isComputed() )
            {
                methodEntityPropertyModelEntry.getValue().setState( property, entityState );
            }
        }
    }

    public List<PropertyDescriptor> properties()
    {
        return new ArrayList<PropertyDescriptor>( propertyModels );
    }

    public Iterable<PropertyType> propertyTypes()
    {
        List<PropertyType> propertyTypes = new ArrayList<PropertyType>();
        for( EntityPropertyModel propertyModel : propertyModels )
        {
            propertyTypes.add( propertyModel.propertyType() );
        }
        return propertyTypes;
    }
}
