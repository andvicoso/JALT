package org.jalt.util;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {

	private static final String DEFAULT_DATE_FORMAT_PATTERN = "dd/MM/yy";
	private static final String DEFAULT_DATE_TIME_FORMAT_PATTERN = "dd/MM/yy HH:mm:ss";
	public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(
			DEFAULT_DATE_FORMAT_PATTERN);
	public static final DateFormat DEFAULT_DATE_TIME_FORMAT = new SimpleDateFormat(
			DEFAULT_DATE_TIME_FORMAT_PATTERN);
	public static final DateFormat FILE_DF = new SimpleDateFormat("ddMMyy-HH_mm_ss");
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

	public static String now() {
		return DEFAULT_DATE_TIME_FORMAT.format(new Date());
	}

	public static String toFileTimeString(long millis) {
		return FILE_DF.format(new Date(millis));
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
