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

package org.glassfish.pfl.basic.algorithm;

import org.glassfish.pfl.basic.testobjects.SerializableClass2;
import org.glassfish.pfl.basic.testobjects.TestObjects;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;

public class ObjectUtilityTest {
    @Test
    public void whenObjectNotInJdk_displayFields() throws Exception {
        SerializableClass2 anObject = new SerializableClass2();
        String string = ObjectUtility.defaultObjectToString(anObject);

        assertThat(string, stringContainsInOrder(values("aNumber=", "java.lang.Integer", Integer.toString(TestObjects.INT_FIELD_VALUE))));
        assertThat(string, stringContainsInOrder(values("aDouble=", "java.lang.Double", "0.0")));
        assertThat(string, stringContainsInOrder(values("aLong=", "java.lang.Long", "0")));
    }

    private Iterable<String> values(String... strings) {
        return Arrays.asList(strings);
    }

    @Test
    public void whenObjectInJdk_displayFields() throws Exception {
        String string = ObjectUtility.defaultObjectToString(System.out);

        assertThat(string, stringContainsInOrder(values("autoFlush=", "true")));
        assertThat(string, stringContainsInOrder(values("formatter=", "null")));
        assertThat(string, stringContainsInOrder(values("maxBytesPerChar=", "java.lang.Float", "3.0")));
    }
}
