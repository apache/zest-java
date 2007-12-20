/*
 * Copyright (c) 2007, Rickard �berg. All Rights Reserved.
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

package org.qi4j.spi.structure;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * TODO
 */
public class PropertyDescriptor
{
    private Class propertyType;
    private Map<Class, Object> propertyInfos;
    private Method accessor;
    private Object defaultValue;

    public PropertyDescriptor( Class propertyType, Map<Class, Object> propertyInfos, Method accessor, Object defaultValue )
    {
        this.propertyType = propertyType;
        this.propertyInfos = propertyInfos;
        this.accessor = accessor;
        this.defaultValue = defaultValue;
    }

    public Class getPropertyType()
    {
        return propertyType;
    }

    public Map<Class, Object> getPropertyInfos()
    {
        return propertyInfos;
    }

    public Method getAccessor()
    {
        return accessor;
    }

    public Object getDefaultValue()
    {
        return defaultValue;
    }
}
