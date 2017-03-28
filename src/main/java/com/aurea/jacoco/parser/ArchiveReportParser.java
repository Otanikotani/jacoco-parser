package com.aurea.jacoco.parser;

import com.aurea.jacoco.JacocoIndex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ArchiveReportParser implements JacocoParser {

    private final InputStream is;

    public ArchiveReportParser(InputStream is) {
        this.is = is;
    }

    @Override
    public JacocoIndex buildIndex() {
        try (ZipInputStream zipInputStream = new ZipInputStream(is)) {
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    if (entry.getName().endsWith(".xml")) {
                        InputStream extracted = extractFileFromArchive(zipInputStream, entry.getName());
                        return new XmlReportParser(extracted).buildIndex();
                    }
                }
            }
            throw new JacocoParserException("Jacoco xml file is not found in the given archive");
        } catch (IOException e) {
            throw new JacocoParserException("Failed reading archive", e);
        }
    }

    private InputStream extractFileFromArchive(ZipInputStream stream, String outputName) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            int len;
            while ((len = stream.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException e) {
            throw new JacocoParserException("Failed to inflate archive for " + outputName, e);
        }
    }
}
