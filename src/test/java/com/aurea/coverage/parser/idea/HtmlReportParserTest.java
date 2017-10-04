package com.aurea.coverage.parser.idea;

import com.aurea.coverage.CoverageIndex;
import com.aurea.coverage.SampleFolder;
import com.aurea.coverage.parser.CoverageParserException;
import com.aurea.coverage.parser.IdeaParsers;
import com.aurea.coverage.unit.ClassCoverage;
import com.aurea.coverage.unit.Named;
import com.aurea.coverage.unit.PackageCoverage;
import org.hamcrest.CoreMatchers;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class HtmlReportParserTest {

    @ClassRule
    public static final SampleFolder SAMPLES = new SampleFolder("idea");

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void onEmptyFolderShouldFail() throws Exception {
        expectedException.expect(CoverageParserException.class);
        expectedException.expectMessage(CoreMatchers.containsString("empty-folder"));

        IdeaParsers.fromHtml(SAMPLES.resolve("empty-folder"));
    }

    @Test
    public void onNullShouldFail() throws Exception {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(HtmlReportParser.NULL_PATH_ERROR);

        IdeaParsers.fromHtml(null);
    }

    @Test
    public void shouldBuildIndexFromGivenDirectory() throws Exception {
        CoverageIndex index = IdeaParsers.fromHtml(SAMPLES.resolve("sample"));

        assertPackages(index.getModuleCoverage().packageCoverages());
        assertClasses(index.getModuleCoverage().classCoverages());
    }

    private void assertPackages(Stream<PackageCoverage> packageCoverages) {
        assertThat(packageCoverages.collect(Collectors.toList()))
                .usingElementComparator(Comparator.comparing(Named::getName))
                .containsOnly(
                        new PackageCoverage("com.aurea.coverage.parser", emptyList()),
                        new PackageCoverage("com.aurea.coverage.parser.idea", emptyList()),
                        new PackageCoverage("com.aurea.coverage.parser.jacoco", emptyList()));
    }

    private void assertClasses(Stream<ClassCoverage> classCoverages) {
        assertThat(classCoverages.collect(Collectors.toList()))
                .containsOnly(
                        new IdeaClassCoverage("CoverageParserException", 4, 0, 4),
                        new IdeaClassCoverage("IdeaParsers", 0, 3, 3),
                        new IdeaClassCoverage("JacocoParsers", 3, 2, 5),
                        new IdeaClassCoverage("ArchiveHtmlReportParser", 0, 2, 2),
                        new IdeaClassCoverage("HtmlReportParser", 0, 2, 2),
                        new IdeaClassCoverage("ArchiveReportParser", 16, 3, 19),
                        new IdeaClassCoverage("XmlReportParser", 99, 6, 105));
    }
}
