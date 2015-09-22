package com.myhexin.qparser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.csv.ReadCsv;
import com.myhexin.qparser.phrase.PhraseParser;

public class TestLua extends TestCase {
	PhraseParser parser;// = new PhraseParser("./conf/qparser.conf", "./data");

	public void test() {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();

		// phraseParser配置于qphrase.xml文件
		parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");

		List<String> qlist = new ArrayList<String>();
		List<String> clist = new ArrayList<String>();
		String fileName = "test1.csv";
		String checkName = "test.csv";
		File f = new File(fileName);
		try {
			// 获取问句
			qlist = readCsvLine(checkName, 1);
			clist = readCsvLine(checkName, 2);
			int num = 0;
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
			bw.write("query,checkLua,lua,index,syntacticIds,semanticIds");
			bw.newLine();
			for (int i = 0; i < qlist.size(); i++) {
				System.out.println(String.format("########################\nQUERY: %d", ++num));
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

	private void saveCsv(BufferedWriter bw, ParseResult parseRet, String query, String check) {
		List<List<String>> queriesIndexList = new ArrayList<List<String>>();
		List<List<String>> syntacticSemanticIdsList = new ArrayList<List<String>>();
		String luaExpression = null;
		try {
			query = query.replace(",", ";");
			queriesIndexList = parseRet.standardQueriesIndexWithProp;
			syntacticSemanticIdsList = parseRet.standardQueriesSyntacticSemanticIds;
			luaExpression = parseRet.luaExpression.get(0);
			if (luaExpression != null && luaExpression.contains(",")) {
				String[] strs = luaExpression.split(",");
				luaExpression = "";
				if (strs.length > 0) {
					luaExpression = strs[0];
					for (int i = 1; i < strs.length; i++) {
						luaExpression = luaExpression+";" + strs[i];
					}
				}
			}
			String index = getS(queriesIndexList);
			String syntacticSemanticIds = getS(syntacticSemanticIdsList);
			String[] syntacticSemantics = getSS(syntacticSemanticIds);
			if (!check.equals(luaExpression)) {
				bw.write(query + "," + check + "," + luaExpression + "," + index + "," + syntacticSemantics[0] + "," + syntacticSemantics[1]);
				bw.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		}
		return qlist;
	}
}
