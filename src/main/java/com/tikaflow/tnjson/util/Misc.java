package com.tikaflow.tnjson.util;

public class Misc {
    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isAlphaNum(char c) {
        return isDigit(c) || isAlpha(c);
    }

    public static boolean isHex(char c) {
        return isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    public static boolean isNotHex(char c) {
        return !isHex(c);
    }

    public static char lastNotOf(String str, char c) {
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) != c) {
                return str.charAt(i);
            }
        }
        return '\0';
    }
}
