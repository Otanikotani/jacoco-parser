package com.aurea.jacoco.parser;

import com.aurea.jacoco.JacocoIndex;
import com.aurea.jacoco.ReportStats;
import com.aurea.jacoco.unit.ClassCoverage;
import com.aurea.jacoco.unit.CoverageUnit;
import com.aurea.jacoco.unit.MethodCoverage;
import com.aurea.jacoco.unit.ModuleCoverage;
import com.aurea.jacoco.unit.PackageCoverage;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class XmlReportParser implements JacocoParser {

    public static final String JACOCO_XML_FILE_NAME = "jacoco.xml";

    private static final QName MISSED = new QName("missed");
    private static final QName NAME = new QName("name");
    private static final QName COVERED = new QName("covered");

    private final Path pathToXml;
    private XMLEventReader xmlEventReader;

    public XmlReportParser(Path pathToXml) {
        this.pathToXml = pathToXml.endsWith(JACOCO_XML_FILE_NAME) ?
                pathToXml :
                pathToXml.resolve(JACOCO_XML_FILE_NAME);
    }

    @Override
    public JacocoIndex buildIndex() {
        ModuleCoverage moduleCoverage = null;
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(pathToXml.toFile()));
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (isStartOf(startElement, "report")) {
                        moduleCoverage = parseModule(startElement);
                    }
                }
                if (isEndOf(xmlEvent, "report")) {
                    break;
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            throw new JacocoParserException("Failed to parse " + pathToXml, e);
        }
        return new JacocoIndex(moduleCoverage, new ReportStats("", 0, 0));
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
        String name = getSimpleName(classElement);
        List<MethodCoverage> methodCoverages = parse("class", "method", this::parseMethod);
        return new ClassCoverage(name, methodCoverages);
    }

    private MethodCoverage parseMethod(StartElement methodElement) {
        String name = getName(methodElement);
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
                throw new JacocoParserException("Failed to parse method " + getName(methodElement), e);
            }
        }
        return new MethodCoverage(name, coveredInstructions, missedInstructions, coveredLocs, missedLocs);
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
                throw new JacocoParserException("Failed to parse " + unitType + " -> " + subUnitType, e);
            }
        }
        return subUnits;
    }

    private String getName(StartElement element) {
        return element.getAttributeByName(NAME).getValue();
    }

    private String getSimpleName(StartElement element) {
        String name = getName(element);
        return name.substring(name.lastIndexOf('/') + 1);
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
}
