package com.aurea.jacoco;

public class ClassCoverage {

    private final String packageName, className;
    private final int covered;
    private final int uncovered;

    public ClassCoverage(String packageName, String className, int covered, int uncovered) {
        this.packageName = packageName;
        this.className = className;
        this.covered = covered;
        this.uncovered = uncovered;
    }

    public String getName() {
        return packageName + "." + className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
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
        if (packageName != null ? !packageName.equals(that.packageName) : that.packageName != null) return false;
        if (className != null ? !className.equals(that.className) : that.className != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = packageName != null ? packageName.hashCode() : 0;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + covered;
        result = 31 * result + uncovered;
        return result;
    }
}
