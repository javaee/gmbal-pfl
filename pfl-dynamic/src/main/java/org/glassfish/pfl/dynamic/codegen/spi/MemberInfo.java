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

package org.glassfish.pfl.dynamic.codegen.spi;

/** An interface that provides information common to all kinds of class
 * members.  This includes data members (represented by FieldInfo) and
 * methods and constructors (represented by MethodInfo).  This can be
 * used to describe both MethodGenerators that are used to generate code
 * and pre-existing Java classes.
 */
public interface MemberInfo {
    /** Return the ClassInfo of the class that contains this
     * member.
     */
    ClassInfo myClassInfo() ;

    /** Return the modifiers on this member
     */
    int modifiers() ;

    /** Return the name of this member.
     */
    String name() ;

    /** Returns true iff this member is accessible in the context
     * defined by definingClass (the class containing the 
     * reference to the member) and accessClass (the type of the
     * expression used to access this member).  This works as follows:
     * <ul>
     * <li>If modifiers() contains PUBLIC, the access is permitted.
     * <li>If modifiers() contains PRIVATE, the access is permitted
     * iff myClassInfo().name() is the same as definingClass.name().
     * <li>If modifiers() contains PROTECTED, the access is permitted as follows:
     * <ul>
     * <li>If myClassInfo().pkgName() is the same as definingClass.pkgName(),
     * the access is permitted.
     * <li>Otherwise, the access is permitted iff definingClass is a subclass of
     * myClassInfo(), and accessClass is a subclass of definingClass.
     * </ul>
     * <li>Otherwise, the access is permitted iff myClassInfo().pkgName is the
     * same as definingClass.pkgName().
     * </ul>
     * @param definingClass the ClassInfo of the class in which the access occurs.
     * @param accessClass the ClassInfo of the class used to access the member.
     */
    boolean isAccessibleInContext( ClassInfo definingClass,
	ClassInfo accessClass ) ;
}

