package com.myhexin.qparser.date.axis;

import java.io.*;
import java.util.ArrayList;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.phrase.*;
import junit.framework.TestCase;

public class DateAxisHandlerRegression extends TestCase {
	public void test() {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();
		
		ArrayList<String> qlist = new ArrayList<String>();

		try {
			// 解析问句读取
			InputStream in = DateAxisHandlerRegression.class.getClassLoader().getResourceAsStream("com/myhexin/qparser/date/axis/query_bugfix.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String s = null;
			while ((s = br.readLine()) != null) {
				s = s.trim();
				int index = s.indexOf("-->");
				if(index>0) {
					s = s.substring(0, index);
				}
				qlist.add(s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// phraseParser配置于qphrase.xml文件
		PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParserBacktestCond");
		parser.setSplitWords("; ");

		
		String s1 = "Date Axis Parser...";
		String s2 = "##";
		String s3 = "## Common Output...";
		
		ArrayList<String[]> res = new ArrayList<String[]>();
		
		//parser.parse(new Query("0"));
		for (String query : qlist) {
			long beforeTime = System.currentTimeMillis();
			Query q = new Query(query.toLowerCase());
			String result = null;
			String commonOutput = null;
			String dateAxisOutput = null;
			try {
				ParseResult pr = parser.parse(q);
				result = q.getParseResult().processLog;
				
				int index = result.indexOf(s3);
				if(index>0) {
					commonOutput = result.substring(index + s3.length());
					if(commonOutput!=null) commonOutput=commonOutput.trim();
				}
				
				index = result.indexOf(s1);
				if(index>0) {
					int idx2 = result.indexOf(s2, index);
					if(idx2>0 && idx2>index) {
						dateAxisOutput = result.substring(index+s1.length(), idx2);
						if(dateAxisOutput!=null) dateAxisOutput=dateAxisOutput.trim();
					}
				}
				
				res.add(new String[]{query.toLowerCase(), dateAxisOutput,commonOutput });
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			long afterTime = System.currentTimeMillis();
			long timeDistance = afterTime - beforeTime;
			System.out.println("time:" + timeDistance);
			//System.out.println("query:" + query.toLowerCase());
			//System.out.println("result:" + result);
		}
		try{
			FileOutputStream fos = new FileOutputStream("D:/date_axis.txt");
			PrintStream out = new PrintStream(fos);
			for(String[] r : res) {
				out.println("######");
				out.println("Query :" +r[0]);
				out.println("Date Axis Output:\n" + r[1] + "\n");
				out.println("Common Output:\n" + r[2]);
				out.println("######\n");
			}
			out.close();
		}catch(Exception e){
			
		}
	}

	public static void main(String[] args) {
		new DateAxisHandlerRegression().test();
	}
}
