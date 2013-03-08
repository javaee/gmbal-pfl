/* 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007-2011 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific 
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at legal/LICENSE.TXT.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
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
 * 
 */ 
package org.glassfish.pfl.basic.facet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/** Interface to access facets of an object.  A facet is
 * an instance of a particular class.  It may be implemented
 * in a variety of ways, including inheritance, delegation,
 * or dynamic construction on demand.
 *
 * @author ken
 */
public interface FacetAccessor {
    /** Access the Facet of Class T from the object.
     * 
     * @param <T> The Type (as a Class) of the Facet.
     * @param cls The class of the facet.
     * @return Instance of cls for this facet.  Null if no such
     * facet is available.
     */
    <T> T facet( Class<T> cls ) ;
    
    /** Add a facet to the object.  The type T must not already
     * be available as a facet.
     * @param <T>
     * @param obj
     */
    <T> void addFacet( T obj ) ;
    
    /** Remove the facet (if any) of the given type.
     * 
     * @param cls The class of the facet to remove.
     */
    void removeFacet( Class<?> cls ) ;
    
    /** Return a list of all facets on this object.
     *
     * @return Collection of all facets.
     */
    Collection<Object> facets() ;
    
    /** Invoke method on the appropriate facet of this 
     * object, that is, on the facet corresponding to 
     * method.getDeclaringClass.
     * @param method The method to invoke.
     * @param args Arguments to the method.
     * @return restult of the invoke call.
     */
    Object invoke( Method method, Object... args ) ;

    /** Fetch the value of the field from whichever facet contains the field.
     * Read-only because that's all that the intended application needs.
     *
     * @param field The field to access
     * @param debug True if debugging trace output is desired
     * @return The value of the field
     */
    Object get( Field field ) ;

    void set( Field field, Object value ) ;
}
