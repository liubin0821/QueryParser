package com.myhexin.qparser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.csv.ReadCsv;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;

import junit.framework.TestCase;

public class TestDetailCheckAll extends TestCase {
	PhraseParser parser;// = new PhraseParser("./conf/qparser.conf", "./data");

	public void test() {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();

		// phraseParser配置于qphrase.xml文件
		parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		
		List<String> qlist = new ArrayList<String>();
		List<String> clist = new ArrayList<String>();
		List<String> slist = new ArrayList<String>();
		List<String> ilist = new ArrayList<String>();
		List<String> slist1 = new ArrayList<String>();
		List<String> slist2 = new ArrayList<String>();
		String fileName = "base.csv";
		String checkName = "result.csv";
		File f = new File(checkName);
		try {
			// 获取问句
			qlist = readCsvLine(fileName, 1);
			clist = readCsvLine(fileName, 2);
			slist = readCsvLine(fileName, 3);
			ilist = readCsvLine(fileName, 4);
			slist1 = readCsvLine(fileName, 5);
			slist2 = readCsvLine(fileName, 6);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f), "UTF-8"));
			bw.write("query,checkRet,score,index,syntacticIds,semanticIds,newRet,newScore,newIndex,newSyntacticIds,newSemanticIds,error,remark,");
			bw.newLine();
			int num = 0;
			for (int i = 0; i < qlist.size(); i++) {
				System.out.println(String.format("########################"));
				System.out.println(String.format("QUERY: %d", ++num));
				String check = clist.get(i);
				String query = qlist.get(i);
				System.out.println(query);
				String checkScore = slist.get(i);
				String checkIndex = ilist.get(i);
				String checkSIds1 = slist1.get(i);
				String checkSIds2 = slist2.get(i);
				Query q = new Query(query.toLowerCase());
				ParseResult parseRet = parser.parse(q);
				saveCsv(bw, parseRet, query, check, checkScore, checkIndex,
						checkSIds1, checkSIds2);
			}
			bw.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void saveCsv(BufferedWriter bw, ParseResult parseRet, String query,
			String check, String checkScore, String checkIndex,
			String checkSIds1, String checkSIds2) {
		List<List<String>> queriesIndexList = new ArrayList<List<String>>();
		List<List<String>> syntacticSemanticIdsList = new ArrayList<List<String>>();
		String ret = "";
		int score = 0;
		try {
			if (parseRet.getStandardQueries().size() > 0) {
				ret = parseRet.getStandardQueries().get(0);
				if (parseRet != null && parseRet.qlist != null && parseRet.qlist.size() > 0 && parseRet.qlist.get(0).size() > 0)
					ret = PhraseParserPluginAbstract.getFromListEnv(parseRet.qlist.get(0), "standardStatement",String.class, false);
				score = parseRet.standardQueriesScore.get(0);
			}
			ret = ret.replace(";", "_&_");
			ret = ret.replace(",", ";");
			query = query.replace(",", ";");
			check = check.replace(",", ";");
			queriesIndexList = parseRet.standardQueriesIndex;
			syntacticSemanticIdsList = parseRet.standardQueriesSyntacticSemanticIds;
			String index = getS(queriesIndexList);
			String syntacticSemanticIds = getS(syntacticSemanticIdsList);
			String[] syntacticSemantics = getSS(syntacticSemanticIds);
			String syntacticIds = syntacticSemantics[0];
			String semanticIds = syntacticSemantics[1];
			bw.write(query + "," + check + "," + checkScore + "," + checkIndex
					+ "," + checkSIds1 + "," + checkSIds2 + "," + ret + ","
					+ score + "," + index + "," + syntacticIds + ","
					+ semanticIds);
			if (!semanticIds.equals(checkSIds2)) {
				bw.write(",语义不同,当前语义号为：" + semanticIds + ";句式号为："
						+ syntacticIds);
				bw.newLine();
			} else if (!index.equals(checkIndex)) {
				bw.write(",指标发生变化,当前指标为：" + index);
				bw.newLine();
			} else if (!ret.equals(check)) {
				bw.write(",仅结果不一样");
				bw.newLine();
			} else {
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
		String[] syntacticSemantics = syntacticSemanticIds.split("_&_");
		String syntactic = "";
		String semantic = "";
		if (syntacticSemanticIds != "null") {
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
		} else {
			syntactic = "null";
			semantic = "null";
		}
		String[] ss = new String[] { syntactic, semantic };
		return ss;
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
			if (colNum > csv.getColNum()) {
				return null;
			}
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
