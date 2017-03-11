package com.aurea.jacoco;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ModuleCoverageBuilder {

    private String name;
    private List<PackageCoverage> packageCoverages = new ArrayList<>();

    private ModuleCoverageBuilder() {
    }

    public static ModuleCoverageBuilder aModuleCoverage() {
        return new ModuleCoverageBuilder();
    }

    public ModuleCoverageBuilder withName(String name) {
        checkNotNull(name);
        this.name = name;
        return this;
    }

    public ModuleCoverageBuilder addPackageCoverage(PackageCoverage packageCoverage) {
        checkNotNull(packageCoverage);
        packageCoverages.add(packageCoverage);
        return this;
    }

    public ModuleCoverage build() {
        checkNotNull(name, "Name must be set!");
        return new ModuleCoverage(name, packageCoverages);
    }
}
