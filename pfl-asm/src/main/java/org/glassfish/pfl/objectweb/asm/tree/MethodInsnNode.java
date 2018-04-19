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
 * A node that represents a method instruction. A method instruction is an
 * instruction that invokes a method.
 * 
 * @author Eric Bruneton
 */
public class MethodInsnNode extends AbstractInsnNode {

    /**
     * The internal name of the method's owner class (see
     * {@link org.objectweb.asm.Type#getInternalName() getInternalName}).
     */
    public String owner;

    /**
     * The method's name.
     */
    public String name;

    /**
     * The method's descriptor (see {@link org.objectweb.asm.Type}).
     */
    public String desc;

    /**
     * Constructs a new {@link MethodInsnNode}.
     * 
     * @param opcode the opcode of the type instruction to be constructed. This
     *        opcode must be INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC,
     *        INVOKEINTERFACE or INVOKEDYNAMIC.
     * @param owner the internal name of the method's owner class (see
     *        {@link org.objectweb.asm.Type#getInternalName() getInternalName})
     *        or {@link org.objectweb.asm.Opcodes#INVOKEDYNAMIC_OWNER}.
     * @param name the method's name.
     * @param desc the method's descriptor (see {@link org.objectweb.asm.Type}).
     */
    public MethodInsnNode(
        final int opcode,
        final String owner,
        final String name,
        final String desc)
    {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    /**
     * Sets the opcode of this instruction.
     * 
     * @param opcode the new instruction opcode. This opcode must be
     *        INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
     */
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }

    public int getType() {
        return METHOD_INSN;
    }

    public void accept(final MethodVisitor mv) {
        mv.visitMethodInsn(opcode, owner, name, desc);
    }

    public AbstractInsnNode clone(final Map labels) {
        return new MethodInsnNode(opcode, owner, name, desc);
    }
}
