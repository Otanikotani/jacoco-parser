package com.aurea.jacoco;

import com.google.common.base.MoreObjects;

abstract class Named {

    private final String name;

    protected Named(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .toString();
    }
}
