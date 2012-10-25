package org.emast.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.emast.infra.log.Log;
import org.emast.model.problem.Problem;

/**
 *
 * @author Anderson
 */
public class FileUtils {

    public static boolean toFile(Problem pProblem, String pFilename, boolean pUnique) {
        boolean ret = true;
        String path = pFilename;
        String filename = getFilename(path);
        String dirPath = getDir(path);
        OutputStream fos = null;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file;
        do {
            file = new File(path);
            int uniqueId = Math.abs((int) System.currentTimeMillis()) % 100;
            path = dirPath + File.separator + uniqueId + "_" + filename;
        } while (pUnique && file.exists());

        try {
            fos = new BufferedOutputStream(new FileOutputStream(file));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(pProblem);
            Log.info("Problem saved to file: " + file);
        } catch (IOException ex) {
            ret = false;
            ex.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                ret = false;
                ex.printStackTrace();
            }
        }

        return ret;
    }

    public static Problem fromFile(String pFilename) {
        InputStream fos = null;
        try {
            fos = new BufferedInputStream(new FileInputStream(pFilename));
            ObjectInputStream oos = new ObjectInputStream(fos);
            return (Problem) oos.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    private static String getDir(String filename) {
        String dirPath = "";
        int lastSep = filename.lastIndexOf(File.separator);
        if (lastSep > 0) {
            dirPath = filename.substring(0, lastSep);
        }
        return dirPath;
    }

    private static String getFilename(String path) {
        String filename = "";
        int lastSep = path.lastIndexOf(File.separator);
        if (lastSep > 0) {
            filename = path.substring(lastSep + 1);
        }
        return filename;
    }
}
