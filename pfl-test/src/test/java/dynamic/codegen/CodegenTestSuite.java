/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dynamic.codegen;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author ken
 */
public class CodegenTestSuite extends TestCase {
    
    public CodegenTestSuite(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("CodegenTestSuite");
        return suite;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
