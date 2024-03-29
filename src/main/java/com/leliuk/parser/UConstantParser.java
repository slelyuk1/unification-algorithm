package com.leliuk.parser;

import com.leliuk.exception.ParserException;
import com.leliuk.term.UConstant;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class UConstantParser implements Parser<UConstant> {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("'([a-z]+)'");

    private final Pattern pattern;

    public UConstantParser() {
        pattern = DEFAULT_PATTERN;
    }

    @Override
    public Try<UConstant> parse(String toParse) {
        toParse = toParse.trim();
        Matcher matcher = pattern.matcher(toParse);
        if (matcher.matches()) {
            return Try.success(new UConstant(matcher.group(1)));
        }
        return Try.failure(new ParserException(UConstant.class, toParse, "doesn't match specified pattern"));
    }
}
