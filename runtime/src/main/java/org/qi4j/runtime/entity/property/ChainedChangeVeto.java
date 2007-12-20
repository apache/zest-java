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

package org.qi4j.runtime.entity.property;

import org.qi4j.entity.property.PropertyChange;
import org.qi4j.entity.property.PropertyChangeVeto;
import org.qi4j.entity.property.PropertyVetoException;

/**
 * TODO
 */
public class ChainedChangeVeto<T> implements PropertyChangeVeto<T>
{
    private PropertyChangeVeto<T> changeVeto;
    private PropertyChangeVeto<T> changeVeto1;

    public ChainedChangeVeto( PropertyChangeVeto<T> changeVeto, PropertyChangeVeto<T> changeVeto1 )
    {
        this.changeVeto = changeVeto;
        this.changeVeto1 = changeVeto1;
    }

    public void onChange( PropertyChange<T> propertyChange ) throws PropertyVetoException
    {
        changeVeto.onChange( propertyChange );
        changeVeto1.onChange( propertyChange );
    }
}
