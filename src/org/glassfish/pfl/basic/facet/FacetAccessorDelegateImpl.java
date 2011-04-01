/* 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007-2009 Sun Microsystems, Inc. All rights reserved.
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

/** Implementation of FacetAccessor that delegates to another FacetAccessor,
 * typically a FacetAccessorImpl.  The purpose of this call is to provide
 * a convenient template of methods that may be copied into a class that
 * implements FacetAccessor.  Typically such a class implements that
 * FacetAccessor interface and defines a data member initialized as:
 * 
 * FacetAccessor facetAccessorDelegate = new FacetAccessorImpl( this ) ;
 * 
 * and then simply copies the other methods directly.
 * 
 * This is all a workaround for the fact that Java does not
 * support dynamic inheritance, or more than one superclass.  
 * 
 * Because this is a template, I have commented out all of the code.
 * It is not used at runtime or compiletime.
 *
 * @author ken
 */
abstract class FacetAccessorDelegateImpl implements FacetAccessor {
    /*
    private FacetAccessor facetAccessorDelegate ;
    
    public FacetAccessorDelegateImpl( FacetAccessor fa ) {
        facetAccessorDelegate = fa ;
    }
    
    public <T> T facet(Class<T> cls, boolean debug ) {
        return facetAccessorDelegate.facet( cls, debug ) ;
    }

    public <T> void addFacet(T obj) {
        facetAccessorDelegate.addFacet( obj ) ;
    }

    public void removeFacet( Class<?> cls ) {
        facetAccessorDelegate.removeFacet( cls ) ;
    }

    public Object invoke(Method method, boolean debug, Object... args) {
        return facetAccessorDelegate.invoke( method, debug, args ) ;
    }

    public Object get( Field field, boolean debug ) {
        return facetAccessorDelegate.get( field, debug ) ;
    }

    public Collection<Object> facets() {
        return facetAccessorDelegate.facets() ;
    }

    Object get( Field field, boolean debug ) {
        return facetAccessorDelegate.get( field, debug ) ;
    }

    void set( Field field, Object value, boolean debug ) {
        facetAccessorDelegate.set( field, value, debug ) ;
    }

    */
}
