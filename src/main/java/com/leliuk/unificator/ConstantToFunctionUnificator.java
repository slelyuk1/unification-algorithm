package com.leliuk.unificator;

import com.leliuk.unificator.substitution.Substitution;
import com.leliuk.unificator.exception.UnificationException;
import com.leliuk.term.UConstant;
import com.leliuk.term.UFunction;
import io.vavr.control.Try;

import java.util.List;

public class ConstantToFunctionUnificator implements Unificator<UConstant, UFunction> {
    @Override
    public Try<List<Substitution<?>>> unify(UConstant left, UFunction right) {
        return Try.failure(new UnificationException(left, right));
    }
}
