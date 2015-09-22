package com.myhexin.qparser.util.lightparser;

import javax.servlet.http.HttpServletRequest;

/**
 * lightparser servlet参数
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-4-24
 *
 */
public class LightParserServletParam {

	
	public static LightParserServletParam getParam(String query, String channel) {
		LightParserServletParam p = new LightParserServletParam();
		p.query = query;
		p.channel = channel;
		return p;
	}
	
	private String query;
	/*private String cmd;
	private String cmd_web;
	private String options;
	private String debug;*/
	private String channel;
	private String qs;
	private String ms_ltp;
	private boolean id_list = false;
	private boolean id_name = false;
	
	private LightParserServletParam(){}
	
	public LightParserServletParam(String query, String channel,String qs) {
		this.query = query;
		this.channel = channel;
		this.qs = qs;
		this.id_list = true;
	}
	
	public LightParserServletParam(HttpServletRequest request) {
		query = request.getParameter("q");
		if(query!=null) query = query.trim();
		
		/*cmd = request.getParameter("cmd"); //?
		cmd_web = request.getParameter("cmd_web"); //?
		options = request.getParameter("options"); //?
		debug = request.getParameter("debug");*/
		channel = request.getParameter("channel"); //channel=news, yanbao
		String id_list_param = request.getParameter("id_list");
		String id_name_param = request.getParameter("id_name");
		qs = request.getParameter("qs");
		ms_ltp = request.getParameter("ms_ltp");
		if("1".equals(id_list_param)) {
			id_list = true;
		}
		if ("1".equals(id_name_param)) {
		  id_name = true;
		}
	}

	public boolean isShowId_List() {
		return id_list;
	}
	public boolean isShowId_Name() {
    return id_name;
  }
	
	public String getQuery() {
		return query;
	}

	/*public String getCmd() {
		return cmd;
	}

	public String getCmd_web() {
		return cmd_web;
	}

	public String getOptions() {
		return options;
	}

	public String getDebug() {
		return debug;
	}*/

	public String getMs_ltp() {
    return ms_ltp;
  }

  public String getChannel() {
		return channel;
	}
	
	public String getQS() {
    return qs;
  }
	
	public boolean isQueryEmpty() {
		if (query == null || query.length() == 0) {
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isQueryTooLong() {
		if (query != null && query.length() > 50 ) {
			return true;
		}else{
			return false;
		}
	}
	
	private final static String STAR_COLON_STAR = "*:*";
	/*private static String STAR_COLON_STAR_JSON = null;
	static {
		Map<String, Object> STAR_COLON_STAR_DATA = new HashMap<String, Object>();
		STAR_COLON_STAR_DATA.put("type", "UNKNOWN");
		STAR_COLON_STAR_DATA.put("sub_type", "UNKNOWN");
		STAR_COLON_STAR_DATA.put("channel", "app");
		STAR_COLON_STAR_DATA.put("is_comment", false);
		STAR_COLON_STAR_DATA.put("cmd", Consts.STR_BLANK);
		STAR_COLON_STAR_DATA.put("cmd_web",  Consts.STR_BLANK);
		STAR_COLON_STAR_DATA.put("options", Consts.STR_BLANK);
		STAR_COLON_STAR_DATA.put("query", "*:*");
		STAR_COLON_STAR_DATA.put("node_list", new Object[]{});
		
		STAR_COLON_STAR_DATA.put("id_list",Consts.STR_BLANK);
		STAR_COLON_STAR_DATA.put("id_search",Consts.STR_BLANK);
		STAR_COLON_STAR_DATA.put("yanbao_search",Consts.STR_BLANK);
		STAR_COLON_STAR_DATA.put("news_search",Consts.STR_BLANK);
		STAR_COLON_STAR_DATA.put("report_search",Consts.STR_BLANK);
		STAR_COLON_STAR_DATA.put("pubnote_search",Consts.STR_BLANK);
		STAR_COLON_STAR_DATA.put("navigation_search",Consts.STR_BLANK);
		StringWriter out = new StringWriter();
		LightParser.createJson(STAR_COLON_STAR_DATA, "lightparser", out);
		STAR_COLON_STAR_JSON = out.toString();
	}*/
	
	/*public static String getStarColonStarJson() {
		return STAR_COLON_STAR_JSON;
	}*/
	
	
	//50%问句都是*:*,是这种情况直接返回
	public boolean isStarColonStar() {
		if (query != null && query.equals(STAR_COLON_STAR) ) {
			return true;
		}else{
			return false;
		}
	}
}
