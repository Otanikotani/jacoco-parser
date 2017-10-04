package com.aurea.coverage.parser.jacoco;

import com.aurea.coverage.CoverageIndex;
import com.aurea.coverage.parser.CoverageParser;
import com.aurea.coverage.parser.CoverageParserException;
import com.aurea.coverage.unit.CoverageUnit;
import com.aurea.coverage.unit.MethodCoverage;
import com.aurea.coverage.unit.ModuleCoverage;
import com.aurea.coverage.unit.PackageCoverage;
import com.aurea.coverage.unit.ClassCoverage;
import org.jacoco.report.JavaNames;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class XmlReportParser implements CoverageParser {

    public static final String JACOCO_XML_FILE_NAME = "jacoco.xml";

    static final String NULL_PATH_ERROR = "Path to Jacoco xml should not be null";
    static final String NULL_INPUT_STREAM_ERROR = "InputStream to Jacoco xml should not be null";
    static final String INVALID_PATH_ERROR = "jacoco.xml has not been found in %s";
    static final String PARSE_ERROR = "Failed to parse %s";
    static final String IO_ERROR = "Failed to close %s";

    private static final QName MISSED = new QName("missed");
    private static final QName NAME = new QName("name");
    private static final QName DESC = new QName("desc");
    private static final QName COVERED = new QName("covered");

    private XMLEventReader xmlEventReader;
    private final JavaNames javaNames;
    private final Supplier<CoverageIndex> buildStrategy;

    public XmlReportParser(Path path) {
        Objects.requireNonNull(path, NULL_PATH_ERROR);
        File jacocoFile = (Files.isDirectory(path) ? path.resolve(JACOCO_XML_FILE_NAME) : path).toFile();
        if (!jacocoFile.exists()) {
            throw new CoverageParserException(INVALID_PATH_ERROR, path);
        }
        buildStrategy = () -> buildFromFile(jacocoFile);
        javaNames = new JavaNames();

    }

    public XmlReportParser(InputStream inputStream) {
        Objects.requireNonNull(inputStream, NULL_INPUT_STREAM_ERROR);
        buildStrategy = () -> buildFromIs(inputStream);
        javaNames = new JavaNames();
    }

    @Override
    public CoverageIndex buildIndex() {
        return buildStrategy.get();
    }

    private CoverageIndex buildFromFile(File jacocoFile) {
        try (FileInputStream fis = new FileInputStream(jacocoFile)) {
            ModuleCoverage moduleCoverage = buildFrom(fis);
            return new CoverageIndex(moduleCoverage);
        } catch (FileNotFoundException e) {
            throw new CoverageParserException(INVALID_PATH_ERROR, e, jacocoFile.toPath());
        } catch (XMLStreamException e) {
            throw new CoverageParserException(PARSE_ERROR, e, jacocoFile.toPath());
        } catch (IOException e) {
            throw new CoverageParserException(IO_ERROR, e, jacocoFile);
        } finally {
            closeReader();
        }
    }

    private CoverageIndex buildFromIs(InputStream is) {
        try {
            ModuleCoverage moduleCoverage = buildFrom(is);
            return new CoverageIndex(moduleCoverage);
        } catch (XMLStreamException e) {
            throw new CoverageParserException(PARSE_ERROR, e, is);
        } finally {
            closeReader();
        }
    }

    private ModuleCoverage buildFrom(InputStream is) throws XMLStreamException {
        ModuleCoverage moduleCoverage = null;
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        xmlEventReader = xmlInputFactory.createXMLEventReader(is);
        while (xmlEventReader.hasNext()) {
            XMLEvent xmlEvent = xmlEventReader.nextEvent();
            if (xmlEvent.isStartElement()) {
                StartElement startElement = xmlEvent.asStartElement();
                if (isStartOf(startElement, "report")) {
                    moduleCoverage = parseModule(startElement);
                }
            }
        }
        return moduleCoverage;
    }

    private ModuleCoverage parseModule(StartElement moduleElement) {
        String name = getName(moduleElement);
        List<PackageCoverage> packageCoverages = parse("module", "package", this::parsePackage);
        return new ModuleCoverage(name, packageCoverages);
    }

    private PackageCoverage parsePackage(StartElement packageElement) {
        String name = getName(packageElement).replace("/", ".");
        List<ClassCoverage> classCoverages = parse("package", "class", this::parseClass);
        return new PackageCoverage(name, classCoverages);
    }

    private ClassCoverage parseClass(StartElement classElement) {
        String className = getName(classElement);
        String name = className.substring(className.lastIndexOf('/') + 1);
        List<MethodCoverage> methodCoverages = parse("class", "method", (element) -> parseMethod(element, className));
        return new ClassCoverage(name, methodCoverages);
    }

    private MethodCoverage parseMethod(StartElement methodElement, String className) {
        String name = getName(methodElement).replace("&lt;", "<").replace("&gt;", ">");
        String desc = methodElement.getAttributeByName(DESC).getValue();
        String methodName = javaNames.getMethodName(className, name, desc, null);
        int missedInstructions = 0;
        int coveredInstructions = 0;
        int missedLocs = 0;
        int coveredLocs = 0;
        int counterIndex = 0;
        while (xmlEventReader.hasNext()) {
            try {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (isStartOf(startElement, "counter")) {
                        if (counterIndex == 0) {
                            missedInstructions = Integer.parseInt(startElement.getAttributeByName(MISSED).getValue());
                            coveredInstructions = Integer.parseInt(startElement.getAttributeByName(COVERED).getValue());
                        } else if (counterIndex == 1) {
                            missedLocs = Integer.parseInt(startElement.getAttributeByName(MISSED).getValue());
                            coveredLocs = Integer.parseInt(startElement.getAttributeByName(COVERED).getValue());
                        }
                        counterIndex++;
                    }
                }
                if (isEndOf(xmlEvent, "method")) {
                    break;
                }
            } catch (XMLStreamException e) {
                throw new CoverageParserException(PARSE_ERROR, e, "counter of " + getName(methodElement));
            }
        }
        return new MethodCoverage(methodName, coveredInstructions, missedInstructions, coveredLocs, missedLocs);
    }

    private <T extends CoverageUnit> List<T> parse(String unitType, String subUnitType, Function<StartElement, T> subUnitParser) {
        List<T> subUnits = new ArrayList<>();
        while (xmlEventReader.hasNext()) {
            try {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals(subUnitType)) {
                        subUnits.add(subUnitParser.apply(startElement));
                    }
                }
                if (isEndOf(xmlEvent, unitType)) {
                    break;
                }
            } catch (XMLStreamException e) {
                throw new CoverageParserException(PARSE_ERROR, e, unitType);
            }
        }
        return subUnits;
    }

    private String getName(StartElement element) {
        return element.getAttributeByName(NAME).getValue();
    }

    private boolean isStartOf(StartElement element, String name) {
        return element.getName().getLocalPart().equals(name);
    }

    private boolean isEndOf(XMLEvent event, String name) {
        if (event.isEndElement()) {
            EndElement endElement = event.asEndElement();
            return endElement.getName().getLocalPart().equals(name);
        }
        return false;
    }

    private void closeReader() {
        if (null != xmlEventReader) {
            try {
                xmlEventReader.close();
            } catch (XMLStreamException e) {
                throw new CoverageParserException("Failed to close XMLStreamReader", e);
            }
        }
    }
}
