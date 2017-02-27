package org.glassfish.pfl.basic.testobjects;

/**
 * A class which initializes some fields that will be ignored by deserialization.
 */
class NonSerializableBaseClass implements IntHolder {

    private int aNumber;

    NonSerializableBaseClass() {
        aNumber = TestObjects.INT_FIELD_VALUE;
    }

    @Override
    public int getAnInt() {
        return aNumber;
    }
}
