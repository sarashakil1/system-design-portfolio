package com.portfolio.shortener;

public class Base62 {
    private static final String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int base = alphabet.length();

    private  Base62(){}

    public static String encode(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("id must be non-negative: " + id);
        }
        if (id == 0) {
            return String.valueOf(alphabet.charAt(0));   // "0"
        }

        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            int position = (int) (id % base);   // last base-62 digit, 0..61
            sb.append(alphabet.charAt(position));
            id = id / base;                     // drop that digit
        }
        return sb.reverse().toString();         // digits came out last-first
    }
    public static long decode(String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("code must not be empty");
        }

        long total = 0;
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            int position = alphabet.indexOf(c);          // '0'→0 … 'z'→35 … 'Z'→61
            if (position < 0) {
                throw new IllegalArgumentException("invalid character '" + c + "' in code: " + code);
            }
            try {
                total = Math.addExact(Math.multiplyExact(total, base), position);   // total * 62 + position
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("code too large: " + code, e);
            }
        }
        return total;
    }
}
