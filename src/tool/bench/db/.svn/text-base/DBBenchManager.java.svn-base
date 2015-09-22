/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package bench.db;

import java.io.IOException;
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

import com.myhexin.qparser.Query;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

import conf.tool.ToolUtil;

import bench.BenchManager;
import bench.BenchQuery;
import bench.BenchQuery.QueryType;
import bench.CommandException;
import bench.ParserAgent;
import bench.SimiQuerySet;
import bench.BenchQuery.Status;
import bench.db.DataBaseHandler;
/**
 * the Class BenchDataBase
 *
 */
public class DBBenchManager extends BenchManager{
    /**
     * @rm.param parser
     * @rm.param database 
     * @throws SQLException
     * @throws IOException 
     */
    public DBBenchManager(String database) throws SQLException, IOException {
    	this(database, true);
    }
    
    /**
     * @rm.param parser
     * @rm.param database 
     * @rm.param loadQuery 
     * @throws SQLException
     * @throws IOException 
     */
    public DBBenchManager(String database, boolean loadQuery) throws SQLException, IOException {
    	super(database, loadQuery);
    }

	public int addTree(String tree){
		return addTree(tree, currMaxTreeCode+1, true);
	}
	
	public int addPattern(String pattern){
		return addPattern(pattern, currMaxPatternCode+1, true);
	}
	
	public void addNewComingStdQuery(){
		try {
			String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			if(currDate.compareTo(lastLoadDate) <= 0){
				return ;
			}
			lastLoadDate = currDate;
			if(!isLoadQuery){
				System.out.println("不需要加载正确问句库新问句");
				return ;
			}
			if(!webQueryHandler.tryConn()){
				System.out.println("webQueryHandler 连接失败");
				return ;
			}
			String sql = "SELECT distinct(query) FROM `effective_query` WHERE querystatus='正确问句' AND searchtime > '"+lastLoadDate+"' ";
//			String sql = "SELECT distinct(query) FROM `effective_query` WHERE querystatus='正确问句' ";
			ResultSet rs = webQueryHandler.stmt.executeQuery(sql);
			while(rs.next()){
				try{
					String text = rs.getString(1);
					
		            if(text.length() == 0) continue;
		            BenchQuery bq = new BenchQuery(text);
		            System.out.println("loading effective query: "+text);
		            Query query = ParserAgent.parse(bq);
		            ParserAgent.parseBenchQueryFileds(query, bq, this);
		            if(bq.treeCode == 0){
		            	bq.status = Status.ERR;
		            }
		            if(addBenchQuery(bq, false)){
		            	System.out.println("添加成功");
		            }else{
		            	System.out.println("添加失败");
		            }
				}catch (SQLException e){
					e.printStackTrace();
				}catch (CommandException e) {
					System.out.println(e.getMessage());
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			rs.close();
			webQueryHandler.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean addBenchQuery(BenchQuery query, boolean force) throws CommandException{
		return addBenchQuery(query, force, false);
	}

	public boolean delBenchQuery(BenchQuery query){
		query.status = Status.DEL;
		SimiQuerySet simiSet = getSimiQuerySet(query.treeCode);
		if(simiSet == null){
			query.status = Status.DEL;
			return dataBaseHandler.save(query);
		}else{
			ArrayList<BenchQuery> subList = simiSet.getSpecificPtnBqList(query.specificPtnCode);
			if(subList == null){
				return dataBaseHandler.save(query);
			}else{
				for(int idx = subList.size() - 1; idx >= 0; idx--){
					if(query.equals(subList.get(idx))){
						subList.remove(idx);
						break;
					}
				}
				return dataBaseHandler.save(query);
			}
		}
	}
	
	public boolean saveBenchQuery(BenchQuery query) throws CommandException{
		if(query.treeCode == 0){
			query.status = Status.DEL;
			return delBenchQuery(query);
		}
		return dataBaseHandler.save(query);
	}
	
	public void close(){
		treeCode2SimiQuery.clear();
		tree2CodeMap.clear();
		code2TreeMap.clear();
		pattern2CodeMap.clear();
		code2PatternMap.clear();
		this.webQueryHandler.close();
		this.dataBaseHandler.close();
	}
	/**
	 * 从bench_query数据库中导入非“del”的问句，并且全部加入到内存中（即使遇到解析错误，保证上次问句库全部导出）
	 */
	@Override
	protected void loadStdQuery(String dataBase) {
		if(dataBase == null){
			return ;
		}
		try {
			dataBaseHandler = new DataBaseHandler("192.168.23.105", 3306, dataBase, "root", "kernel");
			webQueryHandler = new DataBaseHandler("172.20.0.52", 3306, "web_stockpick", "wc", "kernel");
			
			String sql = "SELECT max(date) FROM " +BenchManager.TABLE_NAME + " WHERE 1";
			ResultSet rs = dataBaseHandler.stmt.executeQuery(sql);
			if(rs.next()){
				lastLoadDate = rs.getString(1);
				rs.close();
			}else{
				lastLoadDate = "2012-08-01";
			}
			
			sql = "SELECT treecode, tree from `tree_type` WHERE 1 ";
			rs = dataBaseHandler.stmt.executeQuery(sql);
			while(rs.next()){
				int code = Integer.parseInt(rs.getString(1));
				String tree = rs.getString(2);
				addTree(tree, code, false);
			}
			rs.close();
			
			sql = "SELECT ptncode, pattern from `pattern_type` WHERE 1 ";
			rs = dataBaseHandler.stmt.executeQuery(sql);
			while(rs.next()){
				int patternCode = Integer.parseInt(rs.getString(1));
				String pattern = rs.getString(2);
				addPattern(pattern, patternCode, false);
			}
			rs.close();
			
			loadBenchQuery(dataBase);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	private boolean addBenchQuery(BenchQuery query, boolean force, boolean fromBench) throws CommandException{
		if(query.status == Status.DEL){
			return false;
		}
		SimiQuerySet simiSet = getSimiQuerySet(query.treeCode);
		if(simiSet == null){
			simiSet = new SimiQuerySet();
			treeCode2SimiQuery.put(query.treeCode, simiSet);
		}
		if(simiSet.addStdQuery(query, force, fromBench)){
			return dataBaseHandler.save(query);
		}
		return false;
	}

	/**
	 * @rm.param tree
	 * @rm.param code
	 * @rm.param writeFlag
	 * @return 树编号
	 */
	private int addTree(String tree, int code, boolean writeFlag){
		if(tree2CodeMap.containsKey(tree)){
			code = tree2CodeMap.get(tree);
		}else{
			if(writeFlag){
				int tmpCode = getDBTreeCode(tree);
				if(tmpCode != -1){
					return tmpCode;
				}
			}
			
			if(currMaxTreeCode < code){
				currMaxTreeCode = code;
			}
			tree2CodeMap.put(tree, code);
			code2TreeMap.put(code, tree);
			
			SimiQuerySet simiSet = new SimiQuerySet();
			simiSet.setTree(tree);
			simiSet.setTreeCode(code);
			treeCode2SimiQuery.put(code, simiSet);
		}
		if(writeFlag && dataBaseHandler != null){
			String sql = "delete from tree_type where treecode="+code+" ";
			try{
				dataBaseHandler.stmt.execute(sql);
			}catch (SQLException e){
			}
			sql = "insert into tree_type (treecode, tree) values("+code+", '"+tree+"')";
			try{
				dataBaseHandler.stmt.execute(sql);
			}catch (SQLException e){
			}
		}
		return code;
	}
	
	private int getDBTreeCode(String tree){
		int treeCode = -1;
		String sqlTree = tree.replaceAll("'", "''");
		String sql = "select treecode from tree_type where tree='"+sqlTree+"' ";
		if(dataBaseHandler != null && dataBaseHandler.tryConn()){
			try {
				ResultSet rs = dataBaseHandler.stmt.executeQuery(sql);
				if(rs.next()){
					treeCode = Integer.parseInt(rs.getString(1));
					if(currMaxPatternCode < treeCode){
						currMaxPatternCode = treeCode;
					}
					pattern2CodeMap.put(tree, treeCode);
					code2PatternMap.put(treeCode, tree);
					return treeCode;
				}
				rs.close();
			} catch (SQLException e) {
			}
		}
		return treeCode;
	}

	/**
	 * @rm.param pattern
	 * @rm.param i
	 * @rm.param b
	 * @return
	 */
	private int addPattern(String pattern, int ptnCode, boolean writeFlag) {
		if(pattern2CodeMap.containsKey(pattern)){
			ptnCode = pattern2CodeMap.get(pattern);
		}else{
			if(writeFlag){
				int tmpCode = getDBPatternCode(pattern);
				if(tmpCode != -1){
					return tmpCode;
				}
			}
			if(currMaxPatternCode < ptnCode){
				currMaxPatternCode = ptnCode;
			}
			pattern2CodeMap.put(pattern, ptnCode);
			code2PatternMap.put(ptnCode, pattern);
		}
		if(writeFlag && dataBaseHandler != null){
			String sql = "delete from pattern_type where ptncode="+ptnCode+" ";
			try{
				dataBaseHandler.stmt.execute(sql);
			}catch (SQLException e){
			}
			sql = "insert into pattern_type (ptncode, pattern) values("+ptnCode+", '"+pattern+"')";
			try{
				dataBaseHandler.stmt.execute(sql);
			}catch (SQLException e){
			}
		}
		return ptnCode;
	}
	
	private int getDBPatternCode(String pattern){
		int ptnCode = -1;
		String sqlPttern = pattern.replaceAll("'", "''");
		String sql = "select ptncode from pattern_type where pattern='"+sqlPttern+"' ";
		if(dataBaseHandler != null && dataBaseHandler.tryConn()){
			try {
				ResultSet rs = dataBaseHandler.stmt.executeQuery(sql);
				if(rs.next()){
					ptnCode = Integer.parseInt(rs.getString(1));
					if(currMaxPatternCode < ptnCode){
						currMaxPatternCode = ptnCode;
					}
					pattern2CodeMap.put(pattern, ptnCode);
					code2PatternMap.put(ptnCode, pattern);
					return ptnCode;
				}
				rs.close();
			} catch (SQLException e) {
			}
		}
		return ptnCode;
	}
	
	/**
	 * 导入bench_query中全部问句，即使解析错误的
	 * @rm.param dataBase
	 * @throws SQLException
	 */
	private void loadBenchQuery(String dataBase) throws SQLException{
		if(!isLoadQuery){
			return ;
		}
		DataBaseHandler tmpDBHandler = new DataBaseHandler("192.168.23.105", 3306, dataBase, "root", "kernel");
		String sql = "SELECT text, date, status, treecode, type FROM " +BenchManager.TABLE_NAME + " WHERE status != 'del'";
//		String sql = "SELECT text, date, status, treecode, type FROM `bench_query` WHERE status != 'del' ";
//		String sql = "SELECT text, date, status, treecode, type FROM `bench_query` WHERE status = 'err' ";
		ResultSet rs = tmpDBHandler.stmt.executeQuery(sql);
		while(rs.next()){
			try{
				String text = rs.getString(1);
				String date = rs.getString(2);
				String status = rs.getString(3);
				int treeCode = Integer.parseInt(rs.getString(4));
				String type = rs.getString(5);
	            if(text.length() == 0) continue;
	            BenchQuery bq = new BenchQuery(text);
	            bq.date = date;
	            bq.oldTreeCode = treeCode;
	            System.out.println("loading rm.bench query: "+text);
	            Query query = ParserAgent.parse(bq);
	            ParserAgent.parseBenchQueryFileds(query, bq, this);
	            //原来的问句状态；如果上次解析错误（本次正确，则恢复）；如果上次正确，本次错误则标记
	            bq.status = Status.toStatus(status);
	            if(bq.status == Status.ERR && bq.treeCode != 0){
	            	 bq.status = Status.STD;
	            }else if(bq.status == Status.STD && bq.treeCode == 0){
	            	 bq.status = Status.ERR;
	            }
	            
	            // 原来问句的type，存于oldType中
	            // '股票_文字型', '股票_财务型', '股票_技术型', '基金_文字型', '基金_财务型', '其它'
	            if(type.equals("股票_文字型"))
	            	bq.oldType = QueryType.STOCK_STR ;
	            else if(type.equals("股票_财务型"))
	            	bq.oldType = QueryType.STOCK_CAIWU ;
	            else if(type.equals("股票_技术型"))
	            	bq.oldType = QueryType.STOCK_TEC ;
	            else if(type.equals("基金_文字型"))
	            	bq.oldType = QueryType.FUND_STR ;
	            else if(type.equals("基金_财务型"))
	            	bq.oldType = QueryType.FUND_CAIWU ;
	            else 
	            	bq.oldType = QueryType.UNKNOWN ;
	            
	            //全部加入进去
	            if(addBenchQuery(bq, true, true)){
	            	System.out.println("添加成功");
	            }else{
	            	System.out.println("添加失败");
	            }
			}catch (SQLException e){
				e.printStackTrace();
			}catch (CommandException e) {
				System.out.println(e.getMessage());
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		rs.close();
		tmpDBHandler.close();
	}
	
	@Override
	public void updateOldPatternDataBase(String saveFileName)throws SQLException,IOException{
		updateOldPatternDataBase(saveFileName, false) ;
	}
	
	
	
	@Override
	public void updateOldPatternDataBase(String saveFileName,boolean isCover) throws SQLException,IOException{
		DataBaseHandler dbHandler = new DataBaseHandler("172.20.0.52", 3306,
				"web_stockpick", "wc", "kernel");
		List<String> rtnList = new ArrayList<String>(2000);
		String sqlLoad = "SELECT pattern FROM `oldpattern_list` WHERE 1 ";
		ResultSet rs = dbHandler.stmt.executeQuery(sqlLoad);
		while(rs.next()){
			String text = rs.getString(1);
			rtnList.add(text);
		}		
		String currDate = new SimpleDateFormat("yyyy年MM月dd日hh时mm分").format(new Date());
		if( saveFileName!= null && !saveFileName.isEmpty())
			ToolUtil.write2Txt(rtnList, "./bench-pattern/" + currDate + "_" + saveFileName) ;	//先在本地存储一份
		
		//清空原有的中转表pattern
		String sqlDel = "DELETE FROM `oldpattern_list_medium` WHERE 1" ;
		try{
			dbHandler.stmt.execute(sqlDel) ;
		} catch (SQLException e){
			e.printStackTrace() ;
		}
		
		//写入新的pattern到中转表
		Collection<String> ptns = this.code2PatternMap.values() ;
		for(String ptn : ptns){
			String sqlWrite = "INSERT `oldpattern_list_medium` (pattern, pattern_code) VALUES ( \"" + ptn + "\" , 1)" ;
			try{
				dbHandler.stmt.execute(sqlWrite) ;				
			} catch (SQLException e){
				e.printStackTrace() ;
			}
		}
		
		//是否需要写入到oldpattern_list
		if(isCover ==  true){
			for(String ptn : ptns){
				if(rtnList.contains(ptn))
					continue ;
				String sqlWrite = "INSERT `oldpattern_list` (pattern, pattern_code) VALUES ( \"" + ptn + "\" , 1)" ;
				try{
					dbHandler.stmt.execute(sqlWrite) ;				
				} catch (SQLException e){
					e.printStackTrace() ;
				}
			}
		}
		
		dbHandler.close() ;
	}
	
	/***
	 * 添加旧的的bench query
	 * @rm.param fileName 添加的文件位置
	 * @throws CommandException 
	 */
	public void addOldBenchQuery(String fileName) throws CommandException{
		if(fileName == null || fileName.equals(""))
			return  ;
		ArrayList<String> queries = null ; 
		try {
			queries = Util.readTxtFile(fileName) ;
		} catch (DataConfException e) {
			e.printStackTrace();
		}
		
		for(String text : queries){
			if(text.length() == 0) continue;
			BenchQuery bq = new BenchQuery(text);
			Query query = ParserAgent.parse(bq);
			
			if(!ParserAgent.parseBenchQueryFileds(query, bq, this))
				return ;
			
		    if(bq.treeCode == 0){
	        	bq.status = Status.ERR;
	        }
	        if(addBenchQuery(bq, false)){
	        	System.out.println("添加成功");
	        }else{
	        	System.out.println("添加失败");
	        }
		}		
	}
	
	/****
	 * 在紧急情况下，提供将本地存储的备份pattern快速写入oldpattern_list
	 * @rm.param fileName - 本地存储的pattern
	 * @throws SQLException
	 * @throws IOException
	 */
	private static void uploadOldPtnList(String fileName) throws SQLException,IOException {
		DataBaseHandler dbHandler = new DataBaseHandler("172.20.0.52", 3306,
				"web_stockpick", "wc", "kernel");
		// 先下载oldpattern_list_medium已有的pattern
		String sqlLoad = "SELECT pattern FROM `oldpattern_list_medium` WHERE 1 ";
		ResultSet rs = dbHandler.stmt.executeQuery(sqlLoad);
		Set<String> oldPtns = new HashSet<String>() ;
		
		while(rs.next()){
			try{
				oldPtns.add(rs.getString(1)) ;
			}catch (SQLException e){
				System.err.println(rs.getString(1));
				e.printStackTrace() ;
			}
		}
		System.out.println("size of oldPtns is : " + oldPtns.size());
		Set<String> ptns = new HashSet<String>() ;
		try {
			ptns.addAll(Util.readTxtFile(fileName)) ;
		} catch (DataConfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("size of ptns is : " + ptns.size());

		//写入oldpattern_list_medium
		for(String ptn : ptns){
			if(oldPtns.contains(ptn))
				continue ;
			String sqlWrite = "INSERT `oldpattern_list_medium` (pattern, pattern_code) VALUES ( \"" + ptn + "\" , 1)" ;
			try {
				dbHandler.stmt.execute(sqlWrite);
			} catch (SQLException e) {
				System.err.println(ptn);
				e.printStackTrace();
			}
		}		
		dbHandler.close() ;
	}
	
	/****
	 * 先删除table
	 * 在紧急情况下，提供将本地存储的备份treecode和pattern快速写入table
	 * @rm.param fileName - 本地存储的pattern
	 * @throws SQLException
	 * @throws IOException
	 * @throws DataConfException 
	 */
	private static void uploadOldPtnList_Treecode(String fileName, String table) throws SQLException,IOException, DataConfException {
		DataBaseHandler dbHandler = new DataBaseHandler("172.20.0.52", 3306,
				"web_stockpick", "wc", "kernel");
		List<String> ptns =  Util.readTxtFile(fileName) ;
		System.out.println(ptns.size());
		if(ptns.size() != 0){
			String sql = "delete from " + table + " where 1" ;
			dbHandler.stmt.execute(sql);
		}
		
		//写入oldpattern_list_medium
		for(String ptn : ptns){
			if(!ptn.contains("\t"))
				continue ;
			String treecode = ptn.substring(0, ptn.indexOf("\t")) ;  
			String p = ptn.substring(ptn.indexOf("\t")+1) ; 
			String sqlWrite = "INSERT " + table + "(pattern, pattern_code) VALUES ( \"" + p + "\" , " +treecode+")" ;
//			System.out.println(sqlWrite);
			try {
				dbHandler.stmt.execute(sqlWrite);
			} catch (SQLException e) {
				System.err.println(ptn);
				e.printStackTrace();
			}
		}		
		dbHandler.close() ;
	}
	
	private DataBaseHandler dataBaseHandler;
	private DataBaseHandler webQueryHandler;
	private String lastLoadDate = "2012-05-28";
	
	public static void main(String[] args) throws Exception{
//		String fileName = "" ;
//			DBBenchManager DBMgr = new DBBenchManager("") ;
//			DBMgr.addOldBenchQuery(fileName) ;		
		
//		ParserAgent.init("./conf/qparser.conf");
//		BenchManager commandManager = new DBBenchManager(null);
//		
//		//生成pattern
//		List<String> ptnLst = new ArrayList<String>(60000) ;
//		List<String> queries = Util.readTxtFile("正确问句库.txt") ;
//		for(String s : queries){
//			BenchQuery bq = new BenchQuery(s);
//            Query query = ParserAgent.parse(bq);
//			String sepecificPtn = ParserAgent.parseQueryPattern(query,commandManager) ;
//			System.out.println(sepecificPtn);
//			ptnLst.add(sepecificPtn) ;
//		}		
//		
//		ToolUtil.write2Txt(ptnLst, "./20120919正确问句库patterns.txt") ;
		
//		DBBenchManager.uploadOldPtnList("./bench-pattern/All正确问句库patterns.txt") ;
		//	uploadOldPtnList_Treecode("./bench-pattern/query2pattern.bak/20120924-17-19_treecode_patern.txt", "oldpattern_list_medium") ;
		uploadOldPtnList_Treecode("./bench-pattern/query2pattern.bak/20121017-09-38_treecode_patern.txt", "oldpattern_list_medium") ;
//		20121016-21-11_treecode_patern.txt
		
//		String ptn = "we" ;
//		String treecode = "1" ;
//		String sqlWrite = "INSERT " + " `table` " + "(pattern, pattern_code) VALUES ( \"" + ptn + "\" , " +treecode+")" ;
//		System.out.println(sqlWrite);
		
	}
}
    
