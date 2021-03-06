/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2003-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
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

package org.glassfish.pfl.basic.contain ;

/** A utilitiy class representing a generic types Pair of elements.
 * Useful for simple data structures, returning multiple values, etc.
 * Pair<Object,Object> is similar to a cons cell.
 */
public class Pair<S,T> {
    protected S _first ;
    protected T _second ;

    public Pair( final S first, final T second ) {
	_first = first ;
	_second = second ;
    }

    public Pair( final S first ) {
	this( first, null ) ;
    }

    public Pair() {
	this( null ) ;
    }

    public S first() {
	return _first ;
    }

    public T second() {
	return _second ;
    }

    @Override
    public boolean equals( Object obj ) {
	if (obj == this) {
	    return true ;
        }

	if (!(obj instanceof Pair)) {
	    return false ;
        }

        @SuppressWarnings("unchecked")
	Pair<S,T> pair = (Pair<S,T>)obj ;

	if (first() == null ? 
	    pair.first() == null : first().equals( pair.first())) {
	    return (second() == null ? 
		pair.second() == null : second().equals( pair.second())) ;
	} else {
	    return false ;
	}
    }

    @Override
    public int hashCode() {
	int result = 0 ;
	if (_first != null) {
            result ^= _first.hashCode();
        }
	if (_second != null) {
            result ^= _second.hashCode();
        }

	return result ;
    }

    @Override
    public String toString() {
	return "Pair[" + _first + "," + _second + "]" ;
    }
}
