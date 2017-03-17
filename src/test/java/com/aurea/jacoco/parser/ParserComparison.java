package com.aurea.jacoco.parser;

import com.aurea.jacoco.JacocoIndex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

//    # Run complete. Total time: 00:01:20
//
//    Benchmark                     Mode  Cnt     Score   Error  Units
//    ParserComparison.htmlParser  thrpt   20   479.509 ± 2.861  ops/s
//    ParserComparison.xmlParser   thrpt   20  1132.858 ± 4.661  ops/s
public class ParserComparison {

    public static final Path PATH_TO_REPORT = getPathToTestReport();

    private static Path getPathToTestReport() {
        URL jacoco = ParserComparison.class.getResource("../report");
        try {
            return Paths.get(jacoco.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Benchmark
    public JacocoIndex htmlParser() throws URISyntaxException {
        return JacocoParsers.fromHtml(PATH_TO_REPORT);
    }

    @Benchmark
    public JacocoIndex xmlParser() throws URISyntaxException {
        return JacocoParsers.fromXml(PATH_TO_REPORT);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ParserComparison.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
