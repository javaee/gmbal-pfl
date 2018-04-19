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

package org.glassfish.pfl.objectweb.asm.commons;

import org.glassfish.pfl.objectweb.asm.AnnotationVisitor;
import org.glassfish.pfl.objectweb.asm.Attribute;
import org.glassfish.pfl.objectweb.asm.FieldVisitor;

/**
 * A <code>FieldVisitor</code> adapter for type remapping.
 * 
 * @author Eugene Kuleshov
 */
public class RemappingFieldAdapter implements FieldVisitor {

    private final FieldVisitor fv;
    
    private final Remapper remapper;

    public RemappingFieldAdapter(FieldVisitor fv, Remapper remapper) {
        this.fv = fv;
        this.remapper = remapper;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor av = fv.visitAnnotation(remapper.mapDesc(desc), visible);
        return av == null ? null : new RemappingAnnotationAdapter(av, remapper);
    }

    public void visitAttribute(Attribute attr) {
        fv.visitAttribute(attr);
    }

    public void visitEnd() {
        fv.visitEnd();
    }
}
