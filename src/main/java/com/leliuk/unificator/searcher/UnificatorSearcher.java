package com.leliuk.unificator.searcher;

import com.leliuk.unificator.Unificator;

import java.util.Optional;

public interface UnificatorSearcher {
    <L, R> void addUnificator(Class<L> fromType, Class<R> toType, Unificator<L, R> unificator);

    <L, R> Optional<Unificator<L, R>> searchUnificator(Class<L> leftType, Class<R> rightType);
}
