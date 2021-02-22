package com.leliuk.unificator;

import com.leliuk.unificator.substitution.Substitution;
import com.leliuk.term.UConstant;
import com.leliuk.term.UVariable;
import io.vavr.control.Try;

import java.util.Collections;
import java.util.List;

public class ConstantToVariableUnificator implements Unificator<UConstant, UVariable> {
    @Override
    public Try<List<Substitution<?>>> unify(UConstant left, UVariable right) {
        return Try.success(Collections.singletonList(new Substitution<>(right, left)));
    }
}
