package at.grahsl.junit.jupiter.params.provider;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class DemoJsonParameterizedTest {

    public static class DemoDtoA {

        public String myString;
        public List<Integer> myNumbers;
        public boolean myBoolean;

        public DemoDtoA() {} //for jackson

        public DemoDtoA(String myString, List<Integer> myNumbers, boolean myBoolean) {
            this.myString = myString;
            this.myNumbers = myNumbers;
            this.myBoolean = myBoolean;
        }

        @Override
        public String toString() {
            return "DemoDtoA{" +
                    "myString='" + myString + '\'' +
                    ", myNumbers=" + myNumbers +
                    ", myBoolean=" + myBoolean +
                    '}';
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DemoDtoB.class, name = "DemoDtoB"),
            @JsonSubTypes.Type(value = DemoDtoC.class, name = "DemoDtoC")
    })
    public interface TypeWrapper {}

    @JsonRootName("DemoDtoB")
    public static class DemoDtoB implements TypeWrapper {

        public double myNumber;
        public String someText;

        public DemoDtoB() {}

        @Override
        public String toString() {
            return "DemoDtoB{" +
                    "myNumber=" + myNumber +
                    ", someText='" + someText + '\'' +
                    '}';
        }
    }

    @JsonRootName("DemoDtoC")
    public static class DemoDtoC implements TypeWrapper {

        public boolean myBoolean;
        public int myNumber;

        public DemoDtoC() {}

        @Override
        public String toString() {
            return "DemoDtoC{" +
                    "myBoolean=" + myBoolean +
                    ", myNumber=" + myNumber +
                    '}';
        }
    }

    @ParameterizedTest
    @JsonSource(records = {
                "{\"myString\":\"junit5 rocks!\",\"myNumbers\":[1,2,3,4],\"myBoolean\":true}",
                "{\"myString\":\"Json Param Source\",\"myNumbers\":[9,8,7,6],\"myBoolean\":true}"},
                clazz = DemoDtoA.class)
    void testWithJsonSourceParamMapping(DemoDtoA obj) {

        assertAll("obj param checks",
                () -> assertNotNull(obj),
                () -> assertNotNull(obj.myString),
                () -> assertFalse(obj.myString.isEmpty()),
                () -> assertTrue(obj.myBoolean)
        );

    }

    @ParameterizedTest
    @JsonFileSource(resources = {"/demo.json"}, clazz = DemoDtoA.class)
    void testWithJsonFileSourceParamMapping(DemoDtoA obj) {

        assertAll("obj param checks",
                () -> assertNotNull(obj),
                () -> assertNotNull(obj.myString),
                () -> assertFalse(obj.myString.isEmpty()),
                () -> assertTrue(obj.myBoolean)
        );

    }

    @ParameterizedTest
    @JsonMultiTypeSource(records = {
            "[{\"DemoDtoB\": {\"myNumber\":12.23,\"someText\":\"foo FOO\"}}," +
                    "{\"DemoDtoC\": {\"myBoolean\":true,\"myNumber\":-123}}]",
            "[{\"DemoDtoB\": {\"myNumber\":43.54,\"someText\":\"foo FOO\"}}," +
                    "{\"DemoDtoC\": {\"myBoolean\":true,\"myNumber\":-987}}]"
    },
            clazz = TypeWrapper.class
    )
    void testWithJsonMultiTypeSourceParamMapping(DemoDtoB obj1, DemoDtoC obj2) {

        assertAll("obj param checks",
                () -> assertNotNull(obj1),
                () -> assertTrue(obj1.myNumber >= 0),
                () -> assertFalse(obj1.someText.isEmpty()),
                () -> assertNotNull(obj2),
                () -> assertEquals(true,obj2.myBoolean),
                () -> assertTrue(obj2.myNumber <= 0)
        );

    }

}
