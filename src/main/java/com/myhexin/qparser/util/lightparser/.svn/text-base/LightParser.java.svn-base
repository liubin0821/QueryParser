package com.myhexin.qparser.util.lightparser;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.util.freemarker.TemplateResultBuilder;

public class LightParser {
	private static TemplateResultBuilder templateBuilder = new TemplateResultBuilder(Param.LIGHT_PARSER_TEMPLATES_PATH);

	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(LightParser.class.getName());
	private static PhraseParser lightParser;
	private static String splitWords = "_&_";
	private static Pattern LONGTOU_TYPE = Pattern.compile("(.*?)(行业|区域|概念)龙头");

	static {
		// parserPluginsLight配置于qparser_plugins_light.xml文件
		lightParser = (PhraseParser) ApplicationContextHelper.getBean("parserPluginsLight");
		lightParser.setSplitWords(splitWords);
	}

	// 解析
	private static ParseResult parse(LightParserServletParam requestParam) {
		ParseResult ret = null;

		if (requestParam==null || requestParam.isQueryEmpty() || requestParam.isQueryTooLong()) { return ret; }
		
		Query q = new Query(requestParam.getQuery().toLowerCase());
		HashMap<String, Object> parameterMap = new HashMap<String, Object>();;
		if (requestParam.getChannel() != null) {
			parameterMap.put("channel", requestParam.getChannel());
		}

		parameterMap.put("ms_ltp", requestParam.getMs_ltp());
		
		try {
			ParserAnnotation annotation = new ParserAnnotation();
			annotation.setQueryText(q.text);
			annotation.setQuery( q);
			annotation.setQueryType(q.getType());
			//annotation.setIsShowIdList(requestParam.isShowId_List());
			
			ret = lightParser.parse(annotation, parameterMap);
		} catch (Exception e) {
			logger_.error("[LIGHTPARSER_ERROR] " + e.getMessage());
		}
		return ret;
	}

	/**
	 * servlet端用于生成lightparserjson
	 * 
	 * @param requestParam LightParserServletParam
	 * @param out Writer
	 */
	public static void createLightParserJson(LightParserServletParam requestParam, Writer out) {
		LightParserTemplate template = new LightParserTemplate();
		if(requestParam!=null && requestParam.getQuery()!=null)
			template.query = requestParam.getQuery();

		ParseResult ret = parse(requestParam);
		if(ret==null || ret.standardQueriesSyntacticSemanticIds == null|| ret.qlist == null || ret.qlist.size() == 0){
		  createJson(template.getTemplateDataMap(), "lightparser", out);
			return ;
		}
		
		//取解析结果
		for (int i = 0; i < ret.qlist.size(); i++) {
			if(ret.qlist.get(i)!=null) {
				getLightParserResult(ret.qlist.get(i),template, requestParam);
				createJson(template.getTemplateDataMap(), "lightparser", out);
				return ;
			}
		}
		//最后一道防线
		createJson(template.getTemplateDataMap(), "lightparser", out);
	}
	
	private static void getLightParserResult(ArrayList<SemanticNode> nodes,LightParserTemplate template, LightParserServletParam requestParam) {
		if (nodes == null || nodes.size() == 0)
			return;
		
		List<String> thematics = null;
		if(nodes.size()>0 && nodes.get(0)!=null)
			thematics = nodes.get(0).getThematics();
		if (thematics == null )
			thematics = new ArrayList<String>();
		if(thematics.size() == 0)
			thematics.add("UNKNOWN");
		
		//类型在lightparser_type.xml中共13种,类型有先后顺序,在前的首先被匹配--是否有问题?
		for (String type : LightParserTypeConf.getLightParserTypes()) {
			if (type!=null && thematics.contains(type) && LightParserTypeConf.getLightParserTypeMap().get(type)!=null) {
				LightParserType lightParserType = LightParserTypeConf.getLightParserTypeMap().get(type);
				getLightParserResult(lightParserType, nodes,template, requestParam);
				return;
			}
		}
	}
	
	private static void getLightParserResult(LightParserType lightParserType, ArrayList<SemanticNode> nodes,LightParserTemplate template,LightParserServletParam requestParam) {
	  ArrayList<String> channels = new ArrayList<String>();
		ArrayList<String> subTypes = new ArrayList<String>();
		
		ArrayList<SemanticNode> nodeList = new ArrayList<SemanticNode>();
		boolean isComment = false;
		List<String> nodeTypeList = new ArrayList<String>();
		for (SemanticNode sn : nodes) {
			if (sn.type == NodeType.FOCUS) {
				FocusNode fn = (FocusNode) sn;
				if (fn.hasIndex()) {
					String channel = lightParserType.hasChannel(fn.getIndex().getText());
					if (channel != null && channel.trim().length() > 0)
						channels.add(channel);

					if (fn.getIndex().getText().equals("点评"))
						isComment = true;
					
					String subType = lightParserType.hasSubType(fn.getIndex().getText());
					if (subType != null){
						subTypes.add(subType);
					}else {  
						ArrayList<String> subTypesTemp = lightParserType.hasSubType(sn);
						if (subTypesTemp != null && subTypesTemp.isEmpty() == false) {
							subTypes.addAll(subTypesTemp);
							nodeList.add(sn);
						}
						else if (!fn.getIndex().getText().equals("点评")) {
							nodeList.add(sn);
						}
					}
				}
				else if(fn.hasKeyword()){
					nodeList.add(fn);
				}
			}
			
			StrNode strNode = getStrValInstance(sn);
			if (strNode != null) {
				String subType = lightParserType.hasSubType(strNode);
				String noAmbSubType = strNode.getNoAmbiguityType();
        if (!noAmbSubType.isEmpty()) { // EntityAmbiguity
          subTypes.add(noAmbSubType);
          nodeList.add(strNode);
          lightParserType.subTypes.add(new SubType(noAmbSubType, ""));
          if (isOnlyOneNode(nodes)) {
            lightParserType.label = "ENTITY";
            template.seg_status = "1";
          }
        } else if (subType != null && subType.trim().length() > 0) {
					subTypes.add(subType);
					nodeList.add(strNode);
				} else if (!sn.isIndexNode()) {
					nodeList.add(strNode);
				}
			}
			
			if (sn.type == NodeType.UNKNOWN && !isSepWord(sn.getText())){
				nodeList.add((UnknownNode)sn);
			}
			if (sn.type == NodeType.DATE && ((DateNode)sn).getDateinfo() != null){
				nodeList.add((DateNode)sn);
			}
			if (sn.type == NodeType.NUM){
				nodeList.add((NumNode)sn);
			}
		}
		channels = sortChannels(lightParserType, channels);
		subTypes = sortSubTypes(lightParserType, subTypes);
		
		if(nodes.size()>0 && nodes.get(0)!=null){
			SemanticNode sn = nodes.get(0);
			if(sn.getType()==NodeType.ENV){
				Environment listEnv = (Environment) sn;
				
			  //龙头LEADER类型特殊处理: 是龙头类型
				String queryStr = (String) listEnv.get("standardStatement",false);
				if(queryStr!=null && queryStr.length()>0) {
					String[] lpr = getLeaderStrList(queryStr);
					if (lpr != null){
						template.type = LightParserTemplate.LEADER;
						template.sub_type = lpr[0];
						template.query = lpr[1];
					}
				}
				
				//最原始分词个数
				Integer segNum = (Integer) listEnv.get("segNum", false);
				if (segNum != null) {
				  template.seg_num = segNum.toString();
				}
			}
		}
		
		//构建Map字段
		template.type=lightParserType.label;
		if(!lightParserType.label.equals(LightParserTemplate.LEADER)){
			String subType="";
			if(subTypes!=null && subTypes.size()>0){//模板中的判断移到代码中
				subType = subTypes.get(0);
				if(subType.equals("ASTOCK")&&subTypes.contains("ASTOCKINDEX")){
					subType="ASTOCKINDEX";
				}else if(subType.equals("FUND")&&subTypes.contains("FUNDINDEX")){
					subType="FUNDINDEX";
				}else if(subType.equals("HKSTOCK")&&subTypes.contains("HKSTOCKINDEX")){
					subType="HKSTOCKINDEX";
				}
			}
			template.sub_type = subType;
		}
		template.channel = (channels==null||channels.size()==0)?"":channels.get(0);
		template.node_list = nodeList;
		template.is_comment = isComment;
		if(nodes != null && nodes.size() > 0){
			String[] classifyResult = null;
			//根据servlet参数决定是否调用idList
			if(requestParam.isShowId_List()){
				classifyResult = CreateQueryClassifyResult.getResult(nodes, template, requestParam);
			}
			if(classifyResult==null) {
				classifyResult = new String[2];
				classifyResult[0] = classifyResult[1] = Consts.STR_BLANK;
			}else{
				for(int i=0;i<classifyResult.length;i++) {
					if(classifyResult[i]==null ) classifyResult[i]= Consts.STR_BLANK;
				}
			}//idList处理结束
			
			if(classifyResult!=null && classifyResult.length==2){
				template.id_list= classifyResult[1];
				template.id_search= classifyResult[0];
				String idList=classifyResult[1];
	        	template.yanbao_search=nodes.get(0).getReportSearch();//no
	        	template.news_search=CreateIdSearchResult.searchIdList(idList,nodeList,"news");
	        	template.hostnews_search=CreateIdSearchResult.searchIdList(idList,nodeList,"hotnews");
	        	template.report_search=CreateIdSearchResult.searchIdList(idList,nodeList,"report");
	        	template.pubnote_search=CreateIdSearchResult.searchIdList(idList,nodeList,"pubnote");
	        	template.navigation_search=CreateIdSearchResult.searchIdList(idList,nodeList,"navigation");
	        	template.usersite_search=CreateIdSearchResult.searchIdList(idList, nodeList, "usersite");
	        	template.investqa_search=CreateIdSearchResult.searchIdList(idList, nodeList, "investqa");
	        	template.weibo_search=CreateIdSearchResult.searchIdList(idList, nodeList, "weibo");
	        	template.blog_search=CreateIdSearchResult.searchIdList(idList, nodeList, "blog");
	        	template.guba_search=CreateIdSearchResult.searchIdList(idList, nodeList, "guba");
	        	template.forum_search=CreateIdSearchResult.searchIdList(idList, nodeList, "forum");
	        	template.product_search=CreateIdSearchResult.searchIdList(idList, nodeList, "product");
	        	template.video_search=CreateIdSearchResult.searchIdList(idList, nodeList, "video");
            template.info_search=CreateIdSearchResult.searchIdList(idList, nodeList, "info");
            template.stockrecommend_search=CreateIdSearchResult.searchIdList(idList, nodeList, "stockrecommend");
			}
		}
		template.adjustLightParserTemplate();
		return;
	}
	
	/**重写模板*/
	/*private static String getNodeStr(List<SemanticNode> nodes){
		StringBuilder type = new StringBuilder();
		for(SemanticNode node:nodes){
			if (node.isStrNode()){
				type.append(" STR");//TODO
			}else if(node.isDateNode()){
				type.append(" DATE");
			}else if(node.isNumNode()){
				type.append(" NUM");
			}else if(node.isIndexNode()){
				type.append(" INDEX");
			}else if(node.isUnknownNode()){
				type.append(" UNKNOWN");
			}else{
				type.append(" TEXT");
			}
		}
		String res = type.toString();
		if(res.length()>0)
			res.substring(1, res.length());
		return null;
	}*/
	
	private static ArrayList<String> sortSubTypes(LightParserType lightParserType, ArrayList<String> subTypes) {
		if (subTypes == null || subTypes.size() == 0)
			return null;
		ArrayList<String> sorted = new ArrayList<String>();
		for (SubType subType : lightParserType.subTypes) {
			if (subTypes.contains(subType.label))
				sorted.add(subType.label);
		}
		if (sorted.size() != 0)
			return sorted;
		return null;
	}

	private static ArrayList<String> sortChannels(LightParserType lightParserType, ArrayList<String> channels) {
		if (channels == null || channels.size() == 0)
			return null;
		ArrayList<String> sorted = new ArrayList<String>();
		for (Channel channel : lightParserType.channels) {
			if (channels.contains(channel.label))
				sorted.add(channel.label);
		}
		if (sorted.size() != 0)
			return sorted;
		return null;
	}
	
	private static StrNode getStrValInstance(SemanticNode node) {
    	if (node == null || node.isCombined == true)
    		return null;
    	StrNode strNode = null;
        if (node.type == NodeType.STR_VAL) {
            strNode = (StrNode)node;
        } else if (node.type == NodeType.FOCUS) {
        	FocusNode focusNode = (FocusNode) node;
        	if (focusNode.hasString()) {
        		strNode = focusNode.getString();
        	}
        }
        return strNode;
    }
	
	// 判断是否为分隔符
	private static boolean isSepWord(String text) {
		if (text == null)
			return false;
		if (Pattern.matches("(\\s|,|;)", text))
			return true;
		return false;
	}
	
	// 判断是否只有1个节点
	private static boolean isOnlyOneNode(ArrayList<SemanticNode> nodes) {
	  int count = 0;
	  for (SemanticNode node : nodes) {
	    if (!node.isBoundaryNode() && node.type != NodeType.ENV && !" ".equals(node.getText()))
	      count++;
	  }
	  return count == 1;
	}
	
	private static String[] getLeaderStrList(String queryRet) {
		List<String> typeList = new ArrayList<String>();
		List<String> queryList = new ArrayList<String>();
		String strArray[] = queryRet.split(splitWords);
		
		if(strArray!=null) {
			for (String str : strArray) {
				Matcher mid = LONGTOU_TYPE.matcher(str);
				if (mid.matches()) {
					String tempTypeStr = mid.group(2);
					String tempQuery = mid.group(1);
					
					String tempType = tempTypeStr.replace("行业", LightParserTemplate.INDUSTRY_LEADER)
							.replace("区域", LightParserTemplate.REGION_LEADER)
							.replace("概念", LightParserTemplate.CONCEPT_LEADER);
					
					if (tempQuery == null || tempQuery.equals("")) {
						typeList.add(tempType);
						queryList.add(tempTypeStr);
						continue;
					}
					String tempArray[] = tempQuery.split("、");
					for (String item : tempArray) {
						typeList.add(tempType);
						queryList.add(item);
					}
				}
			}
		}
		
		String type = getStringFromArrayList(typeList);
		String query = getStringFromArrayList(queryList);

		if (type.equals("")) {
			return null;
		} else {
			String[] result = {type,query};
			return result;
		}
	}
	
	// 将ArrayList<String>转化为String，分隔符为splitWord
	private static final String splitWord = " ";
	private static String getStringFromArrayList(List<String> al) {
		if (al == null || al.size() == 0) return Consts.STR_BLANK;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < al.size(); i++) {
			if (i > 0) sb.append(splitWord);
			sb.append(al.get(i));
		}
		return sb.toString();
	}
	
	/**根据dataMap, templateName生成lightparser json结果*/
	public static void createJson(Map<String, Object> root, String templateName, Writer out) {
		String templateFileName = "default.ftl";
		if (templateName != null && !templateName.equals(""))
			templateFileName = templateName.toLowerCase()+".ftl";
		templateBuilder.createTemplate(root, templateFileName, out);
    }
}
