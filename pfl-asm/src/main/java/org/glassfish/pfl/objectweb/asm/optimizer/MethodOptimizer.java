/*
 * Copyright (c) 2000-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.glassfish.pfl.objectweb.asm.optimizer;

import org.glassfish.pfl.objectweb.asm.AnnotationVisitor;
import org.glassfish.pfl.objectweb.asm.Attribute;
import org.glassfish.pfl.objectweb.asm.Label;
import org.glassfish.pfl.objectweb.asm.MethodAdapter;
import org.glassfish.pfl.objectweb.asm.MethodVisitor;
import org.glassfish.pfl.objectweb.asm.commons.Remapper;
import org.glassfish.pfl.objectweb.asm.commons.RemappingMethodAdapter;

/**
 * A {@link MethodAdapter} that renames fields and methods, and removes debug
 * info.
 * 
 * @author Eugene Kuleshov
 */
public class MethodOptimizer extends RemappingMethodAdapter {

    public MethodOptimizer(
        int access,
        String desc,
        MethodVisitor mv,
        Remapper remapper)
    {
        super(access, desc, mv, remapper);
    }
    
    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    public AnnotationVisitor visitAnnotationDefault() {
        // remove annotations
        return null;
    }

    public AnnotationVisitor visitParameterAnnotation(
        final int parameter,
        final String desc,
        final boolean visible)
    {
        // remove annotations
        return null;
    }

    public void visitLocalVariable(
        final String name,
        final String desc,
        final String signature,
        final Label start,
        final Label end,
        final int index)
    {
        // remove debug info
    }

    public void visitLineNumber(final int line, final Label start) {
        // remove debug info
    }
    
    public void visitFrame(
        int type,
        int local,
        Object[] local2,
        int stack,
        Object[] stack2)
    {
        // remove frame info
    }
    
    public void visitAttribute(Attribute attr) {
        // remove non standard attributes
    }
}
