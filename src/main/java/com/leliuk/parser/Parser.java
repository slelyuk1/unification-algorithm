package com.leliuk.parser;

import io.vavr.control.Try;

public interface Parser<T> {
    Try<T> parse(String toParse);
}
