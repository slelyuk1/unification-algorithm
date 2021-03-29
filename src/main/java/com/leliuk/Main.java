package com.leliuk;

import com.leliuk.term.UConstant;
import com.leliuk.term.UFunction;
import com.leliuk.term.UVariable;
import com.leliuk.unificator.*;
import com.leliuk.unificator.searcher.MapUnificatorSearcher;
import com.leliuk.unificator.searcher.UnificatorSearcher;
import com.leliuk.unificator.substitution.Substitution;
import io.vavr.collection.Stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        UnificatorSearcher searcher = new MapUnificatorSearcher();
        searcher.addUnificator(UConstant.class, UConstant.class, new ConstantToConstantUnificator());
        searcher.addUnificator(UConstant.class, UVariable.class, new ConstantToVariableUnificator());
        searcher.addUnificator(UConstant.class, UFunction.class, new ConstantToFunctionUnificator());

        searcher.addUnificator(UVariable.class, UVariable.class, new VariableToVariableUnificator());
        searcher.addUnificator(UVariable.class, UFunction.class, new VariableToFunctionUnificator());

        searcher.addUnificator(UFunction.class, UFunction.class, new FunctionToFunctionUnificator(searcher));

        int n = 2;
        List<Object> arguments1 = new ArrayList<>(
                Stream.range(1, n + 1)
                        .map(i -> new UVariable("X" + i))
                        .collect(Collectors.toList())
        );
        arguments1.addAll(
                Stream.range(0, n)
                        .map(i -> new UFunction("f", Arrays.asList(new UVariable("Y" + i), new UVariable("Y" + i))))
                        .collect(Collectors.toList())
        );
        arguments1.add(new UVariable("Y" + n));
        UFunction hTerm1 = new UFunction("h", arguments1);

        List<Object> arguments2 = new ArrayList<>(
                Stream.range(0, n)
                        .map(i -> new UFunction("f", Arrays.asList(new UVariable("X" + i), new UVariable("X" + i))))
                        .collect(Collectors.toList())
        );
        arguments2.addAll(
                Stream.range(1, n + 1)
                        .map(i -> new UVariable("Y" + i))
                        .collect(Collectors.toList())
        );
        arguments2.add(new UVariable("X" + n));
        UFunction hTerm2 = new UFunction("h", arguments2);

        System.out.println("To unify:");
        System.out.println(hTerm1);
        System.out.println(hTerm2);
        System.out.println();

        Unificator<UFunction, UFunction> unificator = searcher.searchUnificator(UFunction.class, UFunction.class)
                .orElseThrow(() -> new IllegalStateException("Added unificator cannot be found!"));

        long start = System.nanoTime();
        List<Substitution<?>> substitutions = unificator.unify(hTerm1, hTerm2).get();
        // 20 = 26.479776 seconds
        // 21 = 84.200551 seconds
        double duration = (System.nanoTime() - start) * 1e-9;

        System.out.printf("Finished in %f seconds\n", duration);
        System.out.println("Unified:");
        System.out.println(
                substitutions.stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining("\n"))
        );
    }
}
