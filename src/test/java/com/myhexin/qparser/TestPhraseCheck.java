package com.myhexin.qparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.csv.ReadCsv;
import com.myhexin.qparser.phrase.*;
import com.ndktools.javamd5.Mademd5;

import junit.framework.TestCase;

public class TestPhraseCheck extends TestCase {
	PhraseParser parser;// = new PhraseParser("./conf/qparser.conf", "./data");
	Mademd5 mad = new Mademd5();

	public void test() {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();

		// phraseParser配置于qphrase.xml文件
		parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		
		List<String> qlist = new ArrayList<String>();
		String fileName = "file.csv";
		String queryName = "a.txt";
		File f = new File(fileName);
		File ff = new File("check.csv");
		try {
			// 获取问句
			qlist = readTxtLine(queryName);
			// 与前一份csv比较
			if (!f.exists()) {
				// 如果找不到则读取问句结果并保存成csv
				saveCsv(f, qlist);
			} else {
				Map<String, List<String>> check = new HashMap<String, List<String>>();
				Map<String, List<String>> news = new HashMap<String, List<String>>();
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(ff), "UTF-8"));
				bw.write("query,newRet,oldRet");
				bw.newLine();
				// 如果找到文件则读取问句跟之前比较
				ReadCsv csv = new ReadCsv(fileName);
				for (int i = 1; i < csv.getRowNum(); i++) {
					String s[] = csv.getRow(i).split(",");
					String key = s[1];
					List<String> values = new ArrayList<String>();
					for (int j = 2; j < s.length; j++) {
						values.add(s[j]);
					}
					check.put(key, values);
				}
				// 现在的问句结果
				news = getQuery(qlist);
				// 比较之前的问句结果和现在的问句结果
				if (check.size() == news.size()) {
					Iterator it = check.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, List<String>> entry = (Entry<String, List<String>>) it
								.next();
						List<String> clist = new ArrayList<String>();
						List<String> nlist = new ArrayList<String>();
						String key = entry.getKey();
						clist = entry.getValue();
						nlist = news.get(mad.toMd5(key));
						if (!clist.equals(nlist)) {
							String n = listToString(nlist, "、");
							String c = listToString(clist, "、");
							bw.write(key + "," + n + "," + c);
							bw.newLine();
						}
					}
				} else {
					System.out.println("比较字段不一样，不做比较");
				}
				bw.close();
				// 比较结束保存新csv
				saveCsv(f, qlist);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String listToString(List<String> stringList, String splitString) {
		if (stringList == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		boolean flag = false;
		for (String string : stringList) {
			if (flag) {
				result.append(splitString);
			} else {
				flag = true;
			}
			result.append(string);
		}
		return result.toString();
	}

	private Map<String, List<String>> getQuery(List<String> qlist) {
		Map<String, List<String>> news = new HashMap<String, List<String>>();
		for (String query : qlist) {
			List<String> standardQ = new ArrayList<String>();
			List<String> nlist = new ArrayList<String>();
			Query q = new Query(query.toLowerCase());
			ParseResult parseRet = parser.parse(q);
			standardQ = parseRet.getStandardQueries();
			for (int i = 0; i < standardQ.size(); i++) {
				nlist.add(standardQ.get(i));
			}
			news.put(mad.toMd5(query), nlist);
		}
		return news;
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
