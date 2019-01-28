package io.codebest.balancer


import spock.lang.Specification

import java.util.function.Supplier

class LoadBalancerTest extends Specification {

    def balancer
    def supplier = Mock(Supplier)

    def setup() {
        balancer = new AsymmetricLoadBalancer<String>()
    }

    def "Workload is null"() {

        when: "add workload to balancer"
        def workloadResult = balancer.add(null)

        then: "check that the call returned an exception"
        def e = thrown(IllegalArgumentException)
    }


    def "Supplier inside workload is null"() {

        when: "add workload to balancer"
        def workloadResult = balancer.add(new AsymmetricWorkload<>(5, null))

        then: "check that the call returned an exception"
        def e = thrown(IllegalArgumentException)
    }

    def "Probability is higher than 100"() {

        when: "add workload to balancer"
        def workloadResult = balancer.add(new AsymmetricWorkload<>(102, supplier))

        then: "check that the call returned an exception"
        def e = thrown(IllegalArgumentException)
    }

    def "Probability overflow"() {
        when: "add workload to balancer"
        def workloadResult = balancer.add(new AsymmetricWorkload<>(10, supplier)).add(new AsymmetricWorkload<>(95, supplier))

        then: "check that the call returned an exception"
        def e = thrown(IllegalArgumentException)
    }

    def "Probability 99"() {

        when: "add workload to balancer"
        def workloadResult = balancer
                .add(new AsymmetricWorkload<>(99, { -> "A" }))
                .fill(new AsymmetricWorkload<>({ -> "B" }))
                .balance()

        then: "check that the call returned an exception"
        workloadResult == "A"
    }

    def "Probability 100"() {

        when: "add workload to balancer"
        def workloadResult = balancer
                .add(new AsymmetricWorkload<>(100, { -> "A" }))
                .fill(new AsymmetricWorkload<>({ -> "B" }))
                .balance()

        then: "check that the call returned an exception"
        workloadResult == "A"
    }

    def "Probability Fill is null"() {

        when: "fill workload to balancer"
        def workloadResult = balancer.fill(new AsymmetricWorkload<>(null))

        then: "check that the call returned an exception"
        def e = thrown(IllegalArgumentException)
    }

    def "Probability Fill"() {

        when: "fill workload to balancer"
        def workloadResult = balancer.add(new AsymmetricWorkload<>(10, supplier)).fill(new AsymmetricWorkload<>(supplier))

        then: "check that the call returned an exception"
        workloadResult.accumulativePercentage == 100
    }

    def "Probability not Fill"() {

        when: "fill workload to balancer"
        def workloadResult = balancer.add(new AsymmetricWorkload<>(10, supplier))

        then: "check that the call returned an exception"
        workloadResult.accumulativePercentage == 10
    }

    def "Workload's probability is not 100%"() {

        when: "fill workload to balancer"
        def workloadResult = balancer.add(new AsymmetricWorkload<>(10, supplier)).balance()

        then: "check that the call returned an exception"
        def e = thrown(IllegalStateException)
    }

    def "Workload's run"() {

        when: "fill workload to balancer"
        def workloadResult = balancer
                .add(new AsymmetricWorkload<>(50, { -> "A" }))
                .fill(new AsymmetricWorkload<>({ -> "B" }))
                .balance()

        then: "check that the call returned an exception"
        workloadResult == "A" || "B"
    }

    def "Turn off workload should return workload that is turn on"() {

        when: "workload priority is 0"
        def workloadResult = balancer
                .add(new AsymmetricWorkload<>(0, { -> "A" }))
                .fill(new AsymmetricWorkload<>({ -> "B" }))
                .balance()

        then: "should never call workload A"
        workloadResult == "B"
    }
}