package com.cr.RCSVisualizer.entity;

public enum TypeConstant {
    BOOLEAN("java.lang.Boolean"),
    CHARACTER("java.lang.Character"),
    BYTE("java.lang.Byte"),
    SHORT("java.lang.Short"),
    INTEGER("java.lang.Integer"),
    LONG("java.lang.Long"),
    FLOAT("java.lang.Float"),
    DOUBLE("java.lang.Double"),
    ARRAYLIST("java.util.ArrayList"),
    HASHSET("java.util.HashSet"),
    HASHMAP("java.util.HashMap");

    private final String typeName;

    TypeConstant(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
