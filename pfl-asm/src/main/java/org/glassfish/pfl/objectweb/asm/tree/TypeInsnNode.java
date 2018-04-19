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

import org.glassfish.pfl.objectweb.asm.MethodVisitor;

/**
 * A node that represents a type instruction. A type instruction is an
 * instruction that takes a type descriptor as parameter.
 * 
 * @author Eric Bruneton
 */
public class TypeInsnNode extends AbstractInsnNode {

    /**
     * The operand of this instruction. This operand is an internal name (see
     * {@link org.objectweb.asm.Type}).
     */
    public String desc;

    /**
     * Constructs a new {@link TypeInsnNode}.
     * 
     * @param opcode the opcode of the type instruction to be constructed. This
     *        opcode must be NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
     * @param desc the operand of the instruction to be constructed. This
     *        operand is an internal name (see {@link org.objectweb.asm.Type}).
     */
    public TypeInsnNode(final int opcode, final String desc) {
        super(opcode);
        this.desc = desc;
    }

    /**
     * Sets the opcode of this instruction.
     * 
     * @param opcode the new instruction opcode. This opcode must be NEW,
     *        ANEWARRAY, CHECKCAST or INSTANCEOF.
     */
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }

    public int getType() {
        return TYPE_INSN;
    }

    public void accept(final MethodVisitor mv) {
        mv.visitTypeInsn(opcode, desc);
    }

    public AbstractInsnNode clone(final Map labels) {
        return new TypeInsnNode(opcode, desc);
    }
}
