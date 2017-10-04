package com.aurea.coverage.unit;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class ModuleCoverage extends MethodContainerUnit {

    public static final ModuleCoverage EMPTY = new ModuleCoverage("", Collections.emptyList());

    private final List<PackageCoverage> packageCoverages;

    public ModuleCoverage(String name, List<PackageCoverage> packageCoverages) {
        super(name);
        this.packageCoverages = packageCoverages;
    }

    public List<PackageCoverage> getPackageCoverages() {
        return packageCoverages;
    }

    public Stream<PackageCoverage> packageCoverages() {
        return packageCoverages.stream();
    }

    public Stream<ClassCoverage> classCoverages() {
        return packageCoverages().flatMap(PackageCoverage::classCoverages);
    }

    @Override
    public Stream<MethodCoverage> methodCoverages() {
        return classCoverages().flatMap(ClassCoverage::methodCoverages);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleCoverage that = (ModuleCoverage) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(packageCoverages, that.packageCoverages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageCoverages, getName());
    }
}
