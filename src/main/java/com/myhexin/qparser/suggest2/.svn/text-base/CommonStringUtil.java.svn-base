package com.myhexin.qparser.suggest2;

import java.util.ArrayList;


public class CommonStringUtil {

    public static boolean isChinese(char uchar) {
        // check if this uchar is a chinese char
        if ((uchar >= '\u4e00') && (uchar <= '\u9fa5')) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNumber(char uchar) {
        // check if this uchar is a number
        if ((uchar >= '\u0030') && (uchar <= '\u0039')) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean containAlphabet(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (isAlphabet(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAlphabet(char uchar) {
        // check if this uchar is a alphabet
        if (((uchar >= '\u0041') && (uchar <= '\u005a'))
                || ((uchar >= '\u0061') && (uchar <= '\u007a'))) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isOther(char uchar) {
        // check if this uchar is not chinese or number or alphabet
        if (isChinese(uchar) || isNumber(uchar) || isAlphabet(uchar)) {
            return false;
        } else {
            return true;
        }
    }

    public static String stringWithSpace(String ustr) {
        ArrayList<String> list = stringToList(ustr);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(" ");
            sb.append(list.get(i));
        }
        sb.append(" ");
        return sb.toString();
    }

    public static ArrayList<String> stringToList(String ustr) {
        ArrayList<String> res = new ArrayList<String>();
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < ustr.length(); i++) {
            char uchar = ustr.charAt(i);
            if (isOther(uchar)) {
                if (tmp.length() == 0) {
                    continue;
                } else {
                    res.add(tmp.toString());
                    tmp = new StringBuilder();
                }
            } else {
                if (isChinese(uchar)) {
                    if (tmp.length() > 0) {
                        res.add(tmp.toString());
                        tmp = new StringBuilder();
                    }
                    res.add(Character.toString(uchar));
                } else {
                    tmp.append(uchar);
                }
            }
        }
        if (tmp.length() > 0) {
            res.add(tmp.toString());
        }
        return res;
    }
    
    
}

