package resouremanager.pattern;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

import resouremanager.dataIO.DataGetter;
import resouremanager.dataIO.HttpGetDataGetter;
import resouremanager.db.DataBaseHandler;

/***
 * 标准pattern库的更新，由cronjob通知CronJobRcvServlet，触发更新事件
 * @author lu
 *
 */


public class PatternCorpusUpdate {

	/**
	 * @param args
	 */
	
	/** 从返回的结果中截取 问句泛化的正则 */
	private static final Pattern pattern = Pattern.compile("(?<=queryPattern\":\").*?(?=\",\"synonyms\")") ;
	private static final String urlHead = "http://172.20.23.52:8090/RM/queryclass?qid=1&text=" ;
	private static final HttpGetDataGetter getter = new HttpGetDataGetter("http://172.20.23.52:8090/RM/queryclass?qid=1&text=同花顺的股票") ;
	private static DataBaseHandler dataGetter = null ;
	private static boolean lock = false ;
	
	
	static enum DownLoadCmd {
		ALL, HOUR ;
	}
	
	
	/****
	 * 从原始的http返回的结果中提取，问句泛化的结果
	 * @param originalStr
	 * @return
	 */
	private static String getSpecificPattern(String originalStr){
		Matcher m = pattern.matcher(originalStr) ;
		String rtn = "" ;
		while(m.find()){
			return m.group() ;
		}
		return rtn ;
	}
	
	public static Set<String> getPatternsFromHttp(List<String> queries){
		String url ;
		Set<String> rtnSet = new HashSet<String>() ; 
		if(queries.isEmpty())
			return rtnSet ;
		int icount = 1 ;
		for(String query : queries){
			System.out.print("正在处理第 " + icount++ +" 条:\t" + query + "\t");
			url = urlHead + query ;
			
			// 特殊符号编码转换：“%”转为“%25”，空格转为"%20"
			getter.reSetUrl(url.replaceAll("%", "%25").replace(" ", "%20")) ;
			
			// 约定只会返回一行
			for(String originalStr : getter.getData().data()) {
				String speciPtn = getSpecificPattern(originalStr) ;
				System.out.println(speciPtn);
				rtnSet.add(speciPtn) ;
			}
		}
		return rtnSet ;
	}
	
	
	/**********************************************************************************
	 * 从指点的正确问句库中下载问句
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static List<String> downLoadQuery(DownLoadCmd cmd) throws ClassNotFoundException, SQLException{
		String currTime = new SimpleDateFormat("yyyy-MM-dd HH").format(new Date());
//		currTime = "2012-10-26" ;
//		String sql = "SELECT DISTINCT(query) FROM query,state WHERE query.uid=state.uid AND state.flag=39 AND `optime` like \"" + currTime+ "%\"" + " AND `operators` != \"Computer\"" ;
//		String sql = "SELECT DISTINCT(query) FROM query,state WHERE query.uid=state.uid AND state.flag=39 AND `optime` > \"" + currTime+ "\"" + " AND `operators` != \"Computer\"" ;
		String sql = "" ;
		
		switch(cmd) {
			case HOUR :
				sql = "SELECT DISTINCT(query) FROM query,state WHERE query.uid=state.uid AND state.flag=39 AND `optime` like \"" + currTime+ "%\"" + " AND `operators` != \"Computer\"" ;
				break ;
			case ALL :
				sql = "SELECT DISTINCT(query) FROM query,state WHERE query.uid=state.uid AND state.flag=39 " + " AND `operators` != \"Computer\"" ;
				break ;
			default :
				return Collections.emptyList() ;
		} 
		ResultSet rs = null ;
		if(!sql.isEmpty()){
			rs = dataGetter.stmt.executeQuery(sql);
		}
		
		List<String> rtn_queries = new ArrayList<String>() ;
		while(rs.next()){
			try{
				String text = rs.getString(1);
				rtn_queries.add(text) ;
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return rtn_queries ;
	}
	
	/**
	 * 表的备份
	 * @param fromTabel
	 * @param toTable
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static boolean tableBak(String fromTabel, String toTable) throws ClassNotFoundException, SQLException{
		String sql = "insert ignore `" + toTable + "` select * from `" +fromTabel + "`" ;
		if(dataGetter == null){
			return false ;
		} else {
			return dataGetter.stmt.execute(sql) ;
		}
	}
	
	/*************************************************************************************
	 * 更新标准pattern库
	 * @return
	 */
	private static boolean upadaeStdPaternCorpus(Collection<String> patterns, String tableName){
		DataBaseHandler dbHandler ;
		Pattern tcPtn = Pattern.compile("(?<=TC:)\\d+$") ;
		Matcher matcher ;
		int insertCount = 0 ;
		try {
			dbHandler = new DataBaseHandler("172.20.0.52", 3306,
					"web_stockpick", "wc", "kernel");
			for(String ptn : patterns){
				++ insertCount ;
				matcher = tcPtn.matcher(ptn) ;
				String treecode = "" ;
				while(matcher.find()){
					treecode = matcher.group() ;
				}
				String sql = "INSERT ignore `"+tableName+"` (pattern, pattern_code) VALUES ( \"" + ptn + "\" , " +treecode+")" ;
				dbHandler.stmt.execute(sql); ;
			}
			dbHandler.close() ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			--insertCount ;
			e.printStackTrace();
		}	
		
		if(insertCount > 0)
			return true ;
		else return false ;
	}
	
	public static boolean handle() throws ClassNotFoundException, SQLException{	
		if(lock) {
			System.out.println("the PatternCorpusUpdate is locked, please wait a moment");
			return false ;
		}
		lock = true ;
		dataGetter = new DataBaseHandler("172.20.0.52", 3306, "op_webstockpick", "wc", "kernel") ;
		List<String> queries  = downLoadQuery(DownLoadCmd.HOUR) ;
		Set<String> patterns = getPatternsFromHttp(queries) ;
		boolean rtn ;
		rtn = upadaeStdPaternCorpus(patterns, "oldpattern_list_new") ;
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) ;		
		System.out.println(time + " \t 更新标准pattern库 " + patterns.size() + " 个pattern");
		dataGetter.close() ;
		lock = false ;
		return rtn ;
	}
	
	public static boolean handleAll() throws ClassNotFoundException, SQLException{
		if(lock) {
			System.out.println("the PatternCorpusUpdate is locked, please wait a moment");
			return false ;
		}
		lock = true ;
		dataGetter = new DataBaseHandler("172.20.0.52", 3306, "op_webstockpick", "wc", "kernel") ;
		List<String> queries  = downLoadQuery(DownLoadCmd.ALL) ;
		Set<String> patterns = getPatternsFromHttp(queries) ;
		boolean rtn = false ;
		rtn = upadaeStdPaternCorpus(patterns, "oldpattern_list_medium") ;
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) ;		
		System.out.println(time + " \t 更新标准pattern库 " + patterns.size() + " 个pattern");
		dataGetter.close() ;
		lock = false ;
		tableBak("oldpattern_list_medium" , "oldpattern_list_new") ;
		return rtn ;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
//		String ptn = "STR_VAL:行业概念 UNKNOWN:销售 STR_LEN:1 TC:23" ;
//		HashSet<String> set = new HashSet<String>() ;
//		set.add(ptn) ;
//		upadaeStdPaternCorpus(set) ;
//		String currTime = "2012-10-18" ;
////		String currTime = new SimpleDateFormat("yyyy-MM-dd hh").format(new Date());
//		String sql = "SELECT DISTINCT(query) FROM query,state WHERE query.uid=state.uid AND state.flag=39 AND `optime` < \"" + currTime+ "\"" + " AND `operators` != \"Computer\"" ;
//		System.out.println(sql);
		
//		handle() ;
		
		
//		List<String> queries ;
//		
//		queries = downLoadQuery() ;
//		for(String query : queries){
//			System.out.println(query);
//		}
//		System.out.println(queries.size());
		
//		try {
//			queries = Util.readTxtFile("correct-query.txt") ;
//			for(String s : getPatternsFromHttp(queries)){
//				System.out.println(s);
//			}
//		} catch (DataConfException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
