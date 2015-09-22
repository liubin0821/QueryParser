package com.myhexin.qparser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.csv.ReadCsv;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;

import junit.framework.TestCase;

public class TestDetailCheckK extends TestCase {
	PhraseParser parser;// = new PhraseParser("./conf/qparser.conf", "./data");

	public void test() {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();

		// phraseParser配置于qphrase.xml文件
		parser = (PhraseParser) ApplicationContextHelper
				.getBean("phraseParser");

		List<String> qlist = new ArrayList<String>();
		List<String> clist = new ArrayList<String>();
		String fileName = "研报类问句_file.csv";
		String checkName = "check.csv";
		File f = new File(checkName);
		try {
			// 获取问句
			qlist = readCsvLine(fileName, 1);
			clist = readCsvLine(fileName, 4);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f), "UTF-8"));
			bw.write("query,checkThematic,score,ret,thematic");
			bw.newLine();
			int num = 0;
			for (int i = 0; i < qlist.size(); i++) {
				System.out.println(String.format(
						"########################\nQUERY: %d", ++num));
				String query = qlist.get(i);
				String check = clist.get(i);
				Query q = new Query(query.toLowerCase());
				ParseResult parseRet = parser.parse(q);
				saveCsv(bw, parseRet, query, check);
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveCsv(BufferedWriter bw, ParseResult parseRet, String query,
			String check) {
		String ret = null;
		String thematic = null;
		try {
			if (parseRet != null && parseRet.qlist != null
					&& parseRet.qlist.size() > 0
					&& parseRet.qlist.get(0) != null
					&& parseRet.qlist.get(0).size() > 0) {
				if(parseRet.qlist.get(0).get(0).type == NodeType.ENV){
					Environment listEnv = (Environment) parseRet.qlist.get(0).get(0);
					if(listEnv.containsKey("thematic"))
					thematic = listEnv.get("thematic", String.class, false);
					thematic = thematic == null ? null : thematic.replace(",", "|");
				}
				
			}
			int score = parseRet.standardQueriesScore.get(0);
			if (score != 0 && !thematic.equals(check)) {
				ret = parseRet.getStandardQueries().get(0);
				if (parseRet != null && parseRet.qlist != null
						&& parseRet.qlist.size() > 0
						&& parseRet.qlist.get(0).size() > 0)
					ret = PhraseParserPluginAbstract.getFromListEnv(parseRet.qlist.get(0), "standardStatement",String.class, false);
				ret = ret.replace(";", "_&_");
				ret = ret.replace(",", ";");
				query = query.replace(",", ";");
				bw.write(query + "," + check + "," + score + "," + ret + ","
						+ thematic);
				bw.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * 根据csv文件名，读取第num列
	 * 
	 * @param txtName
	 * @param colNum
	 * @return
	 */
	private ArrayList<String> readCsvLine(String txtName, int colNum) {
		ArrayList<String> qlist = new ArrayList<String>();
		try {
			ReadCsv csv = new ReadCsv(txtName);
			String[] csvStrings = csv.getCol(colNum - 1).split(",");
			String s = null;
			for (int i = 1; i < csvStrings.length; i++) {
				s = csvStrings[i];
				s = s.trim();
				qlist.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return qlist;
	}
}
