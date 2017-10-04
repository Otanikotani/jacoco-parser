package com.aurea.coverage.unit;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class EqualsContractTest {

    @Test
    public void equalsContractForMethodCoverage() {
        EqualsVerifier.forClass(MethodCoverage.class).verify();
    }

    @Test
    public void equalsContractForClassCoverage() {
        EqualsVerifier.forClass(ClassCoverageImpl.class).verify();
    }

    @Test
    public void equalsContractForPackageCoverage() {
        EqualsVerifier.forClass(PackageCoverage.class).verify();
    }

    @Test
    public void equalsContractForModuleCoverage() {
        EqualsVerifier.forClass(ModuleCoverage.class).verify();
    }
}
