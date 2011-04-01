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

package org.glassfish.pfl.dynamic.copyobject.impl;

import org.glassfish.pfl.basic.logex.Chain;
import org.glassfish.pfl.basic.logex.ExceptionWrapper;
import org.glassfish.pfl.basic.logex.Log;
import org.glassfish.pfl.basic.logex.LogLevel;
import org.glassfish.pfl.basic.logex.Message;
import org.glassfish.pfl.basic.logex.WrapperGenerator;

import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException ;

/** Exception wrapper class.  The logex WrapperGenerator uses this interface
 * to generate an implementation which returns the appropriate exception, and
 * generates a log report when the method is called.  This is used for all
 * implementation classes in this package.
 *
 * The exception IDs are allocated in blocks of EXCEPTIONS_PER_CLASS, which is
 * a lot more than is needed, but we have 32 bits for IDs, and multiples of
 * a suitably chosen EXCEPTIONS_PER_CLASS (like 100 here) are easy to read in
 * error messages.
 *
 * @author ken
 */
@ExceptionWrapper( idPrefix="OBJCOPY" )
public interface Exceptions {
    static final Exceptions self = WrapperGenerator.makeWrapper(
        Exceptions.class) ;

    // Allow 100 exceptions per class
    static final int EXCEPTIONS_PER_CLASS = 100 ;

// FallbackCopierImpl
    static final int FB_START = 1 ;

    @Message( "Object copy failed on copy of {0} which has type {1}" )
    @Log( id = FB_START + 0, level=LogLevel.FINE )
    void failureInFallback(
        @Chain ReflectiveCopyException exc, Object obj, Class<?> cls );

// ClassCopierBase
    static final int CCB_START = FB_START + EXCEPTIONS_PER_CLASS ;

    @Message( "Stack overflow while copying {0}" )
    @Log( id = CCB_START + 0, level=LogLevel.WARNING )
    ReflectiveCopyException stackOverflow(Object source,
        @Chain StackOverflowError ex);

// DefaultCopier
    static final int DC_START = CCB_START + EXCEPTIONS_PER_CLASS ;

    @Message( "Could not copy {0}" )
    @Log( id = DC_START + 0, level=LogLevel.WARNING )
    ReflectiveCopyException couldNotCopy(Object obj, ReflectiveCopyException exc);

// ClassCopierFactoryPipelineImpl
    static final int CCFPI_START = DC_START + EXCEPTIONS_PER_CLASS ;

    @Log( id = CCFPI_START + 0, level=LogLevel.WARNING )
    @Message( "Cannot copy interface (attempt was for {0})")
    ReflectiveCopyException cannotCopyInterface( Class<?> cls ) ;

    @Log( id = CCFPI_START + 1, level=LogLevel.WARNING )
    @Message( "Could not find ClassCopier for {0}")
    IllegalStateException couldNotFindClassCopier( Class<?> cls ) ;

    @Log( id = CCFPI_START + 2, level=LogLevel.WARNING )
    @Message( "Could not copy class {0}")
    ReflectiveCopyException cannotCopyClass( Class<?> cls ) ;

// ClassCopierOrdinaryImpl
    static final int CCOI_START = CCFPI_START + EXCEPTIONS_PER_CLASS ;

    @Message( "Exception in readResolve() for {0}")
    @Log( id = CCOI_START + 0, level=LogLevel.WARNING )
    RuntimeException exceptionInReadResolve(Object obj, @Chain Throwable t);

    @Message( "Cannot create ClassFieldCopier for superclass {0} "
        + ": This class already has a ClassCopier" )
    @Log( id = CCOI_START + 0, level=LogLevel.WARNING )
    ReflectiveCopyException noClassCopierForSuperclass( Class<?> superClass ) ;
}
