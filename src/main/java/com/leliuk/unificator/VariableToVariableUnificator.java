package com.leliuk.unificator;

import com.leliuk.unificator.substitution.Substitution;
import com.leliuk.term.UVariable;
import io.vavr.control.Try;

import java.util.Collections;
import java.util.List;

public class VariableToVariableUnificator implements Unificator<UVariable, UVariable> {

    @Override
    public Try<List<Substitution<?>>> unify(UVariable left, UVariable right) {
        return Try.success(Collections.singletonList(new Substitution<>(left, right)));
    }
}
