package com.myhexin.qparser.util.condition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.DateUnit;
import com.myhexin.qparser.except.BacktestCondException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.PhraseParserFactory;
import com.myhexin.qparser.resource.model.DomainInfo;
import com.myhexin.qparser.util.condition.model.BackTestCondAnnotation;
import com.myhexin.qparser.util.condition.model.BackTestCondAnnotationComparator;

public class ConditionParser {

	private final static List<SimpleDateFormat> formats = new ArrayList<SimpleDateFormat>();
	private final static BackTestCondAnnotationComparator backtestAnnotationComparator = new BackTestCondAnnotationComparator();
	/**
	 * 获得回测时间
	 * 将<str name="postData">{"length":"0","dunit":"0","start":"2012-07-01"}</str>转换成DateRange对象
	 * 
	 * @param backTestTime
	 * @return
	 */
	public static Calendar getBackTestTime(String backTestTime ) {
		Calendar dateTime = Calendar.getInstance();
		if(backTestTime!=null && backTestTime.length()>0) {

			for (SimpleDateFormat format : formats) {
				Date date = null;
				try {
					date = format.parse(backTestTime);

				} catch (Exception e) {
				}
				if (date != null) {
					dateTime.setTime(date);
					break;
				}
			}

		}
		int year = dateTime.get(Calendar.YEAR);
		int month = dateTime.get(Calendar.MONTH) + 1;
		int day = dateTime.get(Calendar.DAY_OF_MONTH);
		DateInfoNode from = new DateInfoNode(year, month, day);
		dateTime.set(Calendar.YEAR, from.getYear());
		dateTime.set(Calendar.MONTH, from.getMonth()-1);
		dateTime.set(Calendar.DAY_OF_MONTH, from.getDay());
		
		//转成交易日
		//2015-09-06 liuxiaofeng 如果今天是周日(2015-09-06),那么这里会把backtestTime=2015-09-04
		//问句:昨天涨跌幅>3%
		//那么日期就变成 2015-09-03的涨跌幅了,所以注释掉这一行
		/*try {
			from = DateUtil.rollTradeDate(from, 0);
			dateTime.set(Calendar.YEAR, from.getYear());
			dateTime.set(Calendar.MONTH, from.getMonth()-1);
			dateTime.set(Calendar.DAY_OF_MONTH, from.getDay());
		} catch (UnexpectedException e) {
			e.printStackTrace();
		}*/
		
		/*DateInfoNode to = new DateInfoNode(from.getYear(), from.getMonth(), from.getDay());
		DateRange range =  new DateRange(from, to);
		range.setDateUnit(Unit.DAY);
		return range;*/
		return dateTime;
	}
	
	public static String getBackTestTimeFromJson(String postDataStr) {
    	String backtestTime = null;
        if(postDataStr!=null && postDataStr.trim().length()>0) {
        	try{
            	Gson gson = new Gson();
            	PostDataInfo postData = gson.fromJson(postDataStr, PostDataInfo.class);
            	backtestTime = postData.getStart();
            }catch(Exception e) {
            	//logger_.warn("[WARNING] 解析PostDate ERROR :" + postDataStr + "," + e.getMessage() );
            	backtestTime  = DateUtil.getNow().toString("-");
            }
        }
		//        else{
		//        	backtestTime  = DateUtil.getNow().toString("-");
		//        }
        
        return backtestTime;
    }
	
	
	private final static PhraseParser parser = PhraseParserFactory.createPhraseParser(PhraseParserFactory.ParserName_Condition);
	private final static String yyyy_MM_dd = "yyyy-MM-dd";
	private final static String yyyyMMdd = "yyyyMMdd";
	private final static String yyyyMMddHHMM = "yyyy-MM-dd HH:mm:ss";
	// 静态变量,静态初始化
	static {
		formats.add(new SimpleDateFormat(yyyyMMddHHMM));
		formats.add(new SimpleDateFormat(yyyy_MM_dd));
		formats.add(new SimpleDateFormat(yyyyMMdd));
	}
	
	
	
	// 解析
	public static ParseResult parse(String query, String qType, String domain, Calendar backtestTime) {
		Query q = new Query(query);
		if (qType == null) {
			q.setType(Type.STOCK);
		} else {
			Type theQType = null;
			try {
				theQType = Type.valueOf(Type.class, qType);
			} catch (Exception e) {
				theQType = Type.STOCK;
			}
			if (theQType == null) theQType = Type.STOCK;
			q.setType(theQType);
		}
		q.setDomain(domain);

		ParserAnnotation annotation = new ParserAnnotation();
		annotation.setQueryText(q.text);
		annotation.setQuery(q);
		annotation.setQueryType(q.getType());
		annotation.setWriteLog(false);
		annotation.setBacktestTime(backtestTime);

		ParseResult pr = parser.parse(annotation);
		pr.standardQueries = ParseResult.toStandardQueries(q, pr.qlist, PhraseParserFactory.split_);
		pr.standardQueriesScore = ParseResult.getQueryScores(pr.qlist);
		return pr;
	}

	
	
	
	public static List<BackTestCondAnnotation> compileToCond(String query, String qType, String domain, String postDataStr) {
    	//取到回测时间
    	String backtestTimeStr = getBackTestTimeFromJson(postDataStr);
        Calendar backtestTime = getBackTestTime(backtestTimeStr);
        
        //判断一下传入的domain是否合法,不合法的变成null
        if(DomainInfo.getInstance().contains(domain)==false) {
        	domain = null;
        }
        //解析, 转condition
    	if(query!=null && query.length()>0) {
    		//调解析parser
        	ParseResult pr = parse(query, qType, domain, backtestTime);
        	
        	//转成Condition
        	try {
				List<BackTestCondAnnotation> jsonResults = ConditionBuilder.buildCondition(pr, query, backtestTime);
				
				//删除相同领域,比如有2个股票领域
	        	Map<String, Integer> domainCountMap = new HashMap<String, Integer>();
	        	if(jsonResults!=null && jsonResults.size()>1) {
	        		for(Iterator<BackTestCondAnnotation> it = jsonResults.iterator();it.hasNext();) {
	        			BackTestCondAnnotation ann = it.next();
	        			if(ann.getDomainStr()!=null && domainCountMap.get(ann.getDomainStr())!=null && domainCountMap.get(ann.getDomainStr())==1) {
	        				it.remove();
	        			}else{
	        				domainCountMap.put(ann.getDomainStr(), 1);
	        			}
	        		}
	        	} 
	        	
	        	//股票领域的放前面,排序一下
	        	Collections.sort(jsonResults,backtestAnnotationComparator);
	        	return jsonResults;
			} catch (BacktestCondException e) {
				e.printStackTrace();
			}
    	}
    	return null;
    }
	
	
	
	
	
	static class PostDataInfo {
        private String length =null;
        private String dunit = null;
        private String start = null;
        
        private int getLength() {
            return length == null ? 0 : Integer.valueOf(length);
        }
        
        private DateUnit getDUnit() {
            return dunit == null ? DateUnit.TRADE_DAY : parseDateUnit(dunit);
        }
        
        public String getStart() throws UnexpectedException {
			return start;
        }
        
        /*private String parseStart(String dateStr) throws UnexpectedException {
            Matcher mid = YEAR_MONTH_DAY_AS_NUM.matcher(dateStr);
            if (!mid.matches()) {
                throw new UnexpectedException("Unexpected start date:%s", dateStr);
            }
            int year = Integer.valueOf(mid.group(1));
            int month = Integer.valueOf(mid.group(2));
            int day = Integer.valueOf(mid.group(3));
            DateInfoNode startTmp = new DateInfoNode(year, month, day);
            return startTmp.toString("-");
        }*/
        /*if (!startTmp.isLegal()) {
            throw new UnexpectedException("start date is not legal:%s", dateStr);
        }*/
        
        private DateUnit parseDateUnit(String unitStr) {
            DateUnit rtn = null;
            if("0".equals(unitStr)){
                rtn = DateUnit.TRADE_DAY;
            } else if("1".equals(unitStr)){
                rtn = DateUnit.DAY;
            }else if("2".equals(unitStr)){
                rtn = DateUnit.WEEK;
            }else if("3".equals(unitStr)){
                rtn = DateUnit.MONTH;
            }else if("4".equals(unitStr)){
                rtn = DateUnit.YEAR;
            }
            return rtn;
        }
        
        /*private String getJsonStr(){
            return new Gson().toJson(this);
        }*/
        
        public String toString() {
            try {
                return String.format("length:%d;dunit:%s;start:%s", getLength(), getDUnit(), getStart());
            } catch (QPException e) {
                assert (false);
            }
            return null;
        }
        //private static final Pattern YEAR_MONTH_DAY_AS_NUM = Pattern.compile("^(199\\d|200\\d|201\\d)\\-(0[1-9]|1[0-2])\\-(0[1-9]|[12]\\d|3[01])$");
    }
}
