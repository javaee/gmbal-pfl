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

import java.util.ArrayList ;
import java.util.Stack ;
import org.glassfish.pfl.tf.timer.spi.StatisticsAccumulator;
import org.glassfish.pfl.tf.timer.spi.Timer;
import org.glassfish.pfl.tf.timer.spi.TimerEvent;
import org.glassfish.pfl.tf.timer.spi.TimerFactory;

public class MultiThreadedStatsEventHandlerImpl extends StatsEventHandlerBase {
    private Object saListLock ;
    
    // ArrayList indexed by Timer.id 
    private ThreadLocal<ArrayList<Stack<TimerEvent>>> tlsteList ; 

    MultiThreadedStatsEventHandlerImpl( TimerFactory factory, String name ) {
	super( factory, name ) ;
	final int size = factory.numberOfIds() ;

	saListLock = new Object() ;
	tlsteList = new ThreadLocal<ArrayList<Stack<TimerEvent>>>() {
	    public ArrayList<Stack<TimerEvent>> initialValue() {
		ArrayList<Stack<TimerEvent>> result = new ArrayList<Stack<TimerEvent>>( size ) ;
		for (int ctr=0; ctr<size; ctr++) {
		    result.add( new Stack<TimerEvent>() ) ;
		}
		return result ;
	    }
	} ;
    }

    private Stack<TimerEvent> getSteElement( int id ) {
	ArrayList<Stack<TimerEvent>> ste = tlsteList.get() ;
	ste.ensureCapacity( id + 1 ) ;
	for (int ctr=ste.size(); ctr<=id; ctr++)
	    ste.add( new Stack<TimerEvent>() ) ;
	return ste.get( id ) ;
    }

    public void clear() {
	synchronized (saListLock) {
	    super.clear() ;
	}
    }

    protected void recordDuration( int id, long duration ) {
	synchronized (saListLock) {
	    StatisticsAccumulator acc = saList.get( id ) ;
	    acc.sample( duration ) ;
	}
    }

    public void notify( TimerEvent event ) {
	Timer timer = event.timer() ;
	int id = timer.id() ;
	notify( getSteElement( id ), event ) ;
    }
}

