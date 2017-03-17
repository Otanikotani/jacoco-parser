package com.aurea.jacoco;

import com.aurea.jacoco.unit.ModuleCoverage;

public class JacocoIndex {

    private final ModuleCoverage moduleCoverage;

    public JacocoIndex(ModuleCoverage moduleCoverage) {
        this.moduleCoverage = moduleCoverage;
    }

    public ModuleCoverage getModuleCoverage() {
        return moduleCoverage;
    }
}
