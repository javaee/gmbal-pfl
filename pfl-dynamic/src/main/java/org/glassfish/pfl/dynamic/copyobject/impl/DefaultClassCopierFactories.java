/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2013 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.pfl.dynamic.copyobject.impl;

import java.lang.reflect.Method ;
import java.lang.reflect.Modifier ;

import java.util.Map ;

import java.security.PrivilegedAction;
import java.security.AccessController;

import org.glassfish.pfl.basic.concurrent.WeakHashMapSafeReadLock;

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;

public abstract class DefaultClassCopierFactories
{
    // Note that the FastCache is NOT a weak or soft
    // reference based cache, so it can hold onto 
    // references to classes indefinitely, pinning ClassLoader
    // instances in memory.  Until this is fixed, FastCache is
    // for testing only.
    public static final boolean USE_FAST_CACHE = false ;

    private DefaultClassCopierFactories() {}

    /** Create a ClassCopierFactory that handles arrays.  This 
     * ClassCopierFactory will return null on a get call if the 
     * class is not an array.
     */
    public static ClassCopierFactory makeArrayClassCopierFactory( 
	ClassCopierFactory ccf ) 
    {
	return new ClassCopierFactoryArrayImpl( ccf ) ;
    }

    private static final Class<?>[] SAFE_TO_COPY = new Class<?>[] {
	java.util.TimeZone.class,
	java.lang.Throwable.class,
	java.lang.reflect.Proxy.class
    } ;

    public static ClassCopierFactory makeOrdinaryClassCopierFactory( 
	final PipelineClassCopierFactory ccf )
    {
	return new ClassCopierFactory() {
            @Override
	    public ClassCopier getClassCopier( Class<?> cls ) 
                throws ReflectiveCopyException {

		if (notCopyable( cls )) {
		    return DefaultClassCopiers.getErrorClassCopier() ;
		} else {
		    return new ClassCopierOrdinaryImpl( ccf, cls ) ;
		}
	    }

	    // Returns true if class is (specially) known to be safe
	    // to copy, even if notCopyable(cls) is true.
	    private boolean safe( Class cls ) 
	    {
		for (Class klass : SAFE_TO_COPY) {
		    if (cls == klass) {
			return true ;
		    }
		}

		return false ;
	    }

	    // Scan the methods in cls and all its superclasses (except 
	    // Object!) to see if finalize is defined, or if there are
	    // any native methods.  Classes with such methods are not copyable,
	    // except for a few known classes that ARE safe to copy,
	    // and cause this method to return true.  If there are not 
	    // problematic methods, return false and allow the copy
	    // (at this level: references to non-copyable objects will
	    // cause ReflectiveCopyException to be thrown where ever they
	    // occur).
	    private boolean notCopyable( Class<?> cls ) {
		Class<?> current = cls ;
                Method[] methods ;
		while (current != Object.class) {
		    if (safe(current)) {
                        return false;
                    }
                    // Fix GLASSFISH-18310
                    if (System.getSecurityManager() == null) {
                        methods = current.getDeclaredMethods();
                    } else {
                        final Class<?> _current = current;
                        methods = (Method[]) AccessController.doPrivileged(new PrivilegedAction() {
                            public Object run() {
                                return _current.getDeclaredMethods();
                            }
                        });
                    }
                    for (Method m : methods) {
                        if ((m.getName().equals("finalize"))
                                || Modifier.isNative(m.getModifiers())) {
                            return true;
                        }
                    }                  

		    current = current.getSuperclass() ;
		}

		return false ;
	    }
	} ;
    }

    public static CachingClassCopierFactory makeCachingClassCopierFactory( )
    {
	return new CachingClassCopierFactory() 
	{
	    private Map<Class<?>,ClassCopier> cache = USE_FAST_CACHE ?
		new FastCache<Class<?>,ClassCopier>(
                    new WeakHashMapSafeReadLock<Class<?>,ClassCopier>() ) :
		new WeakHashMapSafeReadLock<Class<?>,ClassCopier>() ;

            @Override
	    public void put( Class<?> cls, ClassCopier copier )
	    {
		cache.put( cls, copier ) ;
	    }

            @Override
	    public ClassCopier getClassCopier( Class<?> cls )
	    {
		return cache.get(cls) ;
	    }
	};
    }
    
    public static ClassCopierFactory getNullClassCopierFactory()
    {
	return new ClassCopierFactory()
	{
            @Override
	    public ClassCopier getClassCopier( Class cls ) 
	    {
		return null ;
	    }
	} ;
    }

    public static PipelineClassCopierFactory getPipelineClassCopierFactory()
    {
	return new ClassCopierFactoryPipelineImpl() ;
    }
}
