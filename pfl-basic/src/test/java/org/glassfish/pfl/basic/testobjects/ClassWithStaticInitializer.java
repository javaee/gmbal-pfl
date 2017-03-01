package org.glassfish.pfl.basic.testobjects;

public class ClassWithStaticInitializer {
    private final static int anInt;

    static {
        anInt = 5;
    }

    public static int getAnInt() {
        return anInt;
    }
}
