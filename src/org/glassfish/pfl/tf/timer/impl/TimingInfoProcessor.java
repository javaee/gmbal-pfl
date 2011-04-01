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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.glassfish.pfl.dynamic.codegen.impl.Identifier;
import org.glassfish.pfl.tf.timer.spi.Controllable;
import org.glassfish.pfl.tf.timer.spi.TimerFactory;
import org.glassfish.pfl.tf.timer.spi.TimerFactoryBuilder;
import org.glassfish.pfl.tf.timer.spi.TimerGroup;
import org.glassfish.pfl.basic.contain.Pair;

public class TimingInfoProcessor {
    private boolean done = false ;
    private String pkg ;
    private TimerFactory tf ;
    private Map<String,List<String>> contents ;
    private TimerGroup currentTimerGroup ;

    private void checkForValidIdentifier( String name ) {
	if (!Identifier.isValidIdentifier( name )) {
	    throw new IllegalArgumentException("name " + name + " is not a valid Java identifier");
	}
    }

    private void checkDone() {
	if (done) {
	    throw new IllegalStateException("past getResult: no other methods may be called");
	}
    }

    public TimingInfoProcessor( String name, String pkg ) {
	this.done = false ;
	this.pkg = pkg ;
	checkForValidIdentifier( name ) ;
	if (!Identifier.isValidFullIdentifier( pkg )) {
	    throw new IllegalArgumentException(pkg + " is not a valid package name");
	}
	this.tf = TimerFactoryBuilder.make( name, name ) ;
	this.contents = new LinkedHashMap<String,List<String>>() ;
	this.currentTimerGroup = null ;
    }

    public void addTimer( String name, String desc ) {
	checkDone() ;
	checkForValidIdentifier( name ) ;
	if (!tf.timerAlreadyExists( name )) {
	    tf.makeTimer( name, desc ) ;
	}
	currentTimerGroup = null ;
    }

    public void addTimerGroup( String name, String desc ) {
	checkDone() ;
	checkForValidIdentifier( name ) ;
	currentTimerGroup = tf.makeTimerGroup( name, desc ) ;
    }

    private void addContained( String timerName, String timerGroupName ) {
	List<String> list = contents.get( timerGroupName ) ;
	if (list == null) {
	    list = new ArrayList<String>() ;
	    contents.put( timerGroupName, list ) ;
	}

	list.add( timerName ) ;
    }

    public void containedIn( String timerName, String timerGroupName ) {
	addContained( timerName, timerGroupName ) ;
    }

    public void contains( String name ) {
	checkDone() ;
	if (currentTimerGroup == null) {
	    throw new IllegalStateException(
		"contains must be called after an addTimerGroup call" ) ;
	} else {
	    String cname = currentTimerGroup.name() ;
	    addContained( name, cname ) ;
	}
    }

    private Controllable getControllable( String name ) {

	Controllable result = tf.timers().get( name ) ;
	if (result == null) {
	    result = tf.timerGroups().get(name);
	}
	if (result == null) {
	    throw new IllegalArgumentException(name +
		" is not a valid Timer or TimerGroup name");
	}
	return result ;
    }

    private void updateTimerFactoryContents() {
	//  Use the Map<String,List<String>> to fill in the TimerGroup
	//  containment relation
	for (String str : contents.keySet()) {
	    List<String> list = contents.get(str) ;
	    TimerGroup tg = tf.timerGroups().get( str ) ;
	    for (String content : list) {
		tg.add( getControllable( content ) ) ;
	    }
	}
    }

    public Pair<String,TimerFactory> getResult() {
	checkDone() ;
	done = true ;
	updateTimerFactoryContents() ;
	Pair<String,TimerFactory> result = 
	    new Pair<String,TimerFactory>( pkg, tf ) ;
	return result ;
    }
}
