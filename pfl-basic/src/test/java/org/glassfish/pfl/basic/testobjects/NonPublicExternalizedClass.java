package org.glassfish.pfl.basic.testobjects;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

class NonPublicExternalizedClass implements IntHolder, Externalizable {

    private int anInt;

    public NonPublicExternalizedClass() {
        this.anInt = TestObjects.INT_FIELD_VALUE;
    }

    @Override
    public int getAnInt() {
        return anInt;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(anInt);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        anInt = in.readInt();
    }
}
