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
}
