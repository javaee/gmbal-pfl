package org.glassfish.pfl.basic.reflection;

import org.glassfish.pfl.basic.testobjects.ClassWithStaticInitializer;
import org.glassfish.pfl.basic.testobjects.IntHolder;
import org.glassfish.pfl.basic.testobjects.SerializableClass1;
import org.glassfish.pfl.basic.testobjects.SerializableClass2;
import org.glassfish.pfl.basic.testobjects.TestObjects;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class BridgeTest {
    private static final byte BYTE_VALUE = (byte) (Math.random() * Byte.MAX_VALUE);
    private static final short SHORT_VALUE = (short) (Math.random() * Short.MAX_VALUE);
    private static final int INTEGER_VALUE = (int) (Math.random() * Integer.MAX_VALUE);
    private static final long LONG_VALUE = (long) (Math.random() * Long.MAX_VALUE);
    private static final float FLOAT_VALUE = (float) (Math.random() * Float.MAX_VALUE);
    private static final double DOUBLE_VALUE = (Math.random() * Double.MAX_VALUE);
    private static final boolean BOOLEAN_VALUE = (BYTE_VALUE % 2 == 0);
    private static final char CHAR_VALUE = (char) (Math.random() * Character.MAX_VALUE);
    private static final Object OBJECT_VALUE = new Object();

    private static final Bridge BRIDGE = AccessController.doPrivileged(
            new PrivilegedAction<Bridge>() {
                @Override
                public Bridge run() {
                    return Bridge.get();
                }
            }
    );
    private final ByteArrayOutputStream rawOutputStream = new ByteArrayOutputStream();
    private ObjectOutputStream objectOutputStream;

    private static class MultiFieldClass {
        private byte aByte;
        private short aShort;
        private int anInt;
        private long aLong;
        private float aFloat;
        private double aDouble;
        private boolean aBoolean;
        private char aChar;
        private Object anObject;
    }

    private MultiFieldClass anInstance = new MultiFieldClass();

    @Test @Ignore("comments indicate required by spec, but not what this actually means")
    public void getLatestUserDefinedLoader() throws Exception {
    }

    @Test
    public void getByte() throws Exception {
        anInstance.aByte = BYTE_VALUE;

        assertThat(BRIDGE.getByte(anInstance, getFieldOffset("aByte")), equalTo(BYTE_VALUE));
    }

    private long getFieldOffset(String fieldName) throws NoSuchFieldException {
        Field f = MultiFieldClass.class.getDeclaredField(fieldName);
        return BRIDGE.objectFieldOffset(f);
    }

    @Test
    public void putByte() throws Exception {
        BRIDGE.putByte(anInstance, getFieldOffset("aByte"), BYTE_VALUE);

        assertThat(anInstance.aByte, equalTo(BYTE_VALUE));
    }

    @Test
    public void getShort() throws Exception {
        anInstance.aShort = SHORT_VALUE;

        assertThat(BRIDGE.getShort(anInstance, getFieldOffset("aShort")), equalTo(SHORT_VALUE));
    }

    @Test
    public void putShort() throws Exception {
        BRIDGE.putShort(anInstance, getFieldOffset("aShort"), SHORT_VALUE);

        assertThat(anInstance.aShort, equalTo(SHORT_VALUE));
    }

    @Test
    public void getInt() throws Exception {
        anInstance.anInt = INTEGER_VALUE;

        assertThat(BRIDGE.getInt(anInstance, getFieldOffset("anInt")), equalTo(INTEGER_VALUE));
    }

    @Test
    public void putInt() throws Exception {
        BRIDGE.putInt(anInstance, getFieldOffset("anInt"), INTEGER_VALUE);

        assertThat(anInstance.anInt, equalTo(INTEGER_VALUE));
    }

    @Test
    public void getLong() throws Exception {
        anInstance.aLong = LONG_VALUE;

        assertThat(BRIDGE.getLong(anInstance, getFieldOffset("aLong")), equalTo(LONG_VALUE));
    }

    @Test
    public void putLong() throws Exception {
        BRIDGE.putLong(anInstance, getFieldOffset("aLong"), LONG_VALUE);

        assertThat(anInstance.aLong, equalTo(LONG_VALUE));
    }

    @Test
    public void getFloat() throws Exception {
        anInstance.aFloat = FLOAT_VALUE;

        assertThat(BRIDGE.getFloat(anInstance, getFieldOffset("aFloat")), equalTo(FLOAT_VALUE));
    }

    @Test
    public void putFloat() throws Exception {
        BRIDGE.putFloat(anInstance, getFieldOffset("aFloat"), FLOAT_VALUE);

        assertThat(anInstance.aFloat, equalTo(FLOAT_VALUE));
    }

    @Test
    public void getDouble() throws Exception {
        anInstance.aDouble = DOUBLE_VALUE;

        assertThat(BRIDGE.getDouble(anInstance, getFieldOffset("aDouble")), equalTo(DOUBLE_VALUE));
    }

    @Test
    public void putDouble() throws Exception {
        BRIDGE.putDouble(anInstance, getFieldOffset("aDouble"), DOUBLE_VALUE);

        assertThat(anInstance.aDouble, equalTo(DOUBLE_VALUE));
    }

    @Test
    public void getBoolean() throws Exception {
        anInstance.aBoolean = BOOLEAN_VALUE;

        assertThat(BRIDGE.getBoolean(anInstance, getFieldOffset("aBoolean")), equalTo(BOOLEAN_VALUE));
    }

    @Test
    public void putBoolean() throws Exception {
        BRIDGE.putBoolean(anInstance, getFieldOffset("aBoolean"), BOOLEAN_VALUE);

        assertThat(anInstance.aBoolean, equalTo(BOOLEAN_VALUE));
    }

    @Test
    public void getChar() throws Exception {
        anInstance.aChar = CHAR_VALUE;

        assertThat(BRIDGE.getChar(anInstance, getFieldOffset("aChar")), equalTo(CHAR_VALUE));
    }

    @Test
    public void putChar() throws Exception {
        BRIDGE.putChar(anInstance, getFieldOffset("aChar"), CHAR_VALUE);

        assertThat(anInstance.aChar, equalTo(CHAR_VALUE));
    }

    @Test
    public void getObject() throws Exception {
        anInstance.anObject = OBJECT_VALUE;

        assertThat(BRIDGE.getObject(anInstance, getFieldOffset("anObject")), sameInstance(OBJECT_VALUE));
    }

    @Test
    public void putObject() throws Exception {
        BRIDGE.putObject(anInstance, getFieldOffset("anObject"), OBJECT_VALUE);

        assertThat(anInstance.anObject, sameInstance(OBJECT_VALUE));
    }

    @Test(expected = IOException.class)
    public void throwUndeclaredCheckedException() {
        BRIDGE.throwException(new IOException());
    }

    @Test
    public void whenBridgeCreatesConstructor_canConstructNonPublicExternalizedClass() throws Exception {
        Class<? extends IntHolder> aClass = TestObjects.getNonPublicExternalizableClass();
        Constructor<? extends IntHolder> constructor = BRIDGE.newConstructorForExternalization(aClass);

        if (constructor == null) fail("no constructorFound");
        IntHolder intHolder = constructor.newInstance();

        assertThat(intHolder.getAnInt(), equalTo(TestObjects.INT_FIELD_VALUE));
    }

    @Test
    public void whenBridgeCreatesConstructor_nonSerializableBaseClassIsInitialized() throws Exception {
        Class<? extends IntHolder> aClass = TestObjects.getNonPublicSerializableClass();
        Constructor<? extends IntHolder> constructor = BRIDGE.newConstructorForSerialization(aClass);

        if (constructor == null) fail("no constructorFound");
        IntHolder intHolder = constructor.newInstance();

        assertThat(intHolder.getAnInt(), equalTo(TestObjects.INT_FIELD_VALUE));
    }

    @Test
    public void whenClassHasNoStaticInitializer_hasStaticInitializerReturnsFalse() throws Exception {
        assertThat(BRIDGE.hasStaticInitializerForSerialization(SerializableClass1.class), is(false));
    }

    @Test
    public void whenClassHasStaticInitializer_hasStaticInitializerReturnsTrue() throws Exception {
        assertThat(BRIDGE.hasStaticInitializerForSerialization(ClassWithStaticInitializer.class), is(true));
    }

    @Test
    public void whenClassHasNoReadObjectMethod_returnNull() throws Exception {
        assertThat(BRIDGE.readObjectForSerialization(SerializableClass1.class), nullValue());
    }

    @Test
    public void whenClassHasNoWriteObjectMethod_returnNull() throws Exception {
        assertThat(BRIDGE.writeObjectForSerialization(SerializableClass1.class), nullValue());
    }

    @Test
    public void whenClassHasReadObjectMethod_mayInvokeViaHandle() throws Throwable {
        MethodHandle methodHandle = BRIDGE.readObjectForSerialization(SerializableClass2.class);

        SerializableClass2 obj = new SerializableClass2();
        createObjectOutputStream().writeLong(123L);
        methodHandle.invoke(obj, createObjectInputStream());

        assertThat(obj.getALong(), equalTo(123L));
    }

    private ObjectOutputStream createObjectOutputStream() throws IOException {
        objectOutputStream = new ObjectOutputStream(rawOutputStream);
        return objectOutputStream;
    }

    private ObjectInputStream createObjectInputStream() throws IOException {
        objectOutputStream.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(rawOutputStream.toByteArray());
        return new ObjectInputStream(bais);
    }

    @Test
    public void whenClassHasWriteObjectMethod_mayInvokeViaHandle() throws Throwable {
        MethodHandle methodHandle = BRIDGE.writeObjectForSerialization(SerializableClass2.class);

        SerializableClass2 obj = new SerializableClass2(456L);
        methodHandle.invoke(obj, createObjectOutputStream());

        assertThat(createObjectInputStream().readLong(), equalTo(456L));
    }

    @Test(expected = OptionalDataException.class)
    public void createOptionalDataException() throws Exception {
        throw BRIDGE.newOptionalDataExceptionForSerialization(true);
    }
}