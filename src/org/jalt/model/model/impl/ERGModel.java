package org.jalt.model.model.impl;

import java.util.Collection;
import java.util.Set;

import org.jalt.model.action.Action;
import org.jalt.model.function.PropositionFunction;
import org.jalt.model.function.reward.RewardFunction;
import org.jalt.model.function.transition.TransitionFunction;
import org.jalt.model.model.ERG;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.State;

/**
 *
 * @author andvicoso
 */
public class ERGModel extends MDPModel implements ERG {

    private Expression goal;
    private Expression preservationGoal;
    private PropositionFunction pf;
    private Set<Proposition> propositions;

    public ERGModel() {
    }

    public ERGModel(Expression goal, Expression preservationGoal, PropositionFunction pf,
            Set<Proposition> propositions, TransitionFunction transitionFunction, RewardFunction rewardFunction,
            Collection<State> states, Collection<Action> actions, int agents) {
        super(transitionFunction, rewardFunction, states, actions, agents);
        this.goal = goal;
        this.preservationGoal = preservationGoal;
        this.pf = pf;
        this.propositions = propositions;
    }

    @Override
    public ERGModel copy() {
        return new ERGModel(goal, preservationGoal, pf, propositions, getTransitionFunction(),
                getRewardFunction(), getStates(), getActions(), getAgents());
    }

    @Override
    public PropositionFunction getPropositionFunction() {
        return pf;
    }

    @Override
    public void setPropositionFunction(PropositionFunction pPf) {
        pf = pPf;
    }

    @Override
    public Expression getGoal() {
        return goal;
    }

    @Override
    public void setGoal(Expression goal) {
        this.goal = goal;
    }

    @Override
    public Expression getPreservationGoal() {
        return preservationGoal;
    }

    @Override
    public void setPreservationGoal(Expression preservationGoal) {
        this.preservationGoal = preservationGoal;
    }

    @Override
    public Set<Proposition> getPropositions() {
        return propositions;
    }

    @Override
    public void setPropositions(Set<Proposition> propositions) {
        this.propositions = propositions;
    }

    @Override
    public String toString() {
        //GridPrinter gp = new GridPrinter();
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("\nPropositions: ").append(getPropositions());
        sb.append("\nFinal goal: ").append(getGoal());
        sb.append("\nPreservation goal: ").append(getPreservationGoal());
        //TODO: sb.append("\nProposition function: ").append("\n").append(gp.print(getPropositionFunction(), this));

        return sb.toString();
    }
}
