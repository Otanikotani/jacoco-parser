package com.aurea.jacoco.json;

import com.aurea.jacoco.ClassCoverage;
import com.aurea.jacoco.JacocoReport;
import com.aurea.jacoco.MethodCoverage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import one.util.streamex.EntryStream;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class JacocoToJsonConverter {

    private final SerializationFeature[] features;

    private JacocoToJsonConverter(SerializationFeature[] features) {
        this.features = features;
    }

    public static JacocoToJsonConverter of(SerializationFeature... features) {
        return new JacocoToJsonConverter(features);
    }

    public String toJson(JacocoReport report, String projectName) throws JsonProcessingException {
        final Project project = Project.of(projectName);
        final String moduleName = report.getModuleName();
        Module module = Module.of(moduleName);
        project.addModule(module);
        Map<ClassCoverage, Set<MethodCoverage>> coveredMethods = report.findCoveredMethods();
        List<Unit> units = EntryStream.of(coveredMethods).flatMapValues(Set::stream).mapKeyValue(Unit::of).toList();
        module.setUnits(units);
        ObjectMapper mapper = newMapper();
        return mapper.writeValueAsString(project);
    }

    private ObjectMapper newMapper() {
        ObjectMapper mapper = new ObjectMapper();
        for (SerializationFeature feature : features) {
            mapper.enable(feature);
        }
        return mapper;
    }
}
