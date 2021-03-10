package com.leliuk.parser;

import com.leliuk.exception.ParserException;
import com.leliuk.term.UConstant;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;

import java.util.regex.Pattern;

@AllArgsConstructor
public class UConstantParser implements Parser<UConstant> {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("[a-z]+");

    private final Pattern pattern;

    public UConstantParser() {
        pattern = DEFAULT_PATTERN;
    }

    @Override
    public Try<UConstant> parse(String toParse) {
        toParse = toParse.trim();
        if (pattern.matcher(toParse).matches()) {
            return Try.success(new UConstant(toParse));
        }
        return Try.failure(new ParserException(UConstant.class, toParse, "doesn't match specified pattern"));
    }
}
