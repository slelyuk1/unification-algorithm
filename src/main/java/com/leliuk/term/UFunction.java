package com.leliuk.term;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Value
public class UFunction {
    String name;
    List<Object> terms;

    public UFunction(String name, List<?> terms) {
        this.name = name;
        this.terms = new ArrayList<>(terms);
    }

    @Override
    public String toString() {
        String args = terms.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        return String.format("%s(%s)", name, args);
    }
}
