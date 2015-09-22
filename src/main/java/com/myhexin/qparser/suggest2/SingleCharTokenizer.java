package com.myhexin.qparser.suggest2;

import java.util.ArrayList;



enum CharType{
    CHARACTER, ENGLISH, NUMBER, OTHER, START, END
}
    
public class SingleCharTokenizer {
    public static void tokenize(String ustr, ArrayList<String> res ){
        if( res == null )return;
        StringBuilder tmp = new StringBuilder();
        CharType preCharType = CharType.START;
        
        for (int i = 0; i < ustr.length(); i++) {
            char uchar = ustr.charAt(i);
            if( CommonStringUtil.isChinese(uchar)){
                //是汉字
                if (tmp.length() > 0) {
                    res.add(tmp.toString());
                    tmp = new StringBuilder();
                }
                res.add(Character.toString(uchar));
                preCharType = CharType.CHARACTER;
            }else if( CommonStringUtil.isAlphabet( uchar) ){
                if( preCharType !=  CharType.ENGLISH && tmp.length() > 0){
                    res.add(tmp.toString());
                    tmp = new StringBuilder();
                }
                //是英文，作为一个分词
                tmp.append(uchar);
                preCharType = CharType.ENGLISH;
            }else if( CommonStringUtil.isNumber(uchar)){
                //是数字，作为一个分词
                if( preCharType !=  CharType.NUMBER && tmp.length() > 0){
                    res.add(tmp.toString());
                    tmp = new StringBuilder();
                }
                tmp.append(uchar);
                preCharType = CharType.NUMBER;
            }else{
                //TODO 看效果如何，如果效果不好可以对特殊字符进行过滤等等
                //是特殊字符,保留特殊字符
                if (tmp.length() > 0) {
                    res.add(tmp.toString());
                    tmp = new StringBuilder();
                }
                res.add(Character.toString(uchar));
                preCharType = CharType.OTHER;
            }
        }
        if (tmp.length() > 0) {
            res.add(tmp.toString());
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String str = "在2022年12mo.nth上市+的天井greateライ*ト123456中有哪些可以吃";
        ArrayList<String> list = new ArrayList<String>();
        SingleCharTokenizer.tokenize(str, list);
        for( String nowStr : list ){
            System.out.println( nowStr );
        }
    }
}

