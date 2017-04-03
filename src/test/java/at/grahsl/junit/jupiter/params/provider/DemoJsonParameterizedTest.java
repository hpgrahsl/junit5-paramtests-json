package at.grahsl.junit.jupiter.params.provider;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

}
