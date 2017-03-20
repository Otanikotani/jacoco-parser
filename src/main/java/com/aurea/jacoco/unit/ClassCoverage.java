package com.aurea.jacoco.unit;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class ClassCoverage extends MethodContainerUnit {

    public static final ClassCoverage EMPTY = new ClassCoverage("", Collections.emptyList());

    private final List<MethodCoverage> methodCoverages;

    public ClassCoverage(String name, List<MethodCoverage> methodCoverages) {
        super(name);
        this.methodCoverages = methodCoverages;
    }

    public List<MethodCoverage> getMethodCoverages() {
        return methodCoverages;
    }

    @Override
    public Stream<MethodCoverage> methodCoverages() {
        return methodCoverages.stream();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassCoverage that = (ClassCoverage) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(methodCoverages, that.methodCoverages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodCoverages, getName());
    }
}
