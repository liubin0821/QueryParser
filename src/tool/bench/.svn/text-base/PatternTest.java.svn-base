/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package bench;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;



import bench.BenchQuery.Status;
import bench.db.DBBenchManager;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Pair;
import com.myhexin.qparser.util.Util;

import conf.tool.ToolUtil;

/**
 * the Class PatternTest
 *
 */

public class PatternTest {
	/**  
	 * @throws SQLException
	 * @throws IOException
	 * @throws DataConfException 
	 */
	public static void test() throws SQLException, IOException, DataConfException{
		BufferedReader reader = new BufferedReader(new FileReader("rm.bench-query-mysql.txt"));
//		BufferedReader reader = new BufferedReader(new FileReader("correct-query.txt"));
		BenchManager commandManager = new DBBenchManager(null);
		ParserAgent.init("./conf/qparser.conf");
		String text = null; 
		
		/***
		 * by weipeng 泛化ptn
		 */
		int count = 1 ;
		while((text = reader.readLine()) != null){
			if(text.length() < 1)
				continue ;
			if(text.startsWith("#"))
				break ;
			text = text.trim();
//			text = "收入增长30%以上，利润增长40%以上";
            if(text.length() == 0) continue;
            System.out.println("正在处理 " + (count++) + " 条");
            BenchQuery bq = new BenchQuery(text);
            bq.status = Status.STD;
            Query query = ParserAgent.parse(bq);
            if(!ParserAgent.parseBenchQueryFileds(query, bq, commandManager)) {
                System.err.println("<<< 标准集问句解析失败：" + bq.text);
                continue;
            }
            
            SimiQuerySet simiQuery = commandManager.getCode2SimiQuery().get(bq.treeCode);
            if(simiQuery == null){		
                simiQuery = new SimiQuerySet();
                commandManager.getCode2SimiQuery().put(bq.treeCode, simiQuery);			
            }
            ArrayList<BenchQuery>  querys = simiQuery.getSubCode2ListMap().get(bq.specificPtnCode);
            if(querys == null){
            	querys = new ArrayList<BenchQuery>();
            	simiQuery.getSubCode2ListMap().put(bq.specificPtnCode, querys);
            }
            boolean flag = true;
            for(BenchQuery tmp : querys){
            	if(tmp.text.equals(bq.text)){
            		flag = false;
            		break;
            	}
            }
            if(flag){
            	querys.add(bq);
            }           
		}
		
		/***
		 * by weipeng
		 * 输出文件查看
		 */
		BufferedWriter writer = new BufferedWriter(new FileWriter("rm.bench-query-pattern.txt"));
		for(Entry<Integer, SimiQuerySet> pair : commandManager.getCode2SimiQuery().entrySet()){
			int code = pair.getKey();
			System.out.println(code);
			String tree = commandManager.getTree(code);
			SimiQuerySet set = pair.getValue();
			writer.write("Treecode:"+code+"\t"+"Tree: "+ tree + "\n");
			if(tree == null){
				System.err.println("tree is null");
				System.exit(0) ;
			}
				
			boolean once = tree.contains("INDEXS:");
			once = false;
			for(Entry<Integer, ArrayList<BenchQuery>> subs : set.getSubCode2ListMap().entrySet()){
				if(once){
					writer.write("\tPattern:"+tree+"\n");
					for(BenchQuery bq : subs.getValue()){
						writer.write("\t\t"+bq.text+"\n");
						break;
					}
					break;
				}else{
					writer.write("\tPattern:"+commandManager.getPattern(subs.getKey())+"\n");
					for(BenchQuery bq : subs.getValue()){
						writer.write("\t\t"+bq.text+"\n");
					}
				}
				
			}
			writer.write("\n\n");
		}
		writer.flush();
		writer.close();
		reader.close() ;
		
//		commandManager.updateOldPatternDataBase("", false) ;
		
		/***
		 * by lu
		 * 新旧泛化结果的对比
		 */
//		diff(commandManager, "./rm.bench-pattern/oldPtn.txt", "./rm.bench-pattern/diffPtn.txt") ;
	}
	
	private static void testNewVersion() throws SQLException, IOException,
			DataConfException {

		long time = System.currentTimeMillis();
		BenchManager commandManager = new DBBenchManager(null);
		ParserAgent.init("./conf/qparser.conf");
		HashSet<String> qureies = new HashSet<String>();
		qureies.addAll(Util.readTxtFile("./rm.bench-pattern/正确问句库.txt"));

		BufferedWriter toutCorrectPtn = new BufferedWriter(new FileWriter(new File(
				"correct-queries-ptn.txt")));
		for (String q : qureies) {
			BenchQuery bq = new BenchQuery(q);
			Query query = ParserAgent.parse(bq);
			if (ParserAgent.parseBenchQueryFileds(query, bq, commandManager) == false) {
				System.err.println("<<< 标准集问句解析失败：" + bq.text);
				continue;
			}
			toutCorrectPtn.write("query: " + bq.text + "\t");
			toutCorrectPtn.write("pattern: " + bq.specificPtn + "\t");
			toutCorrectPtn.newLine();
			System.out.println("query: " + bq.text + "\t" + "pattern: "
					+ bq.specificPtn + "\t");

		}
		toutCorrectPtn.close();

		System.out.println("耗时" + (System.currentTimeMillis() - time) / 1000.0
				+ " s");
		
//		diff(commandManager, "./rm.bench-pattern/oldPtn.txt", "./rm.bench-pattern/diffPtn.txt") ;
	}
	
	private static void diff(BenchManager commandManager,
			String oldPtnFileName, String diffPtnFileName)
			throws DataConfException {
		// String oldPtnFileName = "oldPtn.txt" ;
		// String diffPtnFileName = "diffPtn.txt" ;
		File oldPtnFile = new File(oldPtnFileName);
		File diffPtnFile = new File(diffPtnFileName);
		HashMap<String, String> oldPtnMap = new HashMap<String, String>();
		List<String> diffPtnList = new ArrayList<String>();
		HashMap<String, String> curPtnMap = new HashMap<String, String>();
		List<String> curPtnList = new ArrayList<String>();

		// 所有的问句和ptn
		for (Entry<Integer, SimiQuerySet> pair : commandManager
				.getCode2SimiQuery().entrySet()) {
			SimiQuerySet set = pair.getValue();
			for (Entry<Integer, ArrayList<BenchQuery>> subs : set
					.getSubCode2ListMap().entrySet()) {
				ArrayList<BenchQuery> bqList = subs.getValue();
				for (BenchQuery bq : bqList) {
					curPtnMap.put(bq.text, bq.specificPtn);
					curPtnList.add(bq.text + "\t" + bq.specificPtn);
				}
			}
		}

		if (oldPtnFile.exists()) {
			// 上一版本的问句和ptn
			for (String s : Util.readTxtFile(oldPtnFileName)) {
				String key = s.substring(0, s.indexOf("\t"));
				String value = s.substring(s.indexOf("\t") + 1);
				oldPtnMap.put(key, value);
			}

			Set<Entry<String, String>> curPtnSet = curPtnMap.entrySet();
			for (Entry<String, String> en : curPtnSet) {
				if (oldPtnMap.containsKey(en.getKey())) {
					if (oldPtnMap.get(en.getKey()).equals(en.getValue()))
						continue;
					else
						diffPtnList.add(en.getKey() + "\n\tOldVersion: "
								+ oldPtnMap.get(en.getKey())
								+ "\n\tNewPattern: " + en.getValue());
				} else {
					diffPtnList.add(en.getKey() + "\n\tOldVersion: "
							+ oldPtnMap.get(en.getKey()) + "\n\tNewPattern: "
							+ en.getValue());
				}
			}
		}

		if (!curPtnList.isEmpty())
			ToolUtil.write2Txt(curPtnList, oldPtnFile);
		if (!diffPtnList.isEmpty())
			ToolUtil.write2Txt(diffPtnList, diffPtnFile);
	}
	
	private static void test_out_treecode_query_ptn(String queryFile, String outFile) throws SQLException, IOException{
		
		BufferedReader reader = new BufferedReader(new FileReader(queryFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile)) ;
		BenchManager commandManager = new DBBenchManager(null);
		ParserAgent.init("./conf/qparser.conf");
		
		String text = null ;
		int count = 1 ;
		while((text = reader.readLine()) != null ){
			
			System.out.println("正在处理 "+ (count++) + " 条");
			
			if(text.equals(""))
				continue ;
			
			BenchQuery bqtest = new BenchQuery(text);
			Query queryTest = ParserAgent.parse(bqtest);
			
			if(!ParserAgent.parseBenchQueryFileds(queryTest, bqtest, commandManager)) {
				System.err.println("<<< 标准集问句解析失败：" + bqtest.text);
			} else {
				writer.write(bqtest.treeCode + "\t" + bqtest.text + "\t" + bqtest.specificPtn) ;
				writer.newLine() ;
			}
		}
		
		reader.close() ;
		writer.close() ;
	}
	
	private static void test_out_duplicatePtn(String queryFile, String outFile) throws IOException, SQLException{
		BufferedReader reader = new BufferedReader(new FileReader(queryFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile)) ;
		BenchManager commandManager = new DBBenchManager(null);
		ParserAgent.init("./conf/qparser.conf");
		
		ArrayList<DuplicatePtn> duplicatePtnList = new ArrayList<DuplicatePtn>() ;
		String text = null ;
		int count = 1 ;
		while((text = reader.readLine()) != null ){
			System.out.println("正在处理 "+ (count++) + " 条");
			if(text.equals(""))
				continue ;
			BenchQuery bqtest = new BenchQuery(text);
			Query queryTest = ParserAgent.parse(bqtest);
			
			if(!ParserAgent.parseBenchQueryFileds(queryTest, bqtest, commandManager)) {
				System.err.println("<<< 标准集问句解析失败：" + bqtest.text);
			} else {
				
				boolean isExistInList = false ;
				for(DuplicatePtn dptn : duplicatePtnList){
					if(dptn.pattern.equals(bqtest.specificPtn)){
						if(dptn.tree2query.containsKey(bqtest.generalPtn))
							;
						else {
							dptn.tree2query.put(bqtest.generalPtn, bqtest.text) ;
						}
						isExistInList = true ;
						break ;
					}
				}
				if(isExistInList == false){
					duplicatePtnList.add(new DuplicatePtn(bqtest.specificPtn,bqtest.generalPtn, bqtest.text)) ;
				}
			}
		}
		
		for(DuplicatePtn dptn : duplicatePtnList){
			if(dptn.tree2query.size()>1){
				writer.write(dptn.pattern + ":\n") ;
				for(Map.Entry<String,String> en : dptn.tree2query.entrySet()){
					writer.write("\tquery: " + en.getValue() + "\ttree: " + en.getKey()+"\n") ;
				}
				writer.newLine(); 
			}
		}
		
		reader.close() ;
		writer.close() ;
	}
	
	/***
	 * 根据王玉的需求统计指标，具体见王玉2012-10-15日邮件
	 * @param queryFile
	 * @param outFile
	 * @throws IOException 
	 * @throws SQLException 
	 */
	private static void countSpePtn(String queryFile, String outFile) throws IOException, SQLException{
		BufferedReader reader = new BufferedReader(new FileReader(queryFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile)) ;
		BenchManager commandManager = new DBBenchManager(null);
		ParserAgent.init("./conf/qparser.conf");
		
		
		String line = null ;
		String text = null ;
		String time = null ;
		String STR = "STR_LEN" ;
		int strlen = "STR_LEN".length() ;
		int count = 1 ;
		int changpinCount = 0 ;
		int hangyeCount = 0 ;
		int unknowCount = 0 ;
		int chanpinhangyeCount = 0 ;
		while((line = reader.readLine()) != null ){
			String[] ss = line.split("\t") ;
			time = ss[0] ;
			text = ss[1] ;
			
			System.out.println("正在处理 "+ (count++) + " 条");
			if(text.equals(""))
				continue ;
			
			BenchQuery bqtest = new BenchQuery(text);
			Query queryTest = ParserAgent.parse(bqtest);
			
			try{
				if(!ParserAgent.parseBenchQueryFileds(queryTest, bqtest, commandManager)) {
					System.err.println("<<< 标准集问句解析失败：" + bqtest.text);
				} 
			} catch (NullPointerException e){
				writer.flush() ;
			}
			
			String ptn = bqtest.specificPtn ;
			// 这里处理需求
			
			if(ptn.contains("STR_VAL:主营产品名称") || ptn.contains("STR_VAL:行业概念")){
				++chanpinhangyeCount ;
				if(ptn.contains("STR_VAL:主营产品名称"))
					++changpinCount ;
				if(ptn.contains("STR_VAL:行业概念"))
					++hangyeCount ;
				writer.write(time + "\t" + text + "\t" + bqtest.specificPtn + "\n") ;
			} else if(ptn.contains("UNKNOWN")){
				ptn = ptn.substring(0, ptn.indexOf(STR)-1);
				String[] nodes = ptn.split(" ") ;
				boolean isLegal = true ;
				for(String node : nodes){
					if(!node.startsWith("UNKNOWN")){
						isLegal = false ;
						break ;
					}
				}
				if(isLegal){
					++unknowCount ;
					writer.write(time + "\t" + text + "\t" + bqtest.specificPtn + "\n") ;
				}
			} else continue ;
			
		}
		
		writer.write("行业概念: " + hangyeCount + "个\n") ;
		writer.write("主营产品名称: " + changpinCount + "个\n") ;
		writer.write("行业概念+主营产品名称: " + chanpinhangyeCount + "个\n") ;
		writer.write("unknown: " + unknowCount + "个\n") ;
		
		
		reader.close() ;
		writer.close() ;
		
	} 
	
	/**
	 * @rm.param args
	 * @throws SQLException
	 * @throws IOException
	 * @throws DataConfException 
	 */
public static void main(String[] args) throws SQLException, IOException, DataConfException{
		long time = System.currentTimeMillis() ;
//		test();
		test_out_treecode_query_ptn("./bench-pattern/correct-queries/20121016_all_correct-quries.txt", "./bench-pattern/query2pattern.bak/20121016_all_treecode_query_pattern.txt") ;
//		test_out_duplicatePtn("./bench-pattern/correct-queries/20121015_all_correct-quries.txt", "./bench-pattern/query2pattern.bak/duplicatePtn.txt") ;
//		countSpePtn("./bench-pattern/correct-queries/query.txt", "./bench-pattern/query2pattern.bak/王玉.txt") ;
		
		System.out.println("耗时" + (System.currentTimeMillis() - time)/1000.0 + " s");
	}
	
	private static class DuplicatePtn{
		DuplicatePtn(String pattern, String tree, String query) {
			this.pattern = pattern ;
			tree2query.put(tree, query) ;
		}
		
		String pattern ;
		HashMap<String, String> tree2query = new HashMap<String, String>() ;
//		static HashSet<String> PtnSet = new HashSet<String>() ;
//		String pattern ;
//		HashSet<String> trees = new HashSet<String>() ; 
	}
}
