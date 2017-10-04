package com.aurea.coverage.unit;

import java.util.stream.Stream;

public interface ClassCoverage extends Named {
    Stream<MethodCoverage> methodCoverages();
}
