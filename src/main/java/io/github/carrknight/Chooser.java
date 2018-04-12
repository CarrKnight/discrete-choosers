package io.github.carrknight;


/**
 * The main interface of the library: the chooser is basically the algorithm that picks an option of type T from a set of options.
 * It is designed as something that makes decisions repeatedly, ideally in "rounds" or some other discrete time step. <br>
 * Both its two main methods **updateAndChoose** and **skipRoundAndChoose** involve getting some information or assuming some time has passed
 * and then making a decision.
 *
 * @param <O> the class of the options available (say, FishingSpot if we are modelling a fisher deciding where to go next);
 * @param <R> the class describing the reward object (say, FishCaught if we are modelling the fisher judging the spot they have just been to)
 * @param <C> the class describing the context in which the decision or reward was observed (say, Weather for fisher)
 */
public interface Chooser<O,R,C> {


    /**
     * the main method of the chooser. It does two things at once:
     * * Receives new information given a previous action (and possibly additional information from observing other choosers)
     * * Picks a new O for next step and return it
     *
     * @param observation the reward and action taken last
     * @param additionalObservations additional action-rewards observed (by imitation or whatever)
     * @return O chosen to play next
     */
    public O updateAndChoose(Observation<O,R,C> observation,
                             Observation<O,R,C>... additionalObservations);


    /**
     * this is a simple getter that returns what the last choice made was. *this does not update choices*
     * @return
     */
    public O getLastChoice();


    /**
     * this method is similar to updateAndChoose but should be used whenever the action suggested the previous step was not taken
     * (say, because of some malfunction or because you didn't want to follow through on the suggestion).
     * It does the following:
     *     * may act on additional information
     * @param additionalObservations additional action-rewards observed (by imitation or whatever)
     * @return T chosen to play next
     */
    public O skipRoundAndChoose(Observation<O,R,C>... additionalObservations);





}
