package io.codebest.balancer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class AsymmetricLoadBalancer<T> implements LoadBalancer<T> {

    private static final int UPPER_BOUND_MAX_PERCENTAGE = 100;

    private Collection<AsymmetricWorkload<T>> asymmetricWorkloads;

    private int accumulativePercentage = 0;

    private Random random;

    public AsymmetricLoadBalancer() {
        this.asymmetricWorkloads = new ArrayList<>();
        this.random = ThreadLocalRandom.current();
    }

    @Override
    public T balance() {
        assertAccumulatedPercentageReachedOneHundred();
        return executeTarget(raffleTarget());
    }

    public AsymmetricLoadBalancer<T> add(AsymmetricWorkload<T> asymmetricWorkload) {
        assertTargetIsValid(asymmetricWorkload);
        setupTargetBoundaries(asymmetricWorkload);
        calculateAccumulativePercentage(asymmetricWorkload);
        asymmetricWorkloads.add(asymmetricWorkload);
        return this;
    }

    public AsymmetricLoadBalancer<T> fill(AsymmetricWorkload<T> asymmetricWorkload) {
        assertTargetIsValid(asymmetricWorkload);
        asymmetricWorkload.setProbability(UPPER_BOUND_MAX_PERCENTAGE - accumulativePercentage);
        return add(asymmetricWorkload);
    }

    private AsymmetricWorkload<T> raffleTarget() {
        final AsymmetricWorkload<T> chosenTarget = pickTargetCoveringRoundProbability(generateRoundProbability());
        assertTargetIsNotNull(chosenTarget);
        return chosenTarget;
    }

    private T executeTarget(final AsymmetricWorkload<T> target) {
        return target.getSupplier().get();
    }

    private int generateRoundProbability() {
        return random.nextInt(UPPER_BOUND_MAX_PERCENTAGE);
    }


    private AsymmetricWorkload<T> pickTargetCoveringRoundProbability(final int roundProbability) {
        AsymmetricWorkload<T> chosenTarget = null;

        for (AsymmetricWorkload<T> target : asymmetricWorkloads) {
            if (roundProbability >= target.getLowerBound() && roundProbability <= target.getUpperBound()) {
                chosenTarget = target;
            }
        }

        return chosenTarget;
    }


    private void calculateAccumulativePercentage(final AsymmetricWorkload<T> asymmetricWorkload) {
        accumulativePercentage = accumulativePercentage + asymmetricWorkload.getProbability();
    }

    private void setupTargetBoundaries(final AsymmetricWorkload<T> asymmetricWorkload) {
        asymmetricWorkload.setLowerBound(accumulativePercentage);
        asymmetricWorkload.setUpperBound(accumulativePercentage + asymmetricWorkload.getProbability() - 1);
    }


    private void assertTargetIsValid(final AsymmetricWorkload<T> asymmetricWorkload) {
        assertTargetAndSupplierArePresent(asymmetricWorkload);
        assertTargetProbabilityIsWithinZeroAndMax(asymmetricWorkload);
        assertTargetProbabilityDoesNotOverflow(asymmetricWorkload);
    }

    private void assertAccumulatedPercentageReachedOneHundred() {
        if (accumulativePercentage != UPPER_BOUND_MAX_PERCENTAGE) {
            throw new IllegalStateException("AsymmetricWorkload's probability is not 100%");
        }
    }


    private void assertTargetProbabilityDoesNotOverflow(final AsymmetricWorkload<T> asymmetricWorkload) {
        if ((accumulativePercentage + asymmetricWorkload.getProbability()) > UPPER_BOUND_MAX_PERCENTAGE) {
            throw new IllegalArgumentException("AsymmetricWorkload's probability overflow");
        }
    }

    private void assertTargetProbabilityIsWithinZeroAndMax(final AsymmetricWorkload<T> asymmetricWorkload) {
        if (asymmetricWorkload.getProbability() < 0 || asymmetricWorkload.getProbability() > UPPER_BOUND_MAX_PERCENTAGE) {
            throw new IllegalArgumentException("AsymmetricWorkload probability must have a value between 0 and 100");
        }
    }

    private void assertTargetAndSupplierArePresent(final AsymmetricWorkload<T> asymmetricWorkload) {
        if (asymmetricWorkload == null || asymmetricWorkload.getSupplier() == null) {
            throw new IllegalArgumentException("AsymmetricWorkload supplier can not be null");
        }
    }

    private void assertTargetIsNotNull(final AsymmetricWorkload<T> chosenTarget) {
        if (chosenTarget == null) {
            throw new IllegalStateException("AsymmetricLoadBalancer failed to find a target");
        }
    }


}