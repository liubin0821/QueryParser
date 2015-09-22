package com.myhexin.qparser.frequency;

import java.util.regex.Pattern;

public class FrequencyPatterns {
    static Pattern FREQUENCY_COMPARE_UP = Pattern
            .compile("^超过|大于|高于$");
    static Pattern FREQUENCY_COMPARE_LOW = Pattern
            .compile("^低于|小于$");
    static Pattern FREQUENCY_UP = Pattern
            .compile("^以上|之上$");
    static Pattern FREQUENCY_LOW = Pattern
            .compile("^以下|之下$");
    static Pattern FREQUENCY_IGNORE = Pattern
            .compile("^涨停|跌停|涨跌停?$");
    
    public static void main(String... args) {
        System.out.println(FrequencyPatterns.FREQUENCY_COMPARE_UP.matcher("3日")
                .matches());
    }
    
}
