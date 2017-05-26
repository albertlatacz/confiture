package com.github.confiture;

import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;

import static com.github.confiture.Config.constructors.load;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class ConfigTest {
    Config config = load(new File(getClass().getClassLoader().getResource("config.json").getFile()));

    @Test
    public void supportsGettingSingleValue() {
        assertThat(config.stringValue("types.string"), is("some string"));
        assertThat(config.integerValue("types.integer"), is(654));
        assertThat(config.doubleValue("types.double"), is(123.45));
        assertThat(config.booleanValue("types.boolean"), is(true));

        Map<String, Object> http = config.mapValue("types.map");
        assertThat(http, hasEntry("val1", BigDecimal.valueOf(12345)));
        assertThat(http, hasEntry("val2", "some val"));

        assertThat(config.asConfig("types.map").integerValue("val1"), is(12345));
    }

    @Test
    public void supportsGettingListOfValues() {
        assertThat(config.stringValues("types.stringValues"), hasItems("val1", "val2"));
        assertThat(config.integerValues("types.integerValues"), hasItems(1, 2));
        assertThat(config.doubleValues("types.doubleValues"), hasItems(1.2, 3.4));
        assertThat(config.booleanValues("types.booleanValues"), hasItems(true, false));
        assertThat(config.mapValues("types.mapValues"), hasItems(
                hasEntry("val1", "map 1 val 1"),
                hasEntry("val1", "map 2 val 1")));
        assertThat(config.asConfigs("types.mapValues").get(0).stringValue("val1"), is("map 1 val 1"));
    }

    @Test
    public void supportsGettingNestedValueWithPath() {
        assertThat(config.value("nested1.nested2[1].nested3", String.class), is("nested 3 val 2"));
        assertThat(config.values("nested1.nested2[1].nested3list", String.class).get(0), is("nested 3 list 2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenKeyNotFoundForSingleValue() {
        config.value("unknownKey", String.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenKeyNotFoundForListOfValues() {
        config.values("unknownKey", String.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenIntegerNotMatched() {
        config.integerValue("types.string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenDoubleNotMatched() {
        config.doubleValue("types.string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenBooleanNotMatched() {
        config.booleanValue("types.string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenMapNotMatched() {
        config.mapValue("types.string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenConfigNotMatched() {
        config.asConfig("types.string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenIntegerValuesNotMatched() {
        config.integerValues("types.string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenDoubleValuesNotMatched() {
        config.doubleValues("types.string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenBooleanValuesNotMatched() {
        config.booleanValues("types.string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenMapValuesNotMatched() {
        config.mapValues("types.string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenConfigValuesNotMatched() {
        config.asConfigs("types.string");
    }
}