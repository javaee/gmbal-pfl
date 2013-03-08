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

import java.lang.reflect.Modifier ;

import org.glassfish.pfl.dynamic.codegen.spi.Signature ;
import org.glassfish.pfl.dynamic.codegen.spi.Type ;
import org.glassfish.pfl.dynamic.codegen.spi.Variable ;
import org.glassfish.pfl.dynamic.codegen.spi.MemberInfo ;
import org.glassfish.pfl.dynamic.codegen.spi.ClassInfo ;

public class MemberInfoBase implements MemberInfo {
    private ClassInfo myClassInfo ;
    private int modifiers ;
    private String name ;

    public MemberInfoBase( ClassInfo myClassInfo, int modifiers,
	String name ) {

	this.myClassInfo = myClassInfo ;
	this.modifiers = modifiers ;
	this.name = name ;
    }

    public ClassInfo myClassInfo() {
	return this.myClassInfo ;
    }

    public int modifiers() {
	return this.modifiers ;
    }

    public String name() {
	return this.name ;
    }

    public boolean isAccessibleInContext( ClassInfo definingClass,
	ClassInfo accessClass ) {

	if (Modifier.isPublic( modifiers )) {
	    return true ;
	}

	if (Modifier.isPrivate( modifiers)) {
	    return myClassInfo.name().equals( definingClass.name() ) ;
	}

	if (Modifier.isProtected( modifiers)) {
	    if (myClassInfo.pkgName().equals( definingClass.pkgName())) {
		return true ;
	    } else {
		return definingClass.isSubclass( myClassInfo ) &&
		    accessClass.isSubclass( definingClass ) ;
	    }
	}

	// check default access
	return myClassInfo.pkgName().equals( definingClass.pkgName() ) ;
    }

    public int hashCode() {
	return name.hashCode() ^ modifiers ;
    }

    public boolean equals( Object obj ) {
	if (!(obj instanceof MemberInfo))
	    return false ;

	if (obj == this) 
	    return true ;

	MemberInfo other = MemberInfo.class.cast( obj ) ;

	return name.equals(other.name()) &&
	    modifiers == other.modifiers() ; 
    }

    public String toString() {
	return this.getClass().getName() + "[" + Modifier.toString( modifiers ) 
	    + name + "]" ;
    }
}

