package com.aurea.jacoco;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.util.List;
import java.util.stream.Stream;

class NewClassCoverage extends Named implements Coverage {

    private final List<NewMethodCoverage> methodCoverages;

    private final Supplier<Integer> covered = Suppliers
            .memoize(() -> methodCoverages().mapToInt(NewMethodCoverage::getCovered).sum());

    private final Supplier<Integer> uncovered = Suppliers.memoize(() ->
        methodCoverages().mapToInt(NewMethodCoverage::getUncovered).sum());

    private final Supplier<Integer> total = Suppliers.memoize(() ->
        methodCoverages().mapToInt(NewMethodCoverage::getTotal).sum());

    NewClassCoverage(String name, List<NewMethodCoverage> methodCoverages) {
        super(name);
        this.methodCoverages = methodCoverages;
    }

    public List<NewMethodCoverage> getMethodCoverages() {
        return methodCoverages;
    }

    public Stream<NewMethodCoverage> methodCoverages() {
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
        NewClassCoverage that = (NewClassCoverage) o;
        return Objects.equal(getName(), that.getName()) && Objects.equal(methodCoverages, that.methodCoverages);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(methodCoverages, getName());
    }
}
