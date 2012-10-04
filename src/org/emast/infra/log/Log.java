package org.emast.infra.log;

import java.util.logging.Logger;

/**
 *
 * @author Anderson
 */
public class Log {

    private static final Logger logger = Logger.getLogger("org.emast.logger");

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
    public static void info(String pMsg) {
        //logger.info(pMsg);
        System.out.println(pMsg);
    }

    public static void debug(String pMsg) {
        logger.fine(pMsg);
    }

    public static void error(String pMsg) {
        logger.severe(pMsg);
    }
}
