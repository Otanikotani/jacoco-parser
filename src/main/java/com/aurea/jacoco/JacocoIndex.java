package com.aurea.jacoco;

import com.aurea.jacoco.unit.ModuleCoverage;

public class JacocoIndex {

    private final ModuleCoverage moduleCoverage;
    private final ReportStats reportStats;

    public JacocoIndex(ModuleCoverage moduleCoverage, ReportStats reportStats) {
        this.moduleCoverage = moduleCoverage;
        this.reportStats = reportStats;
    }

    public ModuleCoverage getModuleCoverage() {
        return moduleCoverage;
    }

    public ReportStats getReportStats() {
        return reportStats;
    }
}
