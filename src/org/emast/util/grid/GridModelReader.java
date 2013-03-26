package org.emast.util.grid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunctionProposition;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;
import static org.emast.util.DefaultTestProperties.*;

/**
 *
 * @author Anderson
 */
public class GridModelReader {

    private final String filename;

    public GridModelReader(String filename) {
        this.filename = filename;
    }

    public Problem read() throws IOException {
        PropositionFunction pf = new PropositionFunction();
        Set<Proposition> props = new HashSet<Proposition>();
        Set<Proposition> badProps = new HashSet<Proposition>();
        Set<State> finalStates = new HashSet<State>();
        Map<Integer, State> initialStates = new HashMap<Integer, State>();
        Scanner s = new Scanner(new BufferedReader(new FileReader(filename)));
        String[] line = s.nextLine().split("\\|");
        String size = line[line.length - 1];
        int cols = Integer.parseInt(size.trim()) + 1;
        int rows = 0;
        while (s.hasNext()) {
            line = s.nextLine().split("\\|");

            for (int i = 2; i < line.length; i++) {
                String data = line[i];
                String stName = GridUtils.getGridStateName(rows, i - 2);
                State st = new State(stName);

                if (!data.trim().isEmpty()) {
                    if (data.contains(FINAL_GOAL)) {
                        finalStates.add(st);
                    }

                    String[] dts = data.split(" ");
                    Set<Proposition> propsState = new HashSet<Proposition>();

                    for (int j = 0; j < dts.length; j++) {
                        String dt = dts[j];
                        if (!dt.trim().isEmpty()) {
                            try {
                                int agent = Integer.parseInt(dt);
                                initialStates.put(agent, st);
                            } catch (NumberFormatException e) {
                                char first = dt.charAt(0);
                                Proposition p = new Proposition(dt);
                                if (Character.isLetter(first) && Character.isUpperCase(first)) {
                                    badProps.add(p);
                                }
                                propsState.add(p);
                                if (!p.getName().equals(FINAL_GOAL)) {
                                    props.add(p);
                                }
                            }
                        }
                    }

                    if (!propsState.isEmpty()) {
                        pf.add(st, propsState);
                    }
                }
            }
            rows++;
        }

        s.close();

        ERGGridModel model = new ERGGridModel(rows, cols);
        model.setAgents(initialStates.size());
        model.setPropositionFunction(pf);
        model.setPropositions(props);
        model.setRewardFunction(new RewardFunctionProposition(model, CollectionsUtils.createMap(badProps, BAD_REWARD), OTHERWISE));
        model.setGoal(new Expression(FINAL_GOAL));

        return new Problem(model, initialStates, finalStates);
    }
}
