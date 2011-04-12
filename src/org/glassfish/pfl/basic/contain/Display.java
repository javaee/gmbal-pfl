/* 
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *  
 *  Copyright (c) 2008-2011 Oracle and/or its affiliates. All rights reserved.
 *  
 *  The contents of this file are subject to the terms of either the GNU
 *  General Public License Version 2 only ("GPL") or the Common Development
 *  and Distribution License("CDDL") (collectively, the "License").  You
 *  may not use this file except in compliance with the License.  You can
 *  obtain a copy of the License at
 *  https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 *  or packager/legal/LICENSE.txt.  See the License for the specific
 *  language governing permissions and limitations under the License.
 *  
 *  When distributing the software, include this License Header Notice in each
 *  file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 *  
 *  GPL Classpath Exception:
 *  Oracle designates this particular file as subject to the "Classpath"
 *  exception as provided by Oracle in the GPL Version 2 section of the License
 *  file that accompanied this code.
 *  
 *  Modifications:
 *  If applicable, add the following below the License Header, with the fields
 *  enclosed by brackets [] replaced by your own identifying information:
 *  "Portions Copyright [year] [name of copyright owner]"
 *  
 *  Contributor(s):
 *  If you wish your version of this file to be governed by only the CDDL or
 *  only the GPL Version 2, indicate your decision by adding "[Contributor]
 *  elects to include this software in this distribution under the [CDDL or GPL
 *  Version 2] license."  If you don't indicate a single choice of license, a
 *  recipient has the option to distribute your version of this file under
 *  either the CDDL, the GPL Version 2 or to extend the choice of license to
 *  its licensees as provided above.  However, if you add GPL Version 2 code
 *  and therefore, elected the GPL Version 2 license, then the option applies
 *  only if the new code is made subject to such option by the copyright
 *  holder.
 */ 
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.glassfish.pfl.basic.contain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Supports a Display as typically found in Lexical scoping.
 * Upon entering a scope, a new environment is available for
 * binding keys to values.  Exiting a scope remove the top-most
 * environment.  Lookup searches from the top down to find the
 * value for the first matching key.
 *
 * @param <K> The type of the Key
 * @param <V> The type of the Value
 * @author ken
 */
public class Display<K,V> {
    private List<Map<K,V>> display = new ArrayList<Map<K,V>>() ;
    
    public void enterScope() {
        display.add( new HashMap<K,V>() ) ;
    }
    
    public void exitScope() {
        if (display.isEmpty()) {
            throw new IllegalStateException( "Display is empty" ) ;
        }
        
        display.remove( display.size() - 1 ) ;
    }
    
    public void bind( K key, V value ) {
        if (display.isEmpty()) {
            throw new IllegalStateException( "Display is empty" ) ;
        }
        
        display.get( display.size() - 1 ).put( key, value) ;
    }
    
    public void bind( Map<K,V> bindings ) {
        if (display.isEmpty()) {
            throw new IllegalStateException( "Display is empty" ) ;
        }
        
        display.get( display.size() - 1 ).putAll( bindings ) ;   
    }
    
    public V lookup( K key ) {
        V result = null ;
        for (int ctr=display.size()-1; ctr>=0; ctr-- ) {
            result = display.get( ctr ).get( key ) ;
            if (result != null) {
                break;
            }
        }
        
        return result ;
    }
}
