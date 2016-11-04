package com.aurea.jacoco;

public class ClassCoverage {

    private final String name;
    private final int covered;
    private final int uncovered;

    public ClassCoverage(String name, int covered, int uncovered) {
        this.name = name;
        this.covered = covered;
        this.uncovered = uncovered;
    }

    public String getName() {
        return name;
    }

    public int getCovered() {
        return covered;
    }

    public int getUncovered() {
        return uncovered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassCoverage that = (ClassCoverage) o;

        if (covered != that.covered) return false;
        if (uncovered != that.uncovered) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + covered;
        result = 31 * result + uncovered;
        return result;
    }
}
