package com.aurea.jacoco;

import com.aurea.jacoco.unit.ModuleCoverage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JacocoIndex {

    private final List<ModuleCoverage> moduleCoverages;

    public JacocoIndex(ModuleCoverage moduleCoverage) {
        moduleCoverages = new ArrayList<>();
        moduleCoverages.add(moduleCoverage);
    }

    public JacocoIndex(List<ModuleCoverage> moduleCoverages) {
        this.moduleCoverages = moduleCoverages;
    }

    public List<ModuleCoverage> getModuleCoverages() {
        return moduleCoverages;
    }

    public Stream<ModuleCoverage> moduleCoverages() {
        return getModuleCoverages().stream();
    }

    public JacocoIndex addModuleCoverage(ModuleCoverage moduleCoverage) {
        getModuleCoverages().add(moduleCoverage);
        return this;
    }

    public ModuleCoverage getModuleCoverage() {
        return moduleCoverages.get(0);
    }
}
