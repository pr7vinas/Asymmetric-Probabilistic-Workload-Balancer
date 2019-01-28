package io.codebest.balancer;

import java.util.function.Supplier;

public final class AsymmetricWorkload<T> extends Workload<T> {

    private int upperBound;

    private int lowerBound;

    private int probability;

    public AsymmetricWorkload(final int probability, final Supplier<T> supplier) {
        super(supplier);
        this.probability = probability;
    }

    public AsymmetricWorkload(final Supplier<T> supplier) {
        super(supplier);
        this.probability = 0;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(final int upperBound) {
        this.upperBound = upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(final int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(final int probability) {
        this.probability = probability;
    }

}
