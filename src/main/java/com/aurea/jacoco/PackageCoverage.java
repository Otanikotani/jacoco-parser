package com.aurea.jacoco;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import one.util.streamex.StreamEx;

import java.util.List;
import java.util.stream.Stream;

class PackageCoverage extends Named implements CoverageUnit {

    private final List<ClassCoverage> classCoverages;

    private final Supplier<Integer> covered = Suppliers
            .memoize(() -> methodCoverages().mapToInt(MethodCoverage::getCovered).sum());

    private final Supplier<Integer> uncovered = Suppliers.memoize(() ->
            methodCoverages().mapToInt(MethodCoverage::getUncovered).sum());

    private final Supplier<Integer> total = Suppliers.memoize(() ->
            methodCoverages().mapToInt(MethodCoverage::getTotal).sum());


    PackageCoverage(String name, List<ClassCoverage> classCoverages) {
        super(name);
        this.classCoverages = classCoverages;
    }

    public boolean isSubPackageOf(PackageCoverage packageCoverage) {
        return getName().startsWith(packageCoverage.getName());
    }

    public boolean isUnderPackageOf(PackageCoverage packageCoverage) {
        return packageCoverage.getName().startsWith(getName());
    }

    public List<ClassCoverage> getClassCoverages() {
        return classCoverages;
    }

    public Stream<ClassCoverage> classCoverages() {
        return classCoverages.stream();
    }

    public Stream<MethodCoverage> methodCoverages() {
        return StreamEx.of(classCoverages()).flatCollection(ClassCoverage::getMethodCoverages);
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
        PackageCoverage that = (PackageCoverage) o;
        return Objects.equal(getName(), that.getName()) && Objects.equal(classCoverages, that.classCoverages);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(classCoverages, getName());
    }
}
