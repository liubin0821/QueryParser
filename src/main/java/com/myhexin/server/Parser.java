package com.myhexin.server;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.OutputResult;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseParser;

public class Parser {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Parser.class.getName());
	
	public static PhraseParser parser;// = new PhraseParser("./conf/qparser.conf", null);
	
	static{

		// phraseParser配置于qphrase.xml文件
		parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
	}
	
	public static String parserQuery(ParserItem item) {
		return parserQuery(item, false);
	}
	
	public static String parserQuery(ParserItem item, boolean yunyin) {
		String query = item.getInitQuery();
		String qType = item.getQType();
		if (query == null || query.trim().length() == 0) {
			return "<tr><td>### ERROR ###</td></tr>\n";
		}
		
		StringBuffer sb = new StringBuffer();

        parser.setSplitWords("_&_");
		Query q = new Query(query.toLowerCase(), qType);
		ParseResult ret = null;
        try {
        	ParserAnnotation annotation = new ParserAnnotation();
        	annotation.setQueryText( q.text);
        	annotation.setQuery(q);
        	annotation.setQueryType(q.getType());
        	annotation.setWriteLog(true);
        	annotation.setBacktestTime(item.getBacktestTime());
        	if(item.getSkipSet()!=null){
        		annotation.setPluginSkipSet(item.getSkipSet());
        	}
        	if(item.getLogtime()!=null && item.getLogtime().equals("1")) {
        		annotation.setLogTime(true);
        	}
        	
        	if(yunyin) {
        		//yunyinparser有log
        		ret = parser.parseForYunyin(annotation);
        	}else{
        		 ret = parser.parse(annotation);
        	}
		   
        } catch(Exception e) {
            sb.append("<tr><td>"+ExceptionUtil.getStackTrace(e)+"</td></tr>\n");
            return sb.toString();
        }

		if (ret != null) {
			String log = ret.processLog;
			int id= 1;
			String[] lines = log.split("\n");
			for (int i = 0; i < lines.length; i ++) {
				String line = lines[i];
				if (line.startsWith("##")) {
					sb.append("<tr style='color:red'><td>").append(line).append("</td></tr>");
				}else if(line.startsWith("#plugin#") ){
					if(item.isShowPluginDetail() )
						sb.append("<tr style='color:#313233;font-size:10pt;'><td>").append(line).append("</td></tr>");
				} else {
					if(line.indexOf("NodeType:")>=0) {
						sb.append("<tr><td><A HREF='javascript:void(0);' onclick='showOrHidden(\"detail_"+id+"\")' >Show/Hidden</A><div id='detail_"+id+"' style='display:none;'>").append(line).append("</div></td></tr>");
						id++;
					}else{
						sb.append("<tr><td><div id='detail_"+id+"' style='display:block;'>").append(line).append("</div></td></tr>");
						id++;
					}
					
				}
			}
			item.setStandardQueryList(ret.standardQueries);
			item.setScores(ret.standardQueriesScore);
			List<String> syntSmeanIdsList = new ArrayList<String>();
			if (ret.standardQueriesSyntacticSemanticIds != null)
				for (List<String> syntSmeanIds : ret.standardQueriesSyntacticSemanticIds)
					syntSmeanIdsList.add(syntSmeanIds.toString());
			item.setSyntSmeanIdsList(syntSmeanIdsList);
			List<String> standardOutputList = new ArrayList<String>();
			List<String> jsonResultList = new ArrayList<String>();
			List<String> luaResultList = new ArrayList<String>();
			List<String> jsonResultListOfMacroIndustry = new ArrayList<String>();
			List<String> thematicList = new ArrayList<String>();
			List<String> lightParserResultList = new ArrayList<String>();
			List<String> multResultLists = new ArrayList<String>();
			List<String> IndexMultPossibilitys = new ArrayList<String>();
			
			if (ret.qlist != null) {
				for (List<SemanticNode> list : ret.qlist) {
					if (list != null && list.size() > 0) {
						if(list.get(0).type==NodeType.ENV){
							Environment listEnv = (Environment) list.get(0);
							if (listEnv.containsKey("luaResult"))
								luaResultList.add(listEnv.get("luaResult",String.class,false));
							else
								luaResultList.add("");
							
							if (listEnv.containsKey("jsonResult"))
								jsonResultList.add(listEnv.get("jsonResult",String.class,false));
							else
								jsonResultList.add("");
							
							if (listEnv.containsKey("jsonResultOfMacroIndustry"))
								jsonResultListOfMacroIndustry.add(listEnv.get("jsonResultOfMacroIndustry",String.class,false));
							else
								jsonResultListOfMacroIndustry.add("");
							
							if (listEnv.containsKey("thematic"))
								thematicList.add(listEnv.get("thematic",String.class,false));
							else
								thematicList.add("");
							
						}
						//add by 吴永行 2014-04-23
						OutputResult multResult = list.get(0).getMultResult();
						String firstOutput = multResult==null?null:multResult.getFirstOutput();
						if (firstOutput != null)
							standardOutputList.add(firstOutput);
						else
							standardOutputList.add("");
						
						if (multResult != null) 
							multResultLists.add(multResult.toString());
						else 
							multResultLists.add("");
						
						if (list.get(0).getIndexMultPossibility() != null) 
							IndexMultPossibilitys.add(list.get(0).getIndexMultPossibility().toString());
						else 
							IndexMultPossibilitys.add("");
						
						/*if (list.get(0).standardStatement != null)
							standardOutputList.add(list.get(0).standardStatement);
						else
							standardOutputList.add("");*/

						


						if (list.get(0).getLightParserResult() != null)
							lightParserResultList.add(list.get(0).getLightParserResult());
						else
							lightParserResultList.add("");
					} else {
						standardOutputList.add("");
						jsonResultList.add("");
						thematicList.add("");
						lightParserResultList.add("");
					}
				}
			}
			item.setStandardOutputList(standardOutputList);
			item.setJsonResultList(jsonResultList);
			item.setLuaResultList(luaResultList);
			item.setJsonResultListOfMacroIndustry(jsonResultListOfMacroIndustry);
			item.setThematicList(thematicList);
			item.setLightParserResultList(lightParserResultList);
			item.setMultResultLists(multResultLists);
			item.setIndexMultPossibilitys(IndexMultPossibilitys);
		}
		return sb.toString();
	}
}
