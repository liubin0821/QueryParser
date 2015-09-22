package com.myhexin.qparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.phrase.PhraseParser;

public class TestRetPhrase extends TestCase {
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
		try {
			// 获取问句
			qlist = readTxtLine(queryName);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f), "UTF-8"));
			bw.write("query,ret,retPhrase");
			bw.newLine();
			int num = 0;
			for (String query : qlist) {
				System.out.println(String.format(
						"########################\nQUERY: %d", ++num));
				Query q = new Query(query.toLowerCase());
				ParseResult parseRet = parser.parse(q);
				saveCsv(bw, parseRet, query);
			}
			bw.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void saveCsv(BufferedWriter bw, ParseResult parseRet, String query) {
		List<String> standardQ = new ArrayList<String>();
		List<String> retStandardQ = new ArrayList<String>();
		String ret = null;
		String retPhrase = null;
		try {
			standardQ = parseRet.getStandardQueries();
			ret = standardQ.get(0);
			ret = ret.replace("_&_", ";");
			Query retQ = new Query(ret.toLowerCase());
			ParseResult retParseRet = parser.parse(retQ);
			retStandardQ = retParseRet.getStandardQueries();
			retPhrase = retStandardQ.get(0);
			ret = ret.replace(";", "_&_");
			if (!retPhrase.equals(ret)) {
				query = query.replace(",", ";");
				ret = ret.replace(",", ";");
				retPhrase = retPhrase.replace(",", ";");
				bw.write(query + "," + ret + "," + retPhrase);
				bw.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * 根据txt文件名，读取每一行
	 * 
	 * @param txtName
	 * @return
	 */
	private ArrayList<String> readTxtLine(String txtName) {
		ArrayList<String> qlist = new ArrayList<String>();
		try {
			FileReader fr = new FileReader(txtName);
			BufferedReader br = new BufferedReader(fr);
			String s = null;
			boolean flag = true;
			while ((s = br.readLine()) != null) {
				s = s.trim();
				if (s.length() == 0) {
					continue;
				} else if (s.startsWith("break")) {
					break;
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
