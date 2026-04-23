package com.tp.main.Utils;

public class Base62 {

	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = CHARACTERS.length();

    /**
     * Converts a long numeric ID (from your KGS) into a Base62 String.
     */
    public static String encode(long input) {
        StringBuilder sb = new StringBuilder();
        if (input == 0) return String.valueOf(CHARACTERS.charAt(0));
        
        while (input > 0) {
            sb.append(CHARACTERS.charAt((int) (input % BASE)));
            input /= BASE;
        }
        return sb.reverse().toString();
    }

    /**
     * Converts a Base62 string back into a numeric ID (useful for debugging).
     */
    public static long decode(String input) {
        long res = 0;
        for (char c : input.toCharArray()) {
            res = res * BASE + CHARACTERS.indexOf(c);
        }
        return res;
    }
}
