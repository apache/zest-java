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
package org.apache.polygene.api.value;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.polygene.api.association.AssociationDescriptor;
import org.apache.polygene.api.common.ConstructionException;
import org.apache.polygene.api.entity.EntityReference;
import org.apache.polygene.api.property.PropertyDescriptor;

/**
 * Factory for Values and ValueBuilders.
 */
public interface ValueBuilderFactory
{

    /**
     * Instantiate a Value of the given type.
     *
     * @param <T> Value type
     * @param valueType the Value type to instantiate
     *
     * @return a new Value instance
     *
     * @throws NoSuchValueTypeException if no value extending the mixinType has been registered
     * @throws ConstructionException if the value could not be instantiated
     */
    <T> T newValue( Class<T> valueType )
        throws NoSuchValueTypeException, ConstructionException;

    /**
     * Create a builder for creating new Values that implements the given Value type.
     * <p>The returned ValueBuilder can be reused to create several Values instances.</p>
     *
     * @param <T> Value type
     * @param valueType an interface that describes the Composite to be instantiated
     *
     * @return a ValueBuilder for creation of ValueComposites implementing the interface
     *
     * @throws NoSuchValueTypeException if no value extending the mixinType has been registered
     */
    <T> ValueBuilder<T> newValueBuilder( Class<T> valueType )
        throws NoSuchValueTypeException;

    /**
     * Create a builder for creating a new Value starting with the given prototype.
     * <p>The returned ValueBuilder can only be used ONCE.</p>
     *
     * @param <T> Value type
     * @param prototype a prototype the builder will use
     *
     * @return a ValueBuilder for creation of ValueComposites implementing the interface of the prototype
     *
     * @throws NoSuchValueTypeException if no value extending the mixinType has been registered
     */
    <T> ValueBuilder<T> newValueBuilderWithPrototype( T prototype );

    /**
     * Create a builder for creating a new Value starting with the given state.
     * <p>The returned ValueBuilder can only be used ONCE.</p>
     *
     * @param <T> Value type
     * @param mixinType an interface that describes the Composite to be instantiated
     * @param propertyFunction a function providing the state of properties
     * @param associationFunction a function providing the state of associations
     * @param manyAssociationFunction a function providing the state of many associations
     * @param namedAssociationFunction a function providing the state of named associations
     *
     * @return a ValueBuilder for creation of ValueComposites implementing the interface
     *
     * @throws NoSuchValueTypeException if no value extending the mixinType has been registered
     */
    <T> ValueBuilder<T> newValueBuilderWithState( Class<T> mixinType,
                                                  Function<PropertyDescriptor, Object> propertyFunction,
                                                  Function<AssociationDescriptor, EntityReference> associationFunction,
                                                  Function<AssociationDescriptor, Stream<EntityReference>> manyAssociationFunction,
                                                  Function<AssociationDescriptor, Stream<Map.Entry<String, EntityReference>>> namedAssociationFunction );

    /**
     * Instantiate a Value of the given type using the serialized state given as String.
     *
     * @param <T> Value type
     * @param valueType the Value type to instantiate
     * @param serializedState  the state of the Value
     *
     * @return a new Value instance
     *
     * @throws NoSuchValueTypeException if no value extending the mixinType has been registered
     * @throws ConstructionException if the value could not be instantiated
     */
    <T> T newValueFromSerializedState( Class<T> valueType, String serializedState );

}
