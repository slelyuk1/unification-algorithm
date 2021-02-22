package com.leliuk.unificator.searcher;


import com.leliuk.term.UConstant;
import com.leliuk.term.UVariable;
import com.leliuk.unificator.Unificator;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class MapUnificatorSearcherTest {

    private UnificatorSearcher searcher;

    @BeforeEach
    void doBeforeEach() {
        searcher = new MapUnificatorSearcher();
    }

    @Test
    void testSuccessfulAdditionAndSearch() {
        Unificator<UConstant, UVariable> unificator = mockedUnificator();
        searcher.addUnificator(UConstant.class, UVariable.class, unificator);
        Unificator<UConstant, UVariable> found = searcher.searchUnificator(UConstant.class, UVariable.class)
                .orElseThrow(() -> new IllegalStateException("Couldn't found added unificator!"));
        Assertions.assertSame(unificator, found);
    }

    @Test
    void testSuccessfulReversedSearch() {
        Unificator<UConstant, UVariable> unificator = mockedUnificator();
        searcher.addUnificator(UConstant.class, UVariable.class, unificator);
        searcher.searchUnificator(UVariable.class, UConstant.class)
                .orElseThrow(() -> new IllegalStateException("Couldn't found added unificator!"));
    }

    private static <L, R> Unificator<L, R> mockedUnificator() {
        return (l, r) -> Try.success(Collections.emptyList());
    }

}
