package com.aurea.jacoco;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import one.util.streamex.StreamEx;

import java.util.List;
import java.util.stream.Stream;

class ModuleCoverage extends Named implements Coverage {

    private final List<PackageCoverage> packageCoverages;

    private final Supplier<Integer> covered = Suppliers
            .memoize(() -> methodCoverages().mapToInt(NewMethodCoverage::getCovered).sum());

    private final Supplier<Integer> uncovered = Suppliers.memoize(() ->
            methodCoverages().mapToInt(NewMethodCoverage::getUncovered).sum());

    private final Supplier<Integer> total = Suppliers.memoize(() ->
            methodCoverages().mapToInt(NewMethodCoverage::getTotal).sum());


    ModuleCoverage(String name, List<PackageCoverage> packageCoverages) {
        super(name);
        this.packageCoverages = packageCoverages;
    }

    public List<PackageCoverage> getPackageCoverages() {
        return packageCoverages;
    }

    public Stream<PackageCoverage> packageCoverages() {
        return packageCoverages.stream();
    }

    public Stream<NewClassCoverage> classCoverages() {
        return StreamEx.of(packageCoverages()).flatCollection(PackageCoverage::getClassCoverages);
    }

    public Stream<NewMethodCoverage> methodCoverages() {
        return StreamEx.of(classCoverages()).flatCollection(NewClassCoverage::getMethodCoverages);
    }

    @Override
    public Integer getCovered() {
        return covered.get();
    }

    @Override
    public Integer getUncovered() {
        return uncovered.get();
    }

    @Override
    public Integer getTotal() {
        return total.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleCoverage that = (ModuleCoverage) o;
        return Objects.equal(getName(), that.getName()) && Objects.equal(packageCoverages, that.packageCoverages);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(packageCoverages, getName());
    }
}
