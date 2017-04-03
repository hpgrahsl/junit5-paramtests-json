package at.grahsl.junit.jupiter.params.provider;

import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.platform.commons.meta.API;

import java.lang.annotation.*;

import static org.junit.platform.commons.meta.API.Usage.Experimental;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(Experimental)
@ArgumentsSource(JsonFileArgumentsProvider.class)
public @interface JsonFileSource {

    String[] resources();

    Class clazz();

}
