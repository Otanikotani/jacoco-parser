package com.aurea.jacoco;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.util.List;
import java.util.stream.Stream;

class ClassCoverage extends Named implements CoverageUnit {

    private final List<MethodCoverage> methodCoverages;

    private final Supplier<Integer> covered = Suppliers
            .memoize(() -> methodCoverages().mapToInt(MethodCoverage::getCovered).sum());

    private final Supplier<Integer> uncovered = Suppliers.memoize(() ->
        methodCoverages().mapToInt(MethodCoverage::getUncovered).sum());

    private final Supplier<Integer> total = Suppliers.memoize(() ->
        methodCoverages().mapToInt(MethodCoverage::getTotal).sum());

    ClassCoverage(String name, List<MethodCoverage> methodCoverages) {
        super(name);
        this.methodCoverages = methodCoverages;
    }

    public List<MethodCoverage> getMethodCoverages() {
        return methodCoverages;
    }

    public Stream<MethodCoverage> methodCoverages() {
        return methodCoverages.stream();
    }

    public Integer getCovered() {
        return covered.get();
    }

    public Integer getUncovered() {
        return uncovered.get();
    }

    public Integer getTotal() {
        return total.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassCoverage that = (ClassCoverage) o;
        return Objects.equal(getName(), that.getName()) && Objects.equal(methodCoverages, that.methodCoverages);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(methodCoverages, getName());
    }
}
