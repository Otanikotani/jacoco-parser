package com.aurea.jacoco.json;

import com.aurea.jacoco.ReportStats;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

class Module {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "unit")
    private List<Unit> units = new ArrayList<>();

    private String name;
    private int total;
    private int uncovered;

    public static Module of(ReportStats reportStats) {
        Module module = new Module();
        module.setName(reportStats.getModuleName());
        module.setTotal(reportStats.getLinesInModule());
        module.setUncovered(reportStats.getMissingLinesInModule());
        return module;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getUncovered() {
        return uncovered;
    }

    public void setUncovered(int uncovered) {
        this.uncovered = uncovered;
    }
}
