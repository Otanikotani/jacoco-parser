package com.aurea.jacoco;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ClassCoverageBuilder {
    private String name;
    private List<MethodCoverage> methodCoverages = new ArrayList<>();

    private ClassCoverageBuilder() {
    }

    public static ClassCoverageBuilder aNewClassCoverage() {
        return new ClassCoverageBuilder();
    }

    public ClassCoverageBuilder withName(String name) {
        checkNotNull(name);
        this.name = name;
        return this;
    }

    public ClassCoverageBuilder addMethodCoverage(MethodCoverage methodCoverage) {
        checkNotNull(methodCoverage);
        this.methodCoverages.add(methodCoverage);
        return this;
    }

    public ClassCoverage build() {
        return new ClassCoverage(name, methodCoverages);
    }
}
