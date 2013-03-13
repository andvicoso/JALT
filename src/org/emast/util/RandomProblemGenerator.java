package org.emast.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;

/**
 *
 * @author Anderson
 */
public class RandomProblemGenerator {

    public static final String DEFAULT_SUFFIX = "problem.emast";
    public static final String DEFAULT_DIR = "problems";
    private final ProblemFactory factory;
    private static final List<Command> COMMANDS = createCommands();

    public RandomProblemGenerator(ProblemFactory pFactory) {
        factory = pFactory;
    }

    public Problem run() {
        Scanner reader = new Scanner(System.in);
        Problem p = null;

        out:
        do {
            if (p != null) {
                System.out.println("Problem: " + p.toString());
            }

            System.out.print("console: ");
            String cmds = reader.nextLine().toLowerCase();

            for (int i = 0; i < cmds.length(); i++) {
                String c = cmds.charAt(i) + "";

                if (c.equals("r")) {
                    break out;
                } else if (c.equals("s") && p != null) {
                    save(p);
                } else if (c.equals("h")) {
                    System.out.println(printMenu());
                } else if (c.equals("l")) {
                    p = getLastExecuted();
                } else if (c.equals("n")) {
                    p = factory.create();
                } else if (c.equals("q")) {
                    p = null;
                    break out;
                }
            }
        } while (true);

        return p;
    }

    private String printMenu() {
        StringBuilder s = new StringBuilder();
        s.append("Menu:\n");

        for (Command command : COMMANDS) {
            s.append(String.format("%1$s: %2$s - %3$s\n", command.shortcut, command.name, command.description));
        }

        return s.toString();
    }

    private boolean save(Problem p) {
        String modelName = p.getModel().getClass().getSimpleName();
        String dir = DEFAULT_DIR + File.separator + modelName + File.separator;
        String filename = dir + DEFAULT_SUFFIX;

        return FileUtils.toFile(p, filename, true);
    }

    private Problem getLastExecuted() {
        File last = getLastModified(DEFAULT_DIR + File.separator, 0l);
        return last != null ? FileUtils.fromFile(last.getAbsolutePath()) : null;
    }

    public File getLastModified(String path, Long lastModified) {
        File root = new File(path);
        File[] list = root.listFiles();
        File last = null;

        if (list != null) {
            for (File f : list) {
                if (f.isDirectory()) {
                    File dir_last = getLastModified(f.getAbsolutePath(), lastModified);
                    f = dir_last;
                }
                if (f.lastModified() > lastModified) {
                    last = f;
                    lastModified = last.lastModified();
                }
            }
        }

        return last;
    }

    private static Command getCommand(String shortcut) {
        for (Command command : COMMANDS) {
            if (command.shortcut.equals(shortcut)) {
                return command;
            }
        }
        return getCommand("m");
    }

    private static List<Command> createCommands() {
        List<Command> commands = new ArrayList<Command>();
        commands.add(new Command("h", "help", "Show commands"));
        commands.add(new Command("r", "run", "Execute the current environment"));
        commands.add(new Command("s", "save", "Store in a file the current environment"));
        commands.add(new Command("l", "last", "Retrieve the last executed environment"));
        commands.add(new Command("n", "new", "Create a new random environment"));
        commands.add(new Command("q", "quit", "Quit program"));

        return commands;


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
