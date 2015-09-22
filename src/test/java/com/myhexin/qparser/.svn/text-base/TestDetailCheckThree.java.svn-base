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
import junit.framework.TestCase;

public class TestDetailCheckThree extends TestCase {
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
		List<String> sslist = new ArrayList<String>();
		List<String> olist = new ArrayList<String>();
		String fileName = "checkQuery2.csv";
		String checkName = "checkQuery3.csv";
		File f = new File(checkName);
		try {
			// 获取问句
			qlist = readCsvLine(fileName, 1);
			clist = readCsvLine(fileName, 2);
			slist = readCsvLine(fileName, 3);
			ilist = readCsvLine(fileName, 4);
			sslist = readCsvLine(fileName, 5);
			olist = readCsvLine(fileName, 6);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f), "UTF-8"));
			bw.write("query,checkRet,score,index,syntacticSemanticIds,newRet,oldRet,error");
			bw.newLine();
			int num = 0;
			for (int i = 0; i < qlist.size(); i++) {
				System.out.println(String.format(
						"########################\nQUERY: %d", ++num));
				String check = clist.get(i);
				String query = qlist.get(i);
				String checkScore = slist.get(i);
				String checkIndex = ilist.get(i);
				String checkSIds = sslist.get(i);
				String oldRet = olist == null ? "" : olist.get(i);
				Query q = new Query(query.toLowerCase());
				ParseResult parseRet = parser.parse(q);
				saveCsv(bw, parseRet, query, check, checkScore, checkIndex,
						checkSIds, oldRet);
			}
			bw.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void saveCsv(BufferedWriter bw, ParseResult parseRet, String query,
			String check, String checkScore, String checkIndex,
			String checkSIds, String oldRet) {
		List<List<String>> queriesIndexList = new ArrayList<List<String>>();
		List<List<String>> syntacticSemanticIdsList = new ArrayList<List<String>>();
		String ret = "";
		int score = 0;
		try {
			if (parseRet.getStandardQueries().size() > 0) {
				ret = parseRet.getStandardQueries().get(0);
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
			bw.write(query + "," + check + "," + checkScore + "," + checkIndex
					+ "," + checkSIds + "," + ret + "," + oldRet);
			if (!syntacticSemanticIds.equals(checkSIds)) {
				bw.write(",句式语义不同,当前句式语义号为：" + syntacticSemanticIds);
				bw.newLine();
			} else if (!index.equals(checkIndex)) {
				bw.write(",指标发生变化,当前指标为：" + index);
				bw.newLine();
			} else if (score < 80) {
				bw.write(",得分低于80分,现在得分是：" + score);
				bw.newLine();
			} else if (!ret.equals(check)) {
				bw.write(",仅结果不一样");
				bw.newLine();
			} else {
				bw.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private String getS(List<List<String>> listList) {
		if (listList == null) {
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
			System.exit(1);
		}
		return qlist;
	}
}
