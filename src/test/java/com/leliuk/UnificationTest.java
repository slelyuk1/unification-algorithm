package com.leliuk;

import com.leliuk.term.UConstant;
import com.leliuk.term.UFunction;
import com.leliuk.term.UVariable;
import com.leliuk.unificator.*;
import com.leliuk.unificator.exception.UnificationException;
import com.leliuk.unificator.searcher.MapUnificatorSearcher;
import com.leliuk.unificator.searcher.UnificatorSearcher;
import com.leliuk.unificator.substitution.Substitution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnificationTest {
    private final UnificatorSearcher searcher;

    public UnificationTest() {
        this.searcher = new MapUnificatorSearcher();
        populateSearcherWithUnificators(searcher);
    }

    @Test
    @DisplayName("e1 = a; e2 = a; result = {}")
    void testUnificationExample1() {
        UConstant e1 = new UConstant("a");
        UConstant e2 = new UConstant("a");
        List<Substitution<?>> substitutions = searcher.searchUnificator(UConstant.class, UConstant.class)
                .map(unificator -> unificator.unify(e1, e2))
                .orElseThrow(() -> new IllegalStateException("Couldn't find registered unificator!"))
                .get();
        assertTrue(substitutions.isEmpty());
    }

    @Test
    @DisplayName("e1 = a; e2 = b; result = failure")
    void testUnificationExample2() {
        UConstant e1 = new UConstant("a");
        UConstant e2 = new UConstant("b");
        Throwable e = Assertions.assertThrows(UnificationException.class, () -> {
            searcher.searchUnificator(UConstant.class, UConstant.class)
                    .map(unificator -> unificator.unify(e1, e2))
                    .orElseThrow(() -> new IllegalStateException("Couldn't find registered unificator!"))
                    .get();
        });
        System.out.println(e.getMessage());
    }

    @Test
    @DisplayName("e1 = X; e2 = X; result = {}")
    void testUnificationExample3() {
        UVariable e1 = new UVariable("X");
        UVariable e2 = new UVariable("X");
        List<Substitution<?>> substitutions = searcher.searchUnificator(UVariable.class, UVariable.class)
                .map(unificator -> unificator.unify(e1, e2))
                .orElseThrow(() -> new IllegalStateException("Couldn't find registered unificator!"))
                .get();
        assertTrue(substitutions.isEmpty());
    }

    @Test
    @DisplayName("e1 = a; e2 = X; result = {X to a}")
    void testUnificationExample4() {
        UConstant e1 = new UConstant("a");
        UVariable e2 = new UVariable("X");
        List<Substitution<?>> substitutions = searcher.searchUnificator(UConstant.class, UVariable.class)
                .map(unificator -> unificator.unify(e1, e2))
                .orElseThrow(() -> new IllegalStateException("Couldn't find registered unificator!"))
                .get();
        List<Substitution<?>> expected = Collections.singletonList(new Substitution<>(e2, e1));
        assertEquals(expected, substitutions);
    }

    @Test
    @DisplayName("e1 = X; e2 = Y; result = {X to Y}")
    void testUnificationExample5() {
        UVariable e1 = new UVariable("X");
        UVariable e2 = new UVariable("Y");
        List<Substitution<?>> substitutions = searcher.searchUnificator(UVariable.class, UVariable.class)
                .map(unificator -> unificator.unify(e1, e2))
                .orElseThrow(() -> new IllegalStateException("Couldn't find registered unificator!"))
                .get();
        List<Substitution<?>> expected = Collections.singletonList(new Substitution<>(e1, e2));
        assertEquals(expected, substitutions);
    }

    @Test
    @DisplayName("e1 = f(a, X); e2 = f(a, b); result = {X to b}")
    void testUnificationExample6() {
        UFunction e1 = new UFunction("f", Arrays.asList(new UConstant("a"), new UVariable("X")));
        UFunction e2 = new UFunction("f", Arrays.asList(new UConstant("a"), new UConstant("b")));
        List<Substitution<?>> substitutions = searcher.searchUnificator(UFunction.class, UFunction.class)
                .map(unificator -> unificator.unify(e1, e2))
                .orElseThrow(() -> new IllegalStateException("Couldn't find registered unificator!"))
                .get();
        List<Substitution<?>> expected = Collections.singletonList(new Substitution<>(new UVariable("X"), new UConstant("b")));
        assertEquals(expected, substitutions);
    }

    private static void populateSearcherWithUnificators(UnificatorSearcher searcher) {
        searcher.addUnificator(UConstant.class, UConstant.class, new ConstantToConstantUnificator());
        searcher.addUnificator(UConstant.class, UVariable.class, new ConstantToVariableUnificator());
        searcher.addUnificator(UConstant.class, UFunction.class, new ConstantToFunctionUnificator());
        searcher.addUnificator(UVariable.class, UVariable.class, new VariableToVariableUnificator());
        searcher.addUnificator(UVariable.class, UFunction.class, new VariableToFunctionUnificator());
        searcher.addUnificator(UFunction.class, UFunction.class, new FunctionToFunctionUnificator(searcher));
    }
}
