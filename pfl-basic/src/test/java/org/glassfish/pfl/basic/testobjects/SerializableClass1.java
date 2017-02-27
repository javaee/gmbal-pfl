package org.glassfish.pfl.basic.testobjects;

import org.glassfish.pfl.basic.testobjects.NonSerializableBaseClass;

import java.io.Serializable;

public class SerializableClass1 extends NonSerializableBaseClass implements Serializable {

    private double aDouble;


    public double getADouble() {
        return aDouble;
    }
}
