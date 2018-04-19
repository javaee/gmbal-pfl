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

package org.glassfish.pfl.dynamic.codegen;

import org.glassfish.pfl.dynamic.codegen.test.Constants_gen;
import org.glassfish.pfl.dynamic.codegen.test.DefaultPackageTest_gen;
import org.glassfish.pfl.dynamic.codegen.test.Flow_gen;
import org.glassfish.pfl.dynamic.codegen.test.MyRemote__Adapter_Simplified_gen;
import org.glassfish.pfl.dynamic.codegen.test.MyRemote__Adapter_gen;
import org.glassfish.pfl.dynamic.codegen.test.MyRemote_gen;

import java.util.HashMap;
import java.util.Map;

// Import all of the code generators that implement ClassGeneratorFactory here.
// These are used in the tests.
// Every ClassGeneratorFactory must be imported here and registered below.

/** Registry that contains instances of all of the test class generators
 * used in this test.
 */
public abstract class ClassGeneratorFactoryRegistry {
    private ClassGeneratorFactoryRegistry() {}
    
    private static Map<String,ClassGeneratorFactory> map =
	new HashMap<String,ClassGeneratorFactory>() ;

    static {
	register( new MyRemote_gen() ) ;
	register( new MyRemote__Adapter_gen() ) ;
	register( new MyRemote__Adapter_Simplified_gen() ) ;
	register( new Flow_gen() ) ;
	register( new Constants_gen() ) ;
	register( new DefaultPackageTest_gen() ) ;
    }

    private static void register( ClassGeneratorFactory tcg ) {
	map.put( tcg.className(), tcg ) ;
    }

    public static ClassGeneratorFactory get( String name ) {
	return map.get( name ) ;
    }
}
