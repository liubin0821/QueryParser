package com.myhexin.qparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.phrase.*;
import junit.framework.TestCase;

public class TestPhraseNew extends TestCase {
	public void test() {
		// PhraseParser parser = new PhraseParser("./conf/qparser.conf", "./data");

		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();

		// phraseParser配置于qphrase.xml文件
		PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		
		ArrayList<String> qlist = new ArrayList<String>();
		File f = new File("file.txt");
		// 获取问句
		qlist = readTxtLine("query.txt");
		try {
			// 读出问句的一些结果并保存到file.txt
			FileOutputStream fos = new FileOutputStream(f);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter bw = new BufferedWriter(osw);
			int num = 0;
			for (String query : qlist) {
				System.out.println(String.format(
						"########################\nQUERY: %d", ++num));
				Query q = new Query(query.toLowerCase());
				ParseResult parseRet = parser.parse(q);
				savTxt(bw, parseRet, query, num);
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void savTxt(BufferedWriter bw, ParseResult parseRet, String query,
			int num) {
		List<String> standardQ = new ArrayList<String>();
		String ret = null;
		standardQ = parseRet.getStandardQueries();
		ret = standardQ.get(0);
		try {
			for (int i = 0; i < standardQ.size(); i++) {
				if (standardQ.get(i).equals(query)) {
					bw.write(num + "");
					bw.newLine();
				}
			}
			bw.write("query:" + query);
			bw.newLine();
			bw.write("ret:  " + ret);
			bw.newLine();
			bw.newLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private ArrayList<String> readTxtLine(String txtName) {
		ArrayList<String> qlist = new ArrayList<String>();
		try {
			FileReader fr = new FileReader(txtName);
			BufferedReader br = new BufferedReader(fr);
			String s = null;
			boolean flag = true;
			while ((s = br.readLine()) != null) {
				s = s.trim();
				s = s.replace("_SMLNUM_", "1");
				s = s.replace("_BIGNUM_", "2000");
				s = s.replace("_STOCK_", "同花顺");
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
			System.exit(1);
		}
		return qlist;
	}
}
