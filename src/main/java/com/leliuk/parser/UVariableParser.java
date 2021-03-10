package com.leliuk.parser;

import com.leliuk.exception.ParserException;
import com.leliuk.term.UVariable;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;

import java.util.regex.Pattern;

@AllArgsConstructor
public class UVariableParser implements Parser<UVariable> {
    private final static Pattern DEFAULT_PATTERN = Pattern.compile("[A-Z]+");

    private final Pattern pattern;

    public UVariableParser() {
        pattern = DEFAULT_PATTERN;
    }

    @Override
    public Try<UVariable> parse(String toParse) {
        toParse = toParse.trim();
        if (pattern.matcher(toParse).matches()) {
            return Try.success(new UVariable(toParse));
        }
        return Try.failure(new ParserException(UVariable.class, toParse, "doesn't match specified pattern"));
    }
}
