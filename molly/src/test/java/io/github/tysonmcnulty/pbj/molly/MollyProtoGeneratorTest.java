package io.github.tysonmcnulty.pbj.molly;

import io.github.tysonmcnulty.pbj.molly.core.Language;
import io.github.tysonmcnulty.pbj.molly.write.proto.MollyProtoGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MollyProtoGeneratorTest {
    @DisplayName("generator creates proto for language")
    @ParameterizedTest(name = "{0}.molly ==> proto/{0}.json")
    @ValueSource(strings = {"PBJ", "Shelter"})
    public void generator_creates_proto_for_language(String languageName) throws IOException {
        Language language = TestLanguages.get(languageName);

        MollyProtoGenerator generator = new MollyProtoGenerator(language);

        var builder = generator.createFileDescriptorSetBuilder();
        var jsonPrinter = com.google.protobuf.util.JsonFormat.printer().sortingMapKeys();

        var actualJson = jsonPrinter.print(builder);

        var expectedJsonResource = TestUtils.resourcesMatching(String.format("classpath:/proto/%s.json", languageName))[0];
        String expectedJson = new BufferedReader(new InputStreamReader(expectedJsonResource.getInputStream(), UTF_8))
                .lines().collect(joining("\n"));

        assertEquals(expectedJson.trim(), actualJson.trim());
    }
}
