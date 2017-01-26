/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2011 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.pfl.tf.timer.spi;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

// import corba.framework.TimerUtils ;


public class TimerTest {
    // Test TimerFactoryBuilder
    @Test
    public void factoryBuilderCreate1() {
	String name = "TF1" ;
	String description = "First Test Factory" ;
	TimerFactory tf = TimerFactoryBuilder.make( name, description ) ;
	Assert.assertEquals( tf.name(), name ) ;
	Assert.assertEquals( tf.description(), description ) ;
	TimerFactoryBuilder.destroy( tf ) ;
    }

    @Test( expected=IllegalArgumentException.class )
    public void factoryBuilderCreate2() {
	String name = "TF1" ;
	String description = "First Test Factory" ;
	TimerFactory tf = TimerFactoryBuilder.make( name, description ) ;
	try {
	    tf = TimerFactoryBuilder.make( name, description ) ;
	} finally {
	    TimerFactoryBuilder.destroy( tf ) ;
	}
    }

    @Test()
    public void factoryBuilderCreate3() {
	String name = "TF1" ;
	String description = "First Test Factory" ;
	TimerFactory tf = TimerFactoryBuilder.make( name, description ) ;
	TimerFactoryBuilder.destroy( tf ) ;
	tf = TimerFactoryBuilder.make( name, description ) ;
	TimerFactoryBuilder.destroy( tf ) ;
    }

    private static void sleep( int time ) {
	try {
	    Thread.sleep( time ) ;
	} catch (Exception exc) {
	    // ignore it 
	}
    }

    private void recordCall(TimingPoints tp, Timer top,
                            TimerEventController controller, int transportDelay ) {
	
	controller.enter( top ) ;

	controller.enter( tp.ClientDelegateImpl__hasNextNext() );
	sleep( 1 ) ;
	controller.exit( tp.ClientDelegateImpl__hasNextNext() );

        controller.enter( tp.ClientRequestDispatcherImpl__connectionSetup() ) ;
	sleep( 4 ) ;
        controller.exit( tp.ClientRequestDispatcherImpl__connectionSetup() ) ;

        controller.enter( tp.ClientRequestDispatcherImpl__clientEncoding() ) ;
	sleep( 100 ) ;
        controller.exit( tp.ClientRequestDispatcherImpl__clientEncoding() ) ;

        controller.enter( tp.ClientRequestDispatcherImpl__clientTransportAndWait() ) ;
	sleep( transportDelay ) ;
        controller.exit( tp.ClientRequestDispatcherImpl__clientTransportAndWait() ) ;

        controller.enter( tp.ClientRequestDispatcherImpl__clientDecoding() ) ;
	sleep( 40 ) ;
        controller.exit( tp.ClientRequestDispatcherImpl__clientDecoding() ) ;

	controller.exit( top ) ;
    }

    Map<Timer,Statistics> makeData() {
	// Setup timing points and a top-level timer
        TimerManager<TimingPoints> tm = new TimerManager<TimingPoints>(
            ObjectRegistrationManager.nullImpl, "TestTimerManager" ) ;
        TimerFactory tf = tm.factory() ;
        TimingPoints tp = new TimingPoints(tf) ;
        tm.initialize(tp) ;
        TimerEventController controller = tm.controller() ;
        Timer top = tf.makeTimer( "top", "Encloses the entire operation" ) ;

        StatsEventHandler handler = tf.makeStatsEventHandler( "TestStats" ) ;
        controller.register( handler ) ;
        handler.clear() ;

        tp.Subcontract().enable() ;
        top.enable() ;

        // Simulate the actions of the ORB client transport
        recordCall( tp, top, controller, 25 ) ;
        recordCall( tp, top, controller, 31 ) ;
        recordCall( tp, top, controller, 27 ) ;
        recordCall( tp, top, controller, 42 ) ;
        recordCall( tp, top, controller, 19 ) ;
        recordCall( tp, top, controller, 21 ) ;
        recordCall( tp, top, controller, 23 ) ;
        recordCall( tp, top, controller, 25 ) ;
        recordCall( tp, top, controller, 34 ) ;
        recordCall( tp, top, controller, 33 ) ;
        recordCall( tp, top, controller, 31 ) ;
        recordCall( tp, top, controller, 28 ) ;
        recordCall( tp, top, controller, 27 ) ;
        recordCall( tp, top, controller, 29 ) ;
        recordCall( tp, top, controller, 30 ) ;
        recordCall( tp, top, controller, 31 ) ;
        recordCall( tp, top, controller, 28 ) ;

        return handler.stats() ;

    }

    @Test()
    public void generateStatsTable() {
	Map<Timer, Statistics> data = makeData() ;

	// TimerUtils.writeHtmlTable( data, "TimerTest.html", 
	    // "Client Test Timing Data" ) ;
    }
}
