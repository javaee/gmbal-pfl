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

package dynamic.codegen.test ;

import org.glassfish.pfl.dynamic.codegen.spi.ClassGenerator;
import org.glassfish.pfl.dynamic.codegen.spi.MethodInfo;
import org.glassfish.pfl.dynamic.codegen.spi.Type;
import org.glassfish.pfl.dynamic.codegen.spi.ClassInfo;
import java.util.Map ;
import java.util.Set ;

import dynamic.codegen.ClassGeneratorFactory ;

import static java.lang.reflect.Modifier.* ;

import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper.* ;

/** This class implements the Constants interface.  It parses the
 * name of each method declared in constants to determine what value
 * to return.  The purpose of this test is to check that the correct
 * code is being generated for constants.  This is worth checking because
 * the code generator emits the various ICONST/BIPUSH/SIPUSH/LDC bytecodes
 * as needed.  An incorrect choice of bytecode leads to bad results.
 */
public class Constants_gen implements ClassGeneratorFactory {
    private static final String RETURN = "return" ;
    private static final String RETURN_MINUS = "returnMinus" ;
    public String className() {
	return "ConstantsImpl" ;
    }

    public static int getValue( String methodName ) {
	boolean isNegative = false ;
	String numString = "" ;
	if (methodName.startsWith( RETURN_MINUS )) {
	    numString = methodName.substring( RETURN_MINUS.length() ) ;
	    isNegative = true ;
	} else if (methodName.startsWith( RETURN )) {
	    numString = methodName.substring( RETURN.length() ) ;
	} else {
	    throw new RuntimeException( 
		"Bad method methodName " + methodName + " in Constants" ) ;
	}

	int value = Integer.parseInt( numString ) ;
	if (isNegative)
	    value = -value ;

	return value ; 
    }

    private void makeTestMethods( Type interf ) {
	ClassInfo cinfo = interf.classInfo() ;

	Map<String,Set<MethodInfo>> minfoMap = cinfo.methodInfoByName() ;
	for (Set<MethodInfo> minfos : minfoMap.values()) {
	    for (MethodInfo minfo : minfos) {
		String name = minfo.name() ;
		int value = getValue( name ) ;

		_method( PUBLIC, _int(), name ) ; 
		_body() ;
		    _return( _const( value ) ) ;
		_end() ;
	    }
	}
    }

    public ClassGenerator evaluate() {
	_clear() ;
	_setClassLoader( Thread.currentThread().getContextClassLoader() ) ;
	_package( "dynamic.codegen.gen" ) ;
	Type Constants = _import( "dynamic.codegen.lib.Constants" ) ;

	_class( PUBLIC, className(), _Object(), Constants ) ;
	    // Simple default constructor
	    _constructor( PUBLIC ) ;
	    _body() ;
		_expr(_super(_s(_void()))) ;
	    _end() ;

	    // generate all of the test methods
	    makeTestMethods( Constants ) ;
	_end() ; // of Const_gen class

	return _classGenerator() ;
   }
}
