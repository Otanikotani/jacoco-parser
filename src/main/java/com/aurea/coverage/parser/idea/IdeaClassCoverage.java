package com.aurea.coverage.parser.idea;

import com.aurea.coverage.unit.ClassCoverage;
import com.aurea.coverage.unit.MethodContainerUnit;
import com.aurea.coverage.unit.MethodCoverage;

import java.util.Objects;
import java.util.stream.Stream;

public class IdeaClassCoverage extends MethodContainerUnit implements ClassCoverage {

    public static final IdeaClassCoverage EMPTY = new IdeaClassCoverage("", 0, 0, 0);

    private final String name;
    private final int covered;
    private final int uncovered;
    private final int total;

    protected IdeaClassCoverage(String name, int covered, int uncovered, int total) {
        super(name);
        this.name = name;
        this.covered = covered;
        this.uncovered = uncovered;
        this.total = total;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCovered() {
        return covered;
    }

    @Override
    public int getUncovered() {
        return uncovered;
    }

    @Override
    public int getTotal() {
        return total;
    }

    @Override
    public Stream<MethodCoverage> methodCoverages() {
        throw new UnsupportedOperationException("ClassCoverage for IDEA doesn't support Method level coverage. " +
                "Please refer to getCovered(), getUncovered(), getTotal() methods of ClassCoverage for coverage information.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdeaClassCoverage that = (IdeaClassCoverage) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getCovered(), that.getCovered()) &&
                Objects.equals(getUncovered(), that.getUncovered()) &&
                Objects.equals(getTotal(), that.getTotal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCovered(), getUncovered(), getTotal());
    }

    @Override
    public String toString() {
        return "IdeaClassCoverage{" +
                "name='" + name + '\'' +
                ", covered=" + covered +
                ", uncovered=" + uncovered +
                ", total=" + total +
                '}';
    }
}
