package com.aurea.jacoco;

import com.google.common.base.Objects;

class NewMethodCoverage extends Named {

    private final int instructionCovered;
    private final int instructionUncovered;
    private final int covered;
    private final int uncovered;

    protected NewMethodCoverage(String name, int instructionCovered, int instructionUncovered, int covered, int uncovered) {
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

    public int getCovered() {
        return covered;
    }

    public int getUncovered() {
        return uncovered;
    }

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
        NewMethodCoverage that = (NewMethodCoverage) o;
        return
                getName().equals(that.getName()) &&
                instructionCovered == that.instructionCovered &&
                instructionUncovered == that.instructionUncovered &&
                covered == that.covered &&
                uncovered == that.uncovered;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(instructionCovered, instructionUncovered, covered, uncovered, getName());
    }
}
