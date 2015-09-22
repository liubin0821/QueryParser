package com.myhexin.qparser.util.itoperation;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.PhraseParserUtil;
import com.myhexin.qparser.util.condition.ConditionBuilder;
import com.myhexin.qparser.util.lightparser.LightParser;
import com.myhexin.qparser.util.lightparser.LightParserServletParam;

public class FindOutDeadLoop {

	private static PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
	static {
		   parser.setSplitWords("_&_");
	}
	
	private static int count = 0;
	private static void parse(List<String> list) {
		if(list!=null) {
			
			for(String s : list) {
				if(s==null || s.trim().length()>0)
				{
					long start = System.currentTimeMillis();
					System.out.println("["+(count++)+"]现在在跑:  " + s);
					ParseResult pr = PhraseParserUtil.parse(parser, s, "ALL", null);
					long end = System.currentTimeMillis();
					System.out.println("\tparse ["+ (end-start) + "ms]");
					
					//condition
					try {
						ConditionBuilder.buildCondition(pr, s,null);
					} catch (BacktestCondException e) {
						e.printStackTrace();
					}
					long c_time = System.currentTimeMillis();
					System.out.println("\tcondition ["+ (c_time-end) + "ms]");
					
					//lightparser
					LightParserServletParam requestParam = new LightParserServletParam(s, "news", null);
					Writer out = new PrintWriter(new ByteArrayOutputStream());
					LightParser.createLightParserJson(requestParam, out);
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					long l_time = System.currentTimeMillis();
					System.out.println("\tlightparser_include_parse ["+ (l_time-c_time) + "ms]");
					
					//TODO totalparser
					
					end = System.currentTimeMillis();
					System.out.println("\ttotal ["+ (end-start) + "ms]");
				}
			}
		}
	}
	
	private static void loadAndParse(String fileName, int offset) {
		BufferedReader br = null;
		try{
			List<String> list = new ArrayList<String>(1000);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String s = null;
			int i=0;
			count +=offset;
			while( (s=br.readLine())!=null) {
				if(i++<offset) {
					continue;
				}
				list.add(s);
				if(list.size()>999) {
					parse(list);
					list.clear();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length<2) {
			System.out.println("please enter query.txt start_offset");
			System.exit(1);
		}
		int offset = Integer.parseInt(args[1]);
		
		loadAndParse(args[0], offset);
	}

}
