package com.leliuk.unificator;

import com.leliuk.unificator.substitution.Substitution;
import io.vavr.control.Try;

import java.util.List;

@FunctionalInterface
public interface Unificator<L, R> {
    Try<List<Substitution<?>>> unify(L left, R right);

    static <L, R> Unificator<R, L> reversed(Unificator<L, R> toReverse) {
        return (l, r) -> toReverse.unify(r, l);
    }
}
