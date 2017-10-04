package com.aurea.coverage;

import com.aurea.coverage.unit.ModuleCoverage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CoverageIndex {

    private final List<ModuleCoverage> moduleCoverages;

    public CoverageIndex(ModuleCoverage moduleCoverage) {
        moduleCoverages = new ArrayList<>();
        moduleCoverages.add(moduleCoverage);
    }

    public CoverageIndex(List<ModuleCoverage> moduleCoverages) {
        this.moduleCoverages = moduleCoverages;
    }

    public List<ModuleCoverage> getModuleCoverages() {
        return moduleCoverages;
    }

    public Stream<ModuleCoverage> moduleCoverages() {
        return getModuleCoverages().stream();
    }

    public CoverageIndex addModuleCoverage(ModuleCoverage moduleCoverage) {
        getModuleCoverages().add(moduleCoverage);
        return this;
    }

    public ModuleCoverage getModuleCoverage() {
        return moduleCoverages.get(0);
    }
}
