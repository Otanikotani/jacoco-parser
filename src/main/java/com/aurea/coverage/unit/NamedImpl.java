package com.aurea.coverage.unit;

public abstract class NamedImpl implements Named {

    private final String name;

    protected NamedImpl(String name) {
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
