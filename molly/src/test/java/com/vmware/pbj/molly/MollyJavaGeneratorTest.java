package com.vmware.pbj.molly;

import com.vmware.pbj.molly.core.Language;
import com.vmware.pbj.molly.write.MollyJavaGenerator;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MollyJavaGeneratorTest {

    @DisplayName("writer writes all java files for language")
    @ParameterizedTest(name = "{0}.molly ==> {0}Test/*.java")
    @ValueSource(strings = {"PBJ", "Blackjack", "Molly"})
    public void writer_writes_all_java_files_for_kitchen(String languageName) throws IOException {
        Language language = TestLanguages.get(languageName);

        Path tmpdir = Files.createTempDirectory(languageName + "Test-");
        MollyJavaGenerator generator = new MollyJavaGenerator(language);
        generator.write(tmpdir);

        var expectedFileResources = TestUtils.resourcesMatching(String.format("classpath:/%sTest/*.java", languageName));
        var actualFiles = Paths.get(tmpdir.toAbsolutePath().toString(), "io", "github", "tysonmcnulty").toFile().listFiles();
        assertNotNull(actualFiles);

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
