package org.emast.model.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.emast.model.BadReward;
import org.emast.model.BadRewarder;
import org.emast.model.action.Action;
import org.emast.model.function.BadRewardFunction;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.RewardFunction;
import org.emast.model.function.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class ERGModel extends MDPModel implements ERG, BadRewarder {

    private Expression goal;
    private Expression preservationGoal;
    private PropositionFunction pf;
    private Set<Proposition> propositions;
    private Collection<BadReward> badRewards;
    private double otherwiseValue = -1d;

    public ERGModel() {
    }

    public ERGModel(Expression goal, Expression preservationGoal, PropositionFunction pf,
            Set<Proposition> propositions, Collection<BadReward> badRewards,
            TransitionFunction transitionFunction, RewardFunction rewardFunction,
            Collection<State> states, Collection<Action> actions, int agents) {
        super(transitionFunction, rewardFunction, states, actions, agents);
        this.goal = goal;
        this.preservationGoal = preservationGoal;
        this.pf = pf;
        this.propositions = propositions;
        this.badRewards = badRewards;
    }

    @Override
    public ERGModel copy() {
        return new ERGModel(goal, preservationGoal, pf, propositions, badRewards, getTransitionFunction(),
                getRewardFunction(), getStates(), getActions(), getAgents());
    }

    @Override
    public Collection<BadReward> getBadRewards() {
        return badRewards;
    }

    @Override
    public void setBadRewards(Collection<BadReward> badRewards) {
        this.badRewards = badRewards;
    }

    @Override
    public RewardFunction getRewardFunction() {
        return new BadRewardFunction(this);
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
    public void setOtherwiseValue(double pOtherwiseValue) {
        otherwiseValue = pOtherwiseValue;
    }

    @Override
    public double getOtherwiseValue() {
        return otherwiseValue;
    }

    @Override
    public Collection<Proposition> getBadRewardProps() {
        Collection<Proposition> badProps = new ArrayList<Proposition>();
        for (BadReward br : badRewards) {
            badProps.add(br.getBadRewardProp());
        }

        return badProps;
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