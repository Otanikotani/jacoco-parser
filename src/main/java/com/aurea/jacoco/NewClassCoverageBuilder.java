package com.aurea.jacoco;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class NewClassCoverageBuilder {
    private String name;
    private List<NewMethodCoverage> methodCoverages = new ArrayList<>();

    private NewClassCoverageBuilder() {
    }

    public static NewClassCoverageBuilder aNewClassCoverage() {
        return new NewClassCoverageBuilder();
    }

    public NewClassCoverageBuilder withName(String name) {
        checkNotNull(name);
        this.name = name;
        return this;
    }

    public NewClassCoverageBuilder addMethodCoverage(NewMethodCoverage methodCoverage) {
        checkNotNull(methodCoverage);
        this.methodCoverages.add(methodCoverage);
        return this;
    }

    public NewClassCoverage build() {
        return new NewClassCoverage(name, methodCoverages);
    }
}
