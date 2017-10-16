package org.jalt.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jalt.model.exception.InvalidExpressionException;
import org.jalt.model.function.PropositionFunction;
import org.jalt.model.model.ERG;
import org.jalt.model.model.MDP;
import org.jalt.model.problem.Problem;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.GridState;
import org.jalt.model.state.State;
import org.jalt.test.erg.generic.GenericERGGridModel;

public class ImageUtils {
	private static final String PNG = "PNG";
	private static int MULT = 40;

	public static BufferedImage createHeat(Problem<? extends MDP> problem, Map<State, Double> values) {
		MDP model = problem.getModel();
		int size = (int) Math.sqrt(model.getStates().size());
		BufferedImage image = createImage(size);
		Graphics2D g = (Graphics2D) image.getGraphics();
		// fill white background
		fillBackground(g, size);
		// paint the states values -> yellow
		if (values != null) {
			paintStateValues(model, values, g);
		}
		// paint propositions(good and bad)
		if (model instanceof ERG) {
			paintPropositions(model, g);
		}
		// initial states -> green
		paintStates(problem.getInitialStates().values(), g, Color.GREEN);
		// draw grid lines
		drawLines(g, size);

		return image;
	}

	private static void paintStateValues(MDP model, Map<State, Double> values, Graphics2D g) {
		Double max = -Double.MAX_VALUE;
		for (State state : values.keySet()) {
			Double v = values.get(state);
			if (v != null && v > max)
				max = v;
		}

		for (State state : values.keySet()) {
			if (state instanceof GridState) {
				GridState s = (GridState) state;
				Double v = values.get(state);

				if (v != null && v > 0) {
					Color c = new Color(255, 255, 255 - (int) (v * 255 / max));

					paintState(g, s.getCol(), s.getRow(), c);
				}
			}
		}
	}

	public static BufferedImage create(Problem<? extends MDP> problem, Policy pi) {
		MDP model = problem.getModel();
		int size = (int) Math.sqrt(model.getStates().size());
		BufferedImage image = createImage(size);
		Graphics2D g = (Graphics2D) image.getGraphics();
		// fill white background
		fillBackground(g, size);
		// paint propositions(good and bad)
		if (model instanceof ERG) {
			paintPropositions(model, g);
		}
		// paint the plans associated to the policy received -> cyan
		if (pi != null && !pi.isEmpty()) {
			paintPlans(problem, pi, model, g);
		}
		// initial states -> green
		paintStates(problem.getInitialStates().values(), g, Color.GREEN);
		// draw grid lines
		drawLines(g, size);

		return image;
	}

	private static void paintPropositions(MDP model, Graphics2D g) {
		ERG erg = (ERG) model;
		Set<State> badStates = new HashSet<State>();
		Set<State> goodStates = new HashSet<State>();
		PropositionFunction pf = erg.getPropositionFunction();

		for (Proposition prop : erg.getPropositions()) {
			if (GenericERGGridModel.isBadProp(prop))
				badStates.addAll(pf.getStatesWithProposition(prop));
			else
				goodStates.addAll(pf.getStatesWithProposition(prop));
		}

		// bad states -> red
		paintStates(badStates, g, Color.RED);

		// good states -> blue
		paintStates(goodStates, g, Color.BLUE);

		try {
			Collection<State> goalStates = erg.getPropositionFunction().intension(erg.getStates(),
					erg.getPropositions(), erg.getGoal());
			// goal states -> orange
			paintStates(goalStates, g, Color.MAGENTA);
		} catch (InvalidExpressionException e) {
		}
	}

	private static void paintPlans(Problem<? extends MDP> problem, Policy pi, MDP model,
			Graphics2D g) {
		Set<State> ps = new HashSet<State>();
		for (State init : problem.getInitialStates().values()) {
			ps.addAll(PolicyUtils.getPlanStates(model, pi, init));
		}
		paintStates(ps, g, Color.CYAN);
	}

	public static BufferedImage create(Problem<? extends MDP> problem) {
		return create(problem, null);
	}

	private static BufferedImage createImage(int size) {
		BufferedImage image = new BufferedImage(size * MULT, size * MULT,
				BufferedImage.TYPE_INT_ARGB);
		return image;
	}

	private static void drawLines(Graphics2D g, int size) {
		g.setColor(Color.BLACK);
		// draw grid lines
		for (int i = 0; i < size * MULT; i += MULT) {
			g.drawLine(0, i, size * MULT, i);
		}
		// draw grid columns
		for (int i = 0; i < size * MULT; i += MULT) {
			g.drawLine(i, 0, i, size * MULT);
		}
	}

	private static void paintStates(Collection<State> states, Graphics2D g, Color color) {
		for (State state : states) {
			if (state instanceof GridState) {
				GridState s = (GridState) state;
				paintState(g, s.getCol(), s.getRow(), color);
			}
		}
	}

	public static void view(BufferedImage img) {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JLabel(new ImageIcon(img)), BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

	public static void save(BufferedImage img, String file) {
		try {
			ImageIO.write(img, PNG, new File(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void paintState(Graphics2D g, int x, int y, Color color) {
		g.setColor(color);
		g.fillRect(x * MULT, y * MULT, MULT, MULT);
		g.setColor(Color.BLACK);
	}

	private static void fillBackground(Graphics2D g, int size) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size * MULT, size * MULT);
	}
}
