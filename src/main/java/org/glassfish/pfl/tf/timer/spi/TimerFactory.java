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

package org.glassfish.pfl.tf.timer.spi ;

import java.util.Map ;
import java.util.Set ;

/** Factory class for all Timer-related objects.
 * TimerFactory is also a TimerGroup of all timers and timer groups that it creates.
 */
public interface TimerFactory extends TimerGroup {
    /** Returns the maximum id used by this TimerFactory for creating Controllables.
     * The value of con.id() for any Controllable created by this
     * TimerFactory always ranges from 0 inclusive to numberOfIds()
     * exclusive.
     */
    int numberOfIds() ;

    /** Returns the Controllable corresponding to id, for 
     * id in the range 0 (inclusive) to numberOfIds() (exclusive). 
     * @throws IndexOutOfBoundsException if id is not in range.
     */
    Controllable getControllable( int id ) ;

    /** Create a new LogEventHandler.  All LogEventHandler names
     * must be unique within the same TimerFactory.
     */
    LogEventHandler makeLogEventHandler( String name ) ;

    TimerEventHandler makeTracingEventHandler( String name ) ;

    /** Create a new StatsEventHandler.  A StatsEventHandler records 
     * running statistics for all enter/exit pairs until it is cleared,
     * at which point it starts over.  It will keep data separated for
     * each thread, combining information correctly from multiple threads.
     * All StatsEventHandler names
     * must be unique within the same TimerFactory.
     * This StatsEventHandler must be used from a single thread.
     */
    StatsEventHandler makeStatsEventHandler( String name ) ;

    /** Create a new StatsEventHandler.  A StatsEventHandler records 
     * running statistics for all enter/exit pairs until it is cleared,
     * at which point it starts over.  It will keep data separated for
     * each thread, combining information correctly from multiple threads.
     * All StatsEventHandler names
     * must be unique within the same TimerFactory.
     * This StatsEventHandler is multi-thread safe.
     */
    StatsEventHandler makeMultiThreadedStatsEventHandler( String name ) ;

    /** Remove the handler from this TimerFactory.  The handler
     * should not be used after this call.
     */
    void removeTimerEventHandler( TimerEventHandler handler ) ;

    /** Create a new Timer.  Note that Timers cannot be
     * destroyed, other than by garbage collecting the TimerFactory
     * that created them.
     */
    Timer makeTimer( String name, String description )  ;

    /** Returns a read-only map from Timer names to Timers.
     */
    Map<String,? extends Timer> timers() ;

    /** Create a new TimerGroup.  Note that TimerGroups cannot be
     * destroyed, other than by garbage collecting the TimerFactory
     * that created them.
     */
    TimerGroup makeTimerGroup( String name, String description ) ;

    /** Returns a read-only map from TimerGroup names to TimerGroups.
     */
    Map<String,? extends TimerGroup> timerGroups() ;

    /** Create a TimerController, which can create TimerEvents and
     * send them to registered TimerEventHandlers.
     */
    TimerEventController makeController( String name ) ;

    /** Remove the controller from this factory.  The controller 
     * should not be used after this call.
     */
    void removeController( TimerEventControllerBase controller ) ;

    /** Returns a read-only view of the set of enabled Controllables.
     * These have been explicitly enabled via a call to enable().
     */
    Set<? extends Controllable> enabledSet() ;

    /** Returns a read-only view of the set of Controllables that are 
     * currently active.  An enabled Timer is active.  All Controllables
     * contained in an active or enabled TimerGroup are active.
     */
    Set<Timer> activeSet() ;

    /** Return true iff a timer with the given name already exists.
     */
    boolean timerAlreadyExists( String name ) ;
}

