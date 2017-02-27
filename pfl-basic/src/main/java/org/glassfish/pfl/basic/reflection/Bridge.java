/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2017 Oracle and/or its affiliates. All rights reserved.
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

import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Objects;

/**
 * This class provides the methods for fundamental JVM operations
 * needed in the ORB that are not part of the public Java API.  This includes:
 * <ul>
 * <li>throwException, which can throw undeclared checked exceptions.
 * This is needed to handle throwing arbitrary exceptions across a standardized OMG interface that (incorrectly) does not specify appropriate exceptions.</li>
 * <li>putXXX/getXXX methods that allow unchecked access to fields of objects.
 * This is used for setting uninitialzed non-static final fields (which is
 * impossible with reflection) and for speed.</li>
 * <li>objectFieldOffset to obtain the field offsets for use in the putXXX/getXXX methods</li>
 * <li>newConstructorForSerialization to get the special constructor required for a
 * Serializable class</li>
 * <li>latestUserDefinedLoader to get the latest user defined class loader from
 * the call stack as required by the RMI-IIOP specification (really from the
 * JDK 1.1 days)</li>
 * </ul>
 * The code that calls Bridge.get() must have the following Permissions:
 * <ul>
 * <li>RuntimePermission "reflectionFactoryAccess"</li>
 * <li>BridgePermission "getBridge"</li>
 * <li>ReflectPermission "suppressAccessChecks"</li>
 * </ul>
 * <p>
 * All of these permissions are required to obtain and correctly initialize
 * the instance of Bridge.  No security checks are performed on calls
 * made to Bridge instance methods, so access to the Bridge instance
 * must be protected.
 * <p>
 * This class is a singleton (per ClassLoader of course).  Access to the
 * instance is obtained through the Bridge.get() method.
 */
public final class Bridge implements BridgeOperations {
    private static final Permission getBridgePermission = new BridgePermission("getBridge");
    private static Bridge bridge = null;

    // latestUserDefinedLoader() is a private static method
    // in ObjectInputStream in JDK 1.3 through 1.5.
    // We use reflection in a doPrivileged block to get a
    // Method reference and make it accessible.
    private final Method latestUserDefinedLoaderMethod;
    private final Unsafe unsafe;
    private final ReflectionFactory reflectionFactory;

    private Method getLatestUserDefinedLoaderMethod() {
        return AccessController.doPrivileged(
                new PrivilegedAction<Method>() {
                    @SuppressWarnings("unchecked")
                    public Method run() {
                        Method result;

                        try {
                            Class io = ObjectInputStream.class;
                            result = io.getDeclaredMethod("latestUserDefinedLoader");
                            result.setAccessible(true);
                        } catch (NoSuchMethodException nsme) {
                            throw new Error("java.io.ObjectInputStream latestUserDefinedLoader " + nsme, nsme);
                        }

                        return result;
                    }
                }
        );
    }

    private Unsafe getUnsafe() {
        Field fld = AccessController.doPrivileged(
                new PrivilegedAction<Field>() {
                    public Field run() {
                        try {
                            Class unsafeClass = sun.misc.Unsafe.class;
                            Field fld = unsafeClass.getDeclaredField("theUnsafe");
                            fld.setAccessible(true);
                            return fld;
                        } catch (NoSuchFieldException exc) {
                            throw new Error("Could not access Unsafe", exc);
                        }
                    }
                }
        );

        Unsafe theUnsafe;

        try {
            theUnsafe = Unsafe.class.cast(fld.get(null));
        } catch (Throwable t) {
            throw new Error("Could not access Unsafe", t);
        }

        return theUnsafe;
    }


    @SuppressWarnings("unchecked")
    private Bridge() {
        latestUserDefinedLoaderMethod = getLatestUserDefinedLoaderMethod();
        unsafe = getUnsafe();
        reflectionFactory = ReflectionFactory.getReflectionFactory();
    }

    /**
     * Fetch the Bridge singleton.  This requires the following
     * permissions:
     * <ul>
     * <li>RuntimePermission "reflectionFactoryAccess"</li>
     * <li>BridgePermission "getBridge"</li>
     * <li>ReflectPermission "suppressAccessChecks"</li>
     * </ul>
     *
     * @return The singleton instance of the Bridge class
     * @throws SecurityException if the caller does not have the
     *                           required permissions and the caller has a non-null security manager.
     */
    public static synchronized Bridge get() {
        SecurityManager sman = System.getSecurityManager();
        if (sman != null) {
            sman.checkPermission(getBridgePermission);
        }

        if (bridge == null) {
            bridge = new Bridge();
        }

        return bridge;
    }

    @Override
    public final ClassLoader getLatestUserDefinedLoader() {
        try {
            // Invoke the ObjectInputStream.latestUserDefinedLoader method
            return (ClassLoader) latestUserDefinedLoaderMethod.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException ite) {
            throw new Error(getClass().getName() + ".latestUserDefinedLoader: " + ite, ite);
        }
    }

    @Override
    public final int getInt(Object o, long offset) {
        return unsafe.getInt(o, offset);
    }

    @Override
    public final void putInt(Object o, long offset, int x) {
        unsafe.putInt(o, offset, x);
    }

    @Override
    public final Object getObject(Object o, long offset) {
        return unsafe.getObject(o, offset);
    }

    @Override
    public final void putObject(Object o, long offset, Object x) {
        unsafe.putObject(o, offset, x);
    }

    @Override
    public final boolean getBoolean(Object o, long offset) {
        return unsafe.getBoolean(o, offset);
    }

    @Override
    public final void putBoolean(Object o, long offset, boolean x) {
        unsafe.putBoolean(o, offset, x);
    }

    @Override
    public final byte getByte(Object o, long offset) {
        return unsafe.getByte(o, offset);
    }

    @Override
    public final void putByte(Object o, long offset, byte x) {
        unsafe.putByte(o, offset, x);
    }

    @Override
    public final short getShort(Object o, long offset) {
        return unsafe.getShort(o, offset);
    }

    @Override
    public final void putShort(Object o, long offset, short x) {
        unsafe.putShort(o, offset, x);
    }

    @Override
    public final char getChar(Object o, long offset) {
        return unsafe.getChar(o, offset);
    }

    @Override
    public final void putChar(Object o, long offset, char x) {
        unsafe.putChar(o, offset, x);
    }

    @Override
    public final long getLong(Object o, long offset) {
        return unsafe.getLong(o, offset);
    }

    @Override
    public final void putLong(Object o, long offset, long x) {
        unsafe.putLong(o, offset, x);
    }

    @Override
    public final float getFloat(Object o, long offset) {
        return unsafe.getFloat(o, offset);
    }

    @Override
    public final void putFloat(Object o, long offset, float x) {
        unsafe.putFloat(o, offset, x);
    }

    @Override
    public final double getDouble(Object o, long offset) {
        return unsafe.getDouble(o, offset);
    }

    @Override
    public final void putDouble(Object o, long offset, double x) {
        unsafe.putDouble(o, offset, x);
    }

    @Override
    public final long objectFieldOffset(Field f) {
        return unsafe.objectFieldOffset(f);
    }

    @Override
    public final void throwException(Throwable ee) {
        unsafe.throwException(ee);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Constructor<T> newConstructorForExternalization(Class<T> cl) {
        try {
            Constructor<?> cons = cl.getDeclaredConstructor();
            cons.setAccessible(true);
            return isPublic(cons) ? (Constructor<T>) cons : null;
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private static boolean isPublic(Constructor<?> cons) {
        return (cons.getModifiers() & Modifier.PUBLIC) != 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Constructor<T> newConstructorForSerialization(Class<T> aClass, Constructor<?> cons) {
        Constructor newConstructor = reflectionFactory.newConstructorForSerialization(aClass, cons);
        newConstructor.setAccessible(true);
        return (Constructor<T>) newConstructor;
    }

    @Override
    public <T> Constructor<T> newConstructorForSerialization(Class<T> aClass) {
        Class<?> baseClass = getNearestNonSerializableBaseClass(aClass);
        if (baseClass == null) return null;

        try {
            Constructor<?> cons = baseClass.getDeclaredConstructor();
            if (isPrivate(cons) || !isAccessibleFromSubclass(cons, aClass, baseClass)) return null;

            return newConstructorForSerialization(aClass, cons);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private static <T> Class<?> getNearestNonSerializableBaseClass(Class<T> clazz) {
        Class<?> baseClass = clazz;

        while (Serializable.class.isAssignableFrom(baseClass))
            if ((baseClass = baseClass.getSuperclass()) == null) return null;

        return baseClass;
    }

    private static boolean isAccessibleFromSubclass(Constructor<?> constructor, Class<?> clazz, Class<?> baseClass) {
        return isPublicOrProtected(constructor) || inSamePackage(clazz, baseClass);
    }

    private static boolean inSamePackage(Class<?> clazz, Class<?> baseClass) {
        return Objects.equals(clazz.getPackage(), baseClass.getPackage());
    }

    private static boolean isPublicOrProtected(Constructor<?> constructor) {
        return (constructor.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0;
    }

    private static boolean isPrivate(Constructor<?> cons) {
        return (cons.getModifiers() & Modifier.PRIVATE) != 0;
    }
}
