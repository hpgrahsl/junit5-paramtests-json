package at.grahsl.junit.jupiter.params.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.util.Arrays;
import java.util.stream.Stream;

public class JsonMultiTypeArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<JsonMultiTypeSource> {

    private String[] records;
    private Class clazz;
    private ArrayType type;

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void accept(JsonMultiTypeSource source) {
        records = source.records();
        clazz = source.clazz();
        type = OBJECT_MAPPER.getTypeFactory().constructArrayType(clazz);
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Arrays.stream(records)
                .map(this::parseJsonObjectArray)
                .map(Arguments::of);
    }

    private Object[] parseJsonObjectArray(String json) {
        try {
            return (Object[])OBJECT_MAPPER.readValue(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
