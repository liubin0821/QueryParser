package com.myhexin.qparser.util.lightparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.resource.ResourceInst;
import com.myhexin.qparser.resource.model.RefCodeInfo;

/**
 * 对应lightparser.ftl
 * 
 * 成员变量与lightparser.ftl中用到的变量对应
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-4-28
 *
 */
public class LightParserTemplate {
	public static final String LEADER = "LEADER";
	public static final String KNOWLEDGE="KNOWLEDGE";
	public static final String DIAG_FUND="DIAG_FUND";
	public static final String SELECT_FUND="SELECT_FUND";
	public static final String UNKNOWN = "UNKNOWN";
	
	public static final String INDUSTRY_LEADER = "INDUSTRY_LEADER";
	public static final String REGION_LEADER = "REGION_LEADER";
	public static final String CONCEPT_LEADER = "CONCEPT_LEADER";
	
	public static final String APP_CHANNEL = "app";
	
	public String type = UNKNOWN;
	public String sub_type ;
	public String channel ;
	public boolean is_comment = false;
	public String query ;
	public String seg_num;
	public String seg_status = "0";
	public List<SemanticNode> node_list;
	public String id_list ;
	public String id_search ;
	public String yanbao_search ;
	public String news_search ;
	public String hostnews_search ;
	public String report_search ;
	public String pubnote_search ;
	public String navigation_search ;
	public String usersite_search;
	public String investqa_search;
	public String weibo_search;
	public String blog_search;
	public String guba_search;
	public String forum_search;
	public String product_search;
	public String video_search;
	public String info_search;
  public String stockrecommend_search;
	
	public Map<String, Object> getTemplateDataMap() {
		Map<String,Object> templateDataMap = new HashMap<String, Object>();
		templateDataMap.put("type", type==null?UNKNOWN:type);
		templateDataMap.put("sub_type", nullToBlankStr(sub_type));
		templateDataMap.put("channel", nullToBlankStr(channel));
		templateDataMap.put("is_comment", is_comment);
		templateDataMap.put("ip", RefCodeInfo.getInstance().getIp());
		templateDataMap.put("query", nullToBlankStr(query));
		templateDataMap.put("seg_num", nullToBlankStr(seg_num));
		templateDataMap.put("seg_status", nullToBlankStr(seg_status));
		templateDataMap.put("node_list", node_list==null?new ArrayList<SemanticNode>(0):node_list);
		templateDataMap.put("id_list", nullToBlankStr(id_list));
		templateDataMap.put("id_search", nullToBlankStr(id_search));
		templateDataMap.put("yanbao_search", nullToBlankStr(yanbao_search));
		templateDataMap.put("news_search", nullToBlankStr(news_search));
		templateDataMap.put("hotnews_search", nullToBlankStr(hostnews_search));
		templateDataMap.put("report_search", nullToBlankStr(report_search));
		templateDataMap.put("pubnote_search", nullToBlankStr(pubnote_search));
		templateDataMap.put("navigation_search", nullToBlankStr(navigation_search));
		templateDataMap.put("usersite_search", nullToBlankStr(usersite_search));
		templateDataMap.put("investqa_search", nullToBlankStr(investqa_search));
    templateDataMap.put("weibo_search", nullToBlankStr(weibo_search));
    templateDataMap.put("blog_search", nullToBlankStr(blog_search));
    templateDataMap.put("guba_search", nullToBlankStr(guba_search));
    templateDataMap.put("forum_search", nullToBlankStr(forum_search));
    templateDataMap.put("product_search", nullToBlankStr(product_search));
    templateDataMap.put("video_search", nullToBlankStr(video_search));
    templateDataMap.put("info_search", nullToBlankStr(info_search));
    templateDataMap.put("stockrecommend_search", nullToBlankStr(stockrecommend_search));
		return templateDataMap;
	}
	
	/**对一些类型的channel和subType进行特殊处理*/
	public void adjustLightParserTemplate(){
		if(type!=null && (type.equals(KNOWLEDGE)||
				type.equals(DIAG_FUND)||type.equals(SELECT_FUND)||type.equals(LEADER)) ) {
        	channel= APP_CHANNEL;
        	if(!type.equals(LEADER))
        		sub_type=Consts.STR_BLANK;
		}
	}
	
	private Object nullToBlankStr(Object obj){
		if(obj == null)
			return Consts.STR_BLANK;
		return obj;
	}
}
