package eloc.com.aurea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SettersGettersLocFinder {

    private static final int NUMBER_OF_LINES_COLUMN = 8;
    private static final int NUMBER_OF_MISSING_LINES_COLUMN = 7;

    private static final Logger logger = LogManager.getLogger(SettersGettersLocFinder.class.getSimpleName());

    public Map<String, Integer> findUncoveredLinesInSettersGetters(String jacocoDir) throws IOException {
        Path jacocoRoot = Paths.get(jacocoDir);
        Map<String, Integer> result = new HashMap<>();

        Stream<File> fullClassReports = Files.walk(jacocoRoot)
                .map(Path::toFile)
                .filter(f -> !f.isDirectory() && !f.getName().endsWith(".java.html") && f.getName().endsWith(".html"));

        fullClassReports.forEach(reportFile -> {
            String name = reportFile.getParentFile().getName() + "." + reportFile.getName().replace(".html", "");
            try {
                Document doc = null;
                doc = Jsoup.parse(reportFile, "UTF-8");
                Elements methodRows = doc.select("table.coverage > tbody > tr");
                int totalSetterGetterLocs = 0;
                for (Element methodRow : methodRows) {
                    Elements cells = methodRow.select("td");
                    String methodSignature = cells.get(0).child(0).text();
                    if (methodSignature.startsWith("set") && methodSignature.matches(".+\\([^,]+\\)"))  {
                        if (isUncovered(methodRow)) {
                            totalSetterGetterLocs += 2;
                        }
                    } else if (methodSignature.startsWith("get") && methodSignature.endsWith("()")) {
                        if (isUncovered(methodRow)) {
                            totalSetterGetterLocs++;
                        }
                    }
                }
                result.put(name, totalSetterGetterLocs);
            } catch (IOException e) {
                logger.error("Failed on " + name, e);
            }
        });
        return result;
    }

    private boolean isUncovered(Element methodRow) {
        int numberOfLines = Integer.parseInt(methodRow.child(NUMBER_OF_LINES_COLUMN).text().replace(",", ""));
        int numberOfMissingLines = Integer.parseInt(methodRow.child(NUMBER_OF_MISSING_LINES_COLUMN).text().replace(",", ""));
        return numberOfLines == numberOfMissingLines;
    }
}
