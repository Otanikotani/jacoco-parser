[![](https://jitpack.io/v/Otanikotani/jacoco-parser.svg)](https://jitpack.io/#Otanikotani/jacoco-parser) 
[![Coverage Status](https://coveralls.io/repos/github/Otanikotani/jacoco-parser/badge.svg?branch=master)](https://coveralls.io/github/Otanikotani/jacoco-parser?branch=master)
[![Build Status](https://travis-ci.org/Otanikotani/jacoco-parser.svg?branch=master)](https://travis-ci.org/Otanikotani/jacoco-parser)
# Coverage parser


# Jacoco Parser
Parses [JaCoCo](https://github.com/jacoco/jacoco) coverage reports.

Usage:

```java
JacocoIndex index = JacocoParsers.fromXml(Paths.get("path-to-mars"));
ModuleCoverage moduleCoverage = index.getModuleCoverage();
MethodCoverage methodCoverage = moduleCoverage.methodCoverages()
    .findFirst(mc -> mc.getName().equals("amore"))
    .orElse(MethodCoverage.EMPTY);
```
                         
# Intellij IDEA Parser

Parses coverage reports from Intellij IDEA.

Please note that method level coverage is not supported in Intellij reports. 

Usage:

```java
CoverageIndex index = IdeaParsers.fromHtml(Paths.get("path-to-directory-with-html-report"));
ModuleCoverage moduleCoverage = index.getModuleCoverage();
ClassCoverage coverage = moduleCoverage.classCoverages()
    .findFirst(cc -> cc.getName.equals("Foo"))
    .orElse(IdeaClassCoverage.EMPTY);
```                         
                         
