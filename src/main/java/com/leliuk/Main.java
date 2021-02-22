package com.leliuk;

import io.vavr.control.Try;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.*;
import java.util.function.Function;

class UnificationException extends RuntimeException {
    private static final String MESSAGE = "Couldn't unify from %s to %s";
    private static final String MESSAGE_WITH_ADDITIONAL = "Couldn't unify from %s to %s (%s)";

    public UnificationException(Object from, Object to) {
        super(String.format(MESSAGE, from, to));
    }

    public UnificationException(Object from, Object to, String additionalInfo) {
        super(String.format(MESSAGE_WITH_ADDITIONAL, from, to, additionalInfo));
    }
}

@Value
class Substitution<T> {
    UVariable variable;
    T to;
}

@FunctionalInterface
interface Unificator<L, R> {
    Try<List<Substitution<?>>> unify(L left, R right);

    static <L, R> Unificator<R, L> reversed(Unificator<L, R> toReverse) {
        return (l, r) -> toReverse.unify(r, l);
    }
}

class ConstantToConstantUnificator implements Unificator<UConstant, UConstant> {
    @Override
    public Try<List<Substitution<?>>> unify(UConstant left, UConstant right) {
        return Try.of(() -> {
            if (left.getValue().equals(right.getValue())) {
                return Collections.emptyList();
            }
            throw new UnificationException(left, right);
        });
    }
}

class ConstantToVariableUnificator implements Unificator<UConstant, UVariable> {
    @Override
    public Try<List<Substitution<?>>> unify(UConstant left, UVariable right) {
        return Try.success(Collections.singletonList(new Substitution<>(right, left)));
    }
}

class ConstantToFunctionUnificator implements Unificator<UConstant, UFunction> {
    @Override
    public Try<List<Substitution<?>>> unify(UConstant left, UFunction right) {
        return Try.failure(new UnificationException(left, right));
    }
}

class VariableToVariableUnificator implements Unificator<UVariable, UVariable> {

    @Override
    public Try<List<Substitution<?>>> unify(UVariable left, UVariable right) {
        return Try.success(Collections.singletonList(new Substitution<>(left, right)));
    }
}

class VariableToFunctionUnificator implements Unificator<UVariable, UFunction> {

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

interface UnificatorSearcher {
    <L, R> void addUnificator(Class<L> fromType, Class<R> toType, Unificator<L, R> unificator);

    <L, R> Optional<Unificator<L, R>> searchUnificator(Class<L> leftType, Class<R> rightType);
}

class MapUnificatorSearcher implements UnificatorSearcher {

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

@AllArgsConstructor
class FunctionToFunctionUnificator implements Unificator<UFunction, UFunction> {
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

@Value
class UConstant {
    String value;
}

@Value
class UVariable {
    String name;
}

@Value
class UFunction {
    String name;
    List<?> terms;

    public UFunction(String name, List<?> terms) {
        this.name = name;
        this.terms = new ArrayList<>(terms);
    }
}


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
