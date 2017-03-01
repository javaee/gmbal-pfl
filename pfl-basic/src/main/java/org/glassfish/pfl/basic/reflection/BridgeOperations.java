/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.glassfish.pfl.basic.reflection;

import java.io.OptionalDataException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;

public interface BridgeOperations {
    /**
     * This constant differs from all results that will ever be returned from
     * {@link #objectFieldOffset}.
     */
    long INVALID_FIELD_OFFSET = -1;

    /**
     * Obtain the latest user defined ClassLoader from the call stack.
     * This is required by the RMI-IIOP specification.
     */
    ClassLoader getLatestUserDefinedLoader();

    /**
     * Fetches a field element within the given
     * object <code>o</code> at the given offset.
     * The result is undefined unless the offset was obtained from
     * {@link #objectFieldOffset} on the {@link java.lang.reflect.Field}
     * of some Java field and the object referred to by <code>o</code>
     * is of a class compatible with that field's class.
     *
     * @param o      Java heap object in which the field from which the offset
     *               was obtained resides
     * @param offset indication of where the field resides in a Java heap
     *               object
     * @return the value fetched from the indicated Java field
     * @throws RuntimeException No defined exceptions are thrown, not even
     *                          {@link NullPointerException}
     */
    int getInt(Object o, long offset);

    /**
     * Stores a value into a given Java field.
     * <p>
     * The first two parameters are interpreted exactly as with
     * {@link #getInt(Object, long)} to refer to a specific
     * Java field.  The given value is stored into that field.
     * <p>
     * The field must be of the same type as the method
     * parameter <code>x</code>.
     *
     * @param o      Java heap object in which the field resides, if any, else
     *               null
     * @param offset indication of where the field resides in a Java heap
     *               object.
     * @param x      the value to store into the indicated Java field
     * @throws RuntimeException No defined exceptions are thrown, not even
     *                          {@link NullPointerException}
     */
    void putInt(Object o, long offset, int x);

    /**
     * @see #getInt(Object, long)
     */
    Object getObject(Object o, long offset);

    /**
     * @see #putInt(Object, long, int)
     */
    void putObject(Object o, long offset, Object x);

    /**
     * @see #getInt(Object, long)
     */
    boolean getBoolean(Object o, long offset);

    /**
     * @see #putInt(Object, long, int)
     */
    void putBoolean(Object o, long offset, boolean x);

    /**
     * @see #getInt(Object, long)
     */
    byte getByte(Object o, long offset);

    /**
     * @see #putInt(Object, long, int)
     */
    void putByte(Object o, long offset, byte x);

    /**
     * @see #getInt(Object, long)
     */
    short getShort(Object o, long offset);

    /**
     * @see #putInt(Object, long, int)
     */
    void putShort(Object o, long offset, short x);

    /**
     * @see #getInt(Object, long)
     */
    char getChar(Object o, long offset);

    /**
     * @see #putInt(Object, long, int)
     */
    void putChar(Object o, long offset, char x);

    /**
     * @see #getInt(Object, long)
     */
    long getLong(Object o, long offset);

    /**
     * @see #putInt(Object, long, int)
     */
    void putLong(Object o, long offset, long x);

    /**
     * @see #getInt(Object, long)
     */
    float getFloat(Object o, long offset);

    /**
     * @see #putInt(Object, long, int)
     */
    void putFloat(Object o, long offset, float x);

    /**
     * @see #getInt(Object, long)
     */
    double getDouble(Object o, long offset);

    /**
     * @see #putInt(Object, long, int)
     */
    void putDouble(Object o, long offset, double x);

    /**
     * Returns the offset of a non-static field.
     */
    long objectFieldOffset(Field f);

    long staticFieldOffset(Field f);

    /**
     * Throw the exception.
     * The exception may be an undeclared checked exception.
     */
    void throwException(Throwable ee);

    /**
     * Return a constructor that can be used to create an instance of the class for externalization.
     * @param cl the class
     */
    <T> Constructor<?> newConstructorForExternalization(Class<T> cl);

    /**
     * Return a no-arg constructor for the specified class which invokes the specified constructor.
     *
     * @param aClass the class for which a constructor should be returned.
     * @param cons the default constructor on which to model the new constructor.
     */
    <T> Constructor<T> newConstructorForSerialization(Class<T> aClass, Constructor<?> cons);

    /**
     * Return a no-arg constructor for the specified class, based on the default constructor
     * for its nearest non-serializable base class.
     * @param aClass the class for which a constructor should be returned.
     */
    <T> Constructor<T> newConstructorForSerialization(Class<T> aClass);

    /**
     * Defines a class is a specified classloader.
     * @param className the name of the class
     * @param classBytes the byte code for the class
     * @param classLoader the classloader in which it is to be defined
     * @param protectionDomain the domain in which the class should be defined
     */
    Class<?> defineClass(String className, byte[] classBytes, ClassLoader classLoader, ProtectionDomain protectionDomain);

    /**
     * Returns true if the given class defines a static initializer method,
     * false otherwise.
     */
    boolean hasStaticInitializerForSerialization(Class<?> cl);

    /**
     * Returns a method handle to allow invocation of the specified class's writeObject method.
     * @param cl the class containing the method
     */
    MethodHandle writeObjectForSerialization(Class<?> cl) throws NoSuchMethodException, IllegalAccessException;

    /**
     * Returns a method handle to allow invocation of the specified class's readObject method.
     * @param cl the class containing the method
     */
    MethodHandle readObjectForSerialization(Class<?> cl) throws NoSuchMethodException, IllegalAccessException;

    /**
     * Return a new OptionalDataException instance.
     * @return a new OptionalDataException instance
     */
    OptionalDataException newOptionalDataExceptionForSerialization(boolean bool);
}
