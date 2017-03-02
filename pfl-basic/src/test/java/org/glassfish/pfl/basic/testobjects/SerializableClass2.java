package org.glassfish.pfl.basic.testobjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializableClass2 extends SerializableClass1 {

    public final static Object READ_RESOLVE_VALUE = new Object();
    public final static Object WRITE_REPLACE_VALUE = new Object();

    private long aLong;

    public SerializableClass2() {
    }

    public SerializableClass2(long aLong) {
        this.aLong = aLong;
    }

    public long getALong() {
        return aLong;
    }

    private void readObject(ObjectInputStream in) throws IOException {
        aLong = in.readLong();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeLong(aLong);
    }

    private Object readResolve() {
        return READ_RESOLVE_VALUE;
    }

    private Object writeReplace() {
        return WRITE_REPLACE_VALUE;
    }
}
