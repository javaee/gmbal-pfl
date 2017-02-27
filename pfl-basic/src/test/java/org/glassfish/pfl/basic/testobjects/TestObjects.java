package org.glassfish.pfl.basic.testobjects;

public class TestObjects {

    public static final int INT_FIELD_VALUE = (int) (Math.random() * Integer.MAX_VALUE);

    public static Class<? extends IntHolder> getNonPublicExternalizableClass() {
        return NonPublicExternalizedClass.class;
    }

    public static Class<? extends IntHolder> getNonPublicSerializableClass() {
        return SerializableClass2.class;
    }
}
