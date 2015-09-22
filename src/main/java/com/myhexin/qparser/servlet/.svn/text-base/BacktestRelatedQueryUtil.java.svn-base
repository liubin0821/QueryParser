package com.myhexin.qparser.servlet;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.util.condition.ConditionParser;
import com.myhexin.qparser.util.condition.model.BackTestCondAnnotation;

public class BacktestRelatedQueryUtil {
	 /**
     * 相关问句接口
     * 
     */
    public static String compileToCondWithMoreInfo(String query, String qType, String domain, String postDataStr) {
    	
        String backtestTimeStr = ConditionParser.getBackTestTimeFromJson(postDataStr);
        Calendar backtestTime = ConditionParser.getBackTestTime(backtestTimeStr);
        //解析, 转condition
    	if(query!=null && query.length()>0) {
        	ParseResult pr = ConditionParser.parse(query, qType, domain,backtestTime);
        	List<BackTestCondAnnotation> jsonResults = ConditionParser.compileToCond(query, qType, domain, null);
        	return getMoreResult(pr, jsonResults, query);
    	}
    	return null;
    }
	
    /**
     * 为了调用相关问句，语义解析接口至少还需要返回问句的分词、语义节点、指标这3个数。
     * @param pr
     * @param jsonResults
     * @param query
     */
    private static String getMoreResult(ParseResult pr,List<BackTestCondAnnotation> jsonResults,String query){
		JSONObject json = new JSONObject();
		json.put("query", query);
		
		if(pr.standardQueriesScore!=null && pr.standardQueriesScore.size()>0) {
			if(pr.standardQueriesScore.get(0) > 0)
				json.put("parseStatus", "SUCCESS");
			else
				json.put("parseStatus", "FAIL");
		}
		
		if(pr.wordSegment!=null && pr.wordSegment.size()>0){
			String wordSegment = pr.wordSegment.get(0);
			String[] segments = wordSegment.split("\\t");
			JSONArray jsonArray = new JSONArray();
			for(String segment:segments){
				if(segment.equals(" /"))	continue;
				JSONObject jsonObject = new JSONObject();
				String[] words=segment.split("/");
				if(words.length==2){
					jsonObject.put("word", words[0]);
					String pos=words[1];
					if(pos.endsWith(":"))
						pos=pos.substring(0, pos.length()-1);
					jsonObject.put("pos", pos);
				}else if(words.length==1){
					jsonObject.put("word", words[0]);
					jsonObject.put("pos", "");
				}
				jsonArray.put(jsonObject);
			}
			json.put("wordPosList", jsonArray);
		}
		
		if(pr.qlist!=null && pr.qlist.size()>0){
			ArrayList<SemanticNode> nodes = pr.qlist.get(0);
			Set<Type> domains = new HashSet<Type>();
			for(SemanticNode node : nodes){
				if(node.isFocusNode()){
					FocusNode focusNode = (FocusNode) node;
					ClassNodeFacade cNode = focusNode.getIndex();
					if(cNode == null)	continue;
					domains.addAll(cNode.getDomains());
				}
			}
			String domainsStr = domains.toString();
			json.put("queryType", domainsStr.substring(1, domainsStr.length()-1));
		}
		
		if(jsonResults.size()>0){
			String condJson = jsonResults.get(0).getResultCondJson();
			if(condJson != null){
				JSONArray condArray = new JSONArray(condJson);
				Set<String> indexs=new HashSet<String>();
				for(int j=0;j<condArray.length();j++){
					JSONObject obj = condArray.getJSONObject(j);
					if(obj.has("indexName"))
						indexs.add(obj.getString("indexName"));
				}
				json.put("parseTree", condJson);
				json.put("indexs", indexs);
				json.put("semanticNodes",getSemanticNodes(condJson));
			}else{
				json.put("parseTree", "");
				json.put("indexs", new JSONArray());
				json.put("semanticNodes",new JSONArray());
			}
			
			json.put("chunks", Consts.STR_BLANK);
			if(jsonResults.get(0).getPattern() != null)
				json.put("pattern", jsonResults.get(0).getPattern());
			else
				json.put("pattern", "");
		}
		return json.toString();
    }
    
    /***
     * 为了调用相关问句，语义解析接口提供语义节点识别的信息
     * @param condJson
     * @return
     */
    private static JSONArray getSemanticNodes(String condJson){
    	JSONArray jsonArray = new JSONArray(condJson);
    	JSONArray result = new JSONArray();
    	int index =0;
    	for(int i=0;i<jsonArray.length();i++){
    		JSONObject obj = jsonArray.getJSONObject(i);
    		JSONObject res = new JSONObject();
    		String opName = getValFromJson(obj, "opName");
    		String indexName = getValFromJson(obj, "indexName");
    		if(opName!=null){
    			res.put("text",opName.toUpperCase());
    			String oper=opName;
    			if(opName.equalsIgnoreCase("and")||opName.equalsIgnoreCase("or")){
    				oper="logic";
    			}else if(opName.equals("+")&&opName.equals("-")
    					&&opName.equals("*")&&opName.equals("/")){
    				oper="arith";
    			}
				res.put("type", oper.toUpperCase());
				res.put("index", index++);
				res.put("idList",new JSONArray());
				result.put(res);
    		}else if(indexName!=null){
    			res.put("type", "INST");//指标类型
				res.put("index", index++);
				res.put("idList",new JSONArray());
				result.put(res);
				
				res = new JSONObject();//指标信息
				res.put("text",indexName);
				res.put("type","CLASS");
				res.put("index", index++);
				res.put("idList",new JSONArray());
				result.put(res);
				
				if(obj.has("indexProperties")){
					JSONArray indexProp = obj.getJSONArray("indexProperties");
					for(int j=0;j<indexProp.length();j++){
						String prop= indexProp.getString(j);
//						boolean match=Pattern.compile("^(&lt;|<|&lt;=|<=|=|==|&gt;|>|&gt;=|>=|包含)\\d+$").matcher(prop).matches();
						if(!prop.startsWith("交易日期")){
							String attrType="",attrVal="",attrValType="NUM";
							String[] attrs=prop.split(" ");
							if(attrs.length==1){
								attrType="_浮点型数值";
								attrVal=prop;
								//attrValType="NUM";
							}else if(attrs.length==2){
									attrType=attrs[0];
									attrVal=attrs[1];
									//attrValType="MUM";
							}else{
								//logger_.error("attrs length is not 1 or 2");
							}
							res = new JSONObject();//属性类型
							res.put("text",attrType);//属性名未知
							res.put("type","PROP");
							res.put("index", index++);
							res.put("idList",new JSONArray());
							result.put(res);
							
							res = new JSONObject();//属性信息
							res.put("text",attrVal);//
							res.put("type",attrValType);//类型未知
							res.put("index", index++);
							res.put("idList",new JSONArray());
							result.put(res);
						}
					}
				}
    		}
    	}
    	return result;
    }
    
    private static String getValFromJson(JSONObject json,String key){
    	if(json.has(key))
    		return json.getString(key);
    	return null;
    }

    
}
