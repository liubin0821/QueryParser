package com.myhexin.qparser.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LogAnalisys {

	
	static class Num {
		int max;
		int min;
		int total;
		int count=0;
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File f = new File("D:/TDDOWNLOAD/solr_warning.log");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f) ));
		String s = null;
		
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		Map<String, Num> numMap = new HashMap<String, Num>();
		
		//File f2 = new File("D:/TDDOWNLOAD/solr_warning2.log");
		//BufferedWriter out = new BufferedWriter(new FileWriter(f2));
		
		
		while( (s=br.readLine())!=null) {
			int idx = s.indexOf(" WARN ");
			int idx2 = s.indexOf(" ERROR ");
			if(idx>0 || idx2>0) {
				System.out.println(s);
				//out.write(s + "\n");
				
				int timeVal = -1;
				int idx3= s.indexOf("timeout =");
				if(idx3>0) {
					int idx4 = s.indexOf(",", idx3+"timeout =".length());
					
					if(idx4>0) {
						String time = s.substring(idx3+"timeout =".length(), idx4);
						try{
							if(time!=null) time=time.trim();
							timeVal = Integer.parseInt(time);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
				
				Integer value = null;
				String name = null;
				Num num = null;
				if(s.indexOf("TdbClient")>0) {
					name = "TdbClient";
					value = countMap.get(name);
					num = numMap.get(name);
				}else if(s.indexOf("HttpPostClient")>0) {
					name = "HttpPostClient";
					value = countMap.get(name);
					num = numMap.get(name);
					
				}else if(s.indexOf("IfindClient")>0) {
					name = "IfindClient";
					value = countMap.get(name);
					num = numMap.get(name);
					
				}else if(s.indexOf("gZip")>0) {
					name = "gZip";
					value = countMap.get(name);
					num = numMap.get(name);
					
				}else if(s.indexOf("unGZip")>0) {
					name = "unGZip";
					value = countMap.get(name);
					num = numMap.get(name);
					
				}else if(s.indexOf("answerSetSetCache")>0) {
					name = "answerSetSetCache";
					value = countMap.get(name);
					num = numMap.get(name);
				}else if(s.indexOf("fetchCacheByteArray")>0) {
					name = "fetchCacheByteArray";
					value = countMap.get(name);
					num = numMap.get(name);
				}else if(s.indexOf("strSetCache")>0) {
					name = "strSetCache";
					value = countMap.get(name);
					num = numMap.get(name);
				}else if(s.indexOf("fetchCacheAnswerSet")>0) {
					name = "fetchCacheAnswerSet";
					value = countMap.get(name);
					num = numMap.get(name);
				}else if(s.indexOf("fetchCacheStr")>0) {
					name = "fetchCacheStr";
					value = countMap.get(name);
					num = numMap.get(name);
				}
			
				
				if(value==null) value = 0;
				if(num==null ) num = new Num();
				
				if(timeVal>=0) {
					if(num.min<=0 || num.min>timeVal) num.min = timeVal;
					if(num.max<timeVal) num.max = timeVal;
					num.count = num.count +1;
					
					num.total += timeVal;
				}
				
				value = new Integer(value+1);
				countMap.put(name, value);
				numMap.put(name, num);
			}
		}
		
		Set<String> keys  = countMap.keySet();
		System.out.println("function name, count, max,min,avg");
		for(Iterator<String> it = keys.iterator(); it.hasNext(); ) {
			String name = it.next();
			Integer value = countMap.get(name);
			System.out.print(name  + ", " + value + " : ");
			Num num = numMap.get(name);
			if(num.count<=0) num.count=1;
			System.out.println(num.max  + "," + num.min + "," + num.total/num.count);
		}
		
		br.close();
		//out.close();
	}

}
