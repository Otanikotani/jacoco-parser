package com.aurea.jacoco;

public class ClassCoverage {

    private final String name;
    private final int lines;

    ClassCoverage(String name, int lines) {
        this.name = name;
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public int getLines() {
        return lines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassCoverage that = (ClassCoverage) o;

        if (lines != that.lines) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + lines;
        return result;
    }
}
