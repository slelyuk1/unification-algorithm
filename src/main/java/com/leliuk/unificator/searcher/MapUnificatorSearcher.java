package com.leliuk.unificator.searcher;

import com.leliuk.unificator.Unificator;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapUnificatorSearcher implements UnificatorSearcher {

    private final Map<Pair<Class<?>, Class<?>>, Unificator<?, ?>> unificators;

    public MapUnificatorSearcher() {
        this.unificators = new HashMap<>();
    }

    @Override
    public <L, R> void addUnificator(Class<L> fromType, Class<R> toType, Unificator<L, R> unificator) {
        unificators.put(new Pair<>(fromType, toType), unificator);
    }

    @Override
    public <L, R> Optional<Unificator<L, R>> searchUnificator(Class<L> leftType, Class<R> rightType) {
        Unificator<L, R> result = getUnificator(leftType, rightType);
        if (result == null) {
            result = Unificator.reversed(getUnificator(rightType, leftType));
        }
        return Optional.ofNullable(result);
    }

    @SuppressWarnings({"SuspiciousMethodCalls", "unchecked"})
    private <L, R> Unificator<L, R> getUnificator(Class<L> leftType, Class<R> rightType) {
        return (Unificator<L, R>) unificators.get(new Pair<>(leftType, rightType));
    }
}
