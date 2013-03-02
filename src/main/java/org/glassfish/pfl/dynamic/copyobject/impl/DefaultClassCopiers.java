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
import java.util.Iterator ;

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;

public abstract class DefaultClassCopiers
{
    private DefaultClassCopiers() {}

    private static ClassCopier identityClassCopier = 
	new ClassCopierBase( "identity" ) 
	{
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		return source ;
	    }
	} ;

    /** Return a ClassCopier that simple returns its argument as its
     *  result.
     */
    public static ClassCopier getIdentityClassCopier()
    {
	return identityClassCopier ;
    }

    private static ClassCopier errorClassCopier = 
	// Set isReflective true to get better error messages.
	new ClassCopierBase( "error", true ) 
	{
            @Override
	    public Object createCopy( 
		Object source ) throws ReflectiveCopyException 
	    {
		throw new ReflectiveCopyException( 
		    "Cannot copy class " + source.getClass() ) ;
	    }
	} ;

    /** Return a ClassCopier that always raises a ReflectiveCopyException
     * whenever its copy method is called.
     */
    public static ClassCopier getErrorClassCopier()
    {
	return errorClassCopier ;
    }

    /** Return a ClassCopier that is suitable for instances of the Map 
     * interface.  This should be limited to HashMap, Hashtable, 
     * IdentityHashMap, and TreeMap.
     */
    public static ClassCopier makeMapClassCopier( 
	final ClassCopierFactory ccf ) 
    {
	return new ClassCopierBase( "map" ) 
	{
            @Override
	    public Object createCopy( Object source ) 
		throws ReflectiveCopyException
	    {
		try {
		    return source.getClass().newInstance() ;
		} catch (Exception exc) {
		    throw new ReflectiveCopyException( 
			"MapCopier could not copy " + source.getClass(), 
			exc ) ;
		}
	    }

	    private Object myCopy( Map<Object,Object> oldToNew,
		Object obj ) throws ReflectiveCopyException
	    {
		if (obj == null) {
                    return null;
                }

		Class cls = obj.getClass() ;
		ClassCopier copier = ccf.getClassCopier( cls ) ;
		return copier.copy( oldToNew, obj ) ;
	    }

            @Override
	    public Object doCopy( Map<Object,Object> oldToNew,
		Object source, Object result ) throws ReflectiveCopyException
	    {
		Map sourceMap = (Map)source ;
		Map resultMap = (Map)result ;

		Iterator iter = sourceMap.entrySet().iterator() ;
		while (iter.hasNext()) {
		    Map.Entry entry = (Map.Entry)(iter.next()) ;

		    Object key = entry.getKey() ;
		    Object newKey = myCopy( oldToNew, key ) ;

		    Object value = entry.getValue() ;
		    Object newValue = myCopy( oldToNew, value ) ;

		    resultMap.put( newKey, newValue ) ;
		}

		return result ;
	    }
	} ;
    }
}
