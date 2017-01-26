package org.glassfish.pfl.tf.tools.enhancer;

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2010,2017 Oracle and/or its affiliates. All rights reserved.
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

import org.glassfish.pfl.basic.tools.file.FileWrapper;
import org.glassfish.pfl.basic.tools.file.Scanner;
import org.glassfish.pfl.tf.spi.Util;
import org.glassfish.pfl.tf.spi.annotation.MethodMonitorGroup;
import org.glassfish.pfl.tf.timer.spi.TimingInfoProcessor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Scan all classes looking for annotations annotated with @MethodMonitorGroup,
 * and saves the internal names of any such annotations.
 *
 * @author ken
 */
public class AnnotationScannerAction implements Scanner.Action {
    private static Class<MethodMonitorGroup> MMG_CLASS =
        MethodMonitorGroup.class ;

    private static String MMG_DESCRIPTOR =
        Type.getType(MMG_CLASS).getDescriptor() ;

    private final Util util ;
    private final TimingInfoProcessor tip ;

    // NOTE: this is a set of annotation class names in INTERNAL format.
    private Set<String> annotationNames = new HashSet<String>() ;
    private String currentClass ;

    AnnotationScannerAction(Util util, TimingInfoProcessor tip) {
        this.util = util ;
        this.tip = tip ;
    }

    public Set<String> getAnnotationNames() {
        return annotationNames ;
    }

    private class AnnoScanner extends EmptyVisitor {
        private boolean visitingAnnotation = false ;
        private String annotationValueName = null ;
        private String timerGroupDescription ;
        private String timerGroupName ;
        private List<Type> timerGroupMembers ;

        @Override
        public void visit( int version, int access, String name, String signature,
            String superName, String[] interfaces ) {
            util.info( 3, "Visiting class " + name ) ;

            if ((access & Opcodes.ACC_ANNOTATION) == Opcodes.ACC_ANNOTATION) {
                util.info( 2, "\t(Annotation)") ;
                // We are only interested in classes that are annotations
                currentClass = name ;
            }
        }

        private String getGroupName( String desc ) {
            String result = desc ;
            final int index = desc.lastIndexOf('/') ;

            if (index >= 0) {
                result = desc.substring( index + 1 ) ;
            }

            return result ;
        }

        @Override
        public AnnotationVisitor visitAnnotation( String desc,
            boolean visible ) {

            util.info( 3, "\tVisiting annotation " + desc ) ;

            if (desc.equals( MMG_DESCRIPTOR )) {
                // Leave name in internal form.
                annotationNames.add( currentClass  );
                visitingAnnotation = true ;
                timerGroupName = getGroupName( currentClass ) ;
                timerGroupDescription = "TimerGroup for Annotation "
                    + timerGroupName ;
                timerGroupMembers = new ArrayList<Type>() ;

                return this ;
            }

            return null ;
        }

        @Override 
        // visit an Annotation member
        public void visit( String name, Object value ) {
            if (name == null) {
                // This is called after visitArray
                if (annotationValueName.equals( "value" )) {
                    timerGroupMembers.add( (Type)value ) ;
                }
            } else if(name.equals("description")) {
                if (value instanceof String) { 
                    timerGroupDescription = (String)value ;
                }
            } else if (name.equals( "value")) {
                if (value instanceof Type[]) {
                    // This normally doesn't happen: it seems that ASM
                    // always visits the array elements.
                    timerGroupMembers = Arrays.asList( (Type[])value ) ;
                }
            }
        }

        @Override
        public AnnotationVisitor visitArray( String name ) {
            annotationValueName = name ;
            return this ;
        }

        @Override
        // Only interested in the visitEnd on an Annotation!
        public void visitEnd() {
            if (visitingAnnotation) {
                visitingAnnotation = false ;

                tip.addTimerGroup( timerGroupName, timerGroupDescription ) ;
                for (Type type : timerGroupMembers) {
                    String name = type.getClassName() ;
                    final int index = name.lastIndexOf('.') ;
                    if (index >= 0) {
                        name = name.substring(index + 1) ;
                    }
                    tip.contains( name ) ;
                }
            }
        }

        // Don't visit fields or methods: we don't need to look at their 
        // annotations in this visitor.
        @Override
        public MethodVisitor visitMethod( final int access, final String name,
            final String desc, final String signature, 
            final String[] exceptions ) {

            return null ;
        }

        @Override
        public FieldVisitor visitField( final int access, final String name,
            final String desc, final String signature,
            final Object value ) {

            return null ;
        }
    }

    public boolean evaluate(FileWrapper fw) {
        try {
            byte[] inputData = fw.readAll();
            ClassReader cr = new ClassReader( inputData ) ;
            ClassVisitor as = new AnnoScanner() ;
            cr.accept( as, 0 );
        } catch (IOException ex) {
            return true ; // ignore things we can't read
        }

        return true ;
    }
}

