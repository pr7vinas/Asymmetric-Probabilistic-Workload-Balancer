package io.codebest.balancer;

import java.util.function.Supplier;

public abstract class Workload<T> {

    private Supplier<T> supplier;

    public Workload(final Supplier<T> supplier) {
        assertSupplierIsPresent(supplier);
        this.supplier = supplier;
    }

    public Supplier<T> getSupplier() {
        return supplier;
    }

    private void assertSupplierIsPresent(final Supplier<T> supplier) {
        if (supplier == null) throw new IllegalArgumentException("Supplier can not be null");
    }

}