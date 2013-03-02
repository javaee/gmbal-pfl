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

package org.glassfish.pfl.dynamic.copyobject.impl ;

import org.glassfish.pfl.basic.contain.Pair;
import org.glassfish.pfl.dynamic.codegen.spi.Expression;

import java.lang.reflect.Modifier ;
import java.lang.reflect.Field ;

import java.security.AccessController ;
import java.security.PrivilegedAction ;
import java.security.ProtectionDomain ;

import java.util.Map ;
import java.util.Properties ;

import sun.corba.Bridge ;

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;

import org.glassfish.pfl.dynamic.codegen.spi.Type ;

import static java.lang.reflect.Modifier.* ;
import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper.* ;

/** Experimental class that generates a ClassFieldCopier using the codegen library.
 */
public class CodegenCopierGenerator {
    private static final String DEBUG = "false" ;

    private Class<?>	       classToCopy ;
    private String	       className ;

    private static final Bridge bridge = AccessController.doPrivileged(
	new PrivilegedAction<Bridge>() {
        @Override
	    public Bridge run() {
		return Bridge.get() ;
	    }
	} 
    ) ;

    public CodegenCopierGenerator( String className, Class<?> classToCopy ) {
	this.className = className ;
	this.classToCopy = classToCopy ;
    }

    public Class<?> create( ProtectionDomain pd, ClassLoader cl ) {
	_clear() ;

	Pair<String,String> pc = splitClassName( className ) ;
	_package( pc.first() ) ;

	Type PipelineClassCopierFactory = Type.type(
	    PipelineClassCopierFactory.class ) ;
	Type ReflectiveCopyException = Type.type( 
	    ReflectiveCopyException.class ) ;
	Type CodegenCopierBase = Type.type( 
	    CodegenCopierBase.class ) ;
	Type ClassFieldCopier = Type.type( 
	    ClassCopierOrdinaryImpl.ClassFieldCopier.class ) ;
	Type Map = Type.type(
	    Map.class ) ;

	_class( PUBLIC, pc.second(), CodegenCopierBase ) ;
	    // XXX Can we get rid of this, and instead just emit copy calls for
	    // superclass fields?
	    Expression superCopier = _data( PRIVATE, ClassFieldCopier, "superCopier" ) ;

	    _constructor( PUBLIC ) ;
		Expression factory = _arg( PipelineClassCopierFactory, "factory" ) ;
		Expression sc = _arg( ClassFieldCopier, "superCopier" ) ;
	    _body() ;
		_super( factory ) ;
		_assign( superCopier, sc ) ;
	    _end() ;

	    _method( PUBLIC, _void(), "copy", ReflectiveCopyException ) ;
		Expression oldToNew = _arg( Map, "oldToNew" ) ;
		Expression src      = _arg( _Object(), "src" ) ;
		Expression dest     = _arg( _Object(), "dest" ) ;
		Expression debug    = _arg( _boolean(), "debug" ) ;
	    _body() ;
		_if(_ne(superCopier, _null())) ;
		    _expr(_call( superCopier, "copy", oldToNew, src, dest, debug )) ; 
		_end() ;

		// Generate code to copy fields of this object
		for (Field fld : classToCopy.getDeclaredFields()) {
		    if (!Modifier.isStatic( fld.getModifiers())) {
			long offset = bridge.objectFieldOffset( fld ) ;
			String mname = getCopyMethodName( fld.getType() ) ;
			if (mname.equals( "copyObject" ))  {
			    _expr(_call( _this(), mname, oldToNew, _const(offset), 
				src, dest )) ;
			} else {
			    _expr(_call( _this(), mname, _const(offset), 
				src, dest )) ;
			}
		    }
		}
	    _end() ;
	_end() ;

	Properties debugProps = new Properties() ;
	debugProps.setProperty( DUMP_AFTER_SETUP_VISITOR, DEBUG ) ;
	debugProps.setProperty( TRACE_BYTE_CODE_GENERATION, DEBUG ) ;
	debugProps.setProperty( USE_ASM_VERIFIER, DEBUG ) ;
	
	Class<?> cls = _generate( cl, pd, debugProps ) ;
	return cls ;
    }

    private String getCopyMethodName( Class<?> fieldType ) {
	if (fieldType.equals( Boolean.TYPE )) {
            return "copyBoolean";
        } else if (fieldType.equals( Byte.TYPE )) {
            return "copyByte";
        } else if (fieldType.equals( Character.TYPE )) {
            return "copyChar";
        } else if (fieldType.equals( Integer.TYPE )) {
            return "copyInt";
        } else if (fieldType.equals( Short.TYPE )) {
            return "copyShort";
        } else if (fieldType.equals( Long.TYPE )) {
            return "copyLong";
        } else if (fieldType.equals( Float.TYPE )) {
            return "copyFloat";
        } else if (fieldType.equals( Double.TYPE )) {
            return "copyDouble";
        } else {
            return "copyObject";
        }
    }
}
