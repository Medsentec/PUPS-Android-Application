package com.medsentec.data;

/**
 * TODO: Java Docs
 * Created by Justin Ho on 3/14/17.
 */

public class Validation {

    /**
     * TODO: Java Docs
     * @param object
     */
    public static void checkNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Null arguments not allowed.");
        }
    }
}
