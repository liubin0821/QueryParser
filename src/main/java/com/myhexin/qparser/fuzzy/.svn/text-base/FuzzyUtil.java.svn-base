package com.myhexin.qparser.fuzzy;

public class FuzzyUtil {
    
    private static int min(int one, int two, int three) {
        int min = one;
        if (two < min) {
            min = two;
        }
        if (three < min) {
            min = three;
        }
        return min;
    }
    
    public static int ld(String str1, String str2) {
        int d[][];
        int n = str1.length();
        int m = str2.length();
        int i;
        int j;
        char ch1;
        char ch2;
        int temp;
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }
        for (i = 1; i <= n; i++) {
            ch1 = str1.charAt(i - 1);
            for (j = 1; j <= m; j++) {
                ch2 = str2.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]
                        + temp);
            }
        }
        return d[n][m];
    }
    
    //最大公共子串计算函数
    public static String LCS( String str1, String str2 ){
        String res = "";
        int d[][] = new int[str1.length()+1][str2.length()+1];
        for( int i = str1.length() - 1; i>=0; i--){
            for( int j = str2.length() -1; j >= 0; j--){
                if( str1.charAt(i)==str2.charAt(j)){
                    d[i][j] = d[i+1][j+1] + 1;
                }else{
                    d[i][j] = Math.max(d[i+1][j], d[i][j+1]);
                }
            }
        }
        int i = 0, j = 0;
        while( i < str1.length() && j < str2.length() ){
            if( str1.charAt(i) == str2.charAt(j) ){
                res += str1.charAt(i);
                i++;
                j++;
            }else if( d[i+1][j] >= d[i][j+1]){
                i++;
            }else{
                j++;
            }
        }
        return res;
    }
}
