package org.glassfish.pfl.dynamic.codegen.impl;

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2017 Oracle and/or its affiliates. All rights reserved.
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

import junit.framework.TestCase;
import org.glassfish.pfl.basic.contain.Pair;

// Simple tests for Identifier
public class IdentifierTest extends TestCase {
    public IdentifierTest() {
        super();
    }

    public IdentifierTest(String name) {
        super(name);
    }

    public void testIdentifier00() {
        assertTrue(Identifier.isValidIdentifier("frobenius"));
    }

    public void testIdentifier01() {
        assertTrue(Identifier.isValidIdentifier("frob_123"));
    }

    public void testIdentifier02() {
        assertTrue(Identifier.isValidIdentifier("_12_frob_123"));
    }

    public void testIdentifier03() {
        assertFalse(Identifier.isValidIdentifier("2_frob_123"));
    }

    public void testIdentifier04() {
        assertTrue(Identifier.isValidFullIdentifier("frobenius"));
    }

    public void testIdentifier05() {
        assertTrue(Identifier.isValidFullIdentifier("frobenius.ert"));
    }

    public void testIdentifier06() {
        assertTrue(Identifier.isValidFullIdentifier("a.b.c.d.e.f"));
    }

    public void testIdentifier07() {
        assertFalse(Identifier.isValidFullIdentifier("2_frob_123"));
    }

    public void testIdentifier08() {
        assertFalse(Identifier.isValidFullIdentifier("a..b"));
    }

    public void testIdentifier09() {
        assertFalse(Identifier.isValidFullIdentifier("a.b."));
    }

    public void testIdentifier10() {
        assertFalse(Identifier.isValidFullIdentifier(".a.b"));
    }

    public void testIdentifier11() {
        assertEquals(Identifier.makeFQN("a.b", "c"), "a.b.c");
    }

    public void testIdentifier12() {
        assertEquals(Identifier.makeFQN("", "c"), "c");
    }

    public void testIdentifier13() {
        assertEquals(Identifier.makeFQN(null, "c"), "c");
    }

    public void testIdentifier14() {
        assertEquals(Identifier.splitFQN("a.b.c"),
                new Pair("a.b", "c"));
    }

    public void testIdentifier15() {
        assertEquals(Identifier.splitFQN("c"),
                new Pair("", "c"));
    }
}
