/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2011 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.pfl.basic.algorithm ;

import java.io.PrintStream ;

/** Manages printing of indented source code.
 * Line numbers start at 1 and increase by 1
 * every time nl() is called.  Note that the 
 * proper use of this class requires calling nl()
 * at the START of every line (thanks, Harold!),
 * which make indentation much easier to manage.  For example,
 * an if statement can be printed as
 * 
 * nl().p( "if (expr) {" ).in() ;
 * nl().p( "stmt" ).out() ;
 * nl().p( "} else {" ).in() ;
 * nl().p( "stmt" ).out() ;
 */
public class Printer{
    public static final int DEFAULT_INCREMENT = 4 ;

    private PrintStream ps ;
    private int increment ;
    private char padChar ;

    private int indent ;
    private char[] pad ;
    private StringBuilder bld ;
    private int rightJustificationSize ;

    public Printer( PrintStream ps ) {
	this( ps, DEFAULT_INCREMENT, ' ' ) ;
    }

    public Printer( PrintStream ps, int increment, char padChar ) {
	this.ps = ps ;
	this.increment = increment ;
	this.padChar = padChar ;
	this.indent = 0 ;
	this.bld = new StringBuilder() ;
	fill() ;
	this.rightJustificationSize = 0 ;
    }

    /** Right-Justify the next call to p so that the total number of characters
     * is at least size.  Use leading spaces if necessary to ensure this.
     */
    public Printer rj( int size ) {
	rightJustificationSize = size ;
	return this ;
    }

    private Printer rightJustify( String str ) {
	if (str.length() < rightJustificationSize) {
	    for (int ctr=0; ctr<(rightJustificationSize-str.length()); ctr++) {
		bld.append( ' ' ) ;
	    }
	}

	rightJustificationSize = 0 ;
	return this ;
    }

    public Printer p( String str ) {
	rightJustify( str ) ;
	bld.append( str ) ;
	return this ;
    }

    public Printer p( Object... args ) {
        for (Object obj : args)
            p( obj ) ;

        return this ;
    }

    public Printer p( Object obj ) {
	String str = obj.toString() ;
	rightJustify( str ) ;
	bld.append( str ) ;
	return this ;
    }

    public Printer in() {
	indent += increment ;
	fill() ;
	return this ;
    }

    public Printer out() {
	if (indent < increment)
	    throw new IllegalStateException(
		"Cannot undent past start of line" ) ;

	indent -= increment ;
	fill() ;
	return this ;
    }

    public int indent() {
	return indent ;
    }

    private void fill() {
	pad = new char[indent] ;
	for (int ctr = 0; ctr<pad.length; ctr++)
	    pad[ctr] = padChar ;
    }

    public Printer nl() {
	ps.println( bld.toString() ) ;
	bld = new StringBuilder() ;
	bld.append( pad ) ; 
	return this ;
    }

    private boolean isPrintable(char c) {
	if (Character.isJavaIdentifierStart(c)) {
	    // Letters and $ _
	    return true;
	}
	if (Character.isDigit(c)) {
	    return true;
	}
	switch (Character.getType(c)) {
	    case Character.MODIFIER_SYMBOL : return true; // ` ^
	    case Character.DASH_PUNCTUATION : return true; // -
	    case Character.MATH_SYMBOL : return true; // = ~ + | < >
	    case Character.OTHER_PUNCTUATION : return true; // !@#%&*;':",./?
	    case Character.START_PUNCTUATION : return true; // ( [ {
	    case Character.END_PUNCTUATION : return true; // ) ] }
	}
	return false;
    }

    public Printer printBuffer( byte[] buffer ) {
	int length = buffer.length ;

        for (int i = 0; i < length; i += 16) {
            StringBuffer sbuf = new StringBuffer() ;
            int j = 0;
            
            // For every 16 bytes, there is one line of output.  First, 
            // the hex output of the 16 bytes with each byte separated
            // by a space.
            while (j < 16 && (i + j) < length) {
                int k = buffer[i + j];
                if (k < 0)
                    k = 256 + k;
                String hex = Integer.toHexString(k);
                if (hex.length() == 1)
                    hex = "0" + hex;
                sbuf.append(hex + " ");
                j++;
            }
            
            // Add any extra spaces to align the
            // text column in case we didn't end
            // at 16
            while (j < 16) {
                sbuf.append("   ");
                j++;
            }
            
            // Now output the ASCII equivalents.  Non-ASCII
            // characters are shown as periods.
            int x = 0;
            while (x < 16 && x + i < length) {
                char ch = (char)buffer[i + x] ;
                if (isPrintable( ch ))
                    sbuf.append( ch ) ;
                else
                    sbuf.append( '.' ) ;
                x++;
            }

            nl().p( sbuf ) ;
        }

        return this ;
    }
}
