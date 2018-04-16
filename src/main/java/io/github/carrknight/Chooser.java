package io.github.carrknight;


import org.jetbrains.annotations.Nullable;

/**
 * The main interface of the library: the chooser is basically the algorithm that picks an option of type T from a set of options.
 * It is designed as something that makes decisions repeatedly, ideally in "rounds" or some other discrete time step. <br>
 * Both its main method **updateAndChoose** involve getting some information or assuming some time has passed
 * and then making a decision.
 *
 * @param <O> the class of the options available (say, FishingSpot if we are modelling a fisher deciding where to go next);
 * @param <R> the class describing the experiment result (say, FishCaught if we are modelling the fisher judging the spot they have just been to)
 * @param <C> the class describing the context in which the decision or reward was observed (say, Weather for fisher)
 */
public interface Chooser<O,R,C> {


    /**
     * the main method of the chooser. It does two things at once:
     * * Receives new information given a previous action (and possibly additional information from observing others)
     * * Picks a new O for next step and return it
     *
     * @param observation the reward and action taken last (can be null if experiment wasn't valid)
     * @param additionalObservations additional action-rewards observed (by imitation or whatever)
     * @return O chosen to play next
     */
    O updateAndChoose(@Nullable Observation<O,R,C> observation,
                      Observation<O,R,C>... additionalObservations);


    /**
     * this is a simple getter that returns what the last choice made was. *this does not update choices*
     * @return
     */
    public O getLastChoice();


}
