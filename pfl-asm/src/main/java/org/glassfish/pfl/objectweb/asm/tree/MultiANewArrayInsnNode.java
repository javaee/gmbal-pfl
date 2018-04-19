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

package org.glassfish.pfl.objectweb.asm.tree;

import java.util.Map;

import org.glassfish.pfl.objectweb.asm.Opcodes;
import org.glassfish.pfl.objectweb.asm.MethodVisitor;

/**
 * A node that represents a MULTIANEWARRAY instruction.
 * 
 * @author Eric Bruneton
 */
public class MultiANewArrayInsnNode extends AbstractInsnNode {

    /**
     * An array type descriptor (see {@link org.objectweb.asm.Type}).
     */
    public String desc;

    /**
     * Number of dimensions of the array to allocate.
     */
    public int dims;

    /**
     * Constructs a new {@link MultiANewArrayInsnNode}.
     * 
     * @param desc an array type descriptor (see {@link org.objectweb.asm.Type}).
     * @param dims number of dimensions of the array to allocate.
     */
    public MultiANewArrayInsnNode(final String desc, final int dims) {
        super(Opcodes.MULTIANEWARRAY);
        this.desc = desc;
        this.dims = dims;
    }

    public int getType() {
        return MULTIANEWARRAY_INSN;
    }

    public void accept(final MethodVisitor mv) {
        mv.visitMultiANewArrayInsn(desc, dims);
    }

    public AbstractInsnNode clone(final Map labels) {
        return new MultiANewArrayInsnNode(desc, dims);
    }

}
