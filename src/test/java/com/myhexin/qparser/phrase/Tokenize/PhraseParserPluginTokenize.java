package com.myhexin.qparser.phrase.Tokenize;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import junit.framework.TestCase;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.parsePrePlugins.PhraseParserPrePluginAbstract;
import com.myhexin.qparser.phrase.parsePrePlugins.PhraseParserPrePluginWordSegment;
import com.myhexin.qparser.phrase.parsePrePlugins.PhraseParserPrePluginWordSegmentPostTreat;
import com.myhexin.qparser.tokenize.Tokenizer;

public class PhraseParserPluginTokenize extends TestCase{
	
	public void testTokenize(){
		 ApplicationContextHelper.loadApplicationContext();     
		 ArrayList<String> lists = new ArrayList<String>(); 
		 try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("query.txt"),"utf-8"));   //read query.text
				boolean flag = true;
				String s = null;
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
						lists.add(s);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} 
		  StringBuilder log_sb = new StringBuilder(); 
		  for (String ss : lists) {
				
				Query query=new Query(ss.toLowerCase());
				ParserAnnotation pAnnotation = new ParserAnnotation();
				pAnnotation.setQuery(query);
				pAnnotation.setStopProcessFlag(false);
				pAnnotation.setQueryText(query.text);
				PhraseParserPrePluginAbstract ssss=new PhraseParserPrePluginWordSegment();           //make requirements first( wordSegment: get SegmentedText in ParserAnnotation)
				ssss.init();
				ssss.process(pAnnotation);
				
				PhraseParserPrePluginAbstract sss=new PhraseParserPrePluginWordSegmentPostTreat();    //make requirements second( wordSegment:get SegmentedText in ParserAnnotation)
				sss.init();
				sss.process(pAnnotation);
				log_sb.append(String.format("%s\n", sss.getPluginDesc()));
				log_sb.append(String.format("%s\n", sss.getLogResult(pAnnotation)));
			
				
			   String segmentedText = pAnnotation.getSegmentedText();  //get above SegmentedText
		       ArrayList<ArrayList<SemanticNode>> qlist = null;
		       if (segmentedText != null && segmentedText.length()>0) {
	                long start = System.currentTimeMillis();
	            	Tokenizer.tokenize(pAnnotation); //ENV, query, qstrlist.get(0));
	            	qlist = pAnnotation.getQlist();
		            long end = System.currentTimeMillis();
		            
			            log_sb.append(String.format("## %dms %s\n", end-start, "## Tokenize..."));
			            for(int i=0 ; qlist != null && i < qlist.size(); i++) {
			            log_sb.append(String.format("[match %d]: %s\n", i, qlist.get(i)));
			            }
			            PhraseParser pparser=new PhraseParser(null, null, null);   //前提getSentence改为public,
			            //getSentence is private,导致compile error
			            //log_sb.append(String.format("%s\n",pparser.getSentence(qlist, true)));//根据节点类型输出
			            //assertEquals("(pe)(>)(10)",pparser.getSentence(qlist, true));
	            }
		       System.out.println(log_sb);
		   	 
				
		   }
		  
	 
		 
	}

}
