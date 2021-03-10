package com.leliuk.unificator;

import com.leliuk.exception.UnificationException;
import com.leliuk.term.UConstant;
import com.leliuk.unificator.substitution.Substitution;
import io.vavr.control.Try;

import java.util.Collections;
import java.util.List;

public class ConstantToConstantUnificator implements Unificator<UConstant, UConstant> {
    @Override
    public Try<List<Substitution<?>>> unify(UConstant left, UConstant right) {
        return Try.of(() -> {
            if (left.getValue().equals(right.getValue())) {
                return Collections.emptyList();
            }
            throw new UnificationException(left, right, "constants have different values");
        });
    }
}
