package io.github.carrknight.imitators;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.github.carrknight.Chooser;
import io.github.carrknight.Observation;
import io.github.carrknight.heatmaps.BeliefState;
import io.github.carrknight.heatmaps.regression.FeatureExtractor;
import io.github.carrknight.utils.DiscreteChoosersUtilities;
import io.github.carrknight.utils.RewardFunction;
import javafx.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * assumes the agent is a particle in a swarm, pulled along by its best memory so far and what its friends are doing
 * @param <O>
 * @param <R>
 * @param <C>
 */
public class ParticleSwarm<O,R,C> implements Chooser<O,R,C> {

    /**
     * Project options and context to a numerical space where particle swarm acts
     */
    private final FeatureExtractor<O,C> projectors[];

    /**
     * takes numerical features and turn them back into a choice the particle makes
     */
    private final Function<double[],O> inverseProjector;


    private double[] currentPosition;

    private double[] currentVelocity;

    /**
     * maximum weight to friend best action
     */
    final private double alpha;

    /**
     * maximum weight to current best memory
     */
    final private double beta;


    final private double inertia;

    /**
     * memory
     */
    final private BeliefState<O,R,C> memory;


    final private RewardFunction<O,R,C> reward;


    private O lastChoice;

    final private Iterable<O> optionsAvailable;

    final private SplittableRandom random;


    public ParticleSwarm(
            FeatureExtractor<O, C>[] projectors, Function<double[], O> inverseProjector,
            double alpha, double beta,
            double inertia,
            double initialMaxVelocity,
            RewardFunction<O, R, C> reward,
            O[] optionsAvailable,
            long seed){

        this(projectors,
             inverseProjector,
             alpha,beta,
             inertia,
             initialMaxVelocity,
             new MonoBelief<O, R, C>(reward),
             reward,
             optionsAvailable,
             new SplittableRandom(seed),
             null);

    }

    public ParticleSwarm(
            FeatureExtractor<O, C>[] projectors, Function<double[], O> inverseProjector,
            double alpha, double beta,
            double inertia,
            double initialMaxVelocity,
            BeliefState<O, R, C> memory,
            RewardFunction<O, R, C> reward,
            O[] optionsAvailable,
            SplittableRandom random,
            @Nullable
            C initialContext) {
        Preconditions.checkArgument(projectors.length>0, "needs at least one projector!");
        Preconditions.checkArgument(optionsAvailable.length>0, "needs options!");

        this.projectors = projectors;
        this.inverseProjector = inverseProjector;
        this.alpha = alpha;
        this.beta = beta;
        this.inertia = inertia;
        this.memory = memory;
        this.reward = reward;
        this.optionsAvailable = Lists.newArrayList(optionsAvailable);
        this.random = random;

        this.lastChoice = optionsAvailable[random.nextInt(optionsAvailable.length)];

        //if this is the first time you call this, take a second to compute the current position
        if(currentPosition==null) {
            this.currentPosition = new double[projectors.length];
            for (int i = 0; i < projectors.length; i++)
                currentPosition[i] = projectors[i].extract(
                        lastChoice,
                        initialContext
                );
        }

        //initialize velocity
        currentVelocity = new double[projectors.length];
        for (int i = 0; i < projectors.length; i++)
            currentVelocity[i] = random.nextDouble(-initialMaxVelocity,initialMaxVelocity);

    }

    /**
     * the main method of the chooser. It does two things at once:
     * * Receives new information given a previous action (and possibly additional information from observing others)
     * * Picks a new O for next step and return it
     *
     * @param observation            the reward and action taken last (can be null if experiment wasn't valid)
     * @param additionalObservations additional action-rewards observed (by imitation or whatever)
     * @return O chosen to play next
     */
    @SafeVarargs
    @Override
    public final O updateAndChoose(
            @Nullable Observation<O, R, C> observation,
            Observation<O, R, C>... additionalObservations)
    {



        if(observation != null &&
                observation.getChoiceMade() != lastChoice)
        {
            for(int i=0; i<projectors.length; i++)
                currentPosition[i] = projectors[i].extract(
                        observation.getChoiceMade(),
                        observation.getContext()
                );
        }

        final C context = observation == null ? null : observation.getContext();
        //get best memory
        memory.observe(observation);
        Pair<O,Double> bestMemory = DiscreteChoosersUtilities.getBestOption(
                optionsAvailable,
                o -> {
                    return memory.predict(o,
                                          context
                    );
                },
                random,
                Double.NEGATIVE_INFINITY
        );
        double[] memoryPosition = null;
        if(bestMemory!=null) {

            memoryPosition = new double[projectors.length];
            for(int i=0; i<projectors.length; i++)
                memoryPosition[i] = projectors[i].extract(
                        bestMemory.getKey(),
                        context
                );

        }


        //get best friend position
        Map<O,Double> observedChoices = new HashMap<>(additionalObservations.length);
        for (Observation<O, R, C> others : additionalObservations) {
            double reward = this.reward.extractUtility(
                    others.getChoiceMade(),
                    others.getResultObserved(),
                    others.getContext()
            );
            if(Double.isFinite(reward))
                observedChoices.put(others.getChoiceMade(),
                                    reward);
        }
        Pair<O,Double> bestFriend = observedChoices.size() > 0 ? DiscreteChoosersUtilities.getBestOption(
                observedChoices.keySet(),
                observedChoices::get,
                random,
                bestMemory == null ? Double.NEGATIVE_INFINITY : bestMemory.getValue()) :
                null;
        double[] friendPosition = null;
        if(bestFriend!=null ) {

            assert (bestMemory == null || bestFriend.getValue()>bestMemory.getValue());
            friendPosition = new double[projectors.length];
            for(int i=0; i<projectors.length; i++)
                friendPosition[i] = projectors[i].extract(
                        bestFriend.getKey(),
                        context
                );

        }


        //finally do PSO
        for(int dimension=0; dimension<projectors.length; dimension++)
        {
            currentVelocity[dimension] = currentVelocity[dimension] * inertia;
            if(friendPosition != null)
                currentVelocity[dimension] += alpha * random.nextDouble() *
                        (friendPosition[dimension]-currentPosition[dimension]);
            if(memoryPosition != null)
                currentVelocity[dimension] += beta * random.nextDouble() *
                        (memoryPosition[dimension]-currentPosition[dimension]);

            currentPosition[dimension] += currentVelocity[dimension];
        }

        //turn "position" into a choice proper!
        lastChoice = inverseProjector.apply(currentPosition);


        return lastChoice;


    }


    /** {@inheritDoc} */
    @Override
    public O getLastChoice() {
        return lastChoice;
    }

    /**
     * returns a defensive copy of the current position;
     */
    @VisibleForTesting
    public double[] getCurrentPosition() {
        return Arrays.copyOf(currentPosition,currentPosition.length);
    }

    /**
     * returns a defensive copy of the current velocity;
     */
    public double[] getCurrentVelocity() {
        return Arrays.copyOf(currentVelocity,currentVelocity.length);
    }
}
