package com.aurea.coverage;

import org.junit.rules.ExternalResource;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleFolder extends ExternalResource {

    private final Path root;

    public SampleFolder(Path resourceFolder) {
        URL url = SampleFolder.class.getResource(resourceFolder.toString());
        try {
            root = Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to find ../jacoco folder");
        }
    }

    public SampleFolder(String resourceFolder) {
        this(Paths.get(resourceFolder));
    }

    public Path resolve(Path sampleName) {
        return root.resolve(sampleName);
    }

    public Path resolve(String sampleName) {
        return root.resolve(sampleName);
    }
}
