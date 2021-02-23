package com.leliuk.term;

import lombok.Value;

@Value
public class UVariable {
    String name;

    @Override
    public String toString() {
        return name;
    }
}
