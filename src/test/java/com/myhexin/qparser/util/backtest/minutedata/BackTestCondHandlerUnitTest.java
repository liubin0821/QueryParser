package com.myhexin.qparser.util.backtest.minutedata;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.parsePlugins.MatchSyntacticPatterns.MatchSyntacticPatterns1;
//import com.myhexin.qparser.util.MemoryUtil;
//import com.myhexin.qparser.util.MemoryUtil;
import com.myhexin.qparser.util.backtest.minutedata.MinuteDataBackTestQueryParser;

/**
 * 转回测接口单元测试
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2014-11-27
 *
 */
public class BackTestCondHandlerUnitTest {
	/*public static String readFile(String filePath) {
		StringBuilder builder = new StringBuilder();
		BufferedReader br = null;
		try {
		br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
		String s = null;
		
		while ((s = br.readLine()) != null) {
			builder.append(s).append("\n");
		}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return builder.toString();
	}*/
	
	
	//TODO 增加更多测试用例
	

	/*int start = boundaryInfos.bStart;
	int end = boundaryInfos.bEnd;*/
	public static void print(List<SemanticNode> nodes) {
		
		if(nodes.get(0).type==NodeType.ENV){
			  Environment listEnv = (Environment) nodes.get(0);
			  if (listEnv.containsKey("listDomain" )) {  
				  Map.Entry[] sortedListDomain = ( Map.Entry[]) listEnv.get("listDomain", false);     
				  System.out.println(sortedListDomain);
			  }
		}
		
			Iterator<BoundaryInfos> iterator = new SyntacticIteratorImpl(nodes);
			while (iterator.hasNext()) {
	    		BoundaryInfos boundaryInfos = iterator.next();
	    		BoundaryNode bNode = (BoundaryNode) nodes.get(boundaryInfos.bStart);
	    		BoundaryNode.SyntacticPatternExtParseInfo info = bNode.getSyntacticPatternExtParseInfo(false);
	    		ArrayList<Integer> elelist;
	    		for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
	    			for (int pos : elelist) {
	    				SemanticNode defaultNode = null;
	    				if (pos == -1) {
	    					defaultNode = info.absentDefalutIndexMap.get(j);
	    					if(defaultNode!=null)
		    					System.out.println("# Node[" + pos + "] = " + defaultNode.toString());
	    				} else {
	    					//add by wyh 以绑定 不显示
	    					int index = boundaryInfos.bStart + pos;
	    					if (index>0 && index<nodes.size() && nodes.get(index)!=null && nodes.get(index).type == NodeType.FOCUS) {
	    						FocusNode fn = (FocusNode) nodes.get(index);
	    						if(fn.hasIndex() && (fn.isBoundToIndex() || fn.isBoundToSynt)) {
	    							defaultNode = fn;
	    						}
	    					}
	    					if(defaultNode!=null)
	    						System.out.println("# Node[" + index + "] = " + defaultNode.toString());
	    				}
	    				
	    			}
	    		}
			}
			
			for(int i=0;i<nodes.size();i++) {
				SemanticNode n = nodes.get(i);
				System.out.println("# Node[" + i + "] = " + n.toString());
			}
	}
	
	/**
	 * 测试代码
	 * 
	 * 
	 * @param args
	 * @throws UnexpectedException 
	 * @throws BacktestCondException 
	 */
	public static void main(String[] args) throws BacktestCondException, UnexpectedException {
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();

		String query = null;
		String expectedResult = null;
		String domain = null;
		// 解析问句读取
//		BufferedReader br = null;
//		try {
//			
//			String folder = BacktestTestCaseConfig.getProp("file_path");
//			String file = BacktestTestCaseConfig.getProp("unit_test");
//			
//			String filePath = folder + file;
//			
//			
//		br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
//		query = br.readLine();
//		domain = br.readLine();
//		if(domain!=null && !domain.startsWith("abs_") ) domain = null;
//		expectedResult = br.readLine();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(-1);
//		}finally {
//			if(br!=null) {
//				try {
//					br.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		
		//query = "开盘1小时内 连续5分钟股价低于均价2%";
		query = "9:30-10:30 连续5分钟股价低于均价2%";
		
	    // query = "9:30-10:30 股价创10分钟最低";

		//query = "macd死叉 超过10分钟";
		
		MinuteDataBackTestQueryParser.parseMinuteDataQuery(query);
		
		// phraseParser配置于qphrase.xml文件
		//PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		//parser.setSplitWords("; ");

		
		//BacktestCondHandler handler = new BacktestCondHandler();

		long beforeTime = System.currentTimeMillis();
		//String result = handler.compileToCond(query, "2012-07-01");
		//ParseResult pr = SemanticCondCompiler.parse(query, null);
		String postData = "{\"length\":\"0\",\"dunit\":\"0\",\"start\":\"2012-07-01\"}";
		//List<BackTestCondAnnotation> results = SemanticCondCompiler.getInstance().compileToCond(query, "Stock", domain, postData);
			
		long afterTime = System.currentTimeMillis();
		long timeDistance = afterTime - beforeTime;
		
		//System.out.println(handler.getLogMsg());
		
		System.out.println("Unittest - backtestJson:");
		System.out.println(query);
		System.out.println("Unittest - old = " + expectedResult);
		//System.out.println("new retire   = " + result);
		//MemoryUtil.print();
		//MemoryUtil.print();
		//result = getResult(isXml, query, chunk, postDataStr, timeDistance, resultAnnotation);
		//String result = SemanticCondCompiler.getResult("1", query, "1", "", timeDistance, results);
		//System.out.println("result " + result);
		//print(pr.qlist.get(0));
		
		/*try{
			List<String> list = MatchSyntacticPatterns1.logList;
			FileOutputStream fos = new FileOutputStream("D:/syntaxPatterns.txt");
			for(String s : list) {
				//System.out.println(s);
				fos.write(s.getBytes());
				fos.write(("\n").getBytes());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}*/
		
	}
}

/*
int maxSize = pattern.getSyntacticElementMax();
System.out.println("#### SyntacticElement maxSize : " + maxSize);
for(int i=1;i<maxSize;i++) {
	SyntacticElement sElem = pattern.getSyntacticElement(i);
	System.out.println("#### SyntacticElement : " + sElem);
	if(sElem.getArgumentType()!=null) {
		syntaxPattern.append(sElem.getArgumentType());
	}else{
		if(sElem.getSyntElemType() == SyntElemType.KEYWORD) {
			syntaxPattern.append(SyntElemType.KEYWORD);
		}
	}
	
	if(sElem.getSyntElemType() == SyntElemType.KEYWORD) {
		//String keyword = sElem.getKeyword();
		String keywordGroup = sElem.getKeywordGroup();
		//System.out.println("#### keyword : " + keyword);
		//System.out.println("#### keywordGroup : " + keywordGroup);
		
		KeywordGroup kwg = PhraseInfo.getKeywordGroup(keywordGroup);
		if(kwg!=null && kwg.getKeywords()!=null && kwg.getKeywords().size()>0) {
			//indexProperties.add(kwg.getKeywords().get(0));
			buf.append(kwg.getKeywords().get(0));
		}
		
	}else{
		if(sElem.getArgumentType() == SemanticArgument.SemanArgType.INDEX) {
			String indexName = this.getIndexName(subnodes);
			dataCond.setIndexName(indexName);
		}else if(sElem.getArgumentType() == SemanticArgument.SemanArgType.CONSTANT) {
			int index = i; 
			if(index < subnodes.size()) {
				SemanticNode node = subnodes.get(index);
				if(node!=null) {
					buf.append(node.text);
				}
			}
		}
	}
}
}

indexProperties.add(buf.toString());

System.out.println("#### syntaxPattern : " + syntaxPattern.toString());
//如果没有找到,这里补救一下
if(dataCond.getIndexName()==null) {
dataCond.setIndexName("UNKNOW");
}
dataCond.setSonSize(0);
dataCond.setType("index");
dataCond.setIndexProperties(indexProperties);

list.add(dataCond);*/
