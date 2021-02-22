package com.leliuk.unificator.searcher;

import com.leliuk.unificator.searcher.UnificatorSearcher;
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
        Unificator<?, ?> result = unificators.get(new Pair<>(leftType, rightType));
        if (result == null) {
            result = Unificator.reversed(unificators.get(new Pair<>(rightType, leftType)));
        }
        return Optional.ofNullable((Unificator<L, R>) result);
    }
}
