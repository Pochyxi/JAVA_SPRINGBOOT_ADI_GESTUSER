package com.adi.gestuser.utils;

import java.util.Random;

public class FirstPasswordGenerator {

   private static final String[] MAIUSCLETTERS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static final String[] MINUSCLETTERS = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    private static final int[] NUMBERS = {1,2,3,4,5,6,7,8,9};

    private static final String[] SPECIALCHARS = {"!","@","#","$","%","^","&","+","="};

    private static final String BASEPASS = "Password";


    /**
     * Generate a random password
     * @return a random password
     */
    public static String generatePass(){

        Random rand = new Random();

        int maiuscLetterRandomIndex = rand.nextInt(MAIUSCLETTERS.length);

        int minuscLetterRandomIndex = rand.nextInt(MINUSCLETTERS.length);

        int numbersRandomIndex = rand.nextInt(NUMBERS.length);

        int specialCharRandomIndex = rand.nextInt(SPECIALCHARS.length);


        return BASEPASS + MAIUSCLETTERS[maiuscLetterRandomIndex] + MINUSCLETTERS[minuscLetterRandomIndex]
                + NUMBERS[numbersRandomIndex] + SPECIALCHARS[specialCharRandomIndex];
    }



}
