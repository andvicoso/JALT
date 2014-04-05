package org.emast.model.model.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author andvicoso
 */
public class ERGGridModel extends GridModel implements ERG {

    private Expression goal;
    private Expression preservationGoal;
    private PropositionFunction pf;
    private Set<Proposition> propositions;

    public ERGGridModel(final int pRows, final int pCols) {
        super(pRows, pCols);
        goal = new Expression();
        preservationGoal = new Expression();
        propositions = new HashSet<Proposition>(0);
        pf = new PropositionFunction();
    }

    public ERGGridModel(Expression goal, Expression preservationGoal,
            PropositionFunction pf, Set<Proposition> propositions, int rows, int cols,
            TransitionFunction transitionFunction, RewardFunction rewardFunction,
            Collection<State> states, Collection<Action> actions, int agents) {
        super(rows, cols, transitionFunction, rewardFunction, states, actions, agents);
        this.goal = goal;
        this.preservationGoal = preservationGoal;
        this.pf = pf;
        this.propositions = propositions;
    }

    @Override
    public ERGGridModel copy() {
        return new ERGGridModel(goal, preservationGoal, pf, propositions, getRows(), getCols(),
                getTransitionFunction(), getRewardFunction(), getStates(), getActions(), getAgents());
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
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("\nPropositions: ").append(getPropositions());
        sb.append("\nProposition function: ").append(getPropositionFunction());
        sb.append("\nFinal goal: ").append(getGoal());
        sb.append("\nPreservation goal: ").append(getPreservationGoal());

        return sb.toString();
    }
}
