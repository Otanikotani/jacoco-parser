package com.aurea.jacoco.json;

import com.aurea.jacoco.ClassCoverage;
import com.aurea.jacoco.MethodCoverage;
import com.fasterxml.jackson.annotation.JsonProperty;

class Unit {

    @JsonProperty("class")
    private String className;
    @JsonProperty("package")
    private String packageName;
    @JsonProperty("method")
    private String methodName;
    private int covered;
    private int uncovered;
    private int total;

    public static Unit of(ClassCoverage classCoverage, MethodCoverage methodCoverage) {
        Unit unit = new Unit();
        unit.setClassName(classCoverage.getClassName());
        unit.setPackageName(classCoverage.getPackageName());
        unit.setMethodName(methodCoverage.getName());
        unit.setCovered(methodCoverage.getCovered());
        unit.setUncovered(methodCoverage.getUncovered());
        unit.setTotal(methodCoverage.getUncovered() + methodCoverage.getCovered());
        return unit;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getCovered() {
        return covered;
    }

    public void setCovered(int covered) {
        this.covered = covered;
    }

    public int getUncovered() {
        return uncovered;
    }

    public void setUncovered(int uncovered) {
        this.uncovered = uncovered;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
