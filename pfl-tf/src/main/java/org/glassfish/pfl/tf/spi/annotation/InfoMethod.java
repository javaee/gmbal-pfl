/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.pfl.tf.spi.annotation;

import org.glassfish.pfl.tf.spi.TimingPointType;
import java.lang.annotation.Target ;
import java.lang.annotation.Documented ;
import java.lang.annotation.ElementType ;
import java.lang.annotation.Retention ;
import java.lang.annotation.RetentionPolicy ;

/** Used to indicate that a method is used for invoking MethodMonitor.info.
 * Note that the tracing name is the method name unless overridden by a
 * TracingName annotation (which is required if the method is overloaded).
 * Also note that either all overloaded methods of the same name are InfoMethods,
 * or none of them are.
 * The name of this method as a TimingPoint is the same as the tracing name.
 * The method must be private and have a void return type.
 * Any arguments are passed into the
 * MethodMonitor.info call in the instrumented code.
 *
 * @param tpType Indicates whether this info method represents an ENTER, EXIT, or NONE (no timing point).
 * @param tpName Gives the name of timing point to use for this info method.
 * Must not be "" if tpType is not NONE.
 * @author ken
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InfoMethod {
    TimingPointType tpType() default TimingPointType.NONE ;
    String tpName() default "" ;
}
