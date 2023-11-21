package io.github.tysonmcnulty.pbj.molly.write.proto;

import com.google.protobuf.DescriptorProtos;
import io.github.tysonmcnulty.pbj.molly.core.Language;

import java.util.Map;
import java.util.stream.Collectors;

public class MollyProtoGenerationContext {

    private final Map<String, DescriptorProtos.DescriptorProto.Builder> buildersByUnitName;
    private final Language language;
    private final MollyProtoGeneratorConfig config;

    private final Map<String, Integer> fieldNumbersByUnitName;

    public MollyProtoGenerationContext(
            Map<String, DescriptorProtos.DescriptorProto.Builder> buildersByUnitName,
            Language language,
            MollyProtoGeneratorConfig config
    ) {
        this.buildersByUnitName = buildersByUnitName;
        this.language = language;
        this.config = config;

        var categorizer = language.getCategorizer();

        this.fieldNumbersByUnitName = buildersByUnitName.keySet().stream()
                .map(unitName -> Map.entry(unitName, (categorizer.apply(unitName).size() - 1) * 100 + 1))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public int yieldFieldNumber(String unitName) {
        int fieldNumber = fieldNumbersByUnitName.get(unitName);
        fieldNumbersByUnitName.put(unitName, fieldNumber + 1);
        return fieldNumber;
    }

    public Map<String, DescriptorProtos.DescriptorProto.Builder> getBuildersByUnitName() {
        return buildersByUnitName;
    }

    public Language getLanguage() {
        return language;
    }

    public MollyProtoGeneratorConfig getConfig() {
        return config;
    }
}
