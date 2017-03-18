[![](https://jitpack.io/v/Otanikotani/jacoco-parser.svg)](https://jitpack.io/#Otanikotani/jacoco-parser) 
[![Coverage Status](https://coveralls.io/repos/github/Otanikotani/jacoco-parser/badge.svg?branch=master)](https://coveralls.io/github/Otanikotani/jacoco-parser?branch=master)
[![Build Status](https://travis-ci.org/Otanikotani/jacoco-parser.svg?branch=master)](https://travis-ci.org/Otanikotani/jacoco-parser)
# Jacoco-parser

Parses [JaCoCo](https://github.com/jacoco/jacoco) coverage reports.

Usage:

```java
JacocoIndex index = JacocoParsers.fromXml(Paths.get("path-to-mars"));
ModuleCoverage moduleCoverage = index.getModuleCoverage();
MethodCoverage methodCoverage = moduleCoverage.methodCoverages()
    .findFirst((mc -> mc.getName().equals("amore")))
    .orElse(MethodCoverage.EMPTY);
```
                         