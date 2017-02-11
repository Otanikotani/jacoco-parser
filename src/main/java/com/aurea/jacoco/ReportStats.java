package com.aurea.jacoco;

public class ReportStats {
    private final int linesInModule;
    private final int missingLinesInModule;
    private final String moduleName;

    public ReportStats(String moduleName, int linesInModule, int missingLinesInModule) {
        this.linesInModule = linesInModule;
        this.missingLinesInModule = missingLinesInModule;
        this.moduleName = moduleName;
    }

    public int getLinesInModule() {
        return linesInModule;
    }

    public int getMissingLinesInModule() {
        return missingLinesInModule;
    }

    public String getModuleName() {
        return moduleName;
    }
}
