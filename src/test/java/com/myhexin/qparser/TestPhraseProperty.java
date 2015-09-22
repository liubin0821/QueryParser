package com.myhexin.qparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.phrase.*;
import junit.framework.TestCase;

public class TestPhraseProperty extends TestCase {
	public void test() {
		// PhraseParser parser = new PhraseParser("./conf/qparser.conf", "./data");

		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();

		// phraseParser配置于qphrase.xml文件
		PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		
		ArrayList<String> qlist = new ArrayList<String>();
		// 获取问句
		qlist = readTxtLine("query33.txt");
		try {
			
			ArrayList<Integer> intList = new ArrayList<Integer>();
			ArrayList<Long> longList = new ArrayList<Long>();

			ArrayList<ArrayList<String>> qlistList = new ArrayList<ArrayList<String>>();
			int totalTypeNum = 11;
			for (int i=0; i < totalTypeNum; i++) {
				intList.add(0);
				longList.add((long) 0);
				ArrayList<String> tempList = new ArrayList<String>();
				qlistList.add(tempList);
			}
			for (String query : qlist) {
				int upN = (totalTypeNum-1)*10+5;
				int size = query.length();
				if (0 < size && size < 15 ) {
					intList.set(0, intList.get(0)+1);
					qlistList.get(0).add(query);
				} else if (size > upN) {
					intList.set(totalTypeNum-1, intList.get(totalTypeNum-1)+1);
					qlistList.get(totalTypeNum-1).add(query);
				} else {
					int typeNum = size/10 + (size%10<5? 0 : 1) - 1;
					intList.set(typeNum, intList.get(typeNum)+1);
					qlistList.get(typeNum).add(query);
				}
			}
			int totalNum = 0;
			double totalTime = 0;
			Query temp = new Query("pe");
			parser.parse(temp);
			int num = 0;
			for (int i = 0; i < totalTypeNum-1; i++) {
				long beforeTime = System.currentTimeMillis();
				ArrayList<String> tempList = qlistList.get(i);
				for (String query : tempList) {
					System.out.println(String.format("########################\nQUERY[%d]:%s", ++num, query));
					Query q = new Query(query.toLowerCase());
					parser.parse(q);
				}
				long afterTime = System.currentTimeMillis();
				long timeDistance = afterTime - beforeTime;
				longList.set(i, timeDistance);
				
				totalNum += intList.get(i);
				totalTime += timeDistance;
			}
			for (int i = 0; i < totalTypeNum; i++) {
				String type = "";
				if (i == 0) {
					type = "0-14";
				} else if (i == totalTypeNum-1) {
					type = (i*10+5)+"以上";
				} else {
					type = (i*10+5)+"-"+((i+1)*10+4);
				}
				if (intList.get(i) > 0)
					System.out.println(type+"\t"+intList.get(i)+"\t"+longList.get(i)+"\t"+longList.get(i)/intList.get(i)+"\t");
			}
			System.out.println("总共"+totalNum+"句，总耗时"+totalTime+"ms，平均"+totalTime/totalNum+"ms");
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
