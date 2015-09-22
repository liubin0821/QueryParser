package com.myhexin.qparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.phrase.*;
import junit.framework.TestCase;

public class TestPhraseCsv extends TestCase {
	PhraseParser parser;// = new PhraseParser("./conf/qparser.conf", "./data");

	public void test() {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();

		// phraseParser配置于qphrase.xml文件
		parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		
		List<String> qlist = new ArrayList<String>();
		String fileName = "file.csv";
		String queryName = "query.txt";
		File f = new File(fileName);
		// 获取问句
		qlist = readTxtLine(queryName);
		saveCsv(f, qlist);
	}

	private void saveCsv(File f, List<String> qlist) {
		List<String> standardQ = new ArrayList<String>();
		String ret = null;
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f), "UTF-8"));
			bw.write("No,query,ret");
			bw.newLine();
			int num = 0;
			for (String query : qlist) {
				System.out.println(String.format(
						"########################\nQUERY: %d", ++num));
				Query q = new Query(query.toLowerCase());
				ParseResult parseRet = parser.parse(q);
				standardQ = parseRet.getStandardQueries();
				query = query.replace(",", ";");
				bw.write(num + "," + query + ",");
				for (int i = 0; i < standardQ.size(); i++) {
					ret = standardQ.get(i);
					ret = ret.replace(",", ";");
					bw.write(ret + ",");
				}
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
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
