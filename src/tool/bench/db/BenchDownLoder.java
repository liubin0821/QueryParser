/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package bench.db;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import conf.tool.ToolUtil;

import bench.BenchManager;

/**
 * the Class BenchDownLoder
 * 导出正确问句库
 *
 */
public class BenchDownLoder {
	
	/**
	 * 从正确问句库中导入问句
	 * @rm.param saveFileName
	 * @throws SQLException
	 * @throws IOException 
	 */
//	public static void loadFromCorrectQueryCorpus(String saveFileName) throws SQLException, IOException {
//		DataBaseHandler dataGetter = new DataBaseHandler("172.20.0.52", 3306, "web_stockpick", "wc", "kernel");
//		BufferedWriter writer = new BufferedWriter(new FileWriter(saveFileName));
//		
//		String sql = "SELECT distinct(query) FROM `effective_query` WHERE querystatus='正确问句' AND searchtime < '2012-09-09' ";
//		ResultSet rs = dataGetter.stmt.executeQuery(sql);
//		while(rs.next()){
//			try{
//				String text = rs.getString(1);
//				writer.write(text+"\n");
//			}catch(SQLException e){
//				e.printStackTrace();
//			}
//		}
//		writer.flush();
//		writer.close();
//		dataGetter.close();
//	}
	
	/***
	 * 从正确问句库中导入问句
	 * @param saveFileName - 保存文件名，文件会保存到默认目录./bench-pattern/correct-queries/下，
	 * 并添加上时间信息
	 * @param endTime 截止时间，当需要全部导出是使用“all”，时间参数格式“yyyy-MM-dd”
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void loadFromCorrectQueryCorpus(String saveFileName, String endTime) throws SQLException, IOException {
		String currTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String rexYear = "^\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}$" ;
		
		String sql = "" ;
		if(!endTime.matches(rexYear)) {
			if(endTime.equals("all")){
				sql = "SELECT distinct(query) FROM `effective_query` WHERE querystatus='正确问句' and `operators` != 'com' ";
				saveFileName = "./bench-pattern/correct-queries/" + currTime + "_all_"+saveFileName;
			}
			else {
				System.err.println("please input correct time format!");
				System.exit(-1) ;
			}
		}
		else {
			sql = "SELECT distinct(query) FROM `effective_query` WHERE querystatus='正确问句' AND searchtime <= " + "'" + endTime+"'";
			saveFileName = "./bench-pattern/correct-queries/" + "before"+endTime + "_"+saveFileName;
		}

		DataBaseHandler dataGetter = new DataBaseHandler("172.20.0.52", 3306, "web_stockpick", "wc", "kernel");
		BufferedWriter writer = new BufferedWriter(new FileWriter(saveFileName));
		
//		String sql = "SELECT distinct(query) FROM `effective_query` WHERE querystatus='正确问句' AND searchtime < '2012-09-09' ";
		ResultSet rs = dataGetter.stmt.executeQuery(sql);
		while(rs.next()){
			try{
				String text = rs.getString(1);
				writer.write(text+"\n");
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		writer.flush();
		writer.close();
		dataGetter.close();
	
	}
	
	
	
	/**
	 * 从回归测试集中导出问句
	 * @rm.param saveFileName
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static void loadFromBenchQuery(String saveFileName) throws SQLException, IOException {
		DataBaseHandler dataGetter = new DataBaseHandler("192.168.23.105", 3306, "ontoask", "root", "kernel");
		BufferedWriter writer = new BufferedWriter(new FileWriter(saveFileName));
		
		String sql = "SELECT text, status FROM `" +BenchManager.TABLE_NAME + "` WHERE 1 " ;
		ResultSet rs = dataGetter.stmt.executeQuery(sql);
		while(rs.next()){
			try{
				String text = rs.getString(1);
				String status = rs.getString(2);
				writer.write(text+"\t"+status+"\n");
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		writer.close();
		dataGetter.close();
	}
	
	/***
	 * 从运维存储的oldpattern_list导出pattern
	 * @return List a list constructed by query patterns 
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<String> loadFromOldPattern() throws SQLException,IOException {
		DataBaseHandler dataGetter = new DataBaseHandler("172.20.0.52", 3306,
				"web_stockpick", "wc", "kernel");
		List<String> rtnList = new ArrayList<String>(2000);
		String sql = "SELECT pattern FROM `oldpattern_list` WHERE 1 ";
		ResultSet rs = dataGetter.stmt.executeQuery(sql);
		while (rs.next()) {
			try {
				String text = rs.getString(1);
				rtnList.add(text);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		dataGetter.close();
		return rtnList;
	}
	
	/***
	 * 从运维存储的oldpattern_list导出pattern
	 * @return return void, but will save as a txt file 
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void loadFromOldPattern(String saveFileName)throws SQLException,IOException {
		List<String> list = loadFromOldPattern() ;
		ToolUtil.write2Txt(list, saveFileName) ;
	}

	/**
	 * @rm.param args
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws SQLException, IOException{
//		loadFromCorrectQueryCorpus("./bench-pattern/9月9号之前正确问句库.txt");
//		loadFromBenchQuery("bench_query.txt") ;
		loadFromCorrectQueryCorpus("correct-quries.txt","all") ;
		
//		List<String> lst = loadFromOldPattern() ;
//		for(String s : lst)
//			System.out.println(s);
//		System.out.println(lst.size());
//		loadFromOldPattern("./bench-pattern/20120908oldPtnList.txt") ;
	}
}
