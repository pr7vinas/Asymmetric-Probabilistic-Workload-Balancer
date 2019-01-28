# Asymmetric Probabilistic Workload Balancer

**An asymmetric probabilistic workload balancer.**

Helps a developer to execute different code paths in a probabilistic way. 

A developer can define multiple execution paths, which in this library is called *a workload*, and bind those in one *balancer* instance.
 
Each time this balancer is invoked, it will consider the execution probability of all workloads and only executes them as 
many times as their probability percentage dictates so.  

**Meaning**
> given a balancer with two workloads, the first called A with an execution probability of 90% and B with an execution probability of 10%, 
each time balancer is invoked, approximately 90% of the time A will be executed and approximately 10% of the time B will be executed.


*Please, leave a comment or drop me an email with any issues/reports.*

## How To Use

> Balancing with 35 | 65 distribution
```java
  final String workloadOutput = new AsymmetricLoadBalancer<String>()
    .add(new AsymmetricWorkload<>(35, () -> "A"))
    .fill(new AsymmetricWorkload<>(65, () -> "B"))
    .balance();
```
> Balancing with 90 | 10 distribution using *fill*
```java
  final String workloadOutput = new AsymmetricLoadBalancer<String>()
    .add(new AsymmetricWorkload<>(90, () -> "A"))
    .fill(new AsymmetricWorkload<>(() -> "B"))
    .balance();
```
## How It Works

For the moment, this library is making use of `ThreadLocalRandom` to generate a number from `0` to `100`. 
Its output will fall into some workload's probability range and such workload will be executed.

Which means that, the quality of the distribution depends on the `ThreadLocalRandom` effectiveness.

## Motivation

Devops can be very expensive and a talented devops team can find themselves completely overloaded with
requests from different teams. 

This library helps to reduce this burden by enabling developers to progressively change code behaviour without
devops intervention. 

A developer can with this library, change a configuration property and reconfigure its workloads probability.

In practice this could mean experimenting gradually a new query to the DB, gradually transit from legacy to new code, 
gradually test a group of new features ... 
 

## Author

Author:: Vinicius Alvarenga ( <a href="mailto:vini@codebest.io">vini@codebest.io</a> )

## Licence

The MIT License (MIT)

Copyright (c) 2018 Diogo Neves

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
