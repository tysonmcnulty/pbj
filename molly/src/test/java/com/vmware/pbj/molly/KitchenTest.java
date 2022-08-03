package com.vmware.pbj.molly;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class KitchenTest {

    @Test
    void writer_writes_all_java_files_for_kitchen_terms() throws IOException {
        Path tmpdir = Files.createTempDirectory("KitchenTest-");
        MollyJavaGenerator generator = new MollyJavaGenerator();

        generator.read(TestUtils.resource("Kitchen.molly"));
        generator.write(tmpdir);

        Resource[] resources = TestUtils.resourcesMatching("classpath:/KitchenTest/*.java");

        for (Resource r : resources) {
            String expectedFileText = new BufferedReader(new InputStreamReader(r.getInputStream(), UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            String actualFileText = Files.readString(Paths.get(
                    tmpdir.toAbsolutePath().toString(),
                    "com", "vmware", "example",
                    r.getFilename()
            ));

            assertEquals(expectedFileText.trim(), actualFileText.trim());
        }
    }
}
