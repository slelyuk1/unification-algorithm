package com.leliuk.parser;

import com.leliuk.term.UConstant;
import com.leliuk.term.UFunction;
import com.leliuk.term.UVariable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnificationTermParserTest {

    private final Parser<Object> parser;

    public UnificationTermParserTest() {
        UConstantParser constantParser = new UConstantParser();
        UVariableParser variableParser = new UVariableParser();
        this.parser = new UnificationTermParser(new UFunctionParser(constantParser, variableParser));
    }

    @Test
    void testSuccessfulConstantParsing() {
        UConstant e1 = new UConstant("a");
        Object parsed = parser.parse(e1.toString()).get();
        assertEquals(e1, parsed);
    }

    @Test
    void testSuccessfulVariableParsing() {
        UVariable e1 = new UVariable("X");
        Object parsed = parser.parse(e1.toString()).get();
        assertEquals(e1, parsed);
    }

    @Test
    void testSuccessfulFunctionParsing1() {
        UFunction e1 = new UFunction("f", Arrays.asList(new UConstant("a"), new UVariable("X")));
        Object parsed = parser.parse(e1.toString()).get();
        assertEquals(e1, parsed);
    }

    @Test
    void testSuccessfulFunctionParsing2() {
        UFunction e1 = new UFunction("f", Collections.singletonList(new UConstant("a")));
        Object parsed = parser.parse(e1.toString()).get();
        assertEquals(e1, parsed);
    }

    @Test
    void testSuccessfulFunctionParsing3() {
        UFunction e1 = new UFunction("f", Collections.singletonList(new UVariable("X")));
        Object parsed = parser.parse(e1.toString()).get();
        assertEquals(e1, parsed);
    }

    @Test
    void testSuccessfulFunctionParsing4() {
        UFunction e1 = new UFunction("f", Collections.singletonList(
                new UFunction("g", Collections.singletonList(
                        new UVariable("X")))
        ));
        Object parsed = parser.parse(e1.toString()).get();
        assertEquals(e1, parsed);
    }

    @Test
    void testSuccessfulFunctionParsing5() {
        UFunction e1 = new UFunction("f", Arrays.asList(
                new UFunction("g", Collections.singletonList(new UVariable("X"))),
                new UVariable("X")
        ));
        Object parsed = parser.parse(e1.toString()).get();
        assertEquals(e1, parsed);
    }

    @Test
    void testSuccessfulFunctionParsing6() {
        String toParse = "f('a'   , 'd', A, 'adfsa', 'e', E, 'x', y('a'), z('a','b', 'c') , y())";
        UFunction e1 = new UFunction("f", Arrays.asList(
                new UConstant("a"),
                new UConstant("d"),
                new UVariable("A"),
                new UConstant("adfsa"),
                new UConstant("e"),
                new UVariable("E"),
                new UConstant("x"),
                new UFunction("y", Collections.singletonList(new UConstant("a"))),
                new UFunction("z", Arrays.asList(
                        new UConstant("a"),
                        new UConstant("b"),
                        new UConstant("c")
                )),
                new UFunction("y", Collections.emptyList())
        ));
        Object parsed = parser.parse(toParse).get();
        assertEquals(e1, parsed);
    }
}
