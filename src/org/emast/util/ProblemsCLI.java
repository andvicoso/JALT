package org.emast.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;

/**
 * 
 * @author Anderson
 */
public class ProblemsCLI {

	public static final String DEFAULT_SUFFIX = "problem.emast";
	public static final String DEFAULT_DIR = "problems";
	private final ProblemFactory factory;
	private static final List<Command> COMMANDS = createCommands();

	public ProblemsCLI(ProblemFactory pFactory) {
		factory = pFactory;
	}

	public Problem run() {
		Scanner reader = new Scanner(System.in);
		Problem p = null;

		out: do {
			if (p != null) {
				print("\nProblem: " + p.toString());
			}

			print("console: ");
			String cmd = reader.nextLine().toLowerCase();

			for (int i = 0; i < cmd.length(); i++) {
				String c = cmd.charAt(i) + "";

				if (c.equals("r")) {
					if (p != null)
						break out;
					else
						print("No problem selected!");
				} else if (c.equals("s") && p != null) {
					save(p);
				} else if (c.equals("h")) {
					print("\n" + printMenu());
				} else if (c.equals("l")) {
					p = getLastExecuted();
				} else if (c.equals("n")) {
					p = factory.create();
				} else if (c.equals("q")) {
					p = null;
					break out;
				} else if (Character.isDigit(c.charAt(0))) {
					int filePrefix = Integer.parseInt(cmd);
					p = getFileByPrefix(filePrefix);
					i += cmd.length();
				}
			}
		} while (true);

		reader.close();

		return p;
	}

	private String printMenu() {
		StringBuilder s = new StringBuilder();
		s.append("Menu:\n");

		for (Command command : COMMANDS) {
			s.append(String.format("%1$s: %2$s - %3$s\n", command.shortcut, command.name,
					command.description));
		}

		return s.toString();
	}

	private boolean save(Problem p) {
		String modelName = p.getModel().getClass().getSimpleName();
		String dir = DEFAULT_DIR + File.separator + modelName + File.separator;
		String filename = dir + DEFAULT_SUFFIX;

		return FileUtils.toFile(p, filename, true);
	}

	public static List<Problem<ERG>> getAllFromDir(String dir) {
		List<Problem<ERG>> ps = new ArrayList<>();
		List<File> files = FileUtils.getAllFromDir(DEFAULT_DIR + File.separator + dir);
		Collections.sort(files);
		for (File file : files) {
			ps.add(FileUtils.fromFile(file.getAbsolutePath()));
		}

		return ps;
	}

	private Problem getLastExecuted() {
		File last = FileUtils.getLastModified(DEFAULT_DIR + File.separator, "emast", 0l);
		if (last != null)
			print(last.toString());
		return last != null ? FileUtils.fromFile(last.getAbsolutePath()) : null;
	}

	// private static Command getCommand(String shortcut) {
	// for (Command command : COMMANDS) {
	// if (command.shortcut.equals(shortcut)) {
	// return command;
	// }
	// }
	// return getCommand("m");
	// }
	private static List<Command> createCommands() {
		List<Command> commands = new ArrayList<Command>();
		commands.add(new Command("h", "help", "Show commands"));
		commands.add(new Command("r", "run", "Execute the current environment"));
		commands.add(new Command("s", "save", "Store in a file the current environment"));
		commands.add(new Command("l", "last", "Retrieve the last executed environment"));
		commands.add(new Command("n", "new", "Create a new random environment"));
		commands.add(new Command("p", "list", "List problem files"));
		commands.add(new Command("NNN", "select", "Select problem file with prefix NNN"));
		commands.add(new Command("q", "quit", "Quit program"));

		return commands;
	}

	private Problem getFileByPrefix(int filePrefix) {
		File file = FileUtils.getFromPreffix(DEFAULT_DIR, filePrefix);
		if (file != null)
			print(file.toString());
		return file != null ? FileUtils.fromFile(file.getAbsolutePath()) : null;
	}

	private void print(String str) {
		System.out.println(str);
	}

	private static class Command {

		private String shortcut;
		private String name;
		private String description;

		public Command(String shortcut, String name, String description) {
			this.shortcut = shortcut;
			this.name = name;
			this.description = description;
		}

		public Command(String shortcut, String name) {
			this(shortcut, name, "");
		}
	}
}
