package conf.stock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

public class StockIndex {

	StockIndex(){}
	StockIndex(String name){
		this.name = name ;
	}
	
	/**
	 * @param args
	 */
	public DateType dateType ;	
	public String sOutputDate ;
	
	public static final String PREDICT_INDEX = "业绩预告类型,业绩预告日期,业绩预告摘要,预告净利润变动幅度(%)" ;
	public static final String DIVIDEND_COMMON_INDEX = "每股分红送转,每股股利(税前),每股股利(随后),每股红股,每股转增股本,b股最后交易日,分红方案进度,分红对象,是否分红,除权除息日,股权登记日,派息日,红股上市交易日,预案公告日,股东大会公告日,分红实施公告日" ;
	public static final String DIVIDEND_YEAR_INDEX = "年度累计单位分红,年度累计分红总额" ;
	public static final String LIST_NAME = "P00007" ;
	
	public String name ;
	/** 父子指标节点 */
	public StockIndex father ;
	public ArrayList<StockIndex> children = new ArrayList<StockIndex>();
	/** 指标在Ifind中path，如： 机构追踪/主力追踪选股 */ 
	public ArrayList<String> pathInIfind = new ArrayList<String>() ;   
	/** 指标集合 */
	public static HashSet<StockIndex> INDEX_SET = new HashSet<StockIndex>() ;
	/** */
	public static HashMap<String, ArrayList<HashMap<String, ArrayList<StockIndex>>>> 
		INDEX_MAP = new HashMap<String, ArrayList<HashMap<String, ArrayList<StockIndex>>>>() ;
	
	
	
	/** 需要更新的指标**/
	public static HashMap<String , DateType> INDEX_DATE_MAP = new HashMap<String , DateType>() ;
	
	/** 需要更新list_name=“P00007”的指标**/
//	public static Hash
	
	/***
	 * 获取指标需要更新的时间类型
	 */
	static 
	{
		for(String s : PREDICT_INDEX.split(","))
			INDEX_DATE_MAP.put(s, DateType.PREDICT) ;
		for(String s : DIVIDEND_COMMON_INDEX.split(","))
			INDEX_DATE_MAP.put(s, DateType.DIVIDEND_COMMON) ;
		for(String s : DIVIDEND_YEAR_INDEX.split(","))
			INDEX_DATE_MAP.put(s, DateType.DIVIDEND_YEAR) ;			
	}
	
	
	/***
	 * 根据DateType来生成需要更新的日期
	 * @param dateType
	 * @return 生成的日期字符串
	 */
	public static String geneDate(DateType dateType)
	{	
		String sRtnDate = null ;
		switch (dateType)
		{
		 	case PREDICT :
		 		sRtnDate = StockIndex.getPredictDate();
		 		break ;
		 	case DIVIDEND_COMMON :
		 		sRtnDate = StockIndex.getDividendCommonDate();
		 		break ;
		 	case DIVIDEND_YEAR :
		 		sRtnDate = StockIndex.getDividendYearDate();
		 		break ;
		 	case OTHERS :
		 		sRtnDate = StockIndex.getOthersDate();
		 		break ;		
		}		
		return sRtnDate ;
	}
	
	/***
	 * 在1-3月使用一季报（20120331），4-6月使用中报（20120630），其余使用年报（20121231）
	 * @return
	 */
	private static String getPredictDate()
	{
		String sRtn = null ;
		String s1 = "0331" ;
		String s2 = "0630" ;
		String s3 = "1231" ;		
		Calendar ca = Calendar.getInstance() ;
		int iMonth = ca.get(Calendar.MONTH) + 1 ;
		if(iMonth < 1 || iMonth > 12)
		{
			assert(false) ;
		}
		else if(1 <= iMonth && iMonth <= 3)
			sRtn = s1 ;
		else if(4 <= iMonth && iMonth <= 6)
			sRtn = s2 ;
		else sRtn = s3 ;
				
		return ca.get(Calendar.YEAR) + sRtn ;
	}
	
	/***
	 * 在2-7月使用去年年报（20111231），8-下年1月使用中报（20120630），
	 * 其中2个年度指标“年度累计单位分红,年度累计分红总额”1月份使用前年年报，其余使用去年年报。
	 * @return 返回时间
	 */
	private static String getDividendCommonDate()
	{
		String s1 = "1231" ;
		String s2 = "0630" ;
		Calendar ca = Calendar.getInstance() ;
		int iMonth = ca.get(Calendar.MONTH) + 1 ;
		if(iMonth < 1 || iMonth > 12)
		{
			assert(false) ;
		}
		else if(2 <= iMonth && iMonth <= 7)
			return (ca.get(Calendar.YEAR)-1) + s1 ; 
		else if(8 <= iMonth && iMonth <= 12)
			return ca.get(Calendar.YEAR) + s2 ; 
		else if(iMonth == 1)
			return (ca.get(Calendar.YEAR)-1) + s2 ; 
		
		return "-1" ;    //出错返回“-1”
	}
	
	/***
	 * 其中2个年度指标“年度累计单位分红,年度累计分红总额”1月份使用前年年报，其余使用去年年报。
	 * @return
	 */
	private static String getDividendYearDate()
	{
		String s = "1231" ;
		Calendar ca = Calendar.getInstance() ;
		int iMonth = ca.get(Calendar.MONTH) + 1 ;
		if(iMonth < 1 || iMonth > 12)
		{
			assert(false) ;
		}
		else if(iMonth == 1)
			return (ca.get(Calendar.YEAR)-2) + s ;
		else return (ca.get(Calendar.YEAR)-1) + s ;
			
			
		return "-1" ;    //出错返回“-1”
	}
	
	/****
	 * 对除上述指标外所有list_name="P00007"的参数，其default_val，5-8月使用一季报（20120331），
	 * 9-10月使用中报（20120630），11-1月使用三季报（20120930），2-4月使用去年年报（20111231）
	 * @return
	 */
	private static String getOthersDate()
	{
		String s1 = "0331" ;    //5-8月
		String s2 = "0630" ;    //9-10月      
		String s3 = "0930" ;    //11-1月        // 2012.10.25暂作修改，当前10月份使用3季报
		String s4 = "1231" ;    //2-4月
		
		Calendar ca = Calendar.getInstance() ;
		int iMonth = ca.get(Calendar.MONTH) + 1 ;
		if(iMonth < 1 || iMonth > 12)
		{
			assert(false) ;
		}
		
		else if(5 <= iMonth && iMonth <= 8)        // 5-8月
			return ca.get(Calendar.YEAR) + s1 ;
		else if(9 <= iMonth && iMonth <= 9)       // 9-10月   改：9-9月
			return ca.get(Calendar.YEAR) + s2 ;
		else if(2 <= iMonth && iMonth <= 4)        // 2-4月
			return ca.get(Calendar.YEAR) + s4 ;
		else if(iMonth == 1)                       // 1月
			return (ca.get(Calendar.YEAR)-1) + s3 ;
		else return ca.get(Calendar.YEAR) + s3 ;   // 11-12月   改：10-12月
		
		
		
/*****	       原始逻辑		
		else if(5 <= iMonth && iMonth <= 8)        // 5-8月
			return ca.get(Calendar.YEAR) + s1 ;
		else if(9 <= iMonth && iMonth <= 10)       // 9-10月
			return ca.get(Calendar.YEAR) + s2 ;
		else if(2 <= iMonth && iMonth <= 4)        // 2-4月
			return ca.get(Calendar.YEAR) + s4 ;
		else if(iMonth == 1)                       // 1月
			return (ca.get(Calendar.YEAR)-1) + s3 ;
		else return ca.get(Calendar.YEAR) + s3 ;   // 11-12月
******/
		return "-1" ;
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		StockIndex si = new StockIndex() ;
//		Calendar ca = Calendar.getInstance() ;
//
//		
//		System.out.println(si.getPredictDate());
//		System.out.println(si.getDividendCommonDate());
//		System.out.println(si.getDividendYearDate());
//		System.out.println(si.getOthersDate());

	}

}
