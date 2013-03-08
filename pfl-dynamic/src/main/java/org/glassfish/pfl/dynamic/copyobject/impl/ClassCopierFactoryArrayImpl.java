/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Map ;

import java.lang.reflect.Array ;

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;

/** A factory used for creating ClassCopier instances.
 * An instance of this factory can be created and customized to
 * handle special copying semantics for certain classes.
 * This maintains a cache of ClassCopiers, so that a ClassCopier is
 * never created more than once for a particular class.
 */
public class ClassCopierFactoryArrayImpl implements ClassCopierFactory {
    
    // A full ClassCopierFactory for all classes.
    private final ClassCopierFactory classCopierFactory ;

    public ClassCopierFactoryArrayImpl( ClassCopierFactory ccf ) 
    {
	classCopierFactory = ccf ;
    }

    // Copy an array of Objects.  This would also work
    // for an array of primitives, but its more efficient to clone
    // arrays of primitives.  This is not static due to the
    // need to reference classCopierFactory.
    private ClassCopier arrayClassCopier = 
	new ClassCopierBase( "array" ) {
        @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		int alen = Array.getLength( source ) ;
		Object result = Array.newInstance( 
		    source.getClass().getComponentType(), alen ) ;
		return result ;
	    }

            @Override
	    public Object doCopy( Map<Object,Object> oldToNew, Object source,
		Object result ) throws ReflectiveCopyException 
	    {
		int alen = Array.getLength( source ) ;
		for (int ctr=0; ctr<alen; ctr++) {
		    Object aobj = Array.get( source, ctr ) ;

		    if (aobj != null) {
			// Must look up the Copier for each element
			// to handle polymorphic arrays
			ClassCopier copier = classCopierFactory.getClassCopier( 
			    aobj.getClass() ) ;
			aobj = copier.copy( oldToNew, aobj ) ;
		    }

		    Array.set( result, ctr, aobj ) ;
		}

		return result ;
	    }
	} ;

    private static ClassCopier booleanArrayClassCopier =
	new ClassCopierBase( "boolean" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		boolean[] obj = (boolean[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier byteArrayClassCopier =
	new ClassCopierBase( "byte" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		byte[] obj = (byte[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier charArrayClassCopier =
	new ClassCopierBase( "char" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		char[] obj = (char[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier shortArrayClassCopier =
	new ClassCopierBase( "short" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		short[] obj = (short[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier intArrayClassCopier =
	new ClassCopierBase( "int" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		int[] obj = (int[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier longArrayClassCopier =
	new ClassCopierBase( "long" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		long[] obj = (long[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier floatArrayClassCopier =
	new ClassCopierBase( "float" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		float[] obj = (float[])source ;
		return obj.clone() ;
	    } 
	} ;

    private static ClassCopier doubleArrayClassCopier =
	new ClassCopierBase( "double" ) {
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		double[] obj = (double[])source ;
		return obj.clone() ;
	    } 
	} ;

    @Override
    public ClassCopier getClassCopier( Class<?> cls )
    {
	Class<?> compType = cls.getComponentType() ;

	if (compType == null) {
            return null;
        }

	if (compType.isPrimitive()) {
	    // The primitives could be pre-registered in the cache, but
	    // I like having the handling of all arrays grouped together
	    // in the same place.  The result is basically lazy initialization
	    // of the ClassCopierFactoryCachingImpl instance.
	    if (compType == boolean.class) {
                return booleanArrayClassCopier;
            }
	    if (compType == byte.class) {
                return byteArrayClassCopier;
            }
	    if (compType == char.class) {
                return charArrayClassCopier;
            }
	    if (compType == short.class) {
                return shortArrayClassCopier;
            }
	    if (compType == int.class) {
                return intArrayClassCopier;
            }
	    if (compType == long.class) {
                return longArrayClassCopier;
            }
	    if (compType == float.class) {
                return floatArrayClassCopier;
            }
	    if (compType == double.class) {
                return doubleArrayClassCopier;
            }

	    return null ;
	} else {
	    return arrayClassCopier ; 
	}
    }
}
