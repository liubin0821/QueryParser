package com.myhexin.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.server.Parser;

public class TestKnowledge extends TestCase {
	// PhraseParser parser = new PhraseParser("./conf/qparser.conf", "./data");

	public String test(String destpath, String queryName) {
		List<String> qlist = new ArrayList<String>();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date();
		String datetemp = format.format(curDate);
		String fileName = datetemp + "file.csv";
		File f = new File(destpath, fileName);
		try {
			// 获取问句
			qlist = readTxtLine(destpath + queryName);
			int num = 0;
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f), "UTF-8"));
			bw.write("query,score,checkRet,thematic");
			bw.newLine();
			for (String query : qlist) {
				boolean flag = false;
				System.out.println(String.format(
						"########################\nQUERY: %d", ++num));
				if (query.length() > 50) {
					flag = true;
					query = query.replace(",", ";");
					bw.write(query + ",句子过长");
					bw.newLine();
					continue;
				}
				if (flag == false) {
					Query q = new Query(query.toLowerCase());
					ParseResult parseRet = Parser.parser.parse(q);
					saveCsv(bw, parseRet, query);
				}
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}

	private void saveCsv(BufferedWriter bw, ParseResult parseRet, String query) {
		String ret = null;
		String thematic = null;
		try {
			if (parseRet != null && parseRet.qlist != null
					&& parseRet.qlist.size() > 0
					&& parseRet.qlist.get(0) != null
					&& parseRet.qlist.get(0).size() > 0){
				if(parseRet.qlist.get(0).get(0).type == NodeType.ENV){
					Environment listEnv = (Environment) parseRet.qlist.get(0).get(0);
					if(listEnv.containsKey("thematic")){
						thematic = listEnv.get("thematic", String.class, false);
						thematic = thematic == null ? null : thematic.replace(",", "|");
					}
				}
				
			}
			ret = parseRet.getStandardQueries().get(0);
			if (parseRet != null && parseRet.qlist != null
					&& parseRet.qlist.size() > 0
					&& parseRet.qlist.get(0).size() > 0)
				ret = PhraseParserPluginAbstract.getFromListEnv(parseRet.qlist.get(0), "standardStatement",String.class, false);
			query = query.replace(",", ";");
			ret = ret.replace(",", ";");
			int score = parseRet.standardQueriesScore.get(0);
			bw.write(query + "," + score + "," + ret + "," + thematic);
			bw.newLine();
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
