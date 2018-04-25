/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.pfl.tf.timer.spi;

public class TimingPoints
{
    private final Timer ClientDelegateImpl__hasNextNext ;
    private final Timer ClientRequestDispatcherImpl__clientDecoding ;
    private final Timer ClientRequestDispatcherImpl__clientEncoding ;
    private final Timer ClientRequestDispatcherImpl__clientTransportAndWait ;
    private final Timer ClientRequestDispatcherImpl__connectionSetup ;
    private final TimerGroup TimingPoints ;
    private final TimerGroup IsLocal ;
    private final TimerGroup Subcontract ;

    
    public TimingPoints(TimerFactory tf) {
        this.ClientDelegateImpl__hasNextNext = 
	    tf.makeTimer("ClientDelegateImpl__hasNextNext", 
	    "Timer for method enter_hasNextNext in class ClientDelegateImpl") ;
        this.ClientRequestDispatcherImpl__clientDecoding = 
	    tf.makeTimer("ClientRequestDispatcherImpl__clientDecoding", 
	    "Timer for method enter_clientDecoding in class ClientRequestDispatcherImpl") ;
        this.ClientRequestDispatcherImpl__clientEncoding = 
	    tf.makeTimer("ClientRequestDispatcherImpl__clientEncoding", 
	    "Timer for method enter_clientEncoding in class ClientRequestDispatcherImpl") ;
        this.ClientRequestDispatcherImpl__clientTransportAndWait = 
	    tf.makeTimer("ClientRequestDispatcherImpl__clientTransportAndWait", 
	    "Timer for method enter_clientTransportAndWait in class " 
		+ "ClientRequestDispatcherImpl") ;
        this.ClientRequestDispatcherImpl__connectionSetup = 
	    tf.makeTimer("ClientRequestDispatcherImpl__connectionSetup", 
	    "Timer for method enter_connectionSetup in class ClientRequestDispatcherImpl") ;

        this.TimingPoints = tf.makeTimerGroup("TimingPoints", "TimingPoints") ;
        this.IsLocal = tf.makeTimerGroup("IsLocal", "TimerGroup for Annotation IsLocal") ;
        this.Subcontract = tf.makeTimerGroup("Subcontract", "TimerGroup for Annotation Subcontract") ;

	this.TimingPoints.add(this.ClientRequestDispatcherImpl__clientDecoding) ;
	this.TimingPoints.add(this.ClientRequestDispatcherImpl__clientEncoding) ;
	this.TimingPoints.add(this.ClientRequestDispatcherImpl__clientTransportAndWait) ;
	this.TimingPoints.add(this.ClientRequestDispatcherImpl__connectionSetup) ;
	this.TimingPoints.add(this.ClientDelegateImpl__hasNextNext) ;
	this.IsLocal.add(this.ClientDelegateImpl__hasNextNext) ;
	this.Subcontract.add(this.ClientRequestDispatcherImpl__connectionSetup) ;
	this.Subcontract.add(this.ClientRequestDispatcherImpl__clientDecoding) ;
	this.Subcontract.add(this.ClientRequestDispatcherImpl__clientTransportAndWait) ;
	this.Subcontract.add(this.ClientRequestDispatcherImpl__clientEncoding) ;
    }

    public final Timer ClientDelegateImpl__hasNextNext() {
	return this.ClientDelegateImpl__hasNextNext ;
    }

    public final Timer ClientRequestDispatcherImpl__clientDecoding() {
	return this.ClientRequestDispatcherImpl__clientDecoding ;
    }
	
    public final Timer ClientRequestDispatcherImpl__clientEncoding() {
	return this.ClientRequestDispatcherImpl__clientEncoding ;
    }

    public final Timer ClientRequestDispatcherImpl__clientTransportAndWait() {
	return this.ClientRequestDispatcherImpl__clientTransportAndWait ;
    }
	
    public final Timer ClientRequestDispatcherImpl__connectionSetup() {
	return this.ClientRequestDispatcherImpl__connectionSetup ;
    }
	
    public final TimerGroup IsLocal() {
        return this.IsLocal ;
    }
    
    public final TimerGroup Subcontract() {
        return this.Subcontract ;
    }
    
    public final TimerGroup TimingPoints() {
        return this.TimingPoints ;
    }
}
