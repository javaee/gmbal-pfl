/* 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2003-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.glassfish.pfl.basic.contain ;

/** Represents a Triple of values.  Used to return multiple values
 * and other similar uses.
 */
public class Triple<S,T,U> {
    private final Pair<S,Pair<T,U>> delegate ;

    public Triple( final S first, final T second, final U third ) {
        delegate = new Pair<S,Pair<T,U>>( first,
            new Pair<T,U>( second, third ) ) ;
    }

    public S first() {
        return delegate.first() ;
    }

    public T second() {
        return delegate.second().first() ;
    }

    public U third() {
        return delegate.second().second() ;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode() ;
    }

    @Override
    public boolean equals( Object obj ) {
        if (!(obj instanceof Triple)) {
            return false ;
        }

        Triple other = (Triple)obj ;

        return delegate.equals( other.delegate ) ;
    }
}
