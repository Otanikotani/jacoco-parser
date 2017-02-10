package com.aurea.jacoco.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

class Module {

    @JsonProperty("unit")
    private List<Unit> units = new ArrayList<>();

    private String name;

    public static Module of(String moduleName) {
        Module module = new Module();
        module.setName(moduleName);
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
}
