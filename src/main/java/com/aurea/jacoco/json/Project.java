package com.aurea.jacoco.json;

import com.aurea.jacoco.ReportStats;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class Project {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd HH:mm:ss.SSSSSS 'Z'")
    private LocalDateTime timestamp = LocalDateTime.now();

    private String name;

    @JsonProperty("module")
    private List<Module> modules = new ArrayList<>();

    public static Project of(String projectName) {
        Project project = new Project();
        project.setName(projectName);
        return project;
    }

    public Project addModule(Module module) {
        modules.add(module);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

}
