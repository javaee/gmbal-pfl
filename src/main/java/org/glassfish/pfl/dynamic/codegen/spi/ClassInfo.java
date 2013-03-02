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

import java.util.Set ;
import java.util.List ;
import java.util.Map ;

/** An interface that provides information about classes.  This can be
 * used to describe both ClassGenerators that are used to generate code
 * and pre-existing Java classes.
 */
public interface ClassInfo {
    /** Return the modifiers on this class as specified in java.lang.reflect.Modifier.
     */
    public int modifiers() ;

    /** Return the Type of the class represented by this
     * ClassInfo.
     */
    public Type thisType() ;

    /** Return true iff this ClassInfo is an interface.
     */
    public boolean isInterface() ;

    /** Return the fully qualified class name for this
     * ClassInfo.
     */
    public String name() ;

    /** Return the fully qualified package name containing
     * the class represented by this ClassInfo.
     */
    public String pkgName() ;

    /** Return the class name of the class represented by this
     * ClassInfo relative to pkgName().
     */
    public String className() ;

    /** Return the Type of the supertype of this class.
     */
    public Type superType() ;

    /** Return the list of Types of interfaces implemented by this class.
     * May be empty, but never null.
     */
    public List<Type> impls() ;

    /** Return a map from field names to FieldInfo instances for
     * every field defined in this class (not including super types).
     */
    public Map<String,FieldInfo> fieldInfo() ;


    /** Find a field with the given name if one exists.
     * Searches this class and all super classes.
     */ 
    public FieldInfo findFieldInfo( String name ) ;

    /** Return methodInfo for all methods defined on this class.
     * This does not include inherited methods.  Here we return
     * a map from method name to the set of MethodInfo instances for
     * all methods with the same method name.  This form is useful
     * for handling method overload resolution.
     */
    public Map<String,Set<MethodInfo>> methodInfoByName() ;

    public Set<MethodInfo> constructorInfo() ;

    /** Find the method (if any) with the given name and Signature
     * in this ClassInfo, or in any superType of this ClassInfo.
     */
    public MethodInfo findMethodInfo( String name, Signature sig ) ;

    /** Find the MethodInfo (if any) for a Constructor with the given
     * Signature in this ClassInfo.
     */
    public MethodInfo findConstructorInfo( Signature sig ) ;

    /** Return true iff this is a subclass or subinterface of
     * info.
     */
    public boolean isSubclass( ClassInfo info ) ;
}
