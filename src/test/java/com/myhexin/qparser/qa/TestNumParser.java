package com.myhexin.qparser.qa;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.junit.Test;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.except.TBException;
import com.myhexin.qparser.node.LogicNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.number.NumParser;
import com.myhexin.qparser.number.NumRange;

public class TestNumParser {
	/*@BeforeClass
	public static void loadData(){
		String confFile = "./conf/qparser.conf";
		String dataRootDir = "./data";
		System.out.println("load data");
	
		QueryParser qp = QueryParser.getParser(confFile);
		
	}*/
	
	public Query getNumParserResult(NumParser numParser, String methodName, Class[] paraTypes, Object methodPara, String parameter){
		Query query = null ;
		try{
		    Method method = numParser.getClass().getDeclaredMethod(methodName, paraTypes);
		    method.setAccessible(true);
		    //System.out.println(methodName);
		    //System.out.println(methodPara);
		    if (methodPara != null){
		        method.invoke(numParser, methodPara);
		    }else{
		    	//System.out.println("in else loop" );
		    	method.invoke(numParser, null);		    	
		    }
		    
		    Field field = numParser.getClass().getDeclaredField(parameter);
		    field.setAccessible(true);
		    Object after = field.get(numParser);
		    query = (Query)after;		    
		}catch(Exception e){
			e.printStackTrace();
		}		
		return query;
	}
	public NumRange getNumRange(NumParser numParser, String methodName, Class[] paraTypes, Object methodPara, String parameter){
		NumRange nr = null ;
		
		try{
		    Method method = numParser.getClass().getDeclaredMethod(methodName, paraTypes);
		    method.setAccessible(true);
		    //System.out.println("***" + methodPara);
		    if (methodPara != null){
		    	//System.out.println("in not null loop" );
		        nr = (NumRange)method.invoke(numParser, methodPara);
		    }else{
		    	//System.out.println("in else loop" );
		    	nr = (NumRange)method.invoke(numParser, null);
		    }		        
		}catch(Exception e){
			e.printStackTrace();
		}		
		return nr;
	}
	
	@Test
	public void testRemoveSuperfluousSigns(){
		//loadData();		
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
				
		ar_1.add("股价");
		ar_1.add("class");
		ar_2.add(" ");
		ar_2.add("unknown");
		ar_3.add("小于 ");
		ar_3.add("operator");
		ar_4.add("10 ");
		ar_4.add("num");
		
		baseList.add(ar_1);
		baseList.add(ar_2);
		baseList.add(ar_3);
		baseList.add(ar_4);
		
		testList.add(ar_1);
		testList.add(ar_2); 
		testList.add(ar_2);
		testList.add(ar_3);
		testList.add(ar_4);
			
		MockQueryObject testQueryObj = new MockQueryObject(testList);
		Query query = testQueryObj.getQuery();
		//System.out.println(query.nodes().toString())
		NumParser numParser = new NumParser();
		
		
		MockQueryObject baseQueryObj = new MockQueryObject(baseList);
		Query baseQuery = baseQueryObj.getQuery();
		//System.out.println(baseQuery.nodes().toString());

        Pattern BLANK = Pattern.compile("^\\s$");
		Class[] paraTypes = new Class[]{Pattern.class};
		Query testQuery = (Query)getNumParserResult(numParser, "removeSuperfluousSigns", paraTypes,  BLANK, "query_");
		System.out.println(testQuery.getNodes().toString());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
	}
	
	@Test
	public void testRemoveSuperfluousBlankAndConbinator(){
		//loadData();		
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
				
		ar_1.add("-");
		ar_1.add("class");
		ar_2.add("-");
		ar_2.add("unknown");
		ar_3.add("hello");
		ar_3.add("unknown");
		
		baseList.add(ar_1);		
		baseList.add(ar_3);
						
		testList.add(ar_1);
		testList.add(ar_2); 
		testList.add(ar_3);
					
		MockQueryObject testQueryObj = new MockQueryObject(testList);
		Query query = testQueryObj.getQuery();
		//System.out.println(query.nodes().toString())
		NumParser numParser = new NumParser();
		
		
		MockQueryObject baseQueryObj = new MockQueryObject(baseList);
		Query baseQuery = baseQueryObj.getQuery();
		//System.out.println(baseQuery.nodes().toString());

		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = (Query)getNumParserResult(numParser, "removeSuperfluousBlankAndConbinator", paraTypes,  methodPara, "query_");
		System.out.println(testQuery.getNodes().toString());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
	}
	
	@Test
	public void testChangeChineseNumToArabic_1(){
		//loadData();
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
		
		ar_1.add("4.7");
		ar_1.add("unknown");
		
		ar_2.add("4.7");
		ar_2.add("num");
		
		ar_3.add("4.7");
		ar_3.add("class");
		
		ar_4.add("str_val");
		ar_4.add("str_val");
		
		baseList.add(ar_2);
		baseList.add(ar_3);
		baseList.add(ar_4);
		testList.add(ar_1);
		testList.add(ar_3);
		testList.add(ar_4);
		
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();
		NumParser numParser = new NumParser();
		
		MockQueryObject resultQueryObj = new MockQueryObject(baseList);
		Query baseQuery = resultQueryObj.getQuery();
		
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = (Query)getNumParserResult(numParser, "changeChineseNumToArabic", paraTypes, methodPara, "query_");
		System.out.println(baseQuery.getNodes());
	    System.out.println(testQuery.getNodes());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
	}
	
	@Test
	public void testChangeChineseNumToArabic_2(){
		//loadData();
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
		ArrayList<String> ar_5 = new ArrayList<String>();
		ArrayList<String> ar_6 = new ArrayList<String>();
		ArrayList<String> ar_7 = new ArrayList<String>();
		ArrayList<String> ar_8 = new ArrayList<String>();
		ArrayList<String> ar_9 = new ArrayList<String>();
		ArrayList<String> ar_10 = new ArrayList<String>();
		
		ar_1.add("1.5万");
		ar_1.add("num");
		
		ar_2.add("5百万");
		ar_2.add("num");
		
		ar_3.add("前三");
		ar_3.add("unknown");
		
		ar_4.add("三季度");
		ar_4.add("unknown");
		
		ar_5.add("十手");
		ar_5.add("unknown");
		
		//base
		ar_6.add("15000.000000");
		ar_6.add("num");
		
		ar_7.add("5000000");
		ar_7.add("num");
		
		ar_8.add("前3");
		ar_8.add("sort");
		
		ar_9.add("3季度");
		ar_9.add("date");
		
		ar_10.add("10手");
		ar_10.add("num");
		
		baseList.add(ar_6);
		baseList.add(ar_7);
		baseList.add(ar_8);
		baseList.add(ar_9);
		baseList.add(ar_10);
		testList.add(ar_1);
		testList.add(ar_2);
		testList.add(ar_3);
		testList.add(ar_4);
		testList.add(ar_5);
		
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();
		NumParser numParser = new NumParser();
		
		MockQueryObject resultQueryObj = new MockQueryObject(baseList);
		Query baseQuery = resultQueryObj.getQuery();
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery =(Query)getNumParserResult(numParser, "changeChineseNumToArabic", paraTypes, methodPara, "query_");
		System.out.println(baseQuery.getNodes());
		System.out.println(testQuery.getNodes());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
	}
	
	@Test
	public void testAddUnitToNum(){
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
		ArrayList<String> ar_5 = new ArrayList<String>();
		ArrayList<String> ar_6 = new ArrayList<String>();
		ArrayList<String> ar_7 = new ArrayList<String>();
		ArrayList<String> ar_8 = new ArrayList<String>();
		ArrayList<String> ar_9 = new ArrayList<String>();
		ArrayList<String> ar_10 = new ArrayList<String>();
		
		ar_1.add("百分之");
		ar_1.add("unknown");
		
		ar_2.add("50");
		ar_2.add("num");
		
		ar_3.add("10手");
		ar_3.add("num");
		
		ar_4.add("5");
		ar_4.add("num");
		
		ar_5.add("倍");
		ar_5.add("unknown");
				
		ar_6.add("交易日");
		ar_6.add("unknown");
		
		//base
		ar_7.add("50%");
		ar_7.add("num");
		
		ar_8.add("5倍");
		ar_8.add("num");
		
		ar_9.add("5交易日");
		ar_9.add("date");
		
		testList.add(ar_1);
		testList.add(ar_2);
		testList.add(ar_3);
		testList.add(ar_4);
		testList.add(ar_5);
		testList.add(ar_4);
		testList.add(ar_6);
		
		baseList.add(ar_7);
		baseList.add(ar_3);
		baseList.add(ar_8);
		baseList.add(ar_9);
		
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();
		NumParser numParser = new NumParser();
		
		MockQueryObject resultQueryObj = new MockQueryObject(baseList);
		Query baseQuery = resultQueryObj.getQuery();
		
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "addUnitToNum", paraTypes,  methodPara, "query_");
		System.out.println(baseQuery.getNodes());
		System.out.println(testQuery.getNodes());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
	}
	
	@Test
	public void testAddDayWithoutUnit2Month(){
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
		ArrayList<String> ar_5 = new ArrayList<String>();
		ArrayList<String> ar_6 = new ArrayList<String>();
		
		ar_1.add("09月");
		ar_1.add("date");
		
		ar_2.add("50");
		ar_2.add("num");
						
		ar_3.add("28");
		ar_3.add("num");
		
		ar_4.add("31");
		ar_4.add("num");
		//base				
		ar_5.add("09月28");
		ar_5.add("date");					
		
		testList.add(ar_1);
		testList.add(ar_2);
		testList.add(ar_1);
		testList.add(ar_3);
		testList.add(ar_1);
		testList.add(ar_4);
		
		baseList.add(ar_1);
		baseList.add(ar_2);
		baseList.add(ar_5);
		baseList.add(ar_1);
		baseList.add(ar_4);
		
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();
		NumParser numParser = new NumParser();
		
		MockQueryObject resultQueryObj = new MockQueryObject(baseList);
		Query baseQuery = resultQueryObj.getQuery();
		
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "addDayWithoutUnit2Month", paraTypes, methodPara,  "query_");
		System.out.println(baseQuery.getNodes());
		System.out.println(testQuery.getNodes());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
		
	}
	
	@Test
	public void testAddSubtractionSign(){
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();		
		
		ar_1.add("-");
		ar_1.add("unknown");
		
		ar_2.add("5");
		ar_2.add("num");
						
		ar_3.add("-5");
		ar_3.add("num");						
		
		testList.add(ar_1);
		testList.add(ar_2);		
		
		baseList.add(ar_3);		
		
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();
		NumParser numParser = new NumParser();
		
		MockQueryObject resultQueryObj = new MockQueryObject(baseList);
		Query baseQuery = resultQueryObj.getQuery();
		
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "addSubtractionSign", paraTypes, methodPara, "query_");
		System.out.println(baseQuery.getNodes()); 
		System.out.println(testQuery.getNodes());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
		
	}
	
	@Test
	public void testGetWholeNum(){
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
		ArrayList<String> ar_5 = new ArrayList<String>();
		ArrayList<String> ar_6 = new ArrayList<String>();
		ArrayList<String> ar_7 = new ArrayList<String>();
		ArrayList<String> ar_8 = new ArrayList<String>();
		ArrayList<String> ar_9 = new ArrayList<String>();
		ArrayList<String> ar_10 = new ArrayList<String>();
		ArrayList<String> ar_11 = new ArrayList<String>();
		ArrayList<String> ar_12 = new ArrayList<String>();
		ArrayList<String> ar_13 = new ArrayList<String>();
		ArrayList<String> ar_14 = new ArrayList<String>();
		ArrayList<String> ar_15 = new ArrayList<String>();
		ArrayList<String> ar_16 = new ArrayList<String>();
		
		ar_1.add("3");
		ar_1.add("num");
					
		ar_2.add("-");
		ar_2.add("unknown");
						
		ar_3.add("5交易日");
		ar_3.add("date");	
		
		ar_4.add("4块");
		ar_4.add("num");
		
		ar_5.add("2毛");
		ar_5.add("num");
						
		ar_6.add("0.2块");
		ar_6.add("num");	
		
		ar_7.add("0.5交易日");
		ar_7.add("date");	
		//分隔符
		ar_8.add(" ");
		ar_8.add("unknown");
		
		ar_9.add("3-5交易日");
		ar_9.add("date");
		
		ar_10.add("0.2块-4块");
		ar_10.add("num");
		
		ar_11.add("4块2毛");
		ar_11.add("num");
		
		ar_12.add("4块0.2块");
		ar_12.add("num");
		
		ar_13.add("4块3");
		ar_13.add("num");
		
		ar_14.add("30.5交易日");
		ar_14.add("date");
		
		ar_15.add("分隔符");
		ar_15.add("unknown");
		
		ar_16.add("3点20");
		ar_16.add("date");
								
		//num + 连接符  + date
		testList.add(ar_1);
		testList.add(ar_2);	
		testList.add(ar_3);
		
		//num + 连接符 + num
		testList.add(ar_6);
		testList.add(ar_2);	
		testList.add(ar_4);
		
		//钱（单位） + 钱（单位） 不一致
        testList.add(ar_8);
		testList.add(ar_4);
		testList.add(ar_5);
		
		//钱（单位） + 钱（单位） 一致
		testList.add(ar_8);
		testList.add(ar_4);
		testList.add(ar_6);
		
		//钱（单位） + 钱（单位） 前面有单位，后面没单位
		testList.add(ar_8);
		testList.add(ar_4);
		testList.add(ar_1);
		
		//num + data(单位)
		testList.add(ar_8);
		testList.add(ar_1);
		testList.add(ar_7);
		
		//不处理的 ：num + 连接符 且连接符下一个为null 
		testList.add(ar_8);
		testList.add(ar_1);
		testList.add(ar_2);
		
		testList.add(ar_15);
		testList.add(ar_16);
			
		baseList.add(ar_9);	
		baseList.add(ar_10);
		baseList.add(ar_8);
		baseList.add(ar_11);
		baseList.add(ar_8);
		baseList.add(ar_12);
		baseList.add(ar_8);
		baseList.add(ar_13);
		baseList.add(ar_8);
		baseList.add(ar_14);
		baseList.add(ar_8);
		baseList.add(ar_1);
		baseList.add(ar_2);
		baseList.add(ar_15);
		baseList.add(ar_16);
		
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();
		NumParser numParser = new NumParser();
		
		MockQueryObject resultQueryObj = new MockQueryObject(baseList);
		Query baseQuery = resultQueryObj.getQuery();
		
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "getWholeNum", paraTypes, methodPara, "query_");
		System.out.println(baseQuery.getNodes());
		System.out.println(testQuery.getNodes());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
		
	}
	
	@Test
	public void testSeparateDateByUnitFromNum(){
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
		ArrayList<String> ar_5 = new ArrayList<String>();
		ArrayList<String> ar_6 = new ArrayList<String>();
						
		ar_1.add("2个");
		ar_1.add("num");
					
		ar_2.add("月");
		ar_2.add("unknown");						
	
		ar_3.add("2%");
		ar_3.add("unknown");	
		
		ar_4.add("2手");
		ar_4.add("num");
		
		ar_5.add("季度");
		ar_5.add("unknown");
		
		ar_6.add("2个月");
		ar_6.add("date");
						
						
		//2年格式
		testList.add(ar_1);	
		testList.add(ar_2);	
		
		//2%格式
	    testList.add(ar_3);		
	    testList.add(ar_4);
	    testList.add(ar_5);
	    
	    baseList.add(ar_6);	
	    baseList.add(ar_3);
		baseList.add(ar_4);
		baseList.add(ar_5);
				
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();
		NumParser numParser = new NumParser();
		
		MockQueryObject resultQueryObj = new MockQueryObject(baseList);
		Query baseQuery = resultQueryObj.getQuery();
		
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "separateDateByUnitFromNum", paraTypes, methodPara, "query_");
        System.out.println(baseQuery.getNodes());
		System.out.println(testQuery.getNodes());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());		
	}
	
	@Test
	public void testSeparateDateWithYMDFromNum(){
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
		ArrayList<String> ar_5 = new ArrayList<String>();
		ArrayList<String> ar_6 = new ArrayList<String>();
		ArrayList<String> ar_7 = new ArrayList<String>();
		ArrayList<String> ar_8 = new ArrayList<String>();
		ArrayList<String> ar_9 = new ArrayList<String>();
		ArrayList<String> ar_10 = new ArrayList<String>();
		ArrayList<String> ar_11 = new ArrayList<String>();
		ArrayList<String> ar_12 = new ArrayList<String>();
		ArrayList<String> ar_13 = new ArrayList<String>();	
		
		ar_1.add("20091019");
		ar_1.add("num");
					
		ar_2.add("日");
		ar_2.add("unknown");
						
		ar_3.add("20090231");
		ar_3.add("num");	
		
		ar_4.add(" ");
		ar_4.add("unknown");
		
		ar_5.add("2009.10.19");
		ar_5.add("num");
						
		ar_6.add("2009.02.31");
		ar_6.add("num");	
		
		ar_7.add("09.10.19");
		ar_7.add("num");	
		
		ar_8.add("09.02.31");
		ar_8.add("num");
		
		ar_9.add("20091019");
		ar_9.add("date");
		
		ar_10.add("20091019日");
		ar_10.add("date");
		
		ar_11.add("2009.10.19");
		ar_11.add("date");
		
		ar_12.add("2009.10.19日");
		ar_12.add("date");	
		
		ar_13.add("09.10.19");
		ar_13.add("date");
			
		//20091019格式
		testList.add(ar_1);		
		
		//20091019日格式
		testList.add(ar_4);
		testList.add(ar_1);
		testList.add(ar_2);		
		
		//20090231 不能解析		
		testList.add(ar_3);		
		
		//2009.10.19格式
		testList.add(ar_4);
		testList.add(ar_5);
		
		//2009.10.19日格式
		testList.add(ar_4);
		testList.add(ar_5);
		testList.add(ar_2);
		
		//2009.02.31 不能解析		
		testList.add(ar_6);
		
		//09.10.19格式
		testList.add(ar_4);
		testList.add(ar_7);
		
		//09.02.31格式
		testList.add(ar_4);
		testList.add(ar_8);		
		System.out.println(testList);
		
		baseList.add(ar_9);	
		baseList.add(ar_4);
		baseList.add(ar_10);
		baseList.add(ar_3);
		baseList.add(ar_4);
		baseList.add(ar_11);
		baseList.add(ar_4);
		baseList.add(ar_12);
		baseList.add(ar_6);
		baseList.add(ar_4);
		baseList.add(ar_13);
		baseList.add(ar_4);
		baseList.add(ar_8);
		
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();
		NumParser numParser = new NumParser();
		
		MockQueryObject resultQueryObj = new MockQueryObject(baseList);
		Query baseQuery = resultQueryObj.getQuery();
		
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "separateDateWithYMDFromNum", paraTypes, methodPara, "query_");
		System.out.println(baseQuery.getNodes());
		System.out.println(testQuery.getNodes());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
		
	}
	
	@Test
	public void testSeparateDateWithWeekSignFromNum(){
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
		ArrayList<String> ar_5 = new ArrayList<String>();		
						
		ar_1.add("二个");
		ar_1.add("unknown");
					
		ar_2.add("月");
		ar_2.add("unknown");						
	
		ar_3.add("本周");
		ar_3.add("unknown");	
		
		ar_4.add("末");
		ar_4.add("unknown");
		
		ar_5.add("本周末");
		ar_5.add("date");				
						
		//二个月格式
		testList.add(ar_1);	
		testList.add(ar_2);	
		
		//本周末格式
		testList.add(ar_3);		
		testList.add(ar_4);			
						
		baseList.add(ar_1);
		baseList.add(ar_2);
		baseList.add(ar_5);
				
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();
		NumParser numParser = new NumParser();
		
		MockQueryObject resultQueryObj = new MockQueryObject(baseList);
		Query baseQuery = resultQueryObj.getQuery();
		
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "separateDateWithWeekSignFromNum", paraTypes, methodPara, "query_");
		System.out.println(baseQuery.getNodes());
		System.out.println(testQuery.getNodes());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
		
	}
	
	@Test
	public void testSeparateDateWithOnlyYearFromNum(){
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
		ArrayList<String> ar_5 = new ArrayList<String>();
		ArrayList<String> ar_6 = new ArrayList<String>();
		ArrayList<String> ar_7 = new ArrayList<String>();
		ArrayList<String> ar_8 = new ArrayList<String>();
		ArrayList<String> ar_9 = new ArrayList<String>();
						
		ar_1.add("1999");
		ar_1.add("num");
					
		//ar_2.add("2000-2001");
		//ar_2.add("num");						
	
		ar_3.add("2001");
		ar_3.add("num");	
		
		ar_4.add("以上");
		ar_4.add("unknown");
		
		ar_5.add(">=");
		ar_5.add("unknown");	
		
		ar_6.add(" ");
		ar_6.add("unknown");
		
		ar_7.add("1999年");
		ar_7.add("date");
		
		//ar_8.add("2000-2001年");
		//ar_8.add("date");
		
		ar_9.add("2001年");
		ar_9.add("date");
											
		//1990格式
		testList.add(ar_1);	
		
		//2000-2001格式		
		//testList.add(ar_2);
		
		//2001 右不为"以上，以下"，左不为 ">=..."
		testList.add(ar_3);
		
		//2001右不为"以上，以下"，左为 ">=..."
		testList.add(ar_5);
		testList.add(ar_3);
		
		//2001 右为"以上，以下"，左不为 ">=..."
		testList.add(ar_3);
		testList.add(ar_4);				
						
		baseList.add(ar_7);
		//baseList.add(ar_8);
		baseList.add(ar_9);
		baseList.add(ar_5);
		baseList.add(ar_3);
		baseList.add(ar_3);
		baseList.add(ar_4);
				
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();
		NumParser numParser = new NumParser();
		
		MockQueryObject resultQueryObj = new MockQueryObject(baseList);
		Query baseQuery = resultQueryObj.getQuery();
		
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "separateDateWithOnlyYearFromNum", paraTypes, methodPara, "query_");
		System.out.println(baseQuery.getNodes());
		System.out.println(testQuery.getNodes());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
		
	}
	
	@Test
	public void testSeparateDateRangeWithYMDFromNum(){
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
				
		ar_1.add("20091019-20091022");
		ar_1.add("num");
					
		ar_2.add("20090231-20091022");
		ar_2.add("num");						
	
		ar_3.add("20091019-20091022");
		ar_3.add("date");
		
		ar_4.add(" ");
		ar_4.add("unknown");
					
		//20091019-20091022格式
		testList.add(ar_1);		
		
		//20091019日格式
		testList.add(ar_4);		
		testList.add(ar_2);				
					
		baseList.add(ar_3);	
		baseList.add(ar_4);
		baseList.add(ar_2);
								
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();
		NumParser numParser = new NumParser();
		
		MockQueryObject resultQueryObj = new MockQueryObject(baseList);
		Query baseQuery = resultQueryObj.getQuery();
		
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "separateDateRangeWithYMDFromNum", paraTypes, methodPara, "query_");
		System.out.println(baseQuery.getNodes());
		System.out.println(testQuery.getNodes());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
		
	}
	
	@Test
	public void testSeparateDateRangeWithOnlyYearFromNum(){
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
		ArrayList<String> ar_5 = new ArrayList<String>();
				
		ar_1.add("2009-2010");
		ar_1.add("num");
					
		ar_2.add("2009-2009");
		ar_2.add("num");						
	
		ar_3.add("2009-2010年");
		ar_3.add("date");
		
		ar_4.add("2009-2009年");
		ar_4.add("date");
		
		ar_5.add("2009-2009");
		ar_5.add("unknown");
					
		//20091019-20091022格式
		testList.add(ar_1);		
		
		//20091019日格式
		testList.add(ar_2);	
		
		testList.add(ar_5);
					
		baseList.add(ar_3);	
		baseList.add(ar_4);
		baseList.add(ar_5);
										
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();
		NumParser numParser = new NumParser();
		
		MockQueryObject resultQueryObj = new MockQueryObject(baseList);
		Query baseQuery = resultQueryObj.getQuery();
		
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "separateDateRangeWithOnlyYearFromNum", paraTypes, methodPara, "query_");
		System.out.println(baseQuery.getNodes());
		System.out.println(testQuery.getNodes());
		assertEquals(baseQuery.getNodes().toString(), testQuery.getNodes().toString());
		
	}
	
	@Test
	public void testGetNumRange(){
		ArrayList<ArrayList<String>> baseList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testList = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> ar_1 = new ArrayList<String>();
		ArrayList<String> ar_2 = new ArrayList<String>();
		ArrayList<String> ar_3 = new ArrayList<String>();
		ArrayList<String> ar_4 = new ArrayList<String>();
		ArrayList<String> ar_5 = new ArrayList<String>();
		
		ar_1.add("FakeNumNode");
		ar_1.add("vagueNum");
		
		ar_2.add("NumNode1");
		ar_2.add("num");
		
		ar_3.add("大于10元");
		ar_3.add("num");
		
		ar_4.add("NotNumnode");
		ar_4.add("unknown");
		
		String para_1 = "3-5";
		//FakeNumNode
		testList.add(ar_1);
		//_oldstr不为null
		testList.add(ar_2);
		//_oldstr为null, text不为null,numrange格式
		testList.add(ar_3);
		//continue
		testList.add(ar_4);
				
		MockQueryObject queryObj = new MockQueryObject(testList);
		Query query = queryObj.getQuery();			
	    NumNode nn_1 = (NumNode)query.getNodes().get(1);
	    nn_1.oldStr_ = para_1;
	    NumParser numParser = new NumParser();
	    //System.out.println(((NumNode)query.nodes().get(1)).oldStr_);
		   	
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "getNumRange", paraTypes, methodPara, "query_");
		//System.out.println(baseQuery.nodes());
		System.out.println(testQuery.getNodes());
		//System.out.println(((NumNode)query.nodes().get(1)).oldStr_);
		String baseString = "[NodeType:NUM  NodeText:FakeNumNode  NumUnit:UNKNOWN, " +
				"NodeType:NUM  NodeText:NumNode1  NumUnit:UNKNOWN, " +
				"NodeType:NUM  NodeText:大于10元  NumUnit:YUAN, " +
				"NodeType:UNKNOWN  NodeText:NotNumnode]";
		assertEquals(baseString, testQuery.getNodes().toString());
		System.out.println(((NumNode)testQuery.getNodes().get(1)).getNuminfo());
		String numInfo_1 = "3.00--5.00";
		assertEquals(numInfo_1, ((NumNode)testQuery.getNodes().get(1)).getNuminfo().toString());
		
		System.out.println(((NumNode)testQuery.getNodes().get(2)).getNuminfo());
		String numInfo_2 = "10.00--1000000000000000.00";
		assertEquals(numInfo_2, ((NumNode)testQuery.getNodes().get(2)).getNuminfo().toString());		
	}
	//getNumRangeFromNumWithCompare
	@Test
	public void testGetNumRangeFromStr(){
		String para_1 = "10块5毛";
		String para_2 = "3元5角";
		String para_3 = ">=";
		String para_4 = "大于";
		String para_5 = "小于";
		String para_6 = "等于";
		String para_7 = "0块0角";
		String para_8 = "-";
		String para_9 = "<=";
		String para_10 = "3.5元";
		
		//10块5毛
		String case_1 = para_1;
		//0块0毛
		String case_2 = para_7;
		//3块5毛-10块5毛
		String case_3 = para_2 + para_8 + para_1;
		// 0-0
		String case_4 = para_7 + para_8 + para_7;
		//>=
		String case_5 = para_3 + para_10;
		//大于等于
		String case_6 = para_4 + para_6 + para_10;
		//<=
		String case_7 = para_9 + para_10;
		//小于等于
		String case_8 = para_5 + para_6 + para_10;
		//等于
		String case_9 = para_6 + para_10;
		NumRange range_1 = null ;
	    NumRange range_2 = null;
	    NumRange range_3 = null ;
	    NumRange range_4 = null;
	    NumRange range_5 = null;
	    //NumRange range_6 = null;
	    NumRange range_7 = null;
	    //NumRange range_8 = null;
	    NumRange range_9 = null;
		try{	
		    range_1 = NumParser.getNumRangeFromStr(case_1);
		    range_2 = NumParser.getNumRangeFromStr(case_2);
		    range_3 = NumParser.getNumRangeFromStr(case_3);
		    range_4 = NumParser.getNumRangeFromStr(case_4);
		    range_5 = NumParser.getNumRangeFromStr(case_5);
		    //range_6 = NumParser.getNumRangeFromStr(case_6);
		    range_7 = NumParser.getNumRangeFromStr(case_7);
		    //range_8 = NumParser.getNumRangeFromStr(case_8);
		    range_9 = NumParser.getNumRangeFromStr(case_9);
		}catch(Exception e){
			;
		}				
		System.out.println(range_1.toString() + range_1.getUnit());
		//assertEquals((range_1.toString() + range_1.getUnit()), "10.50--10.50元" );
       	System.out.println(range_2.toString() + range_2.getUnit());
       	//assertEquals((range_2.toString() + range_2.getUnit()), "0.00--0.00元" );	
       	System.out.println(range_3.toString() + range_3.getUnit());
       	//assertEquals((range_3.toString() + range_3.getUnit()), "3.50--10.50元" );
       	System.out.println(range_4.toString() + range_4.getUnit());       	
	}
	
	@Test
	public void testGetNumRangeFromRegularChineseNumStr(){
		String para_1 = "2十手";
		String para_2 = "4.5万股";	
				
		Query query = new Query("");
		NumParser numParser = new NumParser();
		Class[] paraTypes = {String.class};
		
		String methodPara_1 = para_1;
		String methodPara_2 = para_2;
			
		NumRange nr_1 = getNumRange(numParser, "getNumRangeFromRegularChineseNumStr", paraTypes, methodPara_1, "numRange");
		System.out.println(nr_1);
		assertEquals("20.00--20.00", nr_1.toString());
		NumRange nr_2 = getNumRange(numParser, "getNumRangeFromRegularChineseNumStr", paraTypes, methodPara_2, "numRange");
		System.out.println(nr_2);	
		assertEquals("45000.00--45000.00", nr_2.toString());
		
	}
	
	@Test
	public void testGetNumRangeFromChineseRangeStr(){
		String para_1 = "2手-2十手";
		String para_2 = "4.5万股";	
		String para_3 = "2-2万";	
		String para_4 = "2-2万亿";	
		String para_5 = "2-2千";	
		String para_6 = "2-2千亿";
		String para_7 = "2-2百";	
		String para_8 = "2-2百亿";
		String para_9 = "2-2十";	
		String para_10 = "2-2十亿";
		String para_11 = "2-2千";	
		String para_12 = "2-2千万";
		String para_13 = "2-2百万";
		String para_14 = "2-2十万";
		String para_15 = "2-2亿";	
		String para_16 = "2万-2亿";
		
				
		Query query = new Query("");
		NumParser numParser = new NumParser();
		Class[] paraTypes = {String.class};
		
		String methodPara_1 = para_1;
		String methodPara_2 = para_2;
		String methodPara_3 = para_3;
		String methodPara_4 = para_4;
		String methodPara_5 = para_5;
		String methodPara_6 = para_6;
		String methodPara_7 = para_7;
		String methodPara_8 = para_8;
		String methodPara_9 = para_9;
		String methodPara_10 = para_10;
		String methodPara_11 = para_11;
		String methodPara_12 = para_12;
		String methodPara_13 = para_13;
		String methodPara_14 = para_14;
		String methodPara_15 = para_15;
		String methodPara_16 = para_16;
			
		NumRange nr_1 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_1, "numRange");
		System.out.println(nr_1);
		assertEquals(null, nr_1);
		NumRange nr_2 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_2, "numRange");
		System.out.println(nr_2);	
		assertEquals(null, nr_2);
		NumRange nr_3 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_3, "numRange");
		System.out.println(nr_3);
		assertEquals("20000.00--20000.00", nr_3.toString());
		NumRange nr_4 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_4, "numRange");
		System.out.println(nr_4);	
		assertEquals("2000000000000.00--2000000000000.00", nr_4.toString());
		NumRange nr_5 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_5, "numRange");
		System.out.println(nr_5);
		assertEquals("2000.00--2000.00", nr_5.toString());
		NumRange nr_6 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_6, "numRange");
		System.out.println(nr_6);	
		assertEquals("200000000000.00--200000000000.00", nr_6.toString());
		NumRange nr_7 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_7, "numRange");
		System.out.println(nr_7);
		assertEquals("100.00--200.00", nr_7.toString());
		NumRange nr_8 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_8, "numRange");
		System.out.println(nr_8);	
		assertEquals("20000000000.00--20000000000.00", nr_8.toString());
		NumRange nr_9 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_9, "numRange");
		System.out.println(nr_9);
		assertEquals("2.00--20.00", nr_9.toString());
		NumRange nr_10 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_10, "numRange");
		System.out.println(nr_10);	
		assertEquals("2000000000.00--2000000000.00", nr_10.toString());
		NumRange nr_11 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_11, "numRange");
		System.out.println(nr_11);
		assertEquals("2000.00--2000.00", nr_11.toString());
		NumRange nr_12 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_12, "numRange");
		System.out.println(nr_12);	
		assertEquals("20000000.00--20000000.00", nr_12.toString());
		NumRange nr_13 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_13, "numRange");
		System.out.println(nr_13);
		assertEquals("2000000.00--2000000.00", nr_13.toString());
		NumRange nr_14 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_14, "numRange");
		System.out.println(nr_14);	
		assertEquals("200000.00--200000.00", nr_14.toString());
		NumRange nr_15 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_15, "numRange");
		System.out.println(nr_15);
		assertEquals("200000000.00--200000000.00", nr_15.toString());
		NumRange nr_16 = getNumRange(numParser, "getNumRangeFromChineseRangeStr", paraTypes, methodPara_16, "numRange");
		System.out.println(nr_16);	
		assertEquals(null, nr_16);
		
	}
	
	@Test
	public void testChangeUnit(){
		double MAX_ = Double.valueOf("1000000000000000");
	    double MIN_ = Double.valueOf("-1000000000000000");
		NumRange nr_1 = new NumRange();
		nr_1.setNumRange("3", "3");
		nr_1.setUnit("手");
		NumNode nn_1 = new NumNode("");
		nn_1.setNuminfo(nr_1);
		
		//rangeType == ,from >=2 
		NumRange nr_2 = new NumRange();
		nr_2.setNumRange("2", "2");
		nr_2.setUnit("股");
		NumNode nn_2 = new NumNode("");
		nn_2.setNuminfo(nr_2);
		
		//rangeType == ,from <2 
		NumRange nr_3 = new NumRange();
		nr_3.setNumRange("1", "1");
		nr_3.setUnit("股");
		NumNode nn_3 = new NumNode("");
		nn_3.setNuminfo(nr_3);
		
		//rangeType <,<= to >=2
		NumRange nr_4 = new NumRange();
		nr_4.setNumRange(String.valueOf(MIN_), "2");
		nr_4.setUnit("股");
		NumNode nn_4 = new NumNode("");
		nn_4.setNuminfo(nr_4);
		
		//rangeType <,<= to < 2 
		NumRange nr_5 = new NumRange();
		nr_5.setNumRange(String.valueOf(MIN_), "1");
		nr_5.setUnit("股");
		NumNode nn_5 = new NumNode("");
		nn_5.setNuminfo(nr_5);
		
		//rangeType >,>= from >=2
		NumRange nr_6 = new NumRange();
		nr_6.setNumRange("2", String.valueOf(MAX_));
		nr_6.setUnit("股");
		NumNode nn_6 = new NumNode("");
		nn_6.setNuminfo(nr_6);
		
		//rangeType >,>= from < 2
		NumRange nr_7 = new NumRange();
		nr_7.setNumRange("1", String.valueOf(MAX_));
		nr_7.setUnit("股");
		NumNode nn_7 = new NumNode("");
		nn_7.setNuminfo(nr_7);
		
		Query query = new Query("");
		
		query.getNodes().add(nn_1);
		query.getNodes().add(nn_2);
		query.getNodes().add(nn_3);
		query.getNodes().add(nn_4);
		query.getNodes().add(nn_5);
		query.getNodes().add(nn_6);
		query.getNodes().add(nn_7);
				
		Class[] paraTypes = null;
		Object methodPara = null;
		NumParser numParser = new NumParser();
		Query testQuery = getNumParserResult(numParser, "changeUnit", paraTypes, methodPara, "query_");
		
		//手
		System.out.println(((NumNode)testQuery.getNodes().get(0)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(0)).getNuminfo().getUnit());
		assertEquals(((NumNode)testQuery.getNodes().get(0)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(0)).getNuminfo().getUnit(), "300.00--300.00股" );
		//==
		System.out.println(((NumNode)testQuery.getNodes().get(1)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(1)).getNuminfo().getUnit());
		assertEquals(((NumNode)testQuery.getNodes().get(1)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(1)).getNuminfo().getUnit(), "2.00--2.00股" );
		System.out.println(((NumNode)testQuery.getNodes().get(2)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(2)).getNuminfo().getUnit());
		assertEquals(((NumNode)testQuery.getNodes().get(2)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(2)).getNuminfo().getUnit(), "1.00--1.00null" );
		//<=
		System.out.println(((NumNode)testQuery.getNodes().get(3)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(3)).getNuminfo().getUnit());
		assertEquals(((NumNode)testQuery.getNodes().get(3)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(3)).getNuminfo().getUnit(), "-1000000000000000.00--2.00股" );
		System.out.println(((NumNode)testQuery.getNodes().get(4)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(4)).getNuminfo().getUnit());
		assertEquals(((NumNode)testQuery.getNodes().get(4)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(4)).getNuminfo().getUnit(), "-1000000000000000.00--1.00null" );
		//>=
		System.out.println(((NumNode)testQuery.getNodes().get(5)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(5)).getNuminfo().getUnit());
		assertEquals(((NumNode)testQuery.getNodes().get(5)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(5)).getNuminfo().getUnit(), "2.00--1000000000000000.00股" );
		System.out.println(((NumNode)testQuery.getNodes().get(6)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(6)).getNuminfo().getUnit());
		assertEquals(((NumNode)testQuery.getNodes().get(6)).getNuminfo().toString() + ((NumNode)testQuery.getNodes().get(6)).getNuminfo().getUnit(), "1.00--1000000000000000.00null" );				
	}
	
	@Test
	public void testChangeRangeByCompareLeft(){
		String MAX_ = "1000000000000000";
	    String MIN_ = "-1000000000000000";
	    //==
		NumRange nr_1 = MockQueryObject.getNumRange("2", "2", null);
		//<=
		NumRange nr_2 = MockQueryObject.getNumRange(MIN_, "100", null);
		//>=
		NumRange nr_3 = MockQueryObject.getNumRange("2", MAX_, null);

		String text_1 = ">=";
		String text_2 = "<=";
		String text_3 = "=";
		String text_4 = "分隔符 ";
				
		UnknownNode nn_1 = new UnknownNode(text_1);
		UnknownNode nn_2 = new UnknownNode(text_2);
		UnknownNode nn_3 = new UnknownNode(text_3);
		UnknownNode nn_4 = new UnknownNode(text_4);
		//==
		NumNode nn_5 = MockQueryObject.getNumNodeWithNumRange("等于", nr_1);
		//<=
		NumNode nn_6 = MockQueryObject.getNumNodeWithNumRange("小于",  nr_2);
		NumNode nn_7 = MockQueryObject.getNumNodeWithNumRange("小于",  nr_2);
		//>=
		NumNode nn_8 = MockQueryObject.getNumNodeWithNumRange("大于",  nr_3);
			
		Query query = new Query("");
		//left >=, cur = 
		query.getNodes().add(nn_1);
		query.getNodes().add(nn_5);
		query.getNodes().add(nn_4);
	    //left >=, cur <	
		query.getNodes().add(nn_1);
		query.getNodes().add(nn_6);
		query.getNodes().add(nn_4);
		//left <=, cur <
		query.getNodes().add(nn_2);
		query.getNodes().add(nn_7);
		query.getNodes().add(nn_4);
		//left =, cur >
		query.getNodes().add(nn_3);
		query.getNodes().add(nn_8);
		NumParser numParser = new NumParser();
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "changeRangeByCompare", paraTypes, methodPara, "query_");
		//QueryUtil.printNodes(testQuery);
		String baseString = "[NodeType:NUM  NodeText:>=等于  NumUnit:UNKNOWN, NodeType:UNKNOWN  NodeText:分隔符 ," +
				" NodeType:NUM  NodeText:>=小于  NumUnit:UNKNOWN, NodeType:UNKNOWN  NodeText:分隔符 , " +
				"NodeType:NUM  NodeText:<=小于  NumUnit:UNKNOWN, NodeType:UNKNOWN  NodeText:分隔符 , " +
				"NodeType:NUM  NodeText:=大于  NumUnit:UNKNOWN]";
		System.out.println(testQuery.getNodes().toString());
		//assertEquals(baseString, testQuery.nodes().toString() );
		String testStr_0 = ((NumNode)testQuery.getNodes().get(0)).getNuminfo().toString() + 
                ((NumNode)testQuery.getNodes().get(0)).getNuminfo().isIncludeFrom() + 
                ((NumNode)testQuery.getNodes().get(0)).getNuminfo().isIncludeTo();
        System.out.println(testStr_0);
        assertEquals("2.00--1000000000000000.00truefalse", testStr_0);
		String testStr_2 = ((NumNode)testQuery.getNodes().get(2)).getNuminfo().toString() + 
		        ((NumNode)testQuery.getNodes().get(2)).getNuminfo().isIncludeFrom() + 
		        ((NumNode)testQuery.getNodes().get(2)).getNuminfo().isIncludeTo();
		System.out.println(testStr_2);
		assertEquals("-1000000000000000.00--100.00falsetrue", testStr_2);
		String testStr_4 = ((NumNode)testQuery.getNodes().get(4)).getNuminfo().toString() + 
                ((NumNode)testQuery.getNodes().get(4)).getNuminfo().isIncludeFrom() + 
                ((NumNode)testQuery.getNodes().get(4)).getNuminfo().isIncludeTo();
		System.out.println(testStr_4);
		assertEquals("-1000000000000000.00--100.00falsetrue", testStr_2);
		String testStr_6 = ((NumNode)testQuery.getNodes().get(6)).getNuminfo().toString() + 
                ((NumNode)testQuery.getNodes().get(6)).getNuminfo().isIncludeFrom() + 
                ((NumNode)testQuery.getNodes().get(6)).getNuminfo().isIncludeTo();
	    System.out.println(testStr_6);
	    assertEquals("2.00--1000000000000000.00truetrue", testStr_6);
	}
	
	@Test
	public void testChangeRangeByCompareRight(){
		String MAX_ = "1000000000000000";
	    String MIN_ = "-1000000000000000";
	    //==
		NumRange nr_1 = MockQueryObject.getNumRange("2", "2", null);
		//<=
		NumRange nr_2 = MockQueryObject.getNumRange(MIN_, "100", null);
		//>=
		NumRange nr_3 = MockQueryObject.getNumRange("2", MAX_, null);

		String text_1 = "以上";
		String text_2 = "以下";
		String text_3 = "分隔符";
						
		UnknownNode nn_1 = new UnknownNode(text_1);
		UnknownNode nn_2 = new UnknownNode(text_2);
		UnknownNode nn_3 = new UnknownNode(text_3);
		
		//==
		NumNode nn_4 = MockQueryObject.getNumNodeWithNumRange("等于", nr_1);
		//<=
		NumNode nn_5 = MockQueryObject.getNumNodeWithNumRange("小于",  nr_2);
		//>=
		NumNode nn_6 = MockQueryObject.getNumNodeWithNumRange("大于",  nr_3);
			
		Query query = new Query("");
		
		//cur = ,right 以上	
		query.getNodes().add(nn_4);
		query.getNodes().add(nn_1);
		query.getNodes().add(nn_3);
	    //cur <, right 以下	
		query.getNodes().add(nn_5);
		query.getNodes().add(nn_2);
		query.getNodes().add(nn_3);
		//cur >, right 以上
		query.getNodes().add(nn_6);
		query.getNodes().add(nn_1);
		query.getNodes().add(nn_3);
		
		NumParser numParser = new NumParser();
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "changeRangeByCompare", paraTypes, methodPara, "query_");;
		//QueryUtil.printNodes(testQuery);
		System.out.println(testQuery.getNodes().toString());
		String baseString = "[NodeType:NUM  NodeText:等于以上  NumUnit:UNKNOWN, NodeType:UNKNOWN  NodeText:分隔符," +
				" NodeType:NUM  NodeText:小于以下  NumUnit:UNKNOWN, NodeType:UNKNOWN  NodeText:分隔符," +
				" NodeType:NUM  NodeText:大于以上  NumUnit:UNKNOWN, NodeType:UNKNOWN  NodeText:分隔符]";
		assertEquals(baseString, testQuery.getNodes().toString() );
		String testStr_0 = ((NumNode)testQuery.getNodes().get(0)).getNuminfo().toString() + 
                ((NumNode)testQuery.getNodes().get(0)).getNuminfo().isIncludeFrom() + 
                ((NumNode)testQuery.getNodes().get(0)).getNuminfo().isIncludeTo();
        System.out.println(testStr_0);
        assertEquals("2.00--1000000000000000.00falsefalse", testStr_0);
        
        String testStr_2 = ((NumNode)testQuery.getNodes().get(2)).getNuminfo().toString() + 
                ((NumNode)testQuery.getNodes().get(2)).getNuminfo().isIncludeFrom() + 
                ((NumNode)testQuery.getNodes().get(2)).getNuminfo().isIncludeTo();
        System.out.println(testStr_2);
        assertEquals("-1000000000000000.00--100.00falsefalse", testStr_2);
        
        String testStr_4 = ((NumNode)testQuery.getNodes().get(4)).getNuminfo().toString() + 
                ((NumNode)testQuery.getNodes().get(4)).getNuminfo().isIncludeFrom() + 
                ((NumNode)testQuery.getNodes().get(4)).getNuminfo().isIncludeTo();
        System.out.println(testStr_4);
        assertEquals("2.00--1000000000000000.00falsefalse", testStr_4);
		
	}
	
	//mark else if 里面少了一个条件LT
	// 右大，左大
	@Test
	public void testGetNewNumByLogic_1() throws TBException{
		double MAX_ = 1000000000000000.00;
	    double MIN_ = -1000000000000000.00;
	    String unit = "元";
	    
	    LogicType lt_and = LogicType.AND;
	    LogicType lt_or = LogicType.OR;
	    
	    NumRange nr_gt_1 = new NumRange();
	    nr_gt_1.setNumRange(2.00, MAX_);
	    nr_gt_1.setUnit(unit);
	    NumRange nr_gt_2 = new NumRange();
	    nr_gt_2.setNumRange(3.00, MAX_);
	    NumRange nr_gt_3 = new NumRange();
	    nr_gt_3.setNumRange(4.00, MAX_);
	    nr_gt_3.setUnit(unit);
	    NumRange nr_gt_4 = new NumRange();
	    nr_gt_4.setNumRange(5.00, MAX_);
	    
		NumNode lnn_gt_1 = new NumNode("左大于", 2.00, MAX_);
		NumNode lnn_gt_2 = new NumNode("左大于", 3.00, MAX_);
		NumNode rnn_gt_1 = new NumNode("右大于", 4.00, MAX_ );
		NumNode rnn_gt_2 = new NumNode("右大于", 5.00, MAX_ );
				
		lnn_gt_1.setNuminfo(nr_gt_1);
		lnn_gt_2.setNuminfo(nr_gt_2);
				
		rnn_gt_1.setNuminfo(nr_gt_3);
		rnn_gt_2.setNuminfo(nr_gt_4);
						
		NumNode newNN_1 ;
		newNN_1 = NumParser.getNewNumByLogic(lnn_gt_1, rnn_gt_2, lt_and );
		String baseStr_1 = "左大于并且右大于/5.00--1000000000000000.00元";		
		String testStr_1 = newNN_1.getText() + "/" +
		        newNN_1.getNuminfo().toString() + newNN_1.getNuminfo().getUnit();
		System.out.println(testStr_1);
		assertEquals(baseStr_1, testStr_1);
		
		NumNode newNN_2 ;
		newNN_2 = NumParser.getNewNumByLogic(lnn_gt_2, rnn_gt_1, lt_or );
		String baseStr_2 = "左大于或者右大于/3.00--1000000000000000.00元";		
		String testStr_2 = newNN_2.getText() + "/" +
		        newNN_2.getNuminfo().toString() + newNN_2.getNuminfo().getUnit();
		System.out.println(testStr_2);
		assertEquals(baseStr_2, testStr_2);
		
	}
	
	//mark else if 里面少了一个条件LT
	// 右小，左小
	@Test
	public void testGetNewNumByLogic_2() throws TBException{
		double MAX_ = 1000000000000000.00;
	    double MIN_ = -1000000000000000.00;
	    String unit = "元";
	    
	    LogicType lt_and = LogicType.AND;
	    LogicType lt_or = LogicType.OR;
	    
	    NumRange nr_lt_1 = new NumRange();
	    nr_lt_1.setNumRange(MIN_, 100.00);
	    nr_lt_1.setUnit(unit);
	    NumRange nr_lt_2 = new NumRange();
	    nr_lt_2.setNumRange(MIN_, 110.00);
	    NumRange nr_lt_3 = new NumRange();
	    nr_lt_3.setNumRange(MIN_, 120.00);
	    nr_lt_3.setUnit(unit);
	    NumRange nr_lt_4 = new NumRange();
	    nr_lt_4.setNumRange(MIN_, 130.00);
	    
		NumNode lnn_lt_1 = new NumNode("左小于", MIN_, 100.00 );
		NumNode lnn_lt_2 = new NumNode("左小于", MIN_, 110.00 );
		NumNode rnn_lt_1 = new NumNode("右小于", MIN_, 120.00 );
		NumNode rnn_lt_2 = new NumNode("右小于", MIN_, 130.00 );
				
		lnn_lt_1.setNuminfo(nr_lt_1);
		lnn_lt_2.setNuminfo(nr_lt_2);
				
		rnn_lt_1.setNuminfo(nr_lt_3);
		rnn_lt_2.setNuminfo(nr_lt_4);
						
		NumNode newNN_1 ;
		newNN_1 = NumParser.getNewNumByLogic(lnn_lt_1, rnn_lt_2, lt_and );
		String baseStr_1 = "左小于并且右小于/-1000000000000000.00--100.00元";		
		String testStr_1 = newNN_1.getText() + "/" +
		        newNN_1.getNuminfo().toString() + newNN_1.getNuminfo().getUnit();
		System.out.println(testStr_1);
		assertEquals(baseStr_1, testStr_1);
		
		NumNode newNN_2 ;
		newNN_2 = NumParser.getNewNumByLogic(lnn_lt_2, rnn_lt_1, lt_or );
		String baseStr_2 = "左小于或者右小于/-1000000000000000.00--120.00元";		
		String testStr_2 = newNN_2.getText() + "/" +
		        newNN_2.getNuminfo().toString() + newNN_2.getNuminfo().getUnit();
		System.out.println(testStr_2);
		assertEquals(baseStr_2, testStr_2);
		
	}
	
	//mark else if 里面少了一个条件LT
	// 右大，左小
	@Test
	public void testGetNewNumByLogic_3() throws TBException{
		double MAX_ = 1000000000000000.00;
	    double MIN_ = -1000000000000000.00;
	    String unit = "元";
	    
	    LogicType lt_and = LogicType.AND;
	    LogicType lt_or = LogicType.OR;
	    
	    NumRange nr_lt_1 = new NumRange();
	    nr_lt_1.setNumRange(MIN_, 100.00);
	    nr_lt_1.setUnit(unit);
	    NumRange nr_lt_2 = new NumRange();
	    nr_lt_2.setNumRange(MIN_, 110.00);
	    NumRange nr_gt_3 = new NumRange();
	    nr_gt_3.setNumRange(20.00, MAX_);
	    nr_gt_3.setUnit(unit);
	    NumRange nr_gt_4 = new NumRange();
	    nr_gt_4.setNumRange(30.00, MAX_);
	    
		NumNode lnn_lt_1 = new NumNode("左小于", MIN_, 100.00 );
		NumNode lnn_lt_2 = new NumNode("左小于", MIN_, 110.00 );
		NumNode rnn_gt_1 = new NumNode("右大于", 20.00, MAX_ );
		NumNode rnn_gt_2 = new NumNode("右大于", 30.00, MAX_ );
				
		lnn_lt_1.setNuminfo(nr_lt_1);
		lnn_lt_2.setNuminfo(nr_lt_2);
				
		rnn_gt_1.setNuminfo(nr_gt_3);
		rnn_gt_2.setNuminfo(nr_gt_4);
						
		NumNode newNN_1 ;
		newNN_1 = NumParser.getNewNumByLogic(lnn_lt_1, rnn_gt_2, lt_and );
		String baseStr_1 = "左小于并且右大于/30.00--100.00元";		
		String testStr_1 = newNN_1.getText() + "/" +
		        newNN_1.getNuminfo().toString() + newNN_1.getNuminfo().getUnit();
		System.out.println(testStr_1);
		assertEquals(baseStr_1, testStr_1);
		
		NumNode newNN_2 ;
		newNN_2 = NumParser.getNewNumByLogic(lnn_lt_2, rnn_gt_1, lt_or );
		String baseStr_2 = "左小于或者右大于/20.00--110.00元";		
		String testStr_2 = newNN_2.getText() + "/" +
		        newNN_2.getNuminfo().toString() + newNN_2.getNuminfo().getUnit();
		System.out.println(testStr_2);
		assertEquals(baseStr_2, testStr_2);
		
	}
	//mark else if 里面少了一个条件LT
	// 右小，左大
	@Test
	public void testGetNewNumByLogic_4() throws TBException{
		double MAX_ = 1000000000000000.00;
	    double MIN_ = -1000000000000000.00;
	    String unit = "元";
	    
	    LogicType lt_and = LogicType.AND;
	    LogicType lt_or = LogicType.OR;
	    
	    NumRange nr_lt_1 = new NumRange();
	    nr_lt_1.setNumRange(MIN_, 100.00);
	    nr_lt_1.setUnit(unit);
	    NumRange nr_lt_2 = new NumRange();
	    nr_lt_2.setNumRange(MIN_, 110.00);
	    NumRange nr_gt_3 = new NumRange();
	    nr_gt_3.setNumRange(20.00, MAX_);
	    nr_gt_3.setUnit(unit);
	    NumRange nr_gt_4 = new NumRange();
	    nr_gt_4.setNumRange(30.00, MAX_);
	    NumRange nr_gt_5 = new NumRange();
	    nr_gt_5.setNumRange(140.00, MAX_);
	    nr_gt_5.setUnit(unit);
	    NumRange nr_lt_6 = new NumRange();
	    nr_lt_6.setNumRange(MIN_, 40.00);
	    
		NumNode rnn_lt_1 = new NumNode("右小于", MIN_, 100.00 );
		NumNode rnn_lt_2 = new NumNode("右小于", MIN_, 110.00 );
		NumNode rnn_lt_3 = new NumNode("右小于", MIN_, 140.00 );
		NumNode lnn_gt_1 = new NumNode("左大于", 20.00, MAX_ );
		NumNode lnn_gt_2 = new NumNode("左大于", 30.00, MAX_ );
		NumNode lnn_gt_3 = new NumNode("左大于", 40.00, MAX_ );
				
		rnn_lt_1.setNuminfo(nr_lt_1);
		rnn_lt_2.setNuminfo(nr_lt_2);
		rnn_lt_3.setNuminfo(nr_lt_6);
				
		lnn_gt_1.setNuminfo(nr_gt_3);
		lnn_gt_2.setNuminfo(nr_gt_4);
		lnn_gt_3.setNuminfo(nr_gt_5);
						
		NumNode newNN_1 ;
		newNN_1 = NumParser.getNewNumByLogic(lnn_gt_1, rnn_lt_2, lt_and );
		String baseStr_1 = "左大于并且右小于/20.00--110.00元";		
		String testStr_1 = newNN_1.getText() + "/" +
		        newNN_1.getNuminfo().toString() + newNN_1.getNuminfo().getUnit();
		System.out.println(testStr_1);
		assertEquals(baseStr_1, testStr_1);
		
		NumNode newNN_2 ;
		newNN_2 = NumParser.getNewNumByLogic(lnn_gt_2, rnn_lt_1, lt_or );
		String baseStr_2 = "左大于或者右小于/30.00--100.00元";		
		String testStr_2 = newNN_2.getText() + "/" +
		        newNN_2.getNuminfo().toString() + newNN_2.getNuminfo().getUnit();
		System.out.println(testStr_2);
		assertEquals(baseStr_2, testStr_2);
		
		NumNode newNN_3 ;
		newNN_3 = NumParser.getNewNumByLogic(lnn_gt_3, rnn_lt_3, lt_or );
		String baseStr_3 = "左大于或者右小于/40.00--140.00元";		
		String testStr_3 = newNN_3.getText() + "/" +
		        newNN_3.getNuminfo().toString() + newNN_2.getNuminfo().getUnit();
		System.out.println(testStr_3);
		assertEquals(baseStr_3, testStr_3);
		
	}
	
	@Test
	public void testChangeByLogic(){
		double MAX_ = 1000000000000000.00;
	    double MIN_ = -1000000000000000.00;
	    
	    //当前节点不是logic节点
	    UnknownNode node_1 = new UnknownNode("NotLogic");
	    
	    //当前节点为logic节点，左节点或右节点的rangetype为空
	    LogicNode node_2 = new LogicNode(",");
	    NumNode node_3 = new NumNode("NumRangeNull");
	    
	    //当前节点为logic节点，logic节点为and,左右节点RangeType不为null，且既不是大于也不是小于
	    LogicNode node_4 = new LogicNode("、");
	    LogicType lt_1 = LogicNode.getLogicType("and");
	    node_4.logicType = lt_1;
	    
	    NumNode node_5 = new NumNode("LeftNumNode", 2.00, MAX_);
	    NumNode node_6 = new NumNode("RightNumNode", MIN_, 100.00);
	    
	    //当前节点为logic节点，logic节点为and,左右节点RangeType不为null，且既不是大于也不是小于
	    LogicNode node_7 = new LogicNode("，");
	    LogicType lt_2 = LogicNode.getLogicType("and");
	    node_7.logicType = lt_2;
	    
	    NumNode node_8 = new NumNode("LeftNumNode_2", MIN_, 200.00);
	    NumNode node_9 = new NumNode("RightNumNode_2", 20, MAX_);
	    
	    UnknownNode node_10 = new UnknownNode("分隔符");
	    
	    Query query = new Query("");
	    query.getNodes().add(node_1);
	    query.getNodes().add(node_2);
	    query.getNodes().add(node_3);
	    query.getNodes().add(node_5);
	    query.getNodes().add(node_4);
	    query.getNodes().add(node_6);
	    query.getNodes().add(node_10);
	    query.getNodes().add(node_8);
	    query.getNodes().add(node_7);
	    query.getNodes().add(node_9);
	   	   
	    NumParser numParser = new NumParser();
	    
	    Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "changeByLogic", paraTypes, methodPara, "query_");
		System.out.println(testQuery.getNodes().toString());    
		String baseString = "[NodeType:UNKNOWN  NodeText:NotLogic, " +
				"NodeType:LOGIC  NodeText:,, " +
				"NodeType:NUM  NodeText:NumRangeNull  NumUnit:UNKNOWN, " +
				"NodeType:NUM  NodeText:LeftNumNode并且RightNumNode  NumUnit:UNKNOWN, " +
				"NodeType:UNKNOWN  NodeText:分隔符, " +
				"NodeType:NUM  NodeText:LeftNumNode_2并且RightNumNode_2  NumUnit:UNKNOWN]" ;
		assertEquals(baseString, testQuery.getNodes().toString());
		System.out.println(((NumNode)testQuery.getNodes().get(3)).getNuminfo().toString());
		assertEquals("2.00--100.00", ((NumNode)testQuery.getNodes().get(3)).getNuminfo().toString());
		System.out.println(((NumNode)testQuery.getNodes().get(5)).getNuminfo().toString());
		assertEquals("20.00--200.00", ((NumNode)testQuery.getNodes().get(5)).getNuminfo().toString());
	}
	
	@Test
	public void testIsBetween_ne(){
		UnknownNode node_1 = new UnknownNode("分隔符");
		UnknownNode node_2 = new UnknownNode("不等于");
		UnknownNode node_3 = new UnknownNode("不为");
		NumNode node_4 = new NumNode("20", 20, 20);
		NumNode node_5 = new NumNode("5-8", 5, 8);
		
		Query query = new Query("");
		query.getNodes().add(node_1);
		query.getNodes().add(node_2);
		query.getNodes().add(node_4);
		query.getNodes().add(node_1);
		query.getNodes().add(node_3);
		query.getNodes().add(node_5);
		NumParser numParser = new NumParser();
					
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "isBetween", paraTypes, methodPara, "query_");
		
		System.out.println(testQuery.getNodes().toString());
		String baseString = "[NodeType:UNKNOWN  NodeText:分隔符, " +
				"NodeType:NUM  NodeText:不等于20  NumUnit:UNKNOWN, " +
				"NodeType:UNKNOWN  NodeText:分隔符, " +
				"NodeType:NUM  NodeText:不为5-8  NumUnit:UNKNOWN]";
		assertEquals(baseString, testQuery.getNodes().toString());
		String isIncludeFrom_1 = String.valueOf(((NumNode)testQuery.getNodes().get(1)).getNuminfo().isIncludeFrom());
		String isIncludeTo_1 = String.valueOf(((NumNode)testQuery.getNodes().get(1)).getNuminfo().isIncludeTo());
		String isIncludeFrom_3 = String.valueOf(((NumNode)testQuery.getNodes().get(3)).getNuminfo().isIncludeFrom());
		String isIncludeTo_3 = String.valueOf(((NumNode)testQuery.getNodes().get(3)).getNuminfo().isIncludeTo());
		assertEquals("true/true", isIncludeFrom_1 + "/" + isIncludeTo_1 );
		assertEquals("true/true", isIncludeFrom_3 + "/" + isIncludeTo_3 );
	}
	
	@Test
	public void testIsBetween_lmovable(){
		UnknownNode node_1 = new UnknownNode("大约");
		UnknownNode node_2 = new UnknownNode("近");
		NumNode node_3 = new NumNode("10", 10, 10);
		NumNode node_4 = new NumNode("20", 20, 20);
	
		
		Query query = new Query("");
		query.getNodes().add(node_1);
		query.getNodes().add(node_4);
		query.getNodes().add(node_2);
		query.getNodes().add(node_3);
		
		NumParser numParser = new NumParser();
					
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "isBetween", paraTypes, methodPara, "query_");
		
		System.out.println(testQuery.getNodes().toString());
		String baseString = "[NodeType:NUM  NodeText:大约20  NumUnit:UNKNOWN, " +
				"NodeType:NUM  NodeText:近10  NumUnit:UNKNOWN]";
		assertEquals(baseString, testQuery.getNodes().toString() );
	}
	
	@Test
	public void testIsBetween_rmovable(){
		UnknownNode node_1 = new UnknownNode("左右");
		UnknownNode node_2 = new UnknownNode("多");
		UnknownNode node_3 = new UnknownNode("之间");
		NumNode node_4 = new NumNode("10", 10, 10);
		NumNode node_5 = new NumNode("20", 20, 20);		
		NumNode node_6 = new NumNode("10-20", 10, 20);
	
		
		Query query = new Query("");
		query.getNodes().add(node_4);
		query.getNodes().add(node_1);
		query.getNodes().add(node_5);
		query.getNodes().add(node_2);
		query.getNodes().add(node_6);
		query.getNodes().add(node_3);
		
		NumParser numParser = new NumParser();
					
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "isBetween", paraTypes, methodPara, "query_");
		
		System.out.println(testQuery.getNodes().toString());
		String baseString = "[NodeType:NUM  NodeText:10左右  NumUnit:UNKNOWN, " +
				"NodeType:NUM  NodeText:20多  NumUnit:UNKNOWN, " +
				"NodeType:NUM  NodeText:10-20之间  NumUnit:UNKNOWN]";
		assertEquals(baseString, testQuery.getNodes().toString() );
	}
	
	@Test
	public void testcheckNum(){
		UnknownNode node_1 = new UnknownNode("分隔符");
		NumNode node_2 = new NumNode("20", 20, 20);
					
		Query query = new Query("");
		query.getNodes().add(node_1);
		query.getNodes().add(node_2);
		NumParser numParser = new NumParser();	
		Class[] paraTypes = null;
		Object methodPara = null;
		Query testQuery = getNumParserResult(numParser, "checkNum", paraTypes, methodPara, "query_");
		System.out.println(testQuery.getNodes().toString());
		String baseString = "[NodeType:UNKNOWN  NodeText:分隔符, NodeType:NUM  NodeText:20  NumUnit:UNKNOWN]";
		assertEquals(baseString, testQuery.getNodes().toString());
	}
	
	/*@Test(expected=Exception.class)
	public void testcheckNum_ex() throws Exception{
		NumNode node = new NumNode("20");				
		Query query = new Query("");
		query.nodes().add(node);
		NumParser numParser = new NumParser(query.nodes());	
		Class[] paraTypes = null;
		Object methodPara = null;
		try{
		    Query testQuery = getNumParserResult(numParser, "checkNum", paraTypes, methodPara, "query_");
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}*/
	
	@Test
	public void testMainParse(){
		UnknownNode node_1 = new UnknownNode("分隔符");
		NumNode node_2 = new NumNode("20", 20, 20);
					
		Query query = new Query("");
		Query testQuery = null;
		query.getNodes().add(node_1);
		query.getNodes().add(node_2);
		NumParser numParser = new NumParser();	
		numParser.parse(query.getNodes());
		try{
		    Field field = numParser.getClass().getDeclaredField("query_");
		    field.setAccessible(true);
		    Object after = field.get(numParser);
		    testQuery = (Query)after;
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(testQuery.getNodes().toString());
		String baseString = "[NodeType:UNKNOWN  NodeText:分隔符, NodeType:NUM  NodeText:20  NumUnit:UNKNOWN]";
		//assertEquals(baseString, testQuery.nodes().toString());
	}
	

}
