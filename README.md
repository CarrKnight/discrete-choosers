# Discrete Choosers

This is a small java library that contains simple algorithms for discrete choices under agent-based models. It's basically a collection of the following:

1. Bandit Algorithms
2. Online/Recursive Regressions
3. Evolutionary methods

The methods are described in a R&R paper, of which a discussion version is available [here](http://carrknight.github.io/poseidon/algorithms.html).

In that paper I split all these methods and use one at a time but in reality if you look at the code it's not too difficult to mix and match (say, use a SOFTMAX bandit algorithm whose memory is actually managed by a Kernel regression and learns from other people's choices as well).

It is complete in the sense that all the algorithms are in but it needs a bit of work in order to make it run on NETLOGO. Once that's settled I'll post this on MAVEN as well.

