package com.myhexin.qparser.tool.encode;

import java.io.UnsupportedEncodingException;

import com.myhexin.qparser.phrase.util.Consts;

/*
 * author: 		吴永行
 * time:   		2013.10.25
 * description: 判断一段文本是否是乱码
 */
public class Encode {

	private final static double defaultMessyCodeRate = 0.5; // 常用汉字占的比例
	private final static String targetEncode = "GBK";
	private final static String placeHolder = "同"; // 归一化时替换数字或者英文的占位符
	private final static String EnglishPattern = "([a-z]|[A-Z])+";
	private final static String NumPattern = "(-?\\d+)(\\.\\d+)?%?";
	private final static String commonPattern = ">=?|<=?"; //把一些常用符号替换常用汉字  比如比较的符号 >= <=
	/**
	 * 我觉得日期没有必要,  (2014.2.24 wyh)
	 * 有这么一长串的日期,  本身就可以判断不是乱码了 , 不进行替换的话,它本身就是很多常用汉字了
	 * 而且这么长的pattern,效率也是问题; 这个东西运行的频率是很高的**/
	//private static String DatePattern = "(199\\d|200\\d|201\\d|9\\d|0\\d|1\\d)(?:年|年度|\\-|\\.|/|、|—)?(1[0-2]|0?[1-9])(?:月|月份|\\-|\\.|/|、|—)?([12]\\d|3[01]|0?[1-9])(?:日|号)?";

	/**
	 *   输入为空时,视为不是乱码
	 *   通过:常用汉字占的比例 defaultMessyCodeRate 来判断是否是乱码
	 *   
	 *   GBK编码规范：一级汉字（第15区-55区） 二级汉字（56区到87区）
	 *   16区对应B0   （中间间隔16个单位，比如说BF就是对应31区）       55区对应D7、87区对应F7
	 *   
	 */
	public static boolean isMessyCode(String queryText) {
		// 为空,视为不是乱码
		if (queryText == null || queryText.equals(Consts.STR_BLANK)) {
			return false;
		}
		String query = queryText;
		double nounChinese = 0; // 不是汉字
		double commonChinese = 0; // 常用汉字
		double nounCommonChinese = 0; // 不常用汉字

		try {			
			query = query.replaceAll(EnglishPattern, placeHolder); // 替换英文
			//query = query.replaceAll(DatePattern, placeHolder); // 替换时间
			query = query.replaceAll(NumPattern, placeHolder); // 替换数字
			query = query.replaceAll(" ", ""); // 移除空格
			query = query.replaceAll(commonPattern, placeHolder); // 替换一些常用的符号

			byte[] bytes = query.getBytes(targetEncode);
			for (int i = 0; i < bytes.length; i++) {
				int temp = bytes[i] & 0xff;              //汉字占两个字节，一个是区码，一个是位码。只需判断区码，位码加一过滤
				if (temp < 128) { // 英文
					nounChinese++;
				} else if (temp < 176) { // 中文符号     
					nounCommonChinese++;
					i++; // 前移两位    
				} else if (temp < 248) {  //   小于88区
					commonChinese++;
					i++;// 前移两位
				} else {
					nounCommonChinese++;
					i++;// 前移两位
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 常用汉字占的比例大于阈值
		// 前面已经检测不为空 ==> commonChinese + nounCommonChinese + nounChinese 的值一定大于0
		if (commonChinese / (commonChinese + nounCommonChinese + nounChinese) >= defaultMessyCodeRate) {
			return false;
		}
		return true;
	}
}
