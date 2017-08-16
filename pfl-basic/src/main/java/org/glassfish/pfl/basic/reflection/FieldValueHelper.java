/*
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *  Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 *  The contents of this file are subject to the terms of either the GNU
 *  General Public License Version 2 only ("GPL") or the Common Development
 *  and Distribution License("CDDL") (collectively, the "License").  You
 *  may not use this file except in compliance with the License.  You can
 *  obtain a copy of the License at
 *  https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 *  or packager/legal/LICENSE.txt.  See the License for the specific
 *  language governing permissions and limitations under the License.
 *
 *  When distributing the software, include this License Header Notice in each
 *  file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 *
 *  GPL Classpath Exception:
 *  Oracle designates this particular file as subject to the "Classpath"
 *  exception as provided by Oracle in the GPL Version 2 section of the License
 *  file that accompanied this code.
 *
 *  Modifications:
 *  If applicable, add the following below the License Header, with the fields
 *  enclosed by brackets [] replaced by your own identifying information:
 *  "Portions Copyright [year] [name of copyright owner]"
 *
 *  Contributor(s):
 *  If you wish your version of this file to be governed by only the CDDL or
 *  only the GPL Version 2, indicate your decision by adding "[Contributor]
 *  elects to include this software in this distribution under the [CDDL or GPL
 *  Version 2] license."  If you don't indicate a single choice of license, a
 *  recipient has the option to distribute your version of this file under
 *  either the CDDL, the GPL Version 2 or to extend the choice of license to
 *  its licensees as provided above.  However, if you add GPL Version 2 code
 *  and therefore, elected the GPL Version 2 license, then the option applies
 *  only if the new code is made subject to such option by the copyright
 *  holder.
 */

package org.glassfish.pfl.basic.reflection;

import java.lang.reflect.Field;

public class FieldValueHelper {
    private static final Bridge bridge = Bridge.get();

    /**
     * Returns the value of a field in an object.
     * @param obj the object holding the field
     * @param field the field whose value is to be returned.
     * @throws IllegalAccessException if the field cannot directly be accessed.
     */
    public static Object getFieldValue(Object obj, final Field field) throws IllegalAccessException {
        if (field.isAccessible())
            return field.get(obj);
        else
            return getPrivateFieldValue(obj, field);
    }

    private static Object getPrivateFieldValue(Object obj, final Field field) throws IllegalAccessException {
        Field privateField = bridge.toAccessibleField(field, FieldValueHelper.class);
        return (privateField != null) ? privateField.get(obj) : getInacessibleFieldValue(obj, field);
    }

    private static Object getInacessibleFieldValue(Object obj, Field field) {
        long offset = bridge.objectFieldOffset(field);

        if (!field.getType().isPrimitive())
            return bridge.getObject(obj, offset);
        else if (field.getType() == Integer.TYPE)
            return bridge.getInt(obj, offset);
        else if (field.getType() == Byte.TYPE)
            return bridge.getByte(obj, offset);
        else if (field.getType() == Long.TYPE)
            return bridge.getLong(obj, offset);
        else if (field.getType() == Float.TYPE)
            return bridge.getFloat(obj, offset);
        else if (field.getType() == Double.TYPE)
            return bridge.getDouble(obj, offset);
        else if (field.getType() == Short.TYPE)
            return bridge.getShort(obj, offset);
        else if (field.getType() == Character.TYPE)
            return bridge.getChar(obj, offset);
        else if (field.getType() == Boolean.TYPE)
            return bridge.getBoolean(obj, offset);
        else
            return null;
    }
}
