package com.aurea.coverage.unit;

public abstract class Named {

    private final String name;

    protected Named(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Named{" +
                "name='" + name + '\'' +
                '}';
    }
}
