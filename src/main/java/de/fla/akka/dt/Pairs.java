package de.fla.akka.dt;

import java.util.HashSet;
import java.util.Set;

public class Pairs<T, K> {

    private Set<Pair<T, K>> pairs;

    private Pairs(Set<Pair<T, K>> pairs) {
        this.pairs = new HashSet<>(pairs);
    }

    public Set<Pair<T, K>> pairs() {
        return new HashSet<>(pairs);
    }

    public static class Builder<T, K> {

        private Set<Pair<T, K>> pairs;

        public Builder() {
            pairs = new HashSet<>();
        }

        public Builder<T, K> and(Pair<T, K> pair) {
            pairs.add(pair);
            return this;
        }

        public Pairs<T, K> build() {
            return new Pairs<>(pairs);
        }
    }
}
