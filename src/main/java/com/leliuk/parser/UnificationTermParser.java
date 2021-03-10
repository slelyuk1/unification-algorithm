package com.leliuk.parser;

import com.leliuk.exception.ParserException;
import io.vavr.control.Try;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UnificationTermParser implements Parser<Object> {

    private final List<Parser<?>> parsers;

    public UnificationTermParser(UFunctionParser functionParser) {
        this.parsers = Collections.unmodifiableList(
                Arrays.asList(functionParser, functionParser.getConstantParser(), functionParser.getVariableParser())
        );
    }

    public UnificationTermParser(List<Parser<?>> parsers) {
        this.parsers = Collections.unmodifiableList(parsers);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Try<Object> parse(String toParse) {
        // todo implement normal cause setting for exceptions
        Try<Object> tryParse = null;
        for (Parser<?> parser : parsers) {
            tryParse = (Try<Object>) parser.parse(toParse);
            if (tryParse.isSuccess()) {
                return tryParse;
            }
        }
        return Optional.ofNullable(tryParse)
                .orElse(Try.failure(new ParserException(Object.class, toParse)));
    }

    // todo implement testing for parser
    public static void main(String[] args) {
        UConstantParser constantParser = new UConstantParser();
        UVariableParser variableParser = new UVariableParser();
        UnificationTermParser termParser = new UnificationTermParser(new UFunctionParser(constantParser, variableParser));
        String toParse = "f(a   , d, A, adfsa, e, E, x, y(a), z(a,b, c) , y())";
        termParser.parse(toParse)
                .onSuccess(function -> System.out.println("Successfully parsed: " + function))
                .onFailure(System.err::println);

    }

}
