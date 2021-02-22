package com.leliuk;

import com.leliuk.term.UConstant;
import com.leliuk.term.UFunction;
import com.leliuk.term.UVariable;
import com.leliuk.unificator.*;
import com.leliuk.unificator.searcher.MapUnificatorSearcher;
import com.leliuk.unificator.searcher.UnificatorSearcher;
import com.leliuk.unificator.substitution.Substitution;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        UnificatorSearcher searcher = new MapUnificatorSearcher();
        searcher.addUnificator(UConstant.class, UConstant.class, new ConstantToConstantUnificator());
        searcher.addUnificator(UConstant.class, UVariable.class, new ConstantToVariableUnificator());
        searcher.addUnificator(UConstant.class, UFunction.class, new ConstantToFunctionUnificator());

        searcher.addUnificator(UVariable.class, UVariable.class, new VariableToVariableUnificator());
        searcher.addUnificator(UVariable.class, UFunction.class, new VariableToFunctionUnificator());

        searcher.addUnificator(UFunction.class, UFunction.class, new FunctionToFunctionUnificator(searcher));

        UFunction e1 = new UFunction("p", Arrays.asList(
                new UFunction("f", Collections.singletonList(new UConstant("a"))),
                new UFunction("g", Collections.singletonList(new UConstant("b"))),
                new UVariable("Y")
        ));

        UFunction e2 = new UFunction("p", Arrays.asList(
                new UVariable("Z"),
                new UFunction("g", Collections.singletonList(new UVariable("W"))),
                new UConstant("c")
        ));

        Unificator<UFunction, UFunction> unificator = searcher.searchUnificator(UFunction.class, UFunction.class)
                .orElseThrow(() -> new IllegalStateException("Added unificator cannot be found!"));
        List<Substitution<?>> substitutions = unificator.unify(e1, e2).get();
        System.out.println(substitutions);
    }
}
