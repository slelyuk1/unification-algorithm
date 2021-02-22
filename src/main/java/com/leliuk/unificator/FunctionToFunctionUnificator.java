package com.leliuk.unificator;

import com.leliuk.unificator.substitution.Substitution;
import com.leliuk.unificator.exception.UnificationException;
import com.leliuk.unificator.searcher.UnificatorSearcher;
import com.leliuk.term.UFunction;
import com.leliuk.term.UVariable;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
public class FunctionToFunctionUnificator implements Unificator<UFunction, UFunction> {
    private final UnificatorSearcher searcher;

    @Override
    public Try<List<Substitution<?>>> unify(UFunction left, UFunction right) {
        List<Substitution<?>> allSubstitutions = new ArrayList<>();
        if (left.getName().equals(right.getName()) && left.getTerms().size() == right.getTerms().size()) {
            return Try.of(() -> {
                UFunction localLeft = left;
                UFunction localRight = right;
                for (int i = 0; i < localLeft.getTerms().size(); ++i) {
                    Object leftTerm = localLeft.getTerms().get(i);
                    Object rightTerm = localRight.getTerms().get(i);
                    Unificator<?, ?> unificator = searcher.searchUnificator(leftTerm.getClass(), rightTerm.getClass())
                            .orElseThrow(() -> new UnificationException(left, right, "couldn't find appropriate unificator"));
                    List<Substitution<?>> substitutions = ((Unificator<Object, Object>) unificator).unify(leftTerm, rightTerm)
                            .getOrElseThrow(Function.identity());
                    localLeft = substitute(substitutions, localLeft);
                    localRight = substitute(substitutions, localRight);
                    allSubstitutions.addAll(substitutions);
                }
                return allSubstitutions;
            });
        }
        return Try.failure(new UnificationException(left, right, "functions have different names or different number of arguments"));
    }

    private UFunction substitute(List<Substitution<?>> substitutions, UFunction function) {
        UFunction result = new UFunction(function.getName(), function.getTerms());
        List<Object> terms = (List<Object>) result.getTerms();
        substitutions.forEach(substitution -> {
            UVariable toSubstitute = substitution.getVariable();
            for (int i = 0; i < terms.size(); ++i) {
                Object term = terms.get(i);
                if (toSubstitute.equals(term)) {
                    terms.set(i, substitution.getTo());
                    continue;
                }
                if (term instanceof UFunction) {
                    UFunction other = substitute(substitutions, (UFunction) term);
                    terms.set(i, other);
                }
            }
        });

        return result;
    }
}
