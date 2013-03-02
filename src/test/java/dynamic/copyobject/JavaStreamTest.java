/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package dynamic.copyobject  ;


import junit.framework.Test ;
import org.glassfish.pfl.dynamic.copyobject.spi.CopyobjectDefaults;
import org.glassfish.pfl.dynamic.copyobject.spi.ObjectCopierFactory;

public class JavaStreamTest extends Client
{
    // Mostly these fail because they are not Serializable.
    // I'm not sure what the UserException problem is, but org.omg.CORBA.UserException
    // is an abstract class (see Client.throwUserException).
    // testIdentityHashMap seems to be failing because the elements come out in a
    // different order?
    // testExternalizable fails because readExternal and writeExternal are not written
    // to write the data.
    // I have not explored the other failures, but this list should be reduced to
    // those tests that are correct for reflective copy and cannot work for stream
    // copy.
    private static final String[] EXCLUDE_LIST = new String[] {
	"testObject", "testTimedObject", "testObjects", "testComplexClassArray",
	"testComplexClassAliasedArray", "testComplexClassGraph",
	"testUserException", 
	"testRemoteStub", "testCORBAObject", "testInnerClass", 
	"testExtendedInnerClass", "testNestedClass", "testLocalInner",
	"testAnonymousLocalInner", "testDynamicProxy", "testIdentityHashMap",
	"testExternalizable", "testTransientNonSerializableField1", 
	"testTransientNonSerializableField2", "testTransientNonSerializableField3", 
	"testNonSerializableSuperClass", "testExternalizableNonStaticContext" 
    } ;

    public JavaStreamTest() { }

    public JavaStreamTest( String name ) { super( name ) ; }

    public static void main( String[] args ) 
    { 
  	// Create an instance of the test suite that is used only
	// to invoke the makeSuite() method.  No name is needed here.
	Client root = new JavaStreamTest() ;
	Client.doMain( args, root ) ; 
    }

    public static Test suite() {
	Client root = new JavaStreamTest() ;
	return root.makeSuite() ;
    }
    
    public ObjectCopierFactory getCopierFactory( )
    {
	return CopyobjectDefaults.makeJavaStreamObjectCopierFactory( ) ;
    }

    public boolean isTestExcluded()
    {
	String testName = getName() ;
	for (int ctr=0; ctr<EXCLUDE_LIST.length; ctr++) 
	    if (testName.equals( EXCLUDE_LIST[ctr]))
		return true ;

	return false ;
    }

    public Client makeTest( String name ) 
    {
	return new JavaStreamTest( name ) ;
    }
}
