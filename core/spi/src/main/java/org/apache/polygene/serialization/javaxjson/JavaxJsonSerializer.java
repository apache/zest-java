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
 */
package org.apache.polygene.serialization.javaxjson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.apache.polygene.api.PolygeneAPI;
import org.apache.polygene.api.association.AssociationStateHolder;
import org.apache.polygene.api.composite.CompositeInstance;
import org.apache.polygene.api.injection.scope.This;
import org.apache.polygene.api.injection.scope.Uses;
import org.apache.polygene.api.serialization.Converter;
import org.apache.polygene.api.serialization.Converters;
import org.apache.polygene.api.serialization.SerializationException;
import org.apache.polygene.api.service.ServiceDescriptor;
import org.apache.polygene.api.type.ArrayType;
import org.apache.polygene.api.type.MapType;
import org.apache.polygene.api.type.ValueCompositeType;
import org.apache.polygene.api.type.ValueType;
import org.apache.polygene.api.util.ArrayIterable;
import org.apache.polygene.api.value.ValueComposite;
import org.apache.polygene.api.value.ValueDescriptor;
import org.apache.polygene.spi.serialization.AbstractTextSerializer;
import org.apache.polygene.spi.serialization.JsonSerializer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.apache.polygene.api.util.Collectors.toMap;

public class JavaxJsonSerializer extends AbstractTextSerializer implements JsonSerializer
{
    @This
    private Converters converters;

    @This
    private JavaxJsonAdapters adapters;

    @Uses
    private ServiceDescriptor descriptor;

    @Override
    public <T> Function<T, JsonValue> toJsonFunction( Options options )
    {
        return object -> doSerialize( options, object, true );
    }

    private JsonValue doSerialize( Options options, Object object, boolean root )
    {
        if( object == null )
        {
            return JsonValue.NULL;
        }
        Class<?> objectClass = object.getClass();
        Converter<Object> converter = converters.converterFor( objectClass );
        if( converter != null )
        {
            return doSerialize( options, converter.toString( object ), false );
        }
        JavaxJsonAdapter<?> adapter = adapters.adapterFor( objectClass );
        if( adapter != null )
        {
            return adapter.serialize( object, obj -> doSerialize( options, obj, false ) );
        }
        if( ValueCompositeType.isValueComposite( objectClass ) )
        {
            return serializeValueComposite( options, object, root );
        }
        if( MapType.isMap( objectClass ) )
        {
            return serializeMap( options, (Map<?, ?>) object );
        }
        if( ArrayType.isArray( objectClass ) )
        {
            return serializeArray( options, object );
        }
        if( Iterable.class.isAssignableFrom( objectClass ) )
        {
            return serializeIterable( options, (Iterable<?>) object );
        }
        if( Stream.class.isAssignableFrom( objectClass ) )
        {
            return serializeStream( options, (Stream<?>) object );
        }
        // Fallback to Java Serialization in Base 64
        byte[] bytes = Base64.getEncoder().encode( serializeJava( object ) );
        return JavaxJson.toJsonString( new String( bytes, UTF_8 ) );
    }

    private JsonObject serializeValueComposite( Options options, Object composite, boolean root )
    {
        CompositeInstance instance = PolygeneAPI.FUNCTION_COMPOSITE_INSTANCE_OF.apply( (ValueComposite) composite );
        ValueDescriptor descriptor = (ValueDescriptor) instance.descriptor();
        AssociationStateHolder state = (AssociationStateHolder) instance.state();
        ValueCompositeType valueType = descriptor.valueType();

        JsonObjectBuilder builder = Json.createObjectBuilder();
        valueType.properties().forEach(
            property -> builder.add(
                property.qualifiedName().name(),
                doSerialize( options, state.propertyFor( property.accessor() ).get(), false ) ) );
        valueType.associations().forEach(
            association -> builder.add(
                association.qualifiedName().name(),
                doSerialize( options, state.associationFor( association.accessor() ).reference(), false ) ) );
        valueType.manyAssociations().forEach(
            association -> builder.add(
                association.qualifiedName().name(),
                doSerialize( options, state.manyAssociationFor( association.accessor() ).references()
                                           .collect( toList() ),
                             false ) ) );
        valueType.namedAssociations().forEach(
            association -> builder.add(
                association.qualifiedName().name(),
                doSerialize( options,
                             state.namedAssociationFor( association.accessor() ).references()
                                  .collect( toMap() ),
                             false ) ) );
        if( !root && options.includeTypeInfo() )
        {
            withTypeInfo( builder, valueType );
        }
        return builder.build();
    }

    private JsonObjectBuilder withTypeInfo( JsonObjectBuilder builder, ValueType valueType )
    {
        return builder.add( getTypeInfoPropertyName(), valueType.primaryType().getName() );
    }

    /**
     * Map serialization.
     *
     * {@literal Map<String, ?>} are serialized to a {@literal JsonObject}.
     * {@literal Map<?, ?>} are serialized to a {@literal JsonArray} or key/value {@literal JsonObject}s.
     * Empty maps are serialized to an empty {@literal JsonObject}.
     */
    private JsonValue serializeMap( Options options, Map<?, ?> map )
    {
        if( map.isEmpty() )
        {
            // Defaults to {}
            return Json.createObjectBuilder().build();
        }
        Predicate<Object> characterKeyPredicate = key ->
            key != null && ( key instanceof CharSequence || key instanceof Character );
        if( map.keySet().stream().allMatch( characterKeyPredicate ) )
        {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            map.entrySet().forEach( entry -> builder.add( entry.getKey().toString(),
                                                          doSerialize( options, entry.getValue(), false ) ) );
            return builder.build();
        }
        else
        {
            JsonArrayBuilder builder = Json.createArrayBuilder();
            map.entrySet().forEach(
                entry -> builder.add(
                    Json.createObjectBuilder()
                        .add( "key", doSerialize( options, entry.getKey(), false ) )
                        .add( "value", doSerialize( options, entry.getValue(), false ) )
                        .build() ) );
            return builder.build();
        }
    }

    private JsonValue serializeArray( Options options, Object object )
    {
        ArrayType valueType = ArrayType.of( object.getClass() );
        if( valueType.isArrayOfPrimitiveBytes() )
        {
            byte[] base64 = Base64.getEncoder().encode( (byte[]) object );
            return JavaxJson.toJsonString( new String( base64, UTF_8 ) );
        }
        if( valueType.isArrayOfPrimitives() )
        {
            return serializeIterable( options, new ArrayIterable( object ) );
        }
        return serializeStream( options, Stream.of( (Object[]) object ) );
    }

    private JsonArray serializeIterable( Options options, Iterable<?> iterable )
    {
        return serializeStream( options, StreamSupport.stream( iterable.spliterator(), false ) );
    }

    private <T> JsonArray serializeStream( Options options, Stream<?> stream )
    {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        stream.forEach( element -> builder.add( doSerialize( options, element, false ) ) );
        return builder.build();
    }

    private byte[] serializeJava( Object object )
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try( ObjectOutputStream out = new ObjectOutputStream( bout ) )
        {
            out.writeUnshared( object );
            out.flush();
            return bout.toByteArray();
        }
        catch( IOException ex )
        {
            throw new SerializationException( "Unable to serialize using Java serialization", ex );
        }
    }

    private String getTypeInfoPropertyName()
    {
        return JavaxJsonSettings.orDefault( descriptor.metaInfo( JavaxJsonSettings.class ) )
                                .getTypeInfoPropertyName();
    }
}