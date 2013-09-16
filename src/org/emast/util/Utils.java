package org.emast.util;

import java.io.*;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.emast.model.problem.Problem;

public class Utils {

    private static final String DEFAULT_DATE_FORMAT_PATTERN = "dd/MM/yy";
    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_FORMAT_PATTERN);
    public static final String D_MIN_D_SEC = "%d min, %d sec";
    public static final String _MS = " ms)";
    public static final String PAR = "(";

    private Utils() {
    }

    public static Object[] createColumnsNames(final int pSize) {
        final Object[] columnsNames = new Object[pSize];
        for (int i = 1; i <= pSize; i++) {
            columnsNames[i] = "" + i;
        }
        return columnsNames;
    }

    public static Method getMethod(final Class<?> pClazz, final String pName) {
        Method m;
        try {
            m = pClazz.getDeclaredMethod(pName);
        } catch (NoSuchMethodException name) {
            if (pClazz.equals(Object.class)) {
                throw new RuntimeException("Method not found in any super class.");
            }

            return getMethod(pClazz.getSuperclass(), pName);
        }
        m.setAccessible(true);

        return m;
    }

    public static String toTimeString(long millis) {
        long min = TimeUnit.MILLISECONDS.toMinutes(millis);
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        return String.format(D_MIN_D_SEC, min, sec) + PAR + millis + _MS;
    }

    public static String toFileTimeString(long millis) {
        final DateFormat df = new SimpleDateFormat("ddMMyy-HH_mm_ss");
        return df.format(new Date(millis));
    }

    public static void toFile(Problem pProblem, String pFilename) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pFilename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(pProblem);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Problem fromFile(String pFilename) {
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(pFilename);
            ObjectInputStream ois = new ObjectInputStream(fos);
            return (Problem) ois.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public static String[][] toStringTable(Object[][] objs) {
        String[][] grid = new String[objs.length][objs[0].length];

        for (int i = 0; i < objs.length; i++) {
            for (int j = 0; j < objs[0].length; j++) {
                grid[j][j] = objs[j][j].toString();
            }
        }
        return grid;
    }
}
