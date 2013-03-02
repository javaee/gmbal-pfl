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

package org.glassfish.pfl.dynamic.codegen.impl;

import java.util.List ;
import java.util.ArrayList ;
import java.util.Map ;
import java.util.HashMap ;
import java.util.Properties ;

import java.io.PrintStream ;
import java.io.IOException ;

import java.security.ProtectionDomain ;

import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.ImportList ;

import org.glassfish.pfl.dynamic.codegen.impl.Visitor ;
import org.glassfish.pfl.dynamic.codegen.impl.CodegenPrinter ;
import org.glassfish.pfl.dynamic.codegen.impl.StatementBase ;
import org.glassfish.pfl.dynamic.codegen.impl.SourceStatementVisitor ;
import org.glassfish.pfl.dynamic.codegen.impl.ASMUtil ;

/** Class used to define classes and interfaces, and to generator source or 
 * byte code from the resulting definitions.  This is the factory for the
 * codegen framework.
 */
public final class CodeGenerator {
    private CodeGenerator() {} 

    /** Define a ClassGeneratorImpl for a class.
     */
    public static ClassGeneratorImpl defineClass( int modifiers, String name,
	Type superType, List<Type> impls ) {
	return new ClassGeneratorImpl( modifiers, name, superType,
	    impls ) ;
    }

    /** Define a ClassGeneratorImpl for an interface.
     */
    public static ClassGeneratorImpl defineInterface( int modifiers, String name,
	List<Type> impls ) {
	return new ClassGeneratorImpl( modifiers, name, impls ) ;
    }

    /** Convert the Java class or interface defined by ClassGeneratorImpl into an array
     * of bytecodes.
     */
    public static byte[] generateBytecode( ClassGeneratorImpl cg, ClassLoader cl,
	ImportList imports, Properties options, PrintStream debugOutput ) {

	return ASMUtil.generate( cl, cg, imports, options, debugOutput ) ;
    }

    /** Write a source code representation of the class or interface defined by
     * cg to the PrintStream ps.
     */
    public static void generateSourceCode( PrintStream ps, 
	ClassGeneratorImpl cg, ImportList imports,
	Properties options ) throws IOException {

	ASMUtil.generateSourceCode( ps, cg, imports, options ) ;
    }

    /** Write a source code representation of the class or interface defined by
     * cg to a file in the SOURCE_GENERATION_DIRECTORY specified in options.
     */
    public static void generateSourceCode( String sdir, 
	ClassGeneratorImpl cg, ImportList imports,
	Properties options ) throws IOException {

	ASMUtil.generateSourceCode( sdir, cg, imports, options ) ;
    }
} 
