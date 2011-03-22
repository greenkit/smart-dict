package com.greenkit.smart.utils;

public class TextUtils {

    private TextUtils() {
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
}
