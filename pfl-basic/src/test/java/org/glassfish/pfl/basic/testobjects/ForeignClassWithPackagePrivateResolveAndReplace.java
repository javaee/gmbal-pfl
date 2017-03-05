package org.glassfish.pfl.basic.testobjects;

@SuppressWarnings("unused")
public class ForeignClassWithPackagePrivateResolveAndReplace {
    Object readResolve() { return null; }
    Object writeReplace() { return null; }
}
