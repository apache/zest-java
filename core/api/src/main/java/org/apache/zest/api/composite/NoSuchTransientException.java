/*
 * Copyright (c) 2012, Niclas Hedhman. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *     You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zest.api.composite;

import java.util.stream.Collectors;
import org.apache.zest.api.structure.TypeLookup;

/**
 * This exception is thrown if client code tries to create a non-existing TransientComposite type.
 */
public class NoSuchTransientException extends NoSuchCompositeException
{
    public NoSuchTransientException( String typeName, String moduleName, TypeLookup typeLookup )
    {
        super( "TransientComposite", typeName, moduleName, formatVisibleTypes( typeLookup ) );
    }

    private static String formatVisibleTypes( TypeLookup typeLookup )
    {
        return typeLookup.allTransients()
            .map(descriptor -> descriptor.primaryType().getName())
            .collect( Collectors.joining( "\n", "Visible transient types are:\n", "" ) );
    }
}
