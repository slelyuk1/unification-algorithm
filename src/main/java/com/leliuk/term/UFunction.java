package com.leliuk.term;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class UFunction {
    String name;
    List<?> terms;

    public UFunction(String name, List<?> terms) {
        this.name = name;
        this.terms = new ArrayList<>(terms);
    }
}
