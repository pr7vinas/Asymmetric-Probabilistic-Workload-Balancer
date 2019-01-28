package io.codebest.balancer;

public interface LoadBalancer<T> {

    T balance();

}