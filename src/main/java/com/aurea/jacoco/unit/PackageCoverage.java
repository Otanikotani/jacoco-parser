package com.aurea.jacoco.unit;


import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class PackageCoverage extends MethodContainerUnit {

    private final List<ClassCoverage> classCoverages;

    public PackageCoverage(String name, List<ClassCoverage> classCoverages) {
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

    @Override
    public Stream<MethodCoverage> methodCoverages() {
        return classCoverages().flatMap(ClassCoverage::methodCoverages);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageCoverage that = (PackageCoverage) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(classCoverages, that.classCoverages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classCoverages, getName());
    }
}
