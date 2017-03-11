package com.aurea.jacoco;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class PackageCoverageBuilder {
    private String name;
    private List<NewClassCoverage> classCoverages = new ArrayList<>();

    private PackageCoverageBuilder() {
    }

    public static PackageCoverageBuilder aPackageCoverage() {
        return new PackageCoverageBuilder();
    }

    public PackageCoverageBuilder withName(String name) {
        checkNotNull(name);
        this.name = name;
        return this;
    }

    public PackageCoverageBuilder addClassCoverage(NewClassCoverage classCoverage) {
        checkNotNull(classCoverage);
        this.classCoverages.add(classCoverage);
        return this;
    }

    public PackageCoverage build() {
        return new PackageCoverage(name, classCoverages);
    }
}
