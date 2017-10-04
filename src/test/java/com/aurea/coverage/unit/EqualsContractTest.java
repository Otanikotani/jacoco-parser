package com.aurea.coverage.unit;

import com.aurea.coverage.unit.ClassCoverage;
import com.aurea.coverage.unit.MethodCoverage;
import com.aurea.coverage.unit.ModuleCoverage;
import com.aurea.coverage.unit.PackageCoverage;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class EqualsContractTest {

    @Test
    public void equalsContractForMethodCoverage() {
        EqualsVerifier.forClass(MethodCoverage.class).verify();
    }

    @Test
    public void equalsContractForClassCoverage() {
        EqualsVerifier.forClass(ClassCoverage.class).verify();
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
