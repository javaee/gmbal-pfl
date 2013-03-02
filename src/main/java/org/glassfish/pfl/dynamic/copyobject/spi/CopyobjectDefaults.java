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

package org.glassfish.pfl.dynamic.copyobject.spi ;

import org.glassfish.pfl.dynamic.copyobject.impl.FallbackObjectCopierImpl;
import org.glassfish.pfl.dynamic.copyobject.impl.JavaStreamObjectCopierImpl;
import org.glassfish.pfl.dynamic.copyobject.impl.ObjectCopierImpl;

public abstract class CopyobjectDefaults
{
    private CopyobjectDefaults() { }

    private static final ObjectCopier javaStream =
        new JavaStreamObjectCopierImpl() ;

    public static ObjectCopierFactory makeJavaStreamObjectCopierFactory( ) 
    {
	return new ObjectCopierFactory() {
	    public ObjectCopier make( )
	    {
		return javaStream ;
	    }
	} ;
    }

    private static final ObjectCopier referenceObjectCopier = new ObjectCopier() {
        @Override
        public Object copy(Object obj) throws ReflectiveCopyException {
            return obj ;
        }
    };

    private static ObjectCopierFactory referenceObjectCopierFactory = 
	new ObjectCopierFactory() {
	    public ObjectCopier make() 
	    {
		return referenceObjectCopier ;
	    }
	} ;

    /** Obtain the reference object "copier".  This does no copies: it just
     * returns whatever is passed to it.
     */
    public static ObjectCopierFactory getReferenceObjectCopierFactory()
    {
	return referenceObjectCopierFactory ;
    }

    /** Create a fallback copier factory from the two ObjectCopierFactory
     * arguments.  This copier makes an ObjectCopierFactory that creates
     * instances of a fallback copier that first tries an ObjectCopier
     * created from f1, then tries one created from f2, if the first
     * throws a ReflectiveCopyException.
     */
    public static ObjectCopierFactory makeFallbackObjectCopierFactory( 
	final ObjectCopierFactory f1, final ObjectCopierFactory f2 )
    {
	return new ObjectCopierFactory() {
            @Override
	    public ObjectCopier make() 
	    {
		ObjectCopier c1 = f1.make() ;
		ObjectCopier c2 = f2.make() ;
		return new FallbackObjectCopierImpl( c1, c2 ) ;
	    }
	} ;
    }

    /** Obtain the new reflective copier factory.  This is 3-4 times faster than the stream
     * copier, and about 10% faster than the old reflective copier.  It should
     * normally be used with a fallback copier, as there are some classes that simply
     * cannot be copied reflectively.
     */
    public static ObjectCopierFactory makeReflectObjectCopierFactory( ) 
    {
	return new ObjectCopierFactory() {
            @Override
	    public ObjectCopier make( )
	    {
		return new ObjectCopierImpl( ) ;
	    }
	} ;
    }
}
