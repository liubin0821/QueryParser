package com.myhexin.qparser.phrase.parsePostPlugins;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;


import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.phrase.util.Consts;
import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * 研究报告语义支持需求,目前支持的语义如下:
 * 语义1：指定股票，查对应的研报
 * 语义2：指定报告来源，查对应的研报
 * 语义3：指定行业，查对应的研报
 * 语义4：指定作者（分析师），查对应的研报
 * 语义5：找指定评级的研报
 * 语义6：找报告类别的研报
 * 语义7：找指定投资逻辑的研报
 * 语义8：找指定页数的研报
 * 语义9：找指定时间的研报
 * 返回结果显示在report_search字段
 */
public class PhraseParserPluginSearchReport extends PhraseParserPluginAbstract{
	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginSearchReport.class.getName());

	public PhraseParserPluginSearchReport() {
		super("Search_Report");
	}

	@Override
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		String result = searchReport(nodes,ENV);
		
		
		if(nodes !=null && nodes.size() > 0)
			nodes.get(0).setReportSearch(result == null ? "" : result);
		ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>(1);
		rlist.add(nodes);
		return rlist;
	}
	private final static String ORG_ID_NAME = "发布机构";
	private final static String INDUSTRY_ID_NAME = "_申万行业分类";
	private final static String ID_NAME_STKNAME = "_股票简称";
	private final static String ID_NAME_STKCODE = "_股票代码";
	private final static String ID_NAME_INV_LOGIC = "_投资逻辑";
	private final static String ID_NAME_REPORT_CATEGORY = "_报告类别";
	
	private final static String ID_NAME_QUANSHANG = "_券商";
	private final static String ID_NAME_FABU_JIGOU = "发布机构";
	private final static String ID_NAME_SHENWANG = "_申万行业";
	private final static String ID_NAME_SUOSHU_SHENWANG = "_所属申万行业";
	private final static String ID_NAME_ANALYZER = "_分析师姓名";
	private final static String ID_NAME_YANBAO_PJ = "_研报评级";
	
	
	private final static StringBuilder empty_buf = new StringBuilder(0);
	
	public String searchReport(ArrayList<SemanticNode> nodes,Environment ENV){
		if((nodes==null)){
			logger_.error("nodes is null");
			return null;
		}
		int reportFlag = 0;
		
		//TODO ENV.get("channel",false).toString().equalsIgnoreCase("report")
		//这个不要这么写, 不要滥用内存，生成String
		if(ENV.containsKey("channel") && ENV.get("channel",false).toString().equalsIgnoreCase("report"))
		{
			reportFlag = 1;
		}
		
		//请记住一定要判断NULL EXCEPTION
		else if(nodes.get(0)!=null && nodes.get(0).thematicContain("REPORT"))
		{
			reportFlag = 1;
		}
		if(reportFlag==0)
			return null;
		
		StringBuilder org = null; //new StringBuilder();
		StringBuilder orgID = null; //new StringBuilder();//证券名,证券代码
		StringBuilder author = null; //new StringBuilder();//分析师
		StringBuilder industry = null; //new StringBuilder();
		StringBuilder industryID = null; //new StringBuilder();//行业,行业代码
		StringBuilder stkcode = null; //new StringBuilder();
		StringBuilder stkcodeID = null; //new StringBuilder();//股票名,股票代码
		StringBuilder rating = null; //new StringBuilder();//研报评级
		StringBuilder logic = null; //new StringBuilder();
		StringBuilder logicID = null; //new StringBuilder();//投资逻辑
		StringBuilder time = null; //new StringBuilder();//时间
		StringBuilder reportCategory = null; //new StringBuilder();
		StringBuilder reportCategoryID = null; //new StringBuilder();//报告类别,报告类别ID
		StringBuilder page = null; //new StringBuilder();//页码
		StringBuilder unknown = null; //new StringBuilder();//unknown
		List<String> options = new ArrayList<String>();//options
		
		
		//页码
		//TODO page既然重新赋值了,那么上面的new StringBuilder()就是浪费内存, new StringBuilder()一次16bytes
		String pageStr = getMatchedPages(getFromListEnv(nodes, "standardStatement",String.class, false));
		if(pageStr!=null && pageStr.length()>0){
			if(page==null) page = new StringBuilder();
			textToStringBuilder(page, pageStr);
		}
		
		for(SemanticNode node : nodes){
			if(node.isFocusNode()){
				FocusNode focusNode = (FocusNode)node;
				if(focusNode.hasString())
					node = focusNode.getString();
			}
			if(node.isStrNode()){
				StrNode strNode = (StrNode) node;
				
				if(strNode.subType == null || strNode.getText() == null) continue;
				//String subType = strNode.subType.toString();
				String[] textArray = strNode.getText().split(",");
				
				if(strNode.subType.contains(ID_NAME_QUANSHANG)||strNode.subType.contains(ID_NAME_FABU_JIGOU)){//券商
					if(org==null)  org = new StringBuilder();
					textArrayToStringBuilder(org, textArray);
					
					if(strNode.getId(ORG_ID_NAME) != null)//券商ID
					{
						if(orgID==null)  orgID = new StringBuilder();
						textToStringBuilder(orgID,strNode.getId(ORG_ID_NAME));
					}
					if(strNode.subType.contains(ID_NAME_STKCODE)||strNode.subType.contains(ID_NAME_STKNAME))//是券商同时也是股票的
						options.add(String.format("股票代码为%s", strNode.getText()));
				}
				else if(strNode.subType.contains(ID_NAME_SHENWANG)||strNode.subType.contains(ID_NAME_SUOSHU_SHENWANG)){//行业
					if(industry==null)  industry = new StringBuilder();
					textArrayToStringBuilder(industry, textArray);
					
					String industryName = strNode.getId(INDUSTRY_ID_NAME);
					if(!industryName.equals(Consts.STR_BLANK)){ //ID
						if(industryID==null)  industryID = new StringBuilder();
						textToStringBuilder(industryID, industryName.replace("|", OR_STR));
					}
				}
				else if(strNode.subType.contains(ID_NAME_STKCODE)||strNode.subType.contains(ID_NAME_STKNAME)){//股票
					if(stkcode==null)  stkcode = new StringBuilder();
					textArrayToStringBuilder(stkcode, textArray);
					
					String idName = strNode.getId(ID_NAME_STKNAME);
					if(idName!=null && idName.length()>0)//ID
					{
						if(stkcodeID==null)  stkcodeID = new StringBuilder();
						textToStringBuilder(stkcodeID,strNode.getId(ID_NAME_STKNAME));
					}
					else{
						String idName2 = strNode.getId(ID_NAME_STKCODE);
						if(idName2!=null && idName2.length()>0)//ID
						{
							if(stkcodeID==null)  stkcodeID = new StringBuilder();
							textToStringBuilder(stkcodeID,strNode.getId(ID_NAME_STKCODE));
						}
					}
				}
				else if(strNode.subType.contains(ID_NAME_ANALYZER))//分析师姓名
				{
					if(author==null)  author = new StringBuilder();
					textArrayToStringBuilder(author, textArray);
				}
				else if(strNode.subType.contains(ID_NAME_YANBAO_PJ))
				{
					if(rating==null)  rating = new StringBuilder();
					textArrayToStringBuilder(rating, textArray);
				}
				else if(strNode.subType.contains(ID_NAME_INV_LOGIC)){
					if(logic==null)  logic = new StringBuilder();
					textArrayToStringBuilder(logic, textArray);
					
					String invLogic = strNode.getId(ID_NAME_INV_LOGIC) ;
					if(invLogic!= null)//ID
					{
						if(logicID==null)  logicID = new StringBuilder();
						textToStringBuilder(logicID, invLogic.replace("|",OR_STR));
					}
				}
				else if(strNode.subType.contains(ID_NAME_REPORT_CATEGORY)){
					if(reportCategory==null)  reportCategory = new StringBuilder();
					textArrayToStringBuilder(reportCategory, textArray);
					String reportCat = strNode.getId(ID_NAME_REPORT_CATEGORY);
					if(reportCat != null)//ID
					{
						if(reportCategoryID==null)  reportCategoryID = new StringBuilder();
						textToStringBuilder(reportCategoryID, reportCat.replace("|",OR_STR));
					}
				}
			}else if(node.isDateNode()){//时间
				DateNode dateNode = (DateNode) node;
				if(dateNode.getDateinfo() == null) continue;
				DateRange dateRange = dateNode.getDateinfo();
				if(dateRange.getFrom()!=null && dateRange.getTo()!=null) {
					Long fromLong = getTimestamp(dateRange.getFrom().toString()+"000000");
					Long toLong = getTimestamp(dateRange.getTo().toString()+"235959");
					if(fromLong!=null && toLong!=null)
					{
						if(time==null)  time = new StringBuilder();
						textToStringBuilder(time, fromLong+","+toLong);
					}
				}
			}else if(node.isUnknownNode()){//unknown
				UnknownNode unknownNode = (UnknownNode) node;
				if(unknownNode.getText() == null || unknownNode.getText().trim().length() == 0) continue;
				
				if(unknown==null)  unknown = new StringBuilder();
				textArrayToStringBuilder(unknown, unknownNode.getText().split(","));
			}
		}
		
		
		
		if(org == null) org = empty_buf;
		if(orgID == null) orgID = empty_buf;
		if(author == null) author = empty_buf;
		if(industry == null) industry = empty_buf;
		if(industryID == null) industryID = empty_buf;
		if(stkcode == null) stkcode = empty_buf;
		if(stkcodeID == null) stkcodeID = empty_buf;
		if(rating == null) rating = empty_buf;
		if(logic == null) logic = empty_buf;
		if(logicID == null) logicID = empty_buf;
		if(time == null) time = empty_buf;
		if(reportCategory == null) reportCategory = empty_buf;
		if(reportCategoryID == null) reportCategoryID = empty_buf;
		if(unknown == null) unknown = empty_buf;
		if(reportCategory == null) reportCategory = empty_buf;

		Map<String,StringBuilder[]> map = new HashMap<String,StringBuilder[]>();
		map.put("OrgID",new StringBuilder[]{org,orgID});
		map.put("Author",new StringBuilder[]{author});
		map.put("Industry", new StringBuilder[]{industry,industryID});
		map.put("StockName", new StringBuilder[]{stkcode,stkcodeID});
		map.put("Rating", new StringBuilder[]{rating});
		map.put("InvestmentLogicName", new StringBuilder[]{logic,logicID});
		map.put("time", new StringBuilder[]{time});
		map.put("Pages", new StringBuilder[]{page});
		map.put("CatID", new StringBuilder[]{reportCategory,reportCategoryID});
		map.put("unknown", new StringBuilder[]{unknown});
		StringBuilder[] resultArray = mapToStringBuilder(map);//各类语义综合处理
		
		JSONObject json = new JSONObject();
		json.put("cmd", resultArray[0]);
		json.put("cmd_web", resultArray[1]);
		if(nodes.get(0).getMultResult() != null &&  nodes.get(0).getMultResult().allResult != null){
			List<String> mulResult = nodes.get(0).getMultResult().allResult;
			List<String> newMulResult = new ArrayList<String>();
			for(String str : mulResult)
				newMulResult.add(str);
			if(newMulResult.size()>0) newMulResult.remove(0);
			newMulResult.addAll(options);
			json.put("options", newMulResult);
		}else
			json.put("options", options);
		//不要打日志
		//logger_.info(String.format("report_search:%s",json.toString()));
		
		try {
			String result = Base64.encode(json.toString().getBytes("UTF-8"));
			result = result.replaceAll("\n", "");
			result = result.replaceAll("\r", "");
			return new StringBuilder().append("\"").append(result).append("\"").toString();
		} catch (UnsupportedEncodingException e) {
			logger_.error(String.format("Exception:%s", e.getMessage()));
			return null;
		}
	}
	
	private StringBuilder[] mapToStringBuilder(Map<String,StringBuilder[]> map){
		StringBuilder resultCmd = new StringBuilder();
		StringBuilder resultCmdWeb = new StringBuilder();
		for(Entry<String, StringBuilder[]> entry : map.entrySet()){
			String key = entry.getKey();
			StringBuilder[] value = entry.getValue();
			if(value.length == 1){
				if(value[0]!=null && value[0]!=null && value[0].length()>0){
					resultCmdWeb.append("&").append(key).append("=").append(value[0]);
					resultCmd.append("&").append(key).append("=").append(value[0]);
				}
			}else if(value.length == 2){
				if(value[0]!=null && value[0]!=null && value[0].length()>0)
					resultCmdWeb.append("&").append(key).append("=").append(value[0]);
				if(value[1]!=null && value[1]!=null && value[1].length()>0){
					if(!key.equals("StockName"))
						resultCmd.append("&").append(key).append("=").append(value[1]);
					else
						resultCmd.append("&").append("RelatedStock").append("=").append(value[1]);
				}
			}
		}
		if(resultCmdWeb.length()>0)
			resultCmdWeb.deleteCharAt(0);
		if(resultCmd.length()>0)
			resultCmd.deleteCharAt(0);
		StringBuilder[] resultArray = {resultCmd,resultCmdWeb}; 
		return resultArray;
	}
	
	
	/*
	 * 把textArray加到StringBuilder上去
	 */
 	private void textArrayToStringBuilder(StringBuilder builder,String[] textArray){
 		if(builder==null) return;
		for(String text : textArray)
			textToStringBuilder(builder, text);
	}
	
 	private final static String OR_STR = " OR ";
	private void textToStringBuilder(StringBuilder builder,String text){
		if(builder.length()>0)
			builder.append(OR_STR);
		builder.append(text);
	}
	
	private Long getTimestamp(String date){
		try {
			SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-ddhhmmss");
			Long timestamp = simpleDateFormat.parse(date).getTime();
			return timestamp/1000;
		} catch (ParseException e) {
			logger_.error("Exception:"+e.getMessage());
			return null;
		}
	}

	private static String regex = "(页数|页码)(>|<|=|>=|<=)(\\d+)";
	private static 	Pattern pattern = Pattern.compile(regex);
	
	//TODO 请加注释，这段代码干了什么?
	private String getMatchedPages(String str){
		Matcher matcher = pattern.matcher(str);
		String result = "";
		while(matcher.find()){
			String op=matcher.group(2);
			if(op.contains(">"))
				result=matcher.group(3)+","+result;
			else if(op.contains("<"))
				result=result+","+matcher.group(3);
			else if(op.equals("="))
				result=matcher.group(3)+","+matcher.group(3);
			/*if(op.equals(">"))
				result="("+matcher.group(3)+(result.length()==0?"":","+result);
			else if(op.equals(">="))
				result="["+matcher.group(3)+(result.length()==0?"":","+result);
			else if(op.equals("<"))
				result=(result.length()==0?"":result+",")+matcher.group(3)+")";
			else if(op.equals("<="))
				result=(result.length()==0?"":result+",")+matcher.group(3)+"]";
			else if(op.equals("="))
				result=matcher.group(3);*/
		}
		result = result.replace(",,",",");
		return result;
	}
}
