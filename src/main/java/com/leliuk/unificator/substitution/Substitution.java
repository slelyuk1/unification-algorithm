package com.leliuk.unificator.substitution;

import com.leliuk.term.UVariable;
import lombok.Value;

@Value
public class Substitution<T> {
    UVariable variable;
    T to;

    @Override
    public String toString() {
        return String.format("from %s to %s", variable, to);
    }
}
