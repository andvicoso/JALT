package org.emast.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.emast.model.problem.Problem;

/**
 *
 * @author Anderson
 */
public class FileUtils {

    public static boolean toFile(Problem pProblem, String pFilename) {
        boolean ret = true;
        FileOutputStream fos = null;

        int lastSep = pFilename.lastIndexOf(File.separator);
        if (lastSep > 0) {
            String dirPath = pFilename.substring(0, lastSep);
            File dir = new File(dirPath);
            dir.mkdirs();
        }

        try {
            fos = new FileOutputStream(pFilename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(pProblem);
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
        FileInputStream fos = null;
        try {
            fos = new FileInputStream(pFilename);
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
}
