package com.leliuk.unificator;

import com.leliuk.unificator.substitution.Substitution;
import com.leliuk.unificator.exception.UnificationException;
import com.leliuk.term.UFunction;
import com.leliuk.term.UVariable;
import io.vavr.control.Try;

import java.util.Collections;
import java.util.List;

public class VariableToFunctionUnificator implements Unificator<UVariable, UFunction> {

    @Override
    public Try<List<Substitution<?>>> unify(UVariable left, UFunction right) {
        if (containsVariable(left, right)) {
            return Try.failure(new UnificationException(left, right));
        }
        return Try.success(Collections.singletonList(new Substitution<>(left, right)));
    }

    private boolean containsVariable(UVariable toSearch, UFunction rootFunction) {
        boolean contains = rootFunction.getTerms().stream()
                .filter(term -> term instanceof UVariable)
                .map(term -> (UVariable) term)
                .anyMatch(variable -> variable.getName().equals(toSearch.getName()));
        if (!contains) {
            return rootFunction.getTerms().stream()
                    .filter(term -> term instanceof UFunction)
                    .map(term -> (UFunction) term)
                    .anyMatch(function -> containsVariable(toSearch, function));

        }
        return true;
    }
}