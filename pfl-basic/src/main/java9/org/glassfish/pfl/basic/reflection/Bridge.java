/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
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

import sun.reflect.ReflectionFactory;

import java.io.OptionalDataException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Optional;
import java.util.stream.Stream;

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
public final class Bridge extends BridgeBase {
    private static final Permission GET_BRIDGE_PERMISSION = new BridgePermission("getBridge");
    private static Bridge bridge = null;

    private final ReflectionFactory reflectionFactory;



    private Bridge() {
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
            sman.checkPermission(GET_BRIDGE_PERMISSION);
        }

        if (bridge == null) {
            bridge = new Bridge();
        }

        return bridge;
    }

    private final StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    // New implementation for Java 9, supplied by Alan Bateman
    @Override
    public final ClassLoader getLatestUserDefinedLoader() {
        // requires getClassLoader permission => needs doPrivileged.
        PrivilegedAction<ClassLoader> pa = () ->
                stackWalker.walk(this::getLatestUserDefinedLoaderFrame)
                        .map(sf -> sf.getDeclaringClass().getClassLoader())
                        .orElseGet(ClassLoader::getPlatformClassLoader);
        return AccessController.doPrivileged(pa);
    }

    private Optional<StackWalker.StackFrame> getLatestUserDefinedLoaderFrame(Stream<StackWalker.StackFrame> stream) {
        return stream.filter(this::isUserLoader).findFirst();
    }

    private boolean isUserLoader(StackWalker.StackFrame sf) {
        ClassLoader cl = sf.getDeclaringClass().getClassLoader();
        if (cl == null) return false;

        ClassLoader platformClassLoader = ClassLoader.getPlatformClassLoader();
        while (platformClassLoader != null && cl != platformClassLoader) platformClassLoader = platformClassLoader.getParent();
        return cl != platformClassLoader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Constructor<T> newConstructorForExternalization(Class<T> cl) {
        return (Constructor<T>) reflectionFactory.newConstructorForExternalization( cl );
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Constructor<T> newConstructorForSerialization(Class<T> aClass, Constructor<?> cons) {
        return (Constructor<T>) reflectionFactory.newConstructorForSerialization(aClass, cons);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Constructor<T> newConstructorForSerialization(Class<T> aClass) {
        return (Constructor<T>) reflectionFactory.newConstructorForSerialization( aClass );
    }

    /**
     * Returns true if the given class defines a static initializer method,
     * false otherwise.
     */
    @Override
    public final boolean hasStaticInitializerForSerialization(Class<?> cl) {
        return reflectionFactory.hasStaticInitializerForSerialization(cl);
    }

    @Override
    public final MethodHandle writeObjectForSerialization(Class<?> cl) {
        return reflectionFactory.writeObjectForSerialization(cl);
    }

    @Override
    public final MethodHandle readObjectForSerialization(Class<?> cl) {
        return reflectionFactory.readObjectForSerialization(cl);
    }

    public final MethodHandle readObjectNoDataForSerialization(Class<?> cl) {
        return reflectionFactory.readObjectNoDataForSerialization(cl);
    }

    @Override
    public final MethodHandle readResolveForSerialization(Class<?> cl) {
        return reflectionFactory.readResolveForSerialization(cl);
    }

    @Override
    public final MethodHandle writeReplaceForSerialization(Class<?> cl) {
        return reflectionFactory.writeReplaceForSerialization(cl);
    }

    @Override
    public final OptionalDataException newOptionalDataExceptionForSerialization(boolean bool) {
        return reflectionFactory.newOptionalDataExceptionForSerialization(bool);
    }

    @Override
    public Field toAccessibleField(Field field, Class callingClass) {
        return isClassOpenToModule(field.getDeclaringClass(), callingClass.getModule())
              ? super.toAccessibleField(field, callingClass)
              : null;
    }

    private boolean isClassOpenToModule(Class<?> candidateClass, Module callingModule) {
        return callingModule.isNamed()
              ? candidateClass.getModule().isOpen(candidateClass.getPackageName(), callingModule)
              : candidateClass.getModule().isOpen(candidateClass.getPackageName());
    }

    @Override
    public Method toAccessibleMethod(Method method, Class callingClass) {
        return isClassOpenToModule(method.getDeclaringClass(), callingClass.getModule())
              ? super.toAccessibleMethod(method, callingClass)
              : null;
    }
}
