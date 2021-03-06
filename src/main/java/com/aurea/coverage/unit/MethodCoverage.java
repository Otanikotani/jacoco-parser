package com.aurea.coverage.unit;

import java.util.Objects;

public final class MethodCoverage extends NamedImpl implements CoverageUnit {

    public static final MethodCoverage EMPTY = new MethodCoverage("", 0, 0, 0, 0);

    private final int instructionCovered;
    private final int instructionUncovered;
    private final int covered;
    private final int uncovered;

    public MethodCoverage(String name, int instructionCovered, int instructionUncovered, int covered, int uncovered) {
        super(name);
        this.instructionCovered = instructionCovered;
        this.instructionUncovered = instructionUncovered;
        this.covered = covered;
        this.uncovered = uncovered;
    }

    public int getInstructionCovered() {
        return instructionCovered;
    }

    public int getInstructionUncovered() {
        return instructionUncovered;
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
        return covered + uncovered;
    }

    public int getInstructionsTotal() {
        return instructionCovered + instructionUncovered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodCoverage that = (MethodCoverage) o;
        return Objects.equals(getName(), that.getName()) &&
                instructionCovered == that.instructionCovered &&
                instructionUncovered == that.instructionUncovered &&
                covered == that.covered &&
                uncovered == that.uncovered;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instructionCovered, instructionUncovered, covered, uncovered, getName());
    }

    @Override
    public String toString() {
        return "MethodCoverage{" +
                "name=" + getName() +
                ", instructionCovered=" + instructionCovered +
                ", instructionUncovered=" + instructionUncovered +
                ", covered=" + covered +
                ", uncovered=" + uncovered +
                '}';
    }
}
