package com.leliuk.parser;

import com.leliuk.exception.ParserException;
import com.leliuk.term.UConstant;
import com.leliuk.term.UFunction;
import com.leliuk.term.UVariable;
import io.vavr.control.Try;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UFunctionParser implements Parser<UFunction> {
    private static final String[] NO_ARGS = new String[0];

    private static final Pattern DEFAULT_HEADER_PATTERN = Pattern.compile("[a-z]+");
    private static final Pattern DEFAULT_LEFT_BOUND = Pattern.compile("\\(");
    private static final Pattern DEFAULT_RIGHT_BOUND = Pattern.compile("\\)");
    private static final Pattern DEFAULT_ARGUMENT_SPLITTER = Pattern.compile(",");
    private static final Pattern DEFAULT_BODY_PATTERN =
            Pattern.compile(String.format("(([^%1$s]+)(%1$s[^%1$s]+)*)?", DEFAULT_ARGUMENT_SPLITTER.pattern()));

    private final Pattern fullPattern;
    private final Pattern argumentsPattern;
    private final Pattern argumentSplitterPattern;

    private final Parser<UConstant> constantParser;
    private final Parser<UVariable> variableParser;

    public UFunctionParser(@NotNull Parser<UConstant> constantParser, @NotNull Parser<UVariable> variableParser) {
        this.constantParser = constantParser;
        this.variableParser = variableParser;

        this.argumentSplitterPattern = DEFAULT_ARGUMENT_SPLITTER;
        this.argumentsPattern = Pattern.compile(DEFAULT_LEFT_BOUND.pattern() + DEFAULT_BODY_PATTERN.pattern() + DEFAULT_RIGHT_BOUND.pattern());
        this.fullPattern = Pattern.compile(DEFAULT_HEADER_PATTERN.pattern() + argumentsPattern.pattern());
    }

    @SuppressWarnings("ThrowableNotThrown")
    @Override
    public Try<UFunction> parse(String toParse) {
        toParse = toParse.trim();
        if (fullPattern.matcher(toParse).matches()) {
            Matcher argumentsMatcher = argumentsPattern.matcher(toParse);
            String header = argumentsMatcher.replaceFirst("");
            String[] arguments = Optional.ofNullable(argumentsMatcher.group(1))
                    .map(toSplit -> toSplit.split(argumentSplitterPattern.pattern()))
                    .orElse(NO_ARGS);
            UFunction function = new UFunction(header, new ArrayList<>(arguments.length));
            for (int i = 0; i < arguments.length; ++i) {
                String argument = "";
                Object parseResult = null;
                Throwable firstParseError = null;
                while (parseResult == null && i < arguments.length) {
                    argument += argument.isEmpty() ? arguments[i] : ',' + arguments[i];
                    Try<UFunction> tryParseFunction = parse(argument);
                    Try<UConstant> tryParseConstant = constantParser.parse(argument);
                    Try<UVariable> tryParseVariable = variableParser.parse(argument);
                    if (tryParseFunction.isSuccess()) {
                        parseResult = tryParseFunction.get();
                    } else if (tryParseConstant.isSuccess()) {
                        parseResult = tryParseConstant.get();
                    } else if (tryParseVariable.isSuccess()) {
                        parseResult = tryParseVariable.get();
                    } else {
                        tryParseVariable.getCause().initCause(tryParseConstant.getCause());
                        tryParseConstant.getCause().initCause(tryParseFunction.getCause());
                        if (firstParseError == null) {
                            firstParseError = tryParseVariable.getCause();
                        }
                        ++i;
                    }
                }
                if (parseResult != null) {
                    function.getTerms().add(parseResult);
                } else {
                    return Try.failure(firstParseError);
                }
            }
            return Try.success(function);
        }
        return Try.failure(new ParserException(UFunction.class, toParse));
    }

    public Parser<UConstant> getConstantParser() {
        return constantParser;
    }

    public Parser<UVariable> getVariableParser() {
        return variableParser;
    }
}
