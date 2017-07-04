package at.grahsl.junit.jupiter.params.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

public class JsonArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<JsonSource> {

    private String[] records;
    private Class clazz;

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void accept(JsonSource source) {
        records = source.records();
        clazz = source.clazz();
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Arrays.stream(records)
                    .map(this::parseJsonString)
                    .map(Arguments::of);
    }

    private Object parseJsonString(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
