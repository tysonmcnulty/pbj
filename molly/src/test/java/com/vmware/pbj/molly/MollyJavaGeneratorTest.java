package com.vmware.pbj.molly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MollyJavaGeneratorTest {

    @DisplayName("writer writes all java files for language")
    @ParameterizedTest(name = "{0}.molly ==> {0}Test/*.java")
    @ValueSource(strings = {"Kitchen", "Blackjack", "Molly"})
    public void writer_writes_all_java_files_for_language(String language) throws IOException {
        Path tmpdir = Files.createTempDirectory(language + "Test-");
        MollyJavaGenerator generator = new MollyJavaGenerator();

        generator.read(TestUtils.resource(language + ".molly"));
        generator.write(tmpdir);

        var expectedFileResources = TestUtils.resourcesMatching(String.format("classpath:/%sTest/*.java", language));
        var actualFiles = Paths.get(tmpdir.toAbsolutePath().toString(), "io", "github", "tysonmcnulty").toFile().listFiles();

        var expectedFilenames = Arrays.stream(expectedFileResources).map(Resource::getFilename).collect(toSet());
        var actualFilenames = Arrays.stream(actualFiles).map(File::getName).collect(toSet());

        assertEquals(expectedFilenames, actualFilenames);

        for (var resource : expectedFileResources) {
            String expectedFileText = new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))
                    .lines().collect(joining("\n"));

            String actualFileText = Files.readString(Paths.get(
                    tmpdir.toAbsolutePath().toString(),
                    "io", "github", "tysonmcnulty",
                    resource.getFilename()
            ));

            assertEquals(expectedFileText.trim(), actualFileText.trim());
        }
    }
}
