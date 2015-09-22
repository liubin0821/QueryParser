package com.myhexin.qparser.phrase.util;

public class CommonUtil {
    
    /**
     * 将大写全角的字符转成小写半角的相应字符。<br>
     * 所有全角字符共95个(94个+中文空格)。<br>
     * 发生转换的字符列举如下：<br>
     * ！  ＂  ＃  ＄  ％  ＆  ＇  （  ）  ＊  ＋  ，  －  ．  ／  <br>
     *  ０  １  ２  ３  ４  ５  ６  ７  ８  ９  ：  ；  ＜  ＝  ＞  ？ <br>
     *  ＠  Ａ  Ｂ  Ｃ  Ｄ  Ｅ  Ｆ  Ｇ  Ｈ  Ｉ  Ｊ  Ｋ  Ｌ  Ｍ  Ｎ  Ｏ <br>
     *  Ｐ  Ｑ  Ｒ  Ｓ  Ｔ  Ｕ  Ｖ  Ｗ  Ｘ  Ｙ  Ｚ  ［  ＼  ］  ＾  ＿ <br>
     *  ｀  ａ  ｂ  ｃ  ｄ  ｅ  ｆ  ｇ  ｈ  ｉ  ｊ  ｋ  ｌ  ｍ  ｎ  ｏ <br>
     *  ｐ  ｑ  ｒ  ｓ  ｔ  ｕ  ｖ  ｗ  ｘ  ｙ  ｚ  ｛  ｜  ｝  ～ <br>
     */
    public static String toLowerAndHalf(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for(int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if('！' <= ch && ch <= '～') { ch -= ('！'-'!'); } // 全角字符转半角字符，共94个
            if('A' <= ch && ch <= 'Z') { ch += ('a' - 'A'); } // 大写转小写
            else if('　' == ch) { ch = ' '; } // 全角空格转半角空格
            sb.append(ch);
        }
        return sb.toString();
    }
    
    /**
     * 
     * @author: 	    吴永行 
     * @dateTime:	  2014-8-7 下午3:43:02
     * @description:  单独的大写数字 "一二三四五六七八九"   转换为阿拉伯数字
     *
     */
    public static String singleBigNumToArabicNum(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        char preChar = ' ';
        for(int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            //前一个和后一个不是数字
            if(!isBigNum(preChar) && (i+1==text.length() || !isBigNum(text.charAt(i+1))))
	            sb.append(changeOneBigNumToArabic(ch));
            else
            	sb.append(ch);
            
            preChar=ch;
        }
        return sb.toString();
    }
    

	private static final boolean isBigNum(char ch) {
		switch (ch) {
		case '一':
		case '二':
		case '三':
		case '四':
		case '五':
		case '六':
		case '七':
		case '八':
		case '九':
			return true;
		default:
			return false;
		}
	}

	private static char changeOneBigNumToArabic(char ch) {
		switch (ch) {
		case '一':
			return '1';
		case '二':
			return '2';
		case '三':
			return '3';
		case '四':
			return '4';
		case '五':
			return '5';
		case '六':
			return '6';
		case '七':
			return '7';
		case '八':
			return '8';
		case '九':
			return '9';
		default:
			return ch;
		}
	}

	public static void main(String[] args) {
        //所有的汉字都是全角，所有全角字符共95个(94个+中文空格)，如下
        String s = "！＂＃＄％＆＇（）＊＋，－．／０１２３４５６７８９：；＜＝＞？＠ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ［＼］＾＿｀ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ｛｜｝～　";
        System.out.println(s);
        System.out.println(toLowerAndHalf(s));
    }

}
