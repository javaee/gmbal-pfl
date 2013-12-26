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

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException;

import java.util.HashMap;
import java.util.Map;

/** A convenient base class for making ClassCopier types.
 * This takes care of checking oldToNew and updating oldToNew
 * when an actual copy is made.  All subclasses must override
 * createCopy, which allocates a new result.  In some simple
 * cases, this is all that is needed.  In the more complex
 * cases, doCopy must also be overridden to make the actual copy.
 */
public abstract class ClassCopierBase implements ClassCopier {
    private String name ;
    private boolean isReflective ;

    /** Pass a name here that can be used for toString, hashCode, and equals.
     * All different ClassCopier classes derived from this base should
     * have unique names.
     */
    protected ClassCopierBase( String name ) 
    {
	this( name, false ) ;
    }

    protected ClassCopierBase( String name, boolean isReflective )
    {
	this.name = name ;
	this.isReflective = isReflective ;
    }

    // Implement toString() and equals() as debugging and testing aids.
    // Implement hashCode() to satisfy the general contracts of equals and
    // hash.

    @Override
    public final String toString()
    {
	return "ClassCopier[" + name + "]" ;
    }

    @Override
    public final int hashCode()
    {
	return name.hashCode() ;
    }

    @Override
    public final boolean equals( Object obj )
    {
	if (this == obj) {
            return true;
        }

	if (!(obj instanceof ClassCopierBase)) {
            return false;
        }

	ClassCopierBase other = (ClassCopierBase)obj ;

	return name.equals( other.name ) && (isReflective == other.isReflective) ;
    }

    /** Make the actual copy of source, using oldToNew to preserve aliasing.
     * This first checks to see whether source has been previously copied.
     * If so, the value obtained from oldToNew is returned.  Otherwise,
     * <ol>
     * <li>createCopy( source ) is called to create a new copy of source.
     * <li>The new copy is placed in oldToNew with source as its key.
     * <li>doCopy is called to complete the copy.
     * <ol>
     * <p>This split into two phases isolates all subclasses from the need to
     * update oldToNew. It accommodates simple cases (arrays of primitives
     * for example) that only need to define createCopy, as well as more complex
     * case (general objects) that must first create the copy, update oldToNew,
     * and then do the copy, as otherwise self-references would cause 
     * infinite recursion.
     */
    @Override
    public final Object copy(Map<Object, Object> oldToNew, Object source) throws ReflectiveCopyException {
        Object result = oldToNew.get(source);
        if (result == null) {
            result = doSpecialCaseCopy(oldToNew, source);
        }
        if (result == null) {
            result = doByFieldCopy(oldToNew, source);
        }

        return result;
    }

    /**
     * Sometimes the internal representation of an object will be violated by our field-by-field copy. This method
     * handles those special cases. It suggests that there may be a basic problem with our code logic.
     * @param oldToNew a map of already copied objects and their corresponding copies.
     * @param source the object to copy.
     */
    private Object doSpecialCaseCopy(Map<Object, Object> oldToNew, Object source) {
        Object result = null;

        if (isEmptyHashMap(source))
            result = cloneEmptyHashMap();

        if (result != null)
            oldToNew.put(source, result);
        return result;
    }

    /**
     * As of JDK 1.7-40, the JDK relies on the HashMap#table field being identical to a constant. That will not be
     * true if we invoke #doByFieldCopy to copy it, so we need special processing.
     * @param source the object to copy.
     */
    private boolean isEmptyHashMap(Object source) {
        return source instanceof HashMap && ((HashMap) source).isEmpty();
    }

    private HashMap cloneEmptyHashMap() {
        return new HashMap();
    }

    private Object doByFieldCopy(Map<Object, Object> oldToNew, Object source) {
        Object result;
        try {
            result = createCopy(source);
            oldToNew.put(source, result);
            result = doCopy(oldToNew, source, result);
        } catch (StackOverflowError ex) {
            throw Exceptions.self.stackOverflow(source, ex);
        }
        return result;
    }

    @Override
    public boolean isReflectiveClassCopier()
    {
	return isReflective ;
    }

    /** Create a copy of source.  The copy may or may not be fully
     * initialized.  This method must always be overridden in a 
     * subclass.
     */
    protected abstract Object createCopy( 
	Object source ) throws ReflectiveCopyException ;

    /** Do the copying of data from source to result.
     * This just returns the result by default, but it may be overrideden 
     * in a subclass.  When this method completes, result must be fully
     * initialized.
     */
    protected Object doCopy( Map<Object,Object> oldToNew,
	Object source, Object result ) throws ReflectiveCopyException
    {
	return result ;
    }
}
