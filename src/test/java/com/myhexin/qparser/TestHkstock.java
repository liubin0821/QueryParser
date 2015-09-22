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

public class TestHkstock extends TestCase {
	PhraseParser parser;// = new PhraseParser("./conf/qparser.conf", "./data");

	public void test() {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();

		// phraseParser配置于qphrase.xml文件
		parser = (PhraseParser) ApplicationContextHelper
				.getBean("phraseParser");

		List<String> qlist = new ArrayList<String>();
		String fileName = "base.csv";
		String queryName = "test.txt";
		File f = new File(fileName);
		try {
			// 获取问句
			qlist = readTxtLine(queryName);
			int num = 0;
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f), "UTF-8"));
			bw.write("query,checkRet,score,index,syntacticIds,semanticIds");
			bw.newLine();
			for (String query : qlist) {
				boolean flag = false;
				System.out.println(String.format(
						"########################\nQUERY: %d", ++num));
				if (flag == false) {
					Query q = new Query(query.toLowerCase());
					ParseResult parseRet = parser.parse(q);
					saveCsv(bw, parseRet, query);
				}
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveCsv(BufferedWriter bw, ParseResult parseRet, String query) {
		String ret = null;
		List<List<String>> queriesIndexList = new ArrayList<List<String>>();
		List<List<String>> syntacticSemanticIdsList = new ArrayList<List<String>>();
		try {
			ret = parseRet.getStandardQueries().get(0);
			query = query.replace(",", ";");
			ret = ret.replace(",", ";");
			int score = parseRet.standardQueriesScore.get(0);
			// if (score >= 80) {
			queriesIndexList = parseRet.standardQueriesIndex;
			syntacticSemanticIdsList = parseRet.standardQueriesSyntacticSemanticIds;
			String index = getS(queriesIndexList);
			String syntacticSemanticIds = getS(syntacticSemanticIdsList);
			String[] syntacticSemantics = getSS(syntacticSemanticIds);
			bw.write(query + "," + ret + "," + score + "," + index + ","
					+ syntacticSemantics[0] + "," + syntacticSemantics[1]);
			bw.newLine();
			// }
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * 把句式语义变成句式+语义
	 * 
	 * @param syntacticSemanticIds
	 * @return
	 */
	private String[] getSS(String syntacticSemanticIds) {
		if (syntacticSemanticIds == "null") {
			return new String[] { "null", "null" };
		} else {
			String[] syntacticSemantics = syntacticSemanticIds.split("_&_");
			String syntactic = "";
			String semantic = "";
			for (int i = 0; i < syntacticSemantics.length; i++) {
				String syntacticSemantic[] = syntacticSemantics[i].split("\\|");
				if (i != syntacticSemantics.length - 1) {
					syntactic += syntacticSemantic[0] + "_&_";
					semantic += syntacticSemantic[1] + "_&_";
				} else {
					syntactic += syntacticSemantic[0];
					semantic += syntacticSemantic[1];
				}
			}
			String[] ss = new String[] { syntactic, semantic };
			return ss;
		}
	}

	private String getS(List<List<String>> listList) {
		System.out.println(listList);
		System.out.println(listList.get(0));
		if (listList == null || listList.get(0).isEmpty()) {
			return "null";
		} else {
			List<String> list = listList.get(0);
			String s = "";
			int size = list.size();
			for (int i = 0; i < size; i++) {
				if (i != size - 1) {
					s = s + list.get(i) + "_&_";
				} else {
					s = s + list.get(i);
				}
			}
			s = s.replace(",", ";");
			return s;
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
