package bench;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

/**
 * the Class CommandManager
 */
public abstract class BenchManager {
		
	protected HashMap<Integer, SimiQuerySet> treeCode2SimiQuery =
            new HashMap<Integer, SimiQuerySet>();
	protected HashMap<String, Integer> tree2CodeMap =
			new HashMap<String, Integer>();
	protected HashMap<Integer, String> code2TreeMap =
			new HashMap<Integer, String>();
	protected TreeMap<String, Integer> pattern2CodeMap = 
			new TreeMap<String, Integer>();
	protected HashMap<Integer, String> code2PatternMap = 
			new HashMap<Integer, String>();
	private Set<String> oldPtnSet = 
			new HashSet<String>() ;
	
//	/** 存放UNKNOWN的哈希表,lu */
//	protected HashMap<String, LinkedList<String>> unknowPtnsMap = 
//			new HashMap<String, LinkedList<String>>() ; 
	protected int currMaxTreeCode = 0;
	protected int currMaxPatternCode = 0;
	protected boolean isLoadQuery = true;
	public static final String TABLE_NAME = "bench_query" ;
	
    /**
     * @rm.param parser
     * @rm.param stdQueryPath
     * @throws IOException
     */
    public BenchManager(String stdQueryPath) throws IOException {
    	this(stdQueryPath, true);
    }
    
    /**
     * @rm.param parser
     * @rm.param stdQueryPath
     * @rm.param loadQuery 
     * @throws IOException
     */
    public BenchManager(String stdQueryPath, boolean loadQuery) throws IOException {
    	tree2CodeMap.put("问句解析出错", 0);
    	code2TreeMap.put(0, "问句解析出错");
        this.isLoadQuery = loadQuery;
        loadStdQuery(stdQueryPath);
    }
    
	/**
     * 导入标准测试集到HashMap < String, SimiQuerySet > tree2SimiQuery中
     * @rm.param stdQueryPath
     */
    protected abstract void loadStdQuery(String stdQueryPath);
    
	/**
	 * 将问句query加入到回归测试集里面，如果query解析出错，则不添加。
	 * @rm.param query
	 * @rm.param force 
	 * @return 结果
	 * @throws CommandException 
	 */
	public abstract boolean addBenchQuery(BenchQuery query, boolean force) throws CommandException;
	
	/**
	 * 添加树，并返回树的编号
	 * @rm.param tree
	 * @rm.param code
	 * @return 树的编号
	 */
	public int addTree(String tree){
		if(tree2CodeMap.containsKey(tree)){
			return tree2CodeMap.get(tree);
		}
		int code = ++ currMaxTreeCode;
		tree2CodeMap.put(tree, code);
		code2TreeMap.put(code, tree);
		
		SimiQuerySet simiSet = new SimiQuerySet();
		simiSet.setTree(tree);
		simiSet.setTreeCode(code);
		treeCode2SimiQuery.put(code, simiSet);		
		return code;
	}
	
	/***
	 * 查找已有的specificPtn,获取specificPtnCode
	 * 找不到返回-1
	 * @rm.param specificPtn
	 * @return
	 */
	public int getSpecificPtnCode(String specificPtn){
		if(pattern2CodeMap.containsKey(specificPtn))
			return pattern2CodeMap.get(specificPtn) ;
		else return -1 ;
	}
	/**
	 * @rm.param treeCode
	 * @rm.param specificPtn
	 * @return subCode
	 */
	public int addPattern(String specificPtn) {
		if(pattern2CodeMap.containsKey(specificPtn)){
			return pattern2CodeMap.get(specificPtn);
		}
		int subCode = ++currMaxPatternCode;
		pattern2CodeMap.put(specificPtn, subCode);
		code2PatternMap.put(subCode, specificPtn);
		return subCode;
	}
	

	/**
	 * 添加新来增加的正确问句到回归测测集
	 */

	public void addNewComingStdQuery() {
	}
	
	/**
	 * @rm.param query
	 * @return 删除结果
	 * @throws CommandException 
	 */
	public abstract boolean delBenchQuery(BenchQuery query);
    
    /**
     * @return 丢弃全部新
     */
    public ArrayList<BenchQuery> discard() {
        ArrayList<BenchQuery> rtn = new ArrayList<BenchQuery>();
    	for(SimiQuerySet querySet : getCode2SimiQuery().values()){
    		rtn.addAll(querySet.discard());    		
    	}
    	return rtn;
	}
	
	/**
	 * 关闭bench_query Manager
	 */
	public void close(){
		treeCode2SimiQuery.clear();
		tree2CodeMap.clear();
		code2TreeMap.clear();
		pattern2CodeMap.clear();
		code2PatternMap.clear();
	}
	
	/**
	 * @return the tree2SimiQuery_
	 */
	public HashMap<Integer, SimiQuerySet> getCode2SimiQuery() {
		return treeCode2SimiQuery;
	}
	
    /**
     * @rm.param treeCode
     * @return 相思结构的问句
     */
    public SimiQuerySet getSimiQuerySet(int treeCode){
    	return getCode2SimiQuery().get(treeCode);
    }
    
    /**
     * @rm.param tree
     * @return 相思结构的问句
     */
    public SimiQuerySet getSimiQuerySet(String tree){
    	if(tree2CodeMap.containsKey(tree)){
    		int treeCode = tree2CodeMap.get(tree);
    		return getCode2SimiQuery().get(treeCode);
    	}
    	return null;
    }
    
    /**
     * @rm.param treeCode
     * @rm.param ptnCode
     * @return subTree
     */
    public String getPattern(int ptnCode){
    	return code2PatternMap.get(ptnCode);
    }
    
	
	/**
	 * 根据树的编号，获取树结构
	 * @rm.param code
	 * @return 返树结构
	 */
	public String getTree(int code){
		return code2TreeMap.get(code);
	}
	
	/**
	 * 根据树的结构，得到树编号
	 * @rm.param tree
	 * @return 返回树编号
	 */
	public int getTreeCode(String tree){
		if(tree == null || !tree2CodeMap.containsKey(tree)){
			return -1;
		}
		return tree2CodeMap.get(tree);
	}
	
	public TreeMap<String, Integer> getPattern2CodeMap() {
		return pattern2CodeMap ;
	}
	
	public Set<String> oldPtnSet(){
		return this.oldPtnSet ;
	}
	
	public void SetOldPtnSet(Collection<String> c){
		this.oldPtnSet.addAll(c) ;
	}
	
	
	/**
	 * @rm.param bq
	 * @return 保存结果
	 * @throws CommandException 
	 */
	public abstract boolean saveBenchQuery(BenchQuery bq) throws CommandException;
	
	public abstract void updateOldPatternDataBase(String saveFileName, boolean isCover)throws SQLException,IOException ;
	
	public abstract void updateOldPatternDataBase(String saveFileName)throws SQLException,IOException ;
	


}