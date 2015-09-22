package com.myhexin.qparser.findout;

import java.io.*;
import java.util.ArrayList;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.phrase.*;
import junit.framework.TestCase;

/**
 * 找出无法解析的问句
 * 1. 分数小于100分的
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-2-4
 *
 */
public class FindoutNotParsedQueries extends TestCase {
	public void test() {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();
		
		ArrayList<String> qlist = new ArrayList<String>();

		try {
			// 解析问句读取
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream("querys.txt"), "utf-8"));
			String s = null;
			boolean flag = true;
			while ((s = br.readLine()) != null) {
				s = s.trim();
				if (s.startsWith("#") || s.length() == 0) {
					continue;
				} else if (s.startsWith("break")) {
					break;
				} else if (s.startsWith("/*")) {
					flag = false;
				} else if (s.endsWith("*/")) {
					flag = true;
					continue;
				}
				if (flag) {
					qlist.add(s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// phraseParser配置于qphrase.xml文件
		PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		parser.setSplitWords("; ");

		//parser.parse(new Query("0"));
		for (String query : qlist) {
			long beforeTime = System.currentTimeMillis();
			int num = 0;
			// System.out.println(String.format("########################\nQUERY[%d]:%s",
			// ++num, query));
			Query q = new Query(query.toLowerCase());
			try {
				ParseResult pr = parser.parse(q);
				System.out.println(q.getParseResult().processLog);
			} catch (Exception e) {
				e.printStackTrace();
			}
			long afterTime = System.currentTimeMillis();
			long timeDistance = afterTime - beforeTime;
			System.out.println("time:" + timeDistance);
		}
	}

	public static void main(String[] args) {
		new FindoutNotParsedQueries().test();
	}
}
