package com.aurea.coverage.unit;

import java.util.stream.Stream;

public abstract class MethodContainerUnit extends NamedImpl implements CoverageUnit {

    protected MethodContainerUnit(String name) {
        super(name);
    }

    public abstract Stream<MethodCoverage> methodCoverages();

    @Override
    public int getCovered() {
        return methodCoverages().mapToInt(MethodCoverage::getCovered).sum();
    }

    @Override
    public int getUncovered() {
        return methodCoverages().mapToInt(MethodCoverage::getUncovered).sum();
    }

    @Override
    public int getTotal() {
        return methodCoverages().mapToInt(MethodCoverage::getTotal).sum();
    }
}
