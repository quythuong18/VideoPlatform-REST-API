package com.qt.VideoPlatformAPI.Verification;

import java.util.Random;

public class OTPGenerator {
    public static String generate(int length) {
        String digits = "1234567890";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);

        for(short i = 0; i < length; i++) {
            int index = rnd.nextInt(digits.length());
            sb.append(digits.charAt(index));
        }
        return sb.toString();
    }
}
