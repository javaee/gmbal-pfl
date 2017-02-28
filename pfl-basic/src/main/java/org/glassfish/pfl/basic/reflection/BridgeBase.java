package org.glassfish.pfl.basic.reflection;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

public abstract class BridgeBase implements BridgeOperations {
    private final Unsafe unsafe = AccessController.doPrivileged(
                    new PrivilegedAction<Unsafe>() {
                        public Unsafe run() {
                            try {
                                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                                field.setAccessible(true);
                                return (Unsafe) field.get(null);
                            } catch (NoSuchFieldException | IllegalAccessException exc) {
                                throw new Error("Could not access Unsafe", exc);
                            }
                        }
                    }
            );

    protected final Unsafe getUnsafe() {
        return unsafe;
    }

    @Override
    public final int getInt(Object o, long offset) {
        return unsafe.getInt(o, offset);
    }

    @Override
    public final void putInt(Object o, long offset, int x) {
        unsafe.putInt(o, offset, x);
    }

    @Override
    public final Object getObject(Object o, long offset) {
        return unsafe.getObject(o, offset);
    }

    @Override
    public final void putObject(Object o, long offset, Object x) {
        unsafe.putObject(o, offset, x);
    }

    @Override
    public final boolean getBoolean(Object o, long offset) {
        return unsafe.getBoolean(o, offset);
    }

    @Override
    public final void putBoolean(Object o, long offset, boolean x) {
        unsafe.putBoolean(o, offset, x);
    }

    @Override
    public final byte getByte(Object o, long offset) {
        return unsafe.getByte(o, offset);
    }

    @Override
    public final void putByte(Object o, long offset, byte x) {
        unsafe.putByte(o, offset, x);
    }

    @Override
    public final short getShort(Object o, long offset) {
        return unsafe.getShort(o, offset);
    }

    @Override
    public final void putShort(Object o, long offset, short x) {
        unsafe.putShort(o, offset, x);
    }

    @Override
    public final char getChar(Object o, long offset) {
        return unsafe.getChar(o, offset);
    }

    @Override
    public final void putChar(Object o, long offset, char x) {
        unsafe.putChar(o, offset, x);
    }

    @Override
    public final long getLong(Object o, long offset) {
        return unsafe.getLong(o, offset);
    }

    @Override
    public final void putLong(Object o, long offset, long x) {
        unsafe.putLong(o, offset, x);
    }

    @Override
    public final float getFloat(Object o, long offset) {
        return unsafe.getFloat(o, offset);
    }

    @Override
    public final void putFloat(Object o, long offset, float x) {
        unsafe.putFloat(o, offset, x);
    }

    @Override
    public final double getDouble(Object o, long offset) {
        return unsafe.getDouble(o, offset);
    }

    @Override
    public final void putDouble(Object o, long offset, double x) {
        unsafe.putDouble(o, offset, x);
    }

    @Override
    public final long objectFieldOffset(Field f) {
        return unsafe.objectFieldOffset(f);
    }

    @Override
    public final void throwException(Throwable ee) {
        unsafe.throwException(ee);
    }

    @Override
    public final Class<?> defineClass(String className, byte[] classBytes, ClassLoader classLoader, ProtectionDomain protectionDomain) {
        return unsafe.defineClass(className, classBytes, 0, classBytes.length, classLoader, null);
    }
}
