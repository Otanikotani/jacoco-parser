package com.aurea.jacoco.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class Project {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd HH:mm:ss.SSSSSS 'Z'")
    private LocalDateTime created = LocalDateTime.now();

    private String name;

    @JsonProperty("module")
    private List<Module> modules = new ArrayList<>();

    public static Project of(String name) {
        Project project = new Project();
        project.setName(name);
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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }
}
