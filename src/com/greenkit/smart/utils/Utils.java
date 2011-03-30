package com.greenkit.smart.utils;

import java.io.File;

public class Utils {

    private Utils() {
    }

    /**
     * Calculate how many substring in the string orderly.
     * 
     * @return how many substring
     */
    public static int contain(String string, String substring) {
        if (isEmpty(string) || isEmpty(substring)) {
            return 0;
        } else {
            int offset = 0;
            int count = 0;
            int length = substring.length();
            while ((offset = string.indexOf(substring, offset) + length) >= length) {
                count++;
            }

            return count;
        }
    }

    public static String[] split(String string, String divider) {
        int size = contain(string, divider);
        if (size == 0) {
            return null;
        } else {
            int a = 0;
            int b = 0;
            int i = 0;
            int length = divider.length();
            String[] result = new String[size];
            while ((b = string.indexOf(divider, a)) >= 0) {
                result[i++] = string.substring(a, b);
                a = b + length;
            }

            return result;
        }
    }

    public static boolean isEmpty(String string) {
        if (string == null || string.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * The path of directory must be included the suffix '/' If some path don't
     * have the '/' character, we add it.
     */
    public static String formatDirectory(String dir) {
        if (dir.endsWith(File.separator)) {
            return dir;
        } else {
            return dir + File.separator;
        }
    }

    /**
     * Get the parent path according to the path string.
     */
    public static String getParentPath(String path) {
        if (!path.endsWith(File.separator)) {
            return path.substring(0, path.lastIndexOf(File.separator) + 1);
        } else {
            return path.substring(0, path.lastIndexOf(File.separator, path.length() - 2) + 1);
        }
    }
}
