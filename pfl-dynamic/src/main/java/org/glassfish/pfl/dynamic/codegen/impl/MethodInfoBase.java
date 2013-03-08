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

import java.lang.reflect.Method ;
import java.lang.reflect.Constructor ;

import java.util.List ;
import java.util.ArrayList ;

import org.glassfish.pfl.dynamic.codegen.spi.MethodInfo ;
import org.glassfish.pfl.dynamic.codegen.spi.ClassInfo ;
import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.Signature ;
import org.glassfish.pfl.dynamic.codegen.spi.Variable ;

public abstract class MethodInfoBase extends MemberInfoBase
    implements MethodInfo {

    protected Type rtype ;
    protected List<Type> exceptions ;
    protected List<Variable> arguments ;
    protected boolean isConstructor ;

    private Signature sig ;
    private boolean sigIsCached ;

    private int hashValue ;
    private boolean hashIsCached ;

    protected MethodInfoBase( ClassInfo cinfo, int modifiers ) {
	this( cinfo, modifiers, Type._void(), CodeGeneratorUtil.CONSTRUCTOR_METHOD_NAME ) ;
	this.isConstructor = true ;
    }

    protected MethodInfoBase( ClassInfo cinfo, int modifiers, Type rtype, String name ) {
	super( cinfo, modifiers, name ) ;
	this.rtype = rtype ;
	this.exceptions = new ArrayList<Type>() ;
	this.arguments = new ArrayList<Variable>() ;

	sig = null ;
	sigIsCached = false ;

	hashValue = 0 ;
	hashIsCached = false ;

	this.isConstructor = false ;
    }

    public boolean isConstructor() {
	return isConstructor ;
    }

    public Type returnType() {
	return rtype ;
    }

    public List<Type> exceptions() {
	return exceptions ;
    }

    public List<Variable> arguments() {
	return this.arguments ;
    }

    public synchronized Signature signature() {
	if (!sigIsCached) {
	    List<Type> argTypes = new ArrayList<Type>(arguments.size()) ;
	    for (Variable var : arguments)
		argTypes.add( ((VariableInternal)var).type() ) ;
	    sig = Signature.make( rtype, argTypes ) ;
	}

	return sig ;
    }

    public Method getMethod() {
	return null ;
    }

    public Constructor getConstructor() {
	return null ;
    }

    public boolean equals( Object obj ) {
	if (obj == this)
	    return true ;

	if (!(obj instanceof MethodInfo))
	    return false ;
    
	MethodInfo other = MethodInfo.class.cast( obj ) ;

	if (hashCode() != other.hashCode())
	    return false ;

	if (!super.equals( obj ))
	    return false ;

	if (!signature().equals( other.signature() ))
	    return false ;

	if (!exceptions().equals( other.exceptions() )) 
	    return false ;

	return true ;
    }

    public synchronized void clearHashCode() {
	hashIsCached = false ;
	hashValue = 0 ;
    }

    public synchronized int hashCode() {
	if (!hashIsCached) {
	    hashValue = super.hashCode() ;
	    hashValue ^= signature().hashCode() ;
	    hashValue ^= exceptions().hashCode() ;

	    hashIsCached = true ;
	}

	return hashValue ;
    }
}
