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

package org.glassfish.pfl.tf.timer.impl ;

import java.util.Set ;
import java.util.HashSet ;
import java.util.Collections ;
import org.glassfish.pfl.tf.timer.spi.Controllable;

public abstract class ControllableBase extends NamedBaseImpl
    implements Controllable {
    private static final Set<ControllableBase> emptyContent =
	Collections.unmodifiableSet( new HashSet<ControllableBase>() ) ;

    private int id ;
    private String description ;
    private boolean isEnabled ;

    protected ControllableBase( int id, String name, String description, 
	TimerFactoryImpl factory ) {
	super( factory, name ) ;
	this.id = id ;
	this.description = description ;
	isEnabled = false ;
    }

    @Override
    public int id() {
	return id ;
    }

    @Override
    public String description() {
	return description ;
    }

    // Allow setting this so that we can implement the XML parser
    // for generating timer files.
    void description( String description ) {
	this.description = description ;
    }

    @Override
    public final boolean isEnabled() {
	return isEnabled ;
    }

    // Override this in subclass if subclass can contain
    // other Controllables.
    // covariant return type: Set<ControllableBase> is a 
    // subtype of Set<? extends ControllableBase>
    @Override
    public Set<ControllableBase> contents() {
	return emptyContent ;
    }

    @Override
    public void enable() {
	synchronized( factory() ) {
	    if (!isEnabled()) {
		isEnabled = true ;
		factory().updateActivation() ;
	    }
	}
    }

    @Override
    public void disable() {
	synchronized( factory() ) {
	    if (isEnabled()) {
		isEnabled = false ;
		factory().updateActivation() ;
	    }
	}
    }

    // This is only called from TimerGroupImpl.tcContents, which is only 
    // called from TimerFactoryImpl.updateActiation, which in turn
    // is only called from enable and disable.  Therefore this does
    // not need any additional synchronization.
    void transitiveClosure( Set<ControllableBase> result ) {
	result.add( this ) ;
	for (ControllableBase c : contents() ) {
	    if (!result.contains(c)) {
		c.transitiveClosure( result ) ;
	    }
	}
    }
}

