package com.myhexin.qparser.util.lightparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.myhexin.DB.mybatis.mode.SearchIdConfig;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.resource.ResourceInst;

/**根据id_list及配置生成主搜索语义字段:
 * news_search,report_search,pubnote_search,navigation_search,usersite_search*/
public class CreateIdSearchResult{
	
  private static final String ALL_QUERY_ID  = "ALL";
  
	private static Map<String, Pattern> regPatternMap = new HashMap<String, Pattern>();
	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(CreateIdSearchResult.class.getName());
	
	public static String searchIdList(String idList,List<SemanticNode> node_list, String channel){
		//long start = System.currentTimeMillis();
	  //idList = "{\"types\":[{\"id\":\"54052\"}]}";
	  
    List<SearchIdConfig> searchIdConfigs= null;
    Map<String, List<SearchIdConfig>> configs = ResourceInst.getInstance().getSearchIdConfigs();
    if (channel != null && configs.containsKey(channel)) {
      searchIdConfigs = configs.get(channel);
    } else {
      return null;
    }
    
    if (searchIdConfigs == null || searchIdConfigs.size() == 0) {
      return Consts.STR_BLANK;
    }
	  
    JSONObject idListJson = null;
    Set<String> ids = new HashSet<String>();
		if ( idList != null && idList.length() > 0) {
		  try {
	      idListJson = new JSONObject(idList);//str to json
	    } catch(Exception e) {
	      return null;
	    }
	    if (!idListJson.has("types"))  return Consts.STR_BLANK;
	    JSONArray idListTypes = idListJson.getJSONArray("types");
	    
	    
	    for (int i = 0; i < idListTypes.length(); i++) {
	      JSONObject node = new JSONObject(idListTypes.get(i).toString());
	      String id = getValueOfNode(node, "id");
	      if(id!=null)
	        ids.add(id);
	    }
		}

		Set<String> udps = new HashSet<String>();
		Set<String> others = new HashSet<String>();
		Set<String> replacements = new HashSet<String>();
		for (SearchIdConfig searchIdConfig : searchIdConfigs){
			if (ids.contains(String.valueOf(searchIdConfig.getIdnum()))
			    || ALL_QUERY_ID.equals(String.valueOf(searchIdConfig.getIdnum()))) {
				String udp = getUdpResult(searchIdConfig.getUdp(),node_list);
				if (udp != null && udp.length() > 0){
					udp = udp.replaceAll("=", "%3D");
					udp = udp.replaceAll("&", "%15");
					udps.add(udp);
				}
				
				String other = getUdpResult(searchIdConfig.getOther(),node_list);
				if (other != null && other.length() > 0)
					others.add(other);
				
				String replacement = getUdpResult(searchIdConfig.getReplacement(), node_list);
				if (replacement != null && replacement.length() > 0) 
				  replacements.add(replacement);
			}
		}
		
		StringBuilder result = new StringBuilder();
		if(udps.size()>0){
			result.append("udp=");
			int i = 0;
			for (String udp : udps) {
			  if (i > 0) result.append("%15");
			  result.append(udp);
			  i++;
			}
		}
		if(others.size()>0){
      if(result.length()>0) result.append("|001");
      result.append("udf=");
      int i = 0;
      for (String other : others) {
        if (i > 0) result.append("&");
        result.append(other);
        i++;
      }
    }
    if (replacements.size() > 0) {
      if(result.length()>0) result.append("|001");
      result.append("replace=");
      int i = 0;
      for (String replacement : replacements) {
        if (i > 0) result.append(",");
        result.append(replacement);
        i++;
      }
    }
//		System.out.println("result:"+result.toString());
		String ret = "\""+result.toString()+"\"";
		//long end = System.currentTimeMillis();
		//logger_.info("[searchIdList] TIME=" + (end-start) + "ms");
		return ret;
	}
	
	private static String getUdpResult(String udp,List<SemanticNode> node_list){
		if(udp==null || udp.length()==0)	return null;
		List[] lists=getComponentOfRule(udp);
		List<String> expectedList=lists[0];//记录需要取出的字段
		List<String> expectedInfoList=lists[1];//记录需要取出字段的信息类型(默认为text,可能为id等)
		List<List<String>> expectedResultList=new ArrayList<List<String>>();
		for(int i=0;i<expectedList.size();i++){
			expectedResultList.add(new ArrayList<String>());
		}
		if(expectedList.size()==0){
			return udp;
		}else{
			/**提取*/
			for(int i=0;i<node_list.size();i++){
				//JSONObject node = new JSONObject(node_list.get(i).toString());
				SemanticNode node = node_list.get(i);

//				String strSubType = getValueOfNode(node, str_sub_type);
//				String text = getValueOfNode(node, "text");
//				String info = getValueOfNode(node, "info");
//				System.out.println("str_sub_type:"+strSubType+"\ttext:"+text+"\tinfo:"+info);
				for(int index=0;index<expectedList.size();index++){
          String expectedStrSubType = expectedList.get(index);
          String expectedInfo=expectedInfoList.get(index);
//          System.out.println("expected:"+expectedStrSubType+"\t"+expectedInfo+"\ttext:"+text+"\tinfo:"+info);
          expectedResultList=getExpectedResultFromInfo(node, expectedInfo, expectedStrSubType, expectedResultList, index);
        }
			}
			/**填充*/
			String result=fillResult(udp, expectedList, expectedInfoList, expectedResultList);
			return result;
		}
	}
	
	/**
	 * 把匹配到的信息填充到规则中
	 * @param rule 规则
	 * @param expectedList
	 * @param expectedInfoList
	 * @param expectedResultList
	 * @return
	 */
	private static String fillResult(String ruleStr,List<String> expectedList,List<String> expectedInfoList,List<List<String>> expectedResultList){
		for(int i=0;i<expectedList.size();i++){
			String strSubType=expectedList.get(i);
			String infoType=expectedInfoList.get(i);
			List<String> result=expectedResultList.get(i);
			if(result.size() == 0){
				return null;
			}
			infoType=(infoType.length()==0)?"":("."+infoType);
			String key="#"+strSubType+infoType+"#";
			String value=result.toString();
			ruleStr=ruleStr.replace(key, value.substring(1, value.length()-1));
		}
		return ruleStr;
	}
	
	/**获取json中key字段的信息*/
	private static String getValueOfNode(JSONObject node,String key){
		return node.has(key)?node.getString(key):null;
	}
	
	/**
	 * 从一个node节点中取出规则中需要的信息
	 * @param text node的text信息
	 * @param info node的info信息
	 * @param expectedInfo 规则中需要的信息类型,默认为text,可能为id等
	 * @param strSubType node的strSubType类型
	 * @param expectedResultList
	 * @param index
	 * @return
	 */
	private static List<List<String>> getExpectedResult(String text,String info,String expectedInfo,String strSubType,List<List<String>> expectedResultList,int index){
		String result=text;
		if(expectedInfo.length()>0 && info!=null){//不取默认的text,而是info中的id等其它信息
			String regex=String.format("\\[%s\\]:[^|]*%s:([^|&]*)", strSubType,expectedInfo);
			Pattern pattern = regPatternMap.get(regex);
			if(pattern==null) {
				pattern = Pattern.compile(regex);
				regPatternMap.put(regex, pattern);
				if(regPatternMap.size()>100)
					logger_.info("[WARNING] regPatternMap.size=" + regPatternMap.size()) ;
			}
			
			//Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(info);
			int find=0;
			while(matcher.find()){
				result=matcher.group(1);
				find=1;
			}
			if(find == 0) result=null;
		}else if(expectedInfo.length()>0 && info==null)
			result = null;
		if(result != null){
			List<String> list=expectedResultList.get(index);
			list.add(result);
			expectedResultList.set(index, list);
		}
		return expectedResultList;
	}
	
	/**
	 * 如果一个节点的str_sub_type没有或者为空字符串,则从info中先匹配是否包含这个类型,如果包含的话取出相应的信息
	 * @param text
	 * @param info
	 * @param expectedInfo
	 * @param expectedStrSubType
	 * @param expectedResultList
	 * @param index
	 * @return
	 */
	private static List<List<String>> getExpectedResultFromInfo(SemanticNode node, String expectedInfo,String expectedStrSubType,List<List<String>> expectedResultList,int index){
		String result=null;
    String text = node.getText();
    String info = null;
    if(node.isStrNode() ) {
      StrNode strNode = (StrNode)node;
      info = strNode.getInfo();
    }
		if(expectedInfo.length()>0 && info!=null){//不取默认的text,而是info中的id等其它信息
			String regex=String.format("\\[%s\\]:[^|]*%s:([^|&]*)", expectedStrSubType,expectedInfo);
			//pattern预存起来,能省则省
			Pattern pattern = regPatternMap.get(regex);
			if(pattern==null) {
				pattern = Pattern.compile(regex);
				regPatternMap.put(regex, pattern);
				if(regPatternMap.size()>100)
					logger_.info("[WARNING] regPatternMap.size=" + regPatternMap.size()) ;
			}
			Matcher matcher = pattern.matcher(info);
			while(matcher.find()){
				result=matcher.group(1);
			}
		} else if(expectedInfo.length()==0 && node.isStrNode() && ((StrNode) node).hasSubType(expectedStrSubType)){//取默认的text,但是需要从info中先匹配到

		  result=text;
		}
		if(result != null){
			List<String> list=expectedResultList.get(index);
			list.add(result);
			expectedResultList.set(index, list);
		}
		return expectedResultList;
	}
	
	private static Pattern sharpsharpPattern = Pattern.compile("#(.*?)#");
	
	/**
	 * 解析规则,从中取出需要从node中获取的信息
	 * @param rule 数据库中配置的规则
	 * @return list[]数组:0为需要取出的字段;1为需要取出字段的信息类型
	 */
	private static List[] getComponentOfRule(String rule){
		
		List<String> expectedSubTypeList=new ArrayList<String>();
		List<String> expectedSubTypeInfoList=new ArrayList<String>();
		Matcher matcher = sharpsharpPattern.matcher(rule);
		while(matcher.find()){
			String value=matcher.group(1);
			if(value.contains(".")){
				String[] valueArray=value.split("\\.");
				expectedSubTypeList.add(valueArray[0]);
				expectedSubTypeInfoList.add(valueArray[1]);
			}else{
				expectedSubTypeList.add(value);
				expectedSubTypeInfoList.add("");
			}
		}
		List[] lists={expectedSubTypeList,expectedSubTypeInfoList};
		return lists;
	}

}