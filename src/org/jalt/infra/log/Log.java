package org.jalt.infra.log;

import java.util.logging.Logger;

/**
 *
 * @author andvicoso
 */
public class Log {

    private static final Logger logger = Logger.getLogger("org.jalt.logger");

//    private Log() {
//        try {
//            // This block configure the logger with handler and formatter
//            FileHandler fh = new FileHandler("c:\\MyLogFile.log", true);
//            logger.addHandler(fh);
//            logger.setLevel(Level.ALL);
//            SimpleFormatter formatter = new SimpleFormatter();
//            fh.setFormatter(formatter);
//        } catch (Exception ex) {
//        }
//    }
    public static void info(Object pMsg) {
        info(pMsg.toString());
    }

    public static void info(String pMsg) {
        //logger.info(pMsg);
        System.out.println(pMsg);
    }

    public static void debug(Object pMsg) {
        debug(pMsg.toString());
    }

    public static void error(Object pMsg) {
        error(pMsg.toString());
    }

    public static void debug(String pMsg) {
        logger.fine(pMsg);
    }

    public static void error(String pMsg) {
        logger.severe(pMsg);
    }
}
