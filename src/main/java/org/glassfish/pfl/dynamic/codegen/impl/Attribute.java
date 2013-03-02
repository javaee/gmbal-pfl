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

import java.util.Set ;
import java.util.HashSet ;
import java.util.List ;
import java.util.ArrayList ;
import org.glassfish.pfl.basic.func.NullaryFunction;

/** Class used to define dynamic attributes on AttributedObject instances.
 * Note that T cannot be a generic type, due to problems with
 * Class<T> when T is a generic.  To work around this problem,
 * simply create an interface that extends the generic type
 * (you are programming to interfaces, right?).
 */
public class Attribute<T> {
    private static List<Attribute<?>> attributes =
	new ArrayList<Attribute<?>>() ;

    private static synchronized int next( Attribute<?> attr ) {
	for (int ctr=0; ctr<attributes.size(); ctr++) {
            if (attr.name().equals(attributes.get(ctr).name())) {
                return ctr;
            }
        }

	int result = attributes.size() ;
	attributes.add( attr ) ;
	return result ;
    }

    public static int numberOfAttributes() {
	return attributes.size() ;
    }

    public static Attribute<?> get( int index ) {
	if ((index >= 0) && (index < attributes.size()))
	    return attributes.get( index ) ;
	else
	    throw new IllegalArgumentException() ;
    }

    public static Set<Attribute<?>> getAttributes( AttributedObject node ) {
	List<Object> attrs = node.attributes() ;
	Set<Attribute<?>> result = new HashSet<Attribute<?>>() ;

	if (attrs == null)
	    return result ;

	for (int ctr=0; ctr<attrs.size(); ctr++) {
	    Object value = attrs.get(ctr) ;
	    if (value != null) {
		result.add( attributes.get(ctr) ) ;
	    }
	}

	return result ;
    }

    private String name ;
    private NullaryFunction<T> initializer ;
    private T defaultValue ;
    private Class<T> cls ;
    private int attributeIndex ;

    public String toString() {
	return "Attribute[" + name + ":" + cls.getName() + ":" + 
	    attributeIndex + "]" ;
    }

    public Attribute( Class<T> cls, String name, T defaultValue ) {
	this.cls = cls ;
	this.name = name ;
	this.initializer = null ;
	this.defaultValue = defaultValue ;

	attributeIndex = next( this ) ;
    }

    public Attribute( Class<T> cls, String name, 
	NullaryFunction<T> initializer ) {
	this.cls = cls ;
	this.name = name ;
	this.initializer = initializer ;
	this.defaultValue = null ;

	attributeIndex = next( this ) ;
    }

    public T get( AttributedObject node ) {
	T result = cls.cast( node.get( attributeIndex ) ) ;
	if (result == null) {
	    if (initializer != null)
		result = initializer.evaluate() ;
	    else
		result = defaultValue ;

	    node.set( attributeIndex, result ) ;
	}
	return result ;
    }

    public void set( AttributedObject node, T arg ) {
	node.set( attributeIndex, arg ) ;
    }

    public boolean isSet( AttributedObject node ) {
	return node.get( attributeIndex ) != null ;
    }

    public String name() {
	return name ;
    }

    public int index() {
	return attributeIndex ;
    }

    public Class<?> type() {
	return cls ;
    }
}
