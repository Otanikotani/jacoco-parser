package com.aurea.jacoco;

abstract class Named {

    private final String name;

    protected Named(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
