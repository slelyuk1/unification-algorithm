package com.leliuk.term;

import lombok.Value;

@Value
public class UConstant {
    String value;

    @Override
    public String toString() {
        return String.format("'%s'", value);
    }
}
