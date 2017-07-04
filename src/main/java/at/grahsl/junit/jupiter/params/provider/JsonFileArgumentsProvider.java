package at.grahsl.junit.jupiter.params.provider;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.util.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

public class JsonFileArgumentsProvider implements
                ArgumentsProvider, AnnotationConsumer<JsonFileSource> {

    private static class JsonParserIterator implements Iterator<Arguments> {

        private ObjectMapper objectMapper = new ObjectMapper();
        private MappingIterator<?> iterator;

        public JsonParserIterator(Class clazz,InputStream source) throws IOException {
            iterator = objectMapper.readerFor(clazz).readValues(source);
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Arguments next() {
            return Arguments.of(iterator.next());
        }
    }

    private final BiFunction<Class<?>, String, InputStream> inputStreamProvider;

    private String[] resources;
    private Class clazz;

    public JsonFileArgumentsProvider() {
        this(Class::getResourceAsStream);
    }

    public JsonFileArgumentsProvider(BiFunction<Class<?>, String, InputStream> inputStreamProvider) {
        this.inputStreamProvider = inputStreamProvider;
    }

    @Override
    public void accept(JsonFileSource annotation) {
        resources = annotation.resources();
        clazz = annotation.clazz();
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Arrays.stream(resources)
                .map(resource -> openInputStream(context, resource))
                .map(this::getJsonParserIterator)
                .flatMap(this::toStream);
    }

    private InputStream openInputStream(ExtensionContext context, String resource) {
        Class<?> testClass = context.getTestClass().orElseThrow(
                () -> new JUnitException("Cannot load classpath resource without test class"));
        return Preconditions.notNull(inputStreamProvider.apply(testClass, resource),
                () -> "Classpath resource does not exist: " + resource);
    }

    private JsonParserIterator getJsonParserIterator(InputStream source) {
        try {
            return new JsonParserIterator(clazz,source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Stream<Arguments> toStream(JsonParserIterator iterator) {
        return stream(spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }

}
