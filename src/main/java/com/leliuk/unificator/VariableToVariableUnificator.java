package com.leliuk.unificator;

import com.leliuk.term.UVariable;
import com.leliuk.unificator.substitution.Substitution;
import io.vavr.control.Try;

import java.util.Collections;
import java.util.List;

public class VariableToVariableUnificator implements Unificator<UVariable, UVariable> {

    @Override
    public Try<List<Substitution<?>>> unify(UVariable left, UVariable right) {
        if (left.getName().equals(right.getName())) {
            return Try.success(Collections.emptyList());
        }
        return Try.success(Collections.singletonList(new Substitution<>(left, right)));
    }
}
